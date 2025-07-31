import React, { useEffect, useMemo, useRef, useState } from "react";

import {
  Box, Button,
  Grid,
  Link,
  List,
  ListItem,
  ListItemAvatar,
  Typography,
  useMediaQuery,
} from "@mui/material";
import CallIcon from "@mui/icons-material/Call";
import RefreshOutlinedIcon from "@mui/icons-material/RefreshOutlined";
import {
  matchIsValidTel,
  MuiTelInput,
  MuiTelInputCountry,
} from "mui-tel-input";
import discordLogo from "../../common/resources/images/discord-icon.svg";
import {
  getSignedMessagePromise,
  openNewTab,
  resolveCardanoNetwork,
  signMessageWithWallet,
} from "../../utils/utils";
import { ErrorMessage } from "../common/ErrorMessage/ErrorMessage";
import Modal from "../common/Modal/Modal";
import {
  confirmPhoneNumberCode,
  sendSmsCode,
  verifyDiscord,
} from "../../common/api/verificationService";
import { VerifyWalletFlow } from "./VerifyWalletModal.type";
import { env } from "../../common/constants/env";
import { CustomCheckBox } from "../common/CustomCheckBox/CustomCheckBox";
import { validatePhoneNumberLength } from "libphonenumber-js";
import { eventBus, EventName } from "../../utils/EventBus";
import {
  getConnectedWallet,
  getVerificationStarted,
  setVerificationStarted,
  setWalletIsVerified,
} from "../../store/reducers/userCache";
import { useAppDispatch, useAppSelector } from "../../store/hooks";
import { ToastType } from "../common/Toast/Toast.types";
import { CustomInput } from "../common/CustomInput/CustomInput";
import theme from "../../common/styles/theme";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { useMatomo } from "@datapunt/matomo-tracker-react";
import "./VerifyWalletModal.scss";

// TODO: env.
const excludedCountries: MuiTelInputCountry[] | undefined = [];

const VerifyWalletModal = () => {
  const connectedWallet = useAppSelector(getConnectedWallet);
  const dispatch = useAppDispatch();
  const { trackEvent } = useMatomo();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [verifyCurrentPaths, setVerifyCurrentPaths] = useState<
    VerifyWalletFlow[]
  >([VerifyWalletFlow.INTRO]);

  const getDefaultCountry = () => {
    const language = navigator.language;
    return language.split("-")[1] || "US";
  };

  const [defaultCountryCode] = useState<MuiTelInputCountry | undefined>(
    // @ts-ignore
    getDefaultCountry(),
  );
  const [phone, setPhone] = useState<string>("");
  const [codes, setCodes] = useState(Array(6).fill(""));
  const [phoneCodeIsBeenSending, setPhoneCodeIsBeenSending] =
    useState<boolean>(false);
  const [phoneCodeIsSent, setPhoneCodeIsSent] = useState<boolean>(false);
  const [phoneCodeShowError, setPhoneCodeShowError] = useState<boolean>(false);
  const [checkImNotARobot, setCheckImNotARobot] = useState<boolean>(false);
  const [isPhoneInputDisabled] = useState<boolean>(false);
  const [phoneCodeIsBeenConfirming, setPhoneCodeIsBeenConfirming] =
    useState<boolean>(false);
  const [enableSignDiscordSecret, setEnableSignDiscordSecret] =
    useState<boolean>(true);
  const [inputSecret, setInputSecret] = useState("");

  const userVerificationStarted = useAppSelector(getVerificationStarted);

  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);
  inputRefs.current = [];

  const { signMessage } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  const signMessagePromisified = useMemo(
    () => getSignedMessagePromise(signMessage),
    [signMessage],
  );

  useEffect(() => {
    const openVerifyWalletModal = (
      verificationState: VerifyWalletFlow = VerifyWalletFlow.INTRO,
    ) => {
      setIsOpen(true);
      handleSetCurrentPath(verificationState);
    };
    const closeVerifyWalletModal = () => {
      setIsOpen(false);
      handleSetCurrentPath(VerifyWalletFlow.INTRO);
    };
    eventBus.subscribe(EventName.OpenVerifyWalletModal, openVerifyWalletModal);
    eventBus.subscribe(
      EventName.CloseVerifyWalletModal,
      closeVerifyWalletModal,
    );

    return () => {
      eventBus.unsubscribe(
        EventName.OpenVerifyWalletModal,
        openVerifyWalletModal,
      );
      eventBus.unsubscribe(
        EventName.CloseVerifyWalletModal,
        closeVerifyWalletModal,
      );
    };
  }, []);

  const reset = (timout?: boolean) => {
    function clear() {
      setCheckImNotARobot(false);
      setPhoneCodeIsSent(false);
      setPhoneCodeShowError(false);
      setEnableSignDiscordSecret(true);
      setPhone("");
      setInputSecret("");
      setCodes(Array(6).fill(""));
      setVerifyCurrentPaths([VerifyWalletFlow.INTRO]);
    }
    if (timout) {
      setTimeout(() => {
        clear();
      }, 2000);
    } else {
      clear();
    }
  };

  const handleChangePhone = (phoneNumber: string) => {
    setPhone(phoneNumber);
  };

  const handleSendCode = async () => {
    if (matchIsValidTel(phone) && checkImNotARobot) {
      setPhoneCodeIsBeenSending(true);
      sendSmsCode(connectedWallet.address, phone.trim().replace(" ", ""))
        // @ts-ignore
        .then((response) => {
          // @ts-ignore
          if (response?.error) {
            eventBus.publish(
              EventName.ShowToast,
              // @ts-ignore
              response.message || "Error sending sms code",
              ToastType.Error,
            );
            setPhoneCodeIsBeenSending(false);
            trackEvent({ category: "sms-sent-error", action: "backend-event" });
          } else {
            handleSetCurrentPath(VerifyWalletFlow.CONFIRM_CODE);
            dispatch(
              // @ts-ignore
              setVerificationStarted({
                walletIdentifier: connectedWallet.address,
                ...response,
              }),
            );
            setPhoneCodeIsSent(true);
            setCheckImNotARobot(false);
            setPhoneCodeIsBeenSending(false);
            trackEvent({ category: "sms-sent", action: "backend-event" });
          }
        })
        .catch(() => {
          setPhoneCodeIsBeenSending(false);
        });
    }
  };

  const handleVerifyPhoneCode = () => {
    setPhoneCodeIsBeenConfirming(true);
    confirmPhoneNumberCode(
      connectedWallet.address,
      phone.trim().replace(" ", ""),
      userVerificationStarted.requestId,
      codes.join(""),
    )
      // @ts-ignore
      .then((response: { verified: boolean }) => {
        dispatch(setWalletIsVerified(response.verified));
        if (response.verified) {
          reset(true);
          setPhoneCodeIsBeenConfirming(false);
          eventBus.publish(
            EventName.ShowToast,
            "Phone number verified successfully",
          );
          setIsOpen(false);
          trackEvent({
            category: "phone-number-verified",
            action: "backend-event",
          });
        } else {
          setPhoneCodeShowError(true);
          eventBus.publish(
            EventName.ShowToast,
            "Phone number verified failed",
            ToastType.Error,
          );
          setPhoneCodeIsBeenConfirming(false);
          handleSetCurrentPath(VerifyWalletFlow.DID_NOT_RECEIVE_CODE);
          trackEvent({
            category: "phone-number-verified-failed",
            action: "backend-event",
          });
        }
      })
      .catch(() => {
        setPhoneCodeShowError(true);
        setPhoneCodeIsBeenConfirming(false);
      });
  };

  const handleVerifyDiscord = async () => {
    setEnableSignDiscordSecret(false);

    const signedMessageResult = await signMessageWithWallet(
      connectedWallet,
      inputSecret.trim(),
      signMessagePromisified,
    );

    if (!signedMessageResult.success) {
      eventBus.publish("showToast", "Error while signing", ToastType.Error);
      return;
    }
    const parsedSecret = inputSecret.split("|")[1];
    const verifyDiscordResult = await verifyDiscord(
      connectedWallet.address,
      parsedSecret,
      // @ts-ignore
      signedMessageResult.result,
    );

    if ("error" in verifyDiscordResult && verifyDiscordResult.error) {
      eventBus.publish(
        "showToast",
        verifyDiscordResult.message || "Error while verifying",
        ToastType.Error,
      );
      trackEvent({
        category: "discord-verification-failed",
        action: "backend-event",
      });
      return;
    }
    // @ts-ignore
    dispatch(setWalletIsVerified(verifyDiscordResult.verified));
    eventBus.publish(EventName.ShowToast, "Wallet verified successfully");
    handleCloseModal();
    trackEvent({ category: "discord-verified", action: "backend-event" });
  };

  const renderStartVerification = () => {
    return (
      <>
        <Box
          component="div"
          sx={{
            mx: "auto",
            textAlign: "center",
            color: theme.palette.text.neutralLightest,
            borderRadius: "12px",
          }}
        >
          <Typography
            variant="body1"
            sx={{
              mb: 2,
              color: theme.palette.text.neutralLightest,
              textAlign: "center",
              fontSize: "16px",
              fontStyle: "normal",
              fontWeight: "500",
              lineHeight: "24px",
              marginTop: "16px",
              cursor: "pointer",
            }}
          >
            To vote you will need to verify your wallet. If you would prefer,
            you can do this later. Just select your wallet and click the ‘Verify
            Wallet’ option.{" "}
          </Typography>
          <Button
            onClick={() => handleSetCurrentPath(VerifyWalletFlow.SELECT_METHOD)}
            sx={{
              mt: "24px",
              width: "100%",
              color: theme.palette.background.default,
              fontSize: "16px",
              fontStyle: "normal",
              fontWeight: 600,
              lineHeight: "20px",
              cursor: "pointer",
              padding: "12px 16px",
              textTransform: "none",
              borderRadius: "8px",
              backgroundColor: theme.palette.secondary.main,
              "&:hover": {
                backgroundColor: "#FF9277",
                color: theme.palette.background.default,
              },
            }}
          >
            Verify
          </Button>
          <Typography
            onClick={() => handleCloseModal()}
            sx={{
              color: theme.palette.text.neutralLightest,
              textAlign: "center",
              fontSize: "16px",
              fontStyle: "normal",
              fontWeight: "500",
              lineHeight: "24px",
              textDecorationLine: "underline",
              mt: "16px",
              cursor: "pointer",
            }}
          >
            Skip
          </Typography>
        </Box>
      </>
    );
  };

  const renderSelectOption = () => {
    const handleSelectVerifyWithDiscord = () => {
      handleSetCurrentPath(VerifyWalletFlow.VERIFY_DISCORD);
      setInputSecret("");
    };
    const handleSelectVerifyWithSMS = () => {
      setPhoneCodeIsBeenSending(false);
      handleSetCurrentPath(VerifyWalletFlow.VERIFY_SMS);
      setPhone("");
      setCheckImNotARobot(false);
    };
    return (
      <>
        <Typography
          style={{
            wordWrap: "break-word",
            color: theme.palette.text.neutralLightest,
            textAlign: "center",
            fontSize: "16px",
            fontStyle: "normal",
            fontWeight: 500,
            lineHeight: "24px",
            marginTop: "16px",
          }}
        >
          To verify your address please proceed with one of the options.
        </Typography>
        <List>
          <ListItem
            onClick={() => handleSelectVerifyWithDiscord()}
            sx={{
              borderRadius: "8px",
              border: `1px solid ${theme.palette.text.primary}`,
              background: theme.palette.background.default,
              mt: "12px",
              padding: "12px",
              cursor: "pointer",
              display: "flex",
              "&:hover": {
                color: "#D3DCF5",
                border: "1px solid #D3DCF5",
              },
            }}
          >
            <ListItemAvatar sx={{height: "20px", minWidth: "0px", pr: "8px", color: "inherit"}}>
              <img
                src={discordLogo}
                style={{
                  width: "20px",
                  height: "20px",
                  paddingTop: "2px",
                  filter: "brightness(0) invert(1)",
                }}
              />
            </ListItemAvatar>
            <Typography
              sx={{
                color: "inherit",
                textAlign: "center",
                fontSize: "16px",
                fontStyle: "normal",
                fontWeight: 600,
                lineHeight: "20px",
              }}
            >
              Verify with Discord
            </Typography>
          </ListItem>
          <ListItem
            onClick={() => handleSelectVerifyWithSMS()}
            sx={{
              borderRadius: "8px",
              border: `1px solid ${theme.palette.text.primary}`,
              background: theme.palette.background.default,
              mt: "12px",
              padding: "12px",
              cursor: "pointer",
              display: "flex",
              "&:hover": {
                color: "#D3DCF5",
                border: "1px solid #D3DCF5",
              },
            }}
          >
            <ListItemAvatar sx={{height: "20px", minWidth: "0px", pr: "8px", color: "inherit"}}>
              <CallIcon
                style={{ width: "20px", height: "20px", color: "inherit" }}
              />
            </ListItemAvatar>
            <Typography
              sx={{
                color: "inherit",
                textAlign: "center",
                fontSize: "16px",
                fontStyle: "normal",
                fontWeight: 600,
                lineHeight: "20px",
              }}
            >
              Verify with SMS
            </Typography>
          </ListItem>
        </List>
      </>
    );
  };

  const renderVerifyPhoneNumber = () => {
    const isValidPhoneNumber = matchIsValidTel(phone);
    const isValidNumberLength = validatePhoneNumberLength(phone);
    const isPossibleValidPhoneNumber = isValidNumberLength === "TOO_SHORT";

    return (
      <>
        <Typography
          style={{
            wordWrap: "break-word",
            color: theme.palette.text.neutralLightest,
            fontSize: "16px",
            fontStyle: "normal",
            fontWeight: "500",
            lineHeight: "24px",
            marginTop: "16px",
          }}
        >
          To verify your address, please confirm your phone number.
        </Typography>
        <Typography
          style={{
            marginTop: "24px",
            marginLeft: "16px",
            marginBottom: "4px",
            fontSize: "12px",
            fontStyle: "normal",
            fontWeight: 500,
          }}
        >
          Mobile number
        </Typography>
        <Box
          component="div"
          sx={{
            width: "100%",
            marginTop: "4px",
          }}
        >
          <MuiTelInput
            defaultCountry={defaultCountryCode}
            excludedCountries={excludedCountries}
            value={phone}
            onChange={handleChangePhone}
            disabled={isPhoneInputDisabled}
            style={{
              background: theme.palette.background.default,
              width: "100%",
            }}
            sx={{
              "& .MuiOutlinedInput-root": {
                backgroundColor: theme.palette.background.default,
                borderRadius: "12px",
                height: "56px",
                "& fieldset": {
                  borderColor: theme.palette.background.neutralDark,
                },
                "&:hover fieldset": {
                  borderColor: theme.palette.secondary.main,
                },
                "&.Mui-focused fieldset": {
                  borderColor: theme.palette.secondary.main,
                },
              },
            }}
            MenuProps={{
              PaperProps: {
                sx: {
                  backgroundColor: theme.palette.background.neutralDark,
                },
              },
              MenuListProps: {
                sx: {
                  backgroundColor: theme.palette.background.neutralDark,
                },
              },
            }}
          />

          <Box
            component="div"
            sx={{
              height: "10px",
            }}
          >
            {!phone?.length || isPossibleValidPhoneNumber ? null : (
              <>
                {isValidPhoneNumber ? null : (
                  <Typography
                    sx={{
                      color: "var(--error, #FF878C)",
                      fontSize: "12px",
                      fontStyle: "normal",
                      fontWeight: 500,
                      lineHeight: "20px",
                      marginLeft: "3px",
                    }}
                  >
                    Please enter a valid mobile number
                  </Typography>
                )}
              </>
            )}
          </Box>
        </Box>

        <Box
          onClick={() => setCheckImNotARobot(!checkImNotARobot)}
          component="div"
          sx={{ display: "flex", alignItems: "center", marginTop: "4px" }}
        >
          <Box component="div" sx={{ display: "flex", alignItems: "center" }}>
            <CustomCheckBox
              isChecked={checkImNotARobot}
              setIsChecked={setCheckImNotARobot}
            />
          </Box>
          <Typography>I am not a robot</Typography>
        </Box>
        <Grid container style={{ marginTop: "4px" }}>
          <Grid item xs={12}>
            <Box
              component="div"
              sx={{
                width: "100%",
              }}
            >
              <Button
                disabled={
                  !matchIsValidTel(phone) ||
                  !checkImNotARobot ||
                  phoneCodeIsBeenSending
                }
                onClick={() => handleSendCode()}
                sx={{
                  width: "100%",
                  color: theme.palette.background.default,
                  fontSize: "16px",
                  fontStyle: "normal",
                  fontWeight: 600,
                  lineHeight: "20px",
                  cursor: "pointer",
                  padding: "12px 16px",
                  textTransform: "none",
                  borderRadius: "8px",
                  backgroundColor: theme.palette.secondary.main,
                  "&:hover": {
                    backgroundColor: "#FF9277",
                    color: theme.palette.background.default,
                  },
                  "&:disabled": {
                    backgroundColor: "#A0AEDB",
                    color: theme.palette.text.neutralLight,
                  }
                }}
              >
                Send Code
              </Button>
            </Box>
          </Grid>
        </Grid>
      </>
    );
  };

  const renderConfirmCode = () => {
    const handleInputChange = (
      event: React.ChangeEvent<HTMLInputElement>,
      index: number,
    ) => {
      const value = event.target.value;

      if (!(value && /^[0-9]$/.test(value)) && value !== "") return;

      const updatedCodes = [...codes];
      updatedCodes[index] = value;
      setCodes(updatedCodes);

      if (value && /^[0-9]$/.test(value) && index < 5) {
        inputRefs.current[index + 1]?.focus();
      } else if (!value && index > 0) {
        inputRefs.current[index]?.focus();
      }

      setPhoneCodeShowError(false);
    };

    return (
      <>
        <Typography gutterBottom style={{ wordWrap: "break-word" }}>
          Confirm the verification code that’s been sent to <span>{phone}</span>
        </Typography>
        <div
          style={{
            display: "flex",
            justifyContent: "center",
            gap: "8px",
            marginTop: "28px",
          }}
        >
          {[...Array(6)].map((_, index) => (
            <input
              key={index}
              value={codes[index]}
              ref={(el) => (inputRefs.current[index] = el)}
              type="text"
              maxLength={1}
              onChange={(e) => handleInputChange(e, index)}
              onKeyDown={(e) => {
                const target = e.target as HTMLInputElement;
                if (e.key === "Backspace" && target.value === "") {
                  if (index > 0) {
                    inputRefs.current[index - 1]?.focus();
                  }
                }
              }}
              className="confirm-sms-code-input"
              style={{
                width: isMobile ? "43px" : "53px",
                height: isMobile ? "49px" : "58px",
                flexShrink: 0,
                borderRadius: "12px",
                border: `1px solid ${theme.palette.background.neutralDark}`,
                background: theme.palette.background.default,
                textAlign: "center",
                outline: "none",
                color: theme.palette.text.primary,
                fontSize: "18px",
                fontStyle: "normal",
                fontWeight: "600",
                lineHeight: "22px",
              }}
            />
          ))}
        </div>
        <Box
          component="div"
          className="container"
          sx={{
            display: "flex",
            justifyContent: "center",
            height: "16px",
            marginTop: "4px",
          }}
        >
          <ErrorMessage
            show={phoneCodeShowError}
            message="SMS code not valid"
          />
        </Box>
        <Grid container spacing={2} style={{ marginTop: "8px" }}>
          <Grid item xs={12}>
            <Button
              disabled={phoneCodeIsBeenConfirming || codes.includes("")}
              onClick={() => handleVerifyPhoneCode()}
              sx={{
                width: "100%",
                color: theme.palette.background.default,
                fontSize: "16px",
                fontStyle: "normal",
                fontWeight: 600,
                lineHeight: "20px",
                cursor: "pointer",
                padding: "12px 16px",
                textTransform: "none",
                borderRadius: "8px",
                backgroundColor: theme.palette.secondary.main,
                "&:hover": {
                  backgroundColor: "#FF9277",
                  color: theme.palette.background.default,
                },
                "&:disabled": {
                  backgroundColor: "#A0AEDB",
                  color: theme.palette.text.neutralLight,
                }
              }}
            >
              Verify
            </Button>
          </Grid>
        </Grid>
      </>
    );
  };

  const renderDidNotReceiveCode = () => {
    const handleEnterNewNumber = () => {
      reset();
      handleSetCurrentPath(VerifyWalletFlow.VERIFY_SMS);
    };
    const handleSendCodeAgain = async () => {
      handleSetCurrentPath(VerifyWalletFlow.CONFIRM_CODE);
      setPhoneCodeShowError(false);
      setCodes(Array(6).fill(""));
      setPhoneCodeShowError(false);
      await handleSendCode();
    };
    return (
      <>
        <Typography
          style={{
            wordWrap: "break-word",
            color: theme.palette.text.neutralLightest,
            textAlign: "center",
            fontSize: "16px",
            fontStyle: "normal",
            fontWeight: 500,
            lineHeight: "24px",
            marginTop: "16px",
          }}
        >
          Send the code again, please ensure the number you’ve entered is
          correct {phone}
        </Typography>
        <List>
          <ListItem
            onClick={() => handleSendCodeAgain()}
            sx={{
              borderRadius: "8px",
              border: `1px solid ${theme.palette.text.primary}`,
              background: theme.palette.background.default,
              mt: "12px",
              padding: "12px",
              cursor: "pointer",
              display: "flex",
              "&:hover": {
                color: "#D3DCF5",
                border: "1px solid #D3DCF5",
              },
            }}
          >
            <ListItemAvatar sx={{height: "20px", minWidth: "0px", pr: "8px", color: "inherit"}}>
              <RefreshOutlinedIcon
                style={{ width: "20px", height: "20px", color: "inherit" }}
              />
            </ListItemAvatar>
            <Typography
              sx={{
                color: "inherit",
                textAlign: "center",
                fontSize: "16px",
                fontStyle: "normal",
                fontWeight: 600,
                lineHeight: "20px",
              }}
            >
              Send the Code Again
            </Typography>
          </ListItem>
          <ListItem
            onClick={() => handleEnterNewNumber()}
            sx={{
              borderRadius: "8px",
              border: `1px solid ${theme.palette.text.primary}`,
              background: theme.palette.background.default,
              mt: "12px",
              padding: "12px",
              cursor: "pointer",
              display: "flex",
              "&:hover": {
                color: "#D3DCF5",
                border: "1px solid #D3DCF5",
              },
            }}
          >
            <ListItemAvatar sx={{height: "20px", minWidth: "0px", pr: "8px", color: "inherit"}}>
              <CallIcon
                style={{ width: "20px", height: "20px", color: "inherit" }}
              />
            </ListItemAvatar>
            <Typography
              sx={{
                color: "inherit",
                textAlign: "center",
                fontSize: "16px",
                fontStyle: "normal",
                fontWeight: 600,
                lineHeight: "20px",
              }}
            >
              Enter a New Number
            </Typography>
          </ListItem>
        </List>
      </>
    );
  };

  const validateSecret = (value: string) => {
    const pattern = /^[a-zA-Z0-9]+\|[a-zA-Z0-9]+$/;
    return pattern.test(value);
  };

  const handleInputSecretChange = (value: string) => {
    setInputSecret(value);
  };

  const renderVerifyDiscord = () => {
    return (
      <>
        <Typography
          style={{
            color: theme.palette.text.neutralLightest,
            textAlign: "center",
            fontSize: "16px",
            fontStyle: "normal",
            fontWeight: 500,
            lineHeight: "24px",
            wordWrap: "break-word",
            marginTop: "16px",
          }}
        >
          To verify your address you need to sign a secret message. You will get
          the secret from our friendly Discord bot.
        </Typography>
        <Typography
          style={{
            fontSize: "16px",
            fontStyle: "normal",
            fontWeight: 500,
            lineHeight: "24px",
            wordWrap: "break-word",
            marginTop: "24px",
          }}
        >
          1. Join our{" "}
          <Link
            sx={{
              color: theme.palette.secondary.main,
              fontSize: "16px",
              fontStyle: "normal",
              fontWeight: 500,
              lineHeight: "24px",
              textDecorationLine: "underline",
              cursor: "pointer",
            }}
            onClick={() => openNewTab(env.DISCORD_CHANNEL_URL)}
          >
            Discord Server
          </Link>{" "}
          and accept our terms and conditions by reacting with a 🚀 to the
          message in the verification channel.
        </Typography>
        <Typography
          style={{
            fontSize: "16px",
            fontStyle: "normal",
            fontWeight: 500,
            lineHeight: "24px",
            wordWrap: "break-word",
            marginTop: "16px",
          }}
        >
          2. Open the{" "}
          <Link
            sx={{
              color: theme.palette.secondary.main,
              fontSize: "16px",
              fontStyle: "normal",
              fontWeight: 500,
              lineHeight: "24px",
              textDecorationLine: "underline",
              cursor: "pointer",
            }}
            onClick={() => openNewTab(env.DISCORD_BOT_URL)}
          >
            Wallet Verification channel
          </Link>{" "}
          and follow the instructions in Discord.
        </Typography>
        <Typography
          className="verify-wallet-modal-description"
          gutterBottom
          style={{ wordWrap: "break-word", marginTop: "16px" }}
        >
          3. You will be redirected back to the Cardano Ballot application
          within a new window, to complete the sign and verification process.
        </Typography>
        <Typography
          gutterBottom
          style={{
            marginTop: "24px",
            marginLeft: "16px",
            marginBottom: "4px",
            fontSize: "12px",
            fontStyle: "normal",
            fontWeight: 500,
          }}
        >
          Secret Key
        </Typography>
        <CustomInput
          value={inputSecret}
          styles={{
            marginBottom: "24px",
          }}
          placeholder="Enter Secret Key"
          fullWidth={true}
          validate={validateSecret}
          onChange={handleInputSecretChange}
        />
        <Button
          disabled={
            !enableSignDiscordSecret ||
            inputSecret === "" ||
            !validateSecret(inputSecret)
          }
          onClick={() => handleVerifyDiscord()}
          sx={{
            width: "100%",
            color: theme.palette.background.default,
            fontSize: "16px",
            fontStyle: "normal",
            fontWeight: 600,
            lineHeight: "20px",
            cursor: "pointer",
            padding: "12px 16px",
            textTransform: "none",
            borderRadius: "8px",
            backgroundColor: theme.palette.secondary.main,
            "&:hover": {
              backgroundColor: "#FF9277",
              color: theme.palette.background.default,
            },
            "&:disabled": {
              backgroundColor: "#A0AEDB",
              color: theme.palette.text.neutralLight,
            }
          }}
        >
          Verify
        </Button>
      </>
    );
  };

  const handleCloseModal = () => {
    setIsOpen(false);
    reset(true);
  };

  const handleSetCurrentPath = (option: VerifyWalletFlow) => {
    const fileteredVerifyCurrentPaths = verifyCurrentPaths.filter(
      (p) => p !== option,
    );
    setVerifyCurrentPaths([option, ...fileteredVerifyCurrentPaths]);
  };

  const handleBack = () => {
    if (verifyCurrentPaths.length >= 2) {
      const udpatedPaths = verifyCurrentPaths.slice(1);
      setVerifyCurrentPaths(udpatedPaths);

      if (udpatedPaths[0] === VerifyWalletFlow.VERIFY_SMS) {
        setPhone("");
        setPhoneCodeIsSent(false);
      }
      if (udpatedPaths[0] === VerifyWalletFlow.CONFIRM_CODE) {
        setCodes(Array(6).fill(""));
        setPhoneCodeShowError(false);
      }
    } else {
      reset();
    }
  };

  const renderVerify = () => {
    switch (verifyCurrentPaths[0]) {
      case VerifyWalletFlow.INTRO:
        return {
          title: "Verify Your Wallet",
          render: renderStartVerification(),
        };
      case VerifyWalletFlow.SELECT_METHOD:
        return {
          title: "Select Your Method",
          render: renderSelectOption(),
        };
      case VerifyWalletFlow.VERIFY_SMS:
        if (phoneCodeIsSent) {
          return {
            title: "Confirm SMS Code",
            render: renderConfirmCode(),
          };
        } else {
          return {
            title: "Verify with SMS",
            render: renderVerifyPhoneNumber(),
          };
        }
      case VerifyWalletFlow.VERIFY_DISCORD:
        return {
          title: "Verify with Discord",
          render: renderVerifyDiscord(),
        };
      case VerifyWalletFlow.CONFIRM_CODE:
        return {
          title: "Confirm Your Code",
          render: renderConfirmCode(),
        };
      case VerifyWalletFlow.DID_NOT_RECEIVE_CODE:
        return {
          title: "Didn’t Receive a Code",
          render: renderDidNotReceiveCode(),
        };
    }
  };

  const content = renderVerify();
  return (
    <>
      <Modal
        id="verify-wallet-modal"
        isOpen={isOpen}
        name="verify-wallet-modal"
        title={content?.title}
        onClose={() => handleCloseModal()}
        disableBackdropClick={true}
        width={isMobile ? "auto" : "450px"}
        onBack={() => handleBack()}
        backButton={verifyCurrentPaths.length > 1}
      >
        {content?.render}
      </Modal>
    </>
  );
};

export { VerifyWalletModal };

import React, { useEffect, useMemo, useRef, useState } from "react";

import {
  Box,
  Grid,
  Link,
  List,
  ListItem,
  ListItemAvatar,
  Typography,
  useMediaQuery,
  useTheme,
} from "@mui/material";
import CallIcon from "@mui/icons-material/Call";
import RefreshOutlinedIcon from "@mui/icons-material/RefreshOutlined";
import {
  matchIsValidTel,
  MuiTelInput,
  MuiTelInputCountry,
} from "mui-tel-input";
import discordLogo from "../../common/resources/images/discord-icon.svg";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { PhoneNumberCodeConfirmation } from "../../store2/types";
import { useLocation } from "react-router-dom";
import {
  getSignedMessagePromise,
  openNewTab,
  resolveCardanoNetwork,
} from "../../utils/utils";
import { SignedWeb3Request } from "../../types/voting-app-types";
import { ErrorMessage } from "../common/ErrorMessage/ErrorMessage";
import { CustomButton } from "../common/CustomButton/CustomButton";
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
  getVerificationStarted,
  getWalletIdentifier,
  setVerificationStarted,
  setWalletIsVerified,
} from "../../store/reducers/userCache";
import { useAppDispatch, useAppSelector } from "../../store/hooks";
import { VerificationStarted } from "../../store/reducers/userCache/userCache.types";
import { ToastType } from "../common/Toast/Toast.types";

// TODO: env.
const excludedCountries: MuiTelInputCountry[] | undefined = [];

const VerifyWalletModal = () => {
  const theme = useTheme();
  const [verifyCurrentPaths, setVerifyCurrentPaths] = useState<
    VerifyWalletFlow[]
  >([VerifyWalletFlow.INTRO]);
  const walletIdentifier = useAppSelector(getWalletIdentifier);
  const dispatch = useAppDispatch();

  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const [isOpen, setIsOpen] = useState<boolean>(false);
  const [defaultCountryCode] = useState<MuiTelInputCountry | undefined>("ES");
  const [phone, setPhone] = useState<string>("");
  const [codes, setCodes] = useState(Array(6).fill(""));
  const [phoneCodeIsBeenSending, setPhoneCodeIsBeenSending] =
    useState<boolean>(false);
  const [phoneCodeIsBeenConfirming, setPhoneCodeIsBeenConfirming] =
    useState<boolean>(false);
  const [phoneCodeIsSent, setPhoneCodeIsSent] = useState<boolean>(false);
  const [phoneCodeShowError, setPhoneCodeShowError] = useState<boolean>(false);
  const [checkImNotARobot, setCheckImNotARobot] = useState<boolean>(false);
  const [isPhoneInputDisabled] = useState<boolean>(false);
  const { signMessage } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });
  const userVerificationStarted = useAppSelector(getVerificationStarted);

  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

  const location = useLocation();

  const queryParams = new URLSearchParams(location.search);
  const action = queryParams.get("action");
  const discordSecret = queryParams.get("secret");

  inputRefs.current = [];

  useEffect(() => {
    const openVerifyWalletModal = (open: boolean = true) => {
      setIsOpen(open);
    };
    eventBus.subscribe("openVerifyWalletModal", openVerifyWalletModal);

    return () => {
      eventBus.unsubscribe("openVerifyWalletModal", openVerifyWalletModal);
    };
  }, []);

  const signMessagePromisified = useMemo(
    () => getSignedMessagePromise(signMessage),
    [signMessage],
  );

  const reset = (timout?: boolean) => {
    function clear() {
      setCheckImNotARobot(false);
      setPhoneCodeIsSent(false);
      setPhoneCodeShowError(false);
      setPhone("");
      setCodes(Array(6).fill(""));
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
      sendSmsCode(env.EVENT_ID, walletIdentifier, phone.trim().replace(" ", ""))
        .then((response: VerificationStarted) => {
          handleSetCurrentPath(VerifyWalletFlow.CONFIRM_CODE);
          dispatch(
            setVerificationStarted({
              walletIdentifier,
              ...response,
            }),
          );
          setPhoneCodeIsSent(true);
          setCheckImNotARobot(false);
          setPhoneCodeIsBeenSending(false);
        })
        .catch(() => {
          setPhoneCodeIsBeenSending(false);
        });
    }
  };

  const handleVerifyPhoneCode = () => {
    setPhoneCodeIsBeenConfirming(true);

    confirmPhoneNumberCode(
      env.EVENT_ID,
      walletIdentifier,
      phone.trim().replace(" ", ""),
      userVerificationStarted.requestId,
      codes.join(""),
    )
      .then((response: PhoneNumberCodeConfirmation) => {
        dispatch(setWalletIsVerified(response.verified));
        if (response.verified) {
          reset();
          setPhoneCodeIsBeenConfirming(false);
          eventBus.publish(
            EventName.ShowToast,
            "Phone number verified successfully",
          );
          setIsOpen(false);
        } else {
          setPhoneCodeShowError(true);
          setPhoneCodeIsBeenConfirming(false);
          eventBus.publish(
            EventName.ShowToast,
            "Phone number verified successfully",
            ToastType.Error,
          );
          handleSetCurrentPath(VerifyWalletFlow.DID_NOT_RECEIVE_CODE);
        }
      })
      .catch(() => {
        // onError('SMS code verification failed');
        setPhoneCodeShowError(true);
        setPhoneCodeIsBeenConfirming(false);
      });
  };

  const handleVerifyDiscord = async () => {
    if (action === "verification" && discordSecret?.includes("|")) {
      signMessagePromisified(discordSecret.trim())
        .then((signedMessaged: SignedWeb3Request) => {
          const parsedSecret = discordSecret.split("|")[1];
          verifyDiscord(
            env.EVENT_ID,
            walletIdentifier,
            parsedSecret,
            signedMessaged,
          )
            .then((response: { verified: boolean }) => {
              dispatch(setWalletIsVerified(response.verified));
              if (response.verified) {
                reset();
              } else {
              }
            })
            .catch((e) => console.error(e));
        })
        .catch((e) => console.error(e));
    }
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
          <CustomButton
            onClick={() => handleSetCurrentPath(VerifyWalletFlow.SELECT_METHOD)}
            sx={{ mb: 1 }}
            fullWidth
            colorVariant="primary"
          >
            Verify
          </CustomButton>
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
            onClick={() =>
              handleSetCurrentPath(VerifyWalletFlow.VERIFY_DISCORD)
            }
            sx={{
              borderRadius: "12px",
              border: "1px solid var(--neutral, #737380)",
              background: theme.palette.background.default,
              mt: "12px",
              py: "8px",
              height: "56px",
              cursor: "pointer",
              display: "flex",
              "&:hover": {
                backgroundColor: "action.hover",
              },
            }}
          >
            <ListItemAvatar>
              <img
                src={discordLogo}
                style={{
                  width: "24px",
                  height: "24px",
                  paddingTop: "2px",
                  filter: "brightness(0) invert(1)",
                }}
              />
            </ListItemAvatar>
            <Typography
              sx={{
                color: theme.palette.text.neutralLightest,
                textAlign: "center",
                fontSize: "16px",
                fontStyle: "normal",
                fontWeight: 500,
                lineHeight: "24px",
              }}
            >
              Verify with Discord
            </Typography>
          </ListItem>
          <ListItem
            onClick={() => handleSetCurrentPath(VerifyWalletFlow.VERIFY_SMS)}
            sx={{
              borderRadius: "12px",
              border: "1px solid var(--neutral, #737380)",
              background: theme.palette.background.default,
              mt: "12px",
              py: "8px",
              height: "56px",
              cursor: "pointer",
              display: "flex",
              "&:hover": {
                backgroundColor: "action.hover",
              },
            }}
          >
            <ListItemAvatar>
              <CallIcon
                style={{ width: "24px", height: "24px", color: "white" }}
              />
            </ListItemAvatar>
            <Typography
              sx={{
                color: theme.palette.text.neutralLightest,
                textAlign: "center",
                fontSize: "16px",
                fontStyle: "normal",
                fontWeight: 500,
                lineHeight: "24px",
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
            wordWrap: "break-word",
            color: theme.palette.text.neutralLightest,
            fontSize: "12px",
            fontStyle: "normal",
            fontWeight: 500,
            lineHeight: "20px",
            marginTop: "24px",
            marginLeft: "4px",
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
              background: "var(--neutralDark, #272727)",
              width: "100%",
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
              <CustomButton
                disabled={
                  !matchIsValidTel(phone) ||
                  !checkImNotARobot ||
                  phoneCodeIsBeenSending
                }
                onClick={() => handleSendCode()}
                colorVariant="primary"
                sx={{
                  width: "100%",
                }}
              >
                Send Code
              </CustomButton>
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
              style={{
                width: isMobile ? "43px" : "53px",
                height: isMobile ? "49px" : "58px",
                flexShrink: 0,
                borderRadius: "8px",
                border: "1px solid #6c6f89",
                background: "#fff",
                textAlign: "center",
                outline: "none",
                color: "#434656",
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
            <CustomButton
              sx={{
                background: "transparent !important",
                color: "#03021F",
                border: "1px solid #daeefb",
              }}
              onClick={() => handleVerifyPhoneCode()}
              fullWidth={true}
              colorVariant="primary"
              disabled={codes.includes("")}
            >
              Confirm
            </CustomButton>
          </Grid>
        </Grid>
      </>
    );
  };

  const renderDidNotReceiveCode = () => {
    const handleEnterNewNumber = () => {
      setPhone("");
      handleSetCurrentPath(VerifyWalletFlow.VERIFY_SMS);
    };
    const handleSendCodeAgain = async () => {
      handleSetCurrentPath(VerifyWalletFlow.CONFIRM_CODE);
      setPhoneCodeShowError(false);
      setCodes(Array(6).fill(""));
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
              borderRadius: "12px",
              border: "1px solid var(--neutral, #737380)",
              background: theme.palette.background.default,
              mt: "12px",
              py: "8px",
              height: "56px",
              cursor: "pointer",
              display: "flex",
              "&:hover": {
                backgroundColor: "action.hover",
              },
            }}
          >
            <ListItemAvatar>
              <RefreshOutlinedIcon
                sx={{ width: "24px", height: "24px", color: "white" }}
              />
            </ListItemAvatar>
            <Typography
              sx={{
                color: theme.palette.text.neutralLightest,
                textAlign: "center",
                fontSize: "16px",
                fontStyle: "normal",
                fontWeight: 500,
                lineHeight: "24px",
              }}
            >
              Send the Code Again
            </Typography>
          </ListItem>
          <ListItem
            onClick={() => handleEnterNewNumber()}
            sx={{
              borderRadius: "12px",
              border: "1px solid var(--neutral, #737380)",
              background: theme.palette.background.default,
              mt: "12px",
              py: "8px",
              height: "56px",
              cursor: "pointer",
              display: "flex",
              "&:hover": {
                backgroundColor: "action.hover",
              },
            }}
          >
            <ListItemAvatar>
              <CallIcon
                sx={{ width: "24px", height: "24px", color: "white" }}
              />
            </ListItemAvatar>
            <Typography
              sx={{
                color: theme.palette.text.neutralLightest,
                textAlign: "center",
                fontSize: "16px",
                fontStyle: "normal",
                fontWeight: 500,
                lineHeight: "24px",
              }}
            >
              Enter a New Number
            </Typography>
          </ListItem>
        </List>
      </>
    );
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
              color: "var(--orange, #EE9766)",
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
              color: "var(--orange, #EE9766)",
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
        <CustomButton
          colorVariant="primary"
          onClick={() => handleVerifyDiscord()}
          disabled={!discordSecret}
          sx={{
            width: "100%",
            marginTop: "24px",
          }}
        >
          Sign and Verify
        </CustomButton>
      </>
    );
  };

  const handleCloseModal = () => {
    setIsOpen(false);
    setVerifyCurrentPaths([VerifyWalletFlow.INTRO]);
  };

  const handleSetCurrentPath = (option: VerifyWalletFlow) => {
    const fileteredVerifyCurrentPaths = verifyCurrentPaths.filter(
      (p) => p !== option,
    );
    setVerifyCurrentPaths([option, ...fileteredVerifyCurrentPaths]);
  };

  const handleBack = () => {
    if (verifyCurrentPaths.length >= 2) {
      return setVerifyCurrentPaths((prev) => prev.slice(1));
    }
  };

  const handleClose = () => {
    reset();
    setIsOpen(false);
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
          title: "Verify Your Wallet",
          render: renderSelectOption(),
        };
      case VerifyWalletFlow.VERIFY_SMS:
        if (phoneCodeIsSent) {
          return {
            title: "Verify with SMS",
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
        title={content.title}
        onClose={() => handleClose()}
        disableBackdropClick={true}
        width={isMobile ? "auto" : "450px"}
        onBack={() => handleBack()}
        backButton={verifyCurrentPaths.length > 1}
      >
        {content.render}
      </Modal>
    </>
  );
};

export { VerifyWalletModal };

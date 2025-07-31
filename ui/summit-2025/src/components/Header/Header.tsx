import React, { useEffect, useMemo, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { AppBar, Box, Toolbar, Typography, IconButton } from "@mui/material";
import MenuOutlinedIcon from "@mui/icons-material/MenuOutlined";
import Logo from "../../assets/logo.svg";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";
import { eventBus, EventName } from "../../utils/EventBus";
import { ToastType } from "../common/Toast/Toast.types";
import { ConnectWalletModal } from "../ConnectWalletModal/ConnectWalletModal";
import { Toast } from "../common/Toast/Toast";
import { VerifyWalletModal } from "../VerifyWalletModal";
import {
  getSignedMessagePromise,
  resolveCardanoNetwork,
  signMessageWithWallet,
} from "../../utils/utils";
import { env } from "../../common/constants/env";
import { ConnectWalletButton } from "../ConnectWalletButton/ConnectWalletButton";
import { ROUTES } from "../../routes";
import { RightMenu } from "./RightMenu/RightMenu";
import theme from "../../common/styles/theme";
import { useAppDispatch, useAppSelector } from "../../store/hooks";
import {
  getConnectedWallet,
  resetUser,
  setIsLogin,
} from "../../store/reducers/userCache";
import { LoginModal } from "../LoginModal/LoginModal";
import {
  clearUserInSessionStorage,
  saveUserInSession,
} from "../../utils/session";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import {
  getSlotNumber,
  submitGetUserVotes,
} from "../../common/api/voteService";
import {
  buildCanonicalLoginJson,
  submitLogin,
} from "../../common/api/loginService";
import { resolveWalletType } from "../../common/api/utils";
import { clearVotes, setVotes } from "../../store/reducers/votesCache";
import { parseError } from "../../common/constants/errors";
import { CheckWalletModal } from "../CheckWalletModal/CheckWalletModal";

const Header = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const connectedWallet = useAppSelector(getConnectedWallet);
  const [showConnectWalletModal, setShowConnectWalletModal] =
    useState<boolean>(false);
  const [isLogging, setIsLogging] = useState<boolean>(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState<ToastType>(ToastType.Common);
  const [toastOpen, setToastOpen] = useState(false);
  const [menuIsOpen, setMenuIsOpen] = useState(false);
  const [isLoginModalOpen, setIsLoginModalOpen] = useState<boolean>(false);
  const [isCheckWalletModalOpen, setIsCheckWalletModalOpen] =
    useState<boolean>(false);
  const isPortrait = useIsPortrait();

  const { disconnect, signMessage } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  const signMessagePromisified = useMemo(
    () => getSignedMessagePromise(signMessage),
    [signMessage],
  );

  useEffect(() => {
    const openConnectWalletModal = () => {
      setShowConnectWalletModal(true);
    };
    eventBus.subscribe(
      EventName.OpenConnectWalletModal,
      openConnectWalletModal,
    );

    return () => {
      eventBus.unsubscribe(
        EventName.OpenConnectWalletModal,
        openConnectWalletModal,
      );
    };
  }, []);
  useEffect(() => {
    const openLoginModal = () => {
      setIsLoginModalOpen(true);
    };
    const closeVerifyWalletModal = () => {
      setIsLoginModalOpen(false);
    };
    eventBus.subscribe(EventName.OpenLoginModal, openLoginModal);
    eventBus.subscribe(EventName.CloseLoginModal, closeVerifyWalletModal);

    return () => {
      eventBus.unsubscribe(EventName.OpenLoginModal, openLoginModal);
      eventBus.unsubscribe(EventName.CloseLoginModal, closeVerifyWalletModal);
    };
  }, []);
  useEffect(() => {
    const showToastListener = (message: string, type?: ToastType) => {
      showToast(message, type || ToastType.Common);
    };
    eventBus.subscribe(EventName.ShowToast, showToastListener);

    return () => {
      eventBus.unsubscribe(EventName.ShowToast, showToastListener);
    };
  }, []);

  useEffect(() => {
    const openConnectWalletModal = () => {
      setShowConnectWalletModal(true);
    };
    const closeConnectWalletModal = () => {
      setShowConnectWalletModal(false);
    };
    eventBus.subscribe(
      EventName.OpenConnectWalletModal,
      openConnectWalletModal,
    );
    eventBus.subscribe(
      EventName.CloseConnectWalletModal,
      closeConnectWalletModal,
    );

    return () => {
      eventBus.unsubscribe(
        EventName.OpenConnectWalletModal,
        openConnectWalletModal,
      );
      eventBus.unsubscribe(
        EventName.CloseConnectWalletModal,
        closeConnectWalletModal,
      );
    };
  }, []);

  const showToast = (message: string, type?: ToastType) => {
    setToastMessage(message);
    setToastType(type || ToastType.Common);
    setToastOpen(true);
  };
  const handleToastClose = (
    _event?: Event | React.SyntheticEvent<any, Event>,
    reason?: string,
  ) => {
    if (reason === "clickaway") {
      return;
    }
    setToastOpen(false);
  };

  const handleConnectWalletModal = () => {
    setShowConnectWalletModal(true);
  };

  const handleOpenVerify = () => {};

  const onDisconnectWallet = () => {
    dispatch(resetUser());
    dispatch(clearVotes());
    disconnect();
    clearUserInSessionStorage();
  };

  const handleClickMenu = (option: string) => {
    if (option !== location.pathname) navigate(option);
  };

  const handleCloseLoginModal = async () => {
    setIsLogging(false);
    setIsLoginModalOpen(false);
  };

  const handleLogin = async () => {
    try {
      setIsLogging(true);
      // @ts-ignore
      const absoluteSlot = (await getSlotNumber())?.absoluteSlot;
      const canonicalLoginInput = buildCanonicalLoginJson({
        walletId: connectedWallet.address,
        walletType: resolveWalletType(connectedWallet.address),
        slotNumber: absoluteSlot.toString(),
      });

      const loginSignatureResult = await signMessageWithWallet(
        connectedWallet,
        canonicalLoginInput,
        signMessagePromisified,
      );

      if (!loginSignatureResult.success) {
        eventBus.publish(
          EventName.ShowToast,
          loginSignatureResult.error || "Error while signing",
          ToastType.Error,
        );
        return;
      }

      submitLogin(
        // @ts-ignore
        loginSignatureResult.result,
        resolveWalletType(connectedWallet.address),
      )
        .then((response) => {
          const newSession = {
            // @ts-ignore
            accessToken: response.accessToken,
            // @ts-ignore
            expiresAt: response.expiresAt,
          };
          saveUserInSession(newSession);
          dispatch(setIsLogin(true));
          eventBus.publish(EventName.ShowToast, "Login successfully");
          submitGetUserVotes(newSession?.accessToken)
            .then((uVotes) => {
              if (uVotes) {
                // @ts-ignore
                dispatch(setVotes(uVotes));
              }
              handleCloseLoginModal();
            })
            .catch((e) => {
              setIsLogging(false);
              eventBus.publish(
                EventName.ShowToast,
                parseError(e.message),
                ToastType.Error,
              );
            });
        })
        .catch((e) => {
          setIsLogging(false);
          eventBus.publish(
            EventName.ShowToast,
            parseError(e.message),
            ToastType.Error,
          );
        });
    } catch (e) {
      setIsLogging(false);
      // @ts-ignore
      eventBus.publish(EventName.ShowToast, e.message, ToastType.Error);
    }
  };

  return (
    <>
      <AppBar
        position="fixed"
        sx={{
          height: "96px",
          background: "transparent",
          boxShadow: "none",
        }}
      >
        <Toolbar
          sx={{
            padding: "0px !important",
          }}
        >
          <Box
            component="div"
            sx={{
              width: "100%",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
            }}
          >
            <Box
              component="div"
              sx={{
                background: theme.palette.background.default,
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                flexDirection: "row",
                width: "100%",
                maxWidth: "1440px",
                paddingY: "24px",
                paddingX: "16px",
              }}
            >
              <Box
                component="div"
                sx={{ p: 0, cursor: "pointer", display: "flex", alignItems: "center" }}
                onClick={() => navigate(ROUTES.LANDING)}
              >
                <img
                  src={Logo}
                  alt="Logo"
                  style={{ height: "50px", width: "188x" }}
                />
              </Box>

              {!isPortrait && (
                <Box
                  component="div"
                  sx={{
                    flexGrow: 1,
                    display: "flex",
                    justifyContent: "end",
                    alignItems: "center",
                  }}
                >
                  <Typography
                    sx={{
                      color: "inherit",
                      padding: " 16px 24px",
                      borderRadius: "12px",
                      cursor: "pointer",
                        textDecoration: "underline",
                        textDecorationColor: "transparent",
                        textUnderlineOffset: "3px",
                        transition: "text-decoration-color 300ms",
                      "&:hover": {
                          textDecorationColor: "#E8EDFF",
                      },
                    }}
                    onClick={() => handleClickMenu(ROUTES.CATEGORIES)}
                  >
                    Categories
                  </Typography>
                  <Typography
                      sx={{
                          color: "inherit",
                          padding: " 16px 24px",
                          borderRadius: "12px",
                          cursor: "pointer",
                          textDecoration: "underline",
                          textDecorationColor: "transparent",
                          textUnderlineOffset: "3px",
                          transition: "text-decoration-color 300ms",
                          "&:hover": {
                              textDecorationColor: "#E8EDFF",
                          },
                      }}
                    onClick={() => handleClickMenu(ROUTES.LEADERBOARD)}
                  >
                    Leaderboard
                  </Typography>
                  <Typography
                      sx={{
                          color: "inherit",
                          padding: " 16px 24px",
                          borderRadius: "12px",
                          cursor: "pointer",
                          textDecoration: "underline",
                          textDecorationColor: "transparent",
                          textUnderlineOffset: "3px",
                          transition: "text-decoration-color 300ms",
                          "&:hover": {
                              textDecorationColor: "#E8EDFF",
                          },
                      }}
                    onClick={() => handleClickMenu(ROUTES.USER_GUIDE)}
                  >
                    User Guide
                  </Typography>

                  <Box
                    component="div"
                    sx={{
                        flexGrow: 0,
                        display: "flex",
                        justifyContent: "end",
                        alignItems: "center",
                    }}
                  >
                      <ConnectWalletButton
                          label={isPortrait ? "" : "Connect Wallet"}
                          showAddress={!isPortrait}
                          onOpenConnectWalletModal={handleConnectWalletModal}
                          onOpenVerifyWalletModal={handleOpenVerify}
                          onDisconnectWallet={() => onDisconnectWallet()}
                      />
                  </Box>
                </Box>
              )}

              {isPortrait ? (
                <Box
                  component="div"
                  sx={{ display: "flex", alignItems: "center", gap: 2 }}
                >
                  <ConnectWalletButton
                    label={isPortrait ? "" : "Connect Wallet"}
                    showAddress={!isPortrait}
                    onOpenConnectWalletModal={handleConnectWalletModal}
                    onOpenVerifyWalletModal={handleOpenVerify}
                    onDisconnectWallet={() => onDisconnectWallet()}
                  />


                    <IconButton
                      onClick={() => setMenuIsOpen(true)}
                      sx={{
                        marginLeft: "auto",
                        padding: "10px",
                        borderRadius: "8px",
                        border: `1px solid ${theme.palette.text.primary}`,
                        color: theme.palette.text.primary,
                        "&:hover": {
                          color: theme.palette.background.default,
                          backgroundColor: theme.palette.text.primary,
                        },
                      }}
                    >
                      <MenuOutlinedIcon />
                    </IconButton>
                </Box>
              ) : null}
            </Box>
          </Box>
        </Toolbar>
      </AppBar>
      <Box
        component="div"
        sx={{
          height: "96px",
        }}
      />
      <RightMenu
        menuIsOpen={menuIsOpen}
        setMenuIsOpen={(isOpen: boolean) => setMenuIsOpen(isOpen)}
      />
      <ConnectWalletModal
        showPeerConnect={showConnectWalletModal}
        handleCloseConnectWalletModal={(open) =>
          setShowConnectWalletModal(open ? open : false)
        }
      />
      <LoginModal
        handleCloseModal={() => handleCloseLoginModal()}
        handleLogin={() => handleLogin()}
        isLogging={isLogging}
        isOpen={isLoginModalOpen}
      />
      <CheckWalletModal
        isOpen={isCheckWalletModalOpen}
        handleOpenModal={() => setIsCheckWalletModalOpen(true)}
        handleCloseModal={() => setIsCheckWalletModalOpen(false)}
      />
      <VerifyWalletModal />
      <Toast
        isOpen={toastOpen}
        type={toastType}
        message={toastMessage}
        onClose={handleToastClose}
      />
    </>
  );
};

export default Header;

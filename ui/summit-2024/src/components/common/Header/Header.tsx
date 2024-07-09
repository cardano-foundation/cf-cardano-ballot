import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { AppBar, Box, Toolbar, Typography, IconButton } from "@mui/material";
import MenuOutlinedIcon from "@mui/icons-material/MenuOutlined";
import Logo from "../../../assets/logo.svg";
import { useIsPortrait } from "../../../common/hooks/useIsPortrait";
import { eventBus, EventName } from "../../../utils/EventBus";
import { ToastType } from "../Toast/Toast.types";
import { ConnectWalletModal } from "../../ConnectWalletModal/ConnectWalletModal";
import { Toast } from "../Toast/Toast";
import { VerifyWalletModal } from "../../VerifyWalletModal";
import { resolveCardanoNetwork } from "../../../utils/utils";
import { env } from "../../../common/constants/env";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { ConnectWalletButton } from "../../ConnectWalletButton/ConnectWalletButton";
import { ROUTES } from "../../../routes";
import { RightMenu } from "./RightMenu/RightMenu";
import theme from "../../../common/styles/theme";
import { useAppDispatch } from "../../../store/hooks";
import { resetUser } from "../../../store/reducers/userCache";

const Header = () => {
  const dispatch = useAppDispatch();
  const navigate = useNavigate();
  const location = useLocation();
  const [showConnectWalletModal, setShowConnectWalletModal] =
    useState<boolean>(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState<ToastType>(ToastType.Common);
  const [toastOpen, setToastOpen] = useState(false);
  const [menuIsOpen, setMenuIsOpen] = useState(false);

  const isPortrait = useIsPortrait();

  const { disconnect } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

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

  const handleLogin = () => {};

  const onDisconnectWallet = () => {
    dispatch(resetUser());
    disconnect();
  };

  const handleClickMenu = (option: string) => {
    if (option !== location.pathname) navigate(option);
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
        <Toolbar>
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

                paddingTop: "10px",
                paddingBottom: "10px",
              }}
            >
              <Box
                component="div"
                sx={{ p: 0, cursor: "pointer" }}
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
                    justifyContent: "center",
                  }}
                >
                  <Typography
                    component="a"
                    href="#categories"
                    sx={{
                      color: "inherit",
                      px: "24px",
                      textDecoration: "none",
                      cursor: "pointer",
                    }}
                    onClick={() => handleClickMenu(ROUTES.CATEGORIES)}
                  >
                    Categories
                  </Typography>
                  <Typography
                    variant="body1"
                    component="a"
                    href="#leaderboard"
                    sx={{
                      color: "inherit",
                      textDecoration: "none",
                      px: "24px",
                    }}
                    onClick={() => handleClickMenu(ROUTES.LEADERBOARD)}
                  >
                    Leaderboard
                  </Typography>
                  <Typography
                    variant="body1"
                    component="a"
                    href="#user-guide"
                    sx={{
                      color: "inherit",
                      textDecoration: "none",
                      px: "24px",
                    }}
                    onClick={() => handleClickMenu(ROUTES.USER_GUIDE)}
                  >
                    User Guide
                  </Typography>
                </Box>
              )}

              <Box
                component="div"
                sx={{ display: "flex", alignItems: "center", gap: 1.5 }}
              >
                <ConnectWalletButton
                  label={isPortrait ? "" : "Connect Wallet"}
                  onOpenConnectWalletModal={handleConnectWalletModal}
                  onOpenVerifyWalletModal={handleOpenVerify}
                  onLogin={handleLogin}
                  onDisconnectWallet={onDisconnectWallet}
                />

                {isPortrait ? (
                  <IconButton
                    color="inherit"
                    onClick={() => setMenuIsOpen(true)}
                    sx={{
                      marginLeft: "auto",
                      padding: "10px",
                      borderRadius: "12px",
                      background:
                        "linear-gradient(258deg, #EE9766 0%, #40407D 187.58%, #0C7BC5 249.97%)",
                    }}
                  >
                    <MenuOutlinedIcon />
                  </IconButton>
                ) : null}
              </Box>
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

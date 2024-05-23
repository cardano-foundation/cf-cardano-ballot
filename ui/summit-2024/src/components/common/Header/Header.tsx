import React, { useEffect, useState } from "react";
import { AppBar, Box, Toolbar, Typography, IconButton } from "@mui/material";
import MenuOutlinedIcon from "@mui/icons-material/MenuOutlined";
import Logo from "../../../assets/logo.svg";
import { ConnectWalletButton } from "../ConnectWalletButton/ConnectWalletButton";
import { useIsPortrait } from "../../../common/hooks/useIsPortrait";
import { eventBus } from "../../../utils/EventBus";
import { ToastType } from "../Toast/Toast.types";
import { ConnectWalletModal } from "../../ConnectWalletModal/ConnectWalletModal";
import { Toast } from "../Toast/Toast";
import { VerifyWalletModal } from "../../VerifyWalletModal";

const Header = () => {
  const [showConnectWalletModal, setShowConnectWalletModal] =
    useState<boolean>(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState<ToastType>(ToastType.Common);
  const [toastOpen, setToastOpen] = useState(false);

  const isPortrait = useIsPortrait();
  useEffect(() => {
    const openConnectWalletModal = () => {
      setShowConnectWalletModal(true);
    };
    eventBus.subscribe("openConnectWalletModal", openConnectWalletModal);

    return () => {
      eventBus.unsubscribe("openConnectWalletModal", openConnectWalletModal);
    };
  }, []);

  useEffect(() => {
    const showToastListener = (message: string, type?: ToastType) => {
      showToast(message, type || ToastType.Common);
    };
    eventBus.subscribe("showToast", showToastListener);

    return () => {
      eventBus.unsubscribe("showToast", showToastListener);
    };
  }, []);

  useEffect(() => {
    const openConnectWalletModal = () => {
      setShowConnectWalletModal(true);
    };
    const closeConnectWalletModal = () => {
      setShowConnectWalletModal(false);
    };
    eventBus.subscribe("openConnectWalletModal", openConnectWalletModal);
    eventBus.subscribe("closeConnectWalletModal", closeConnectWalletModal);

    return () => {
      eventBus.unsubscribe("openConnectWalletModal", openConnectWalletModal);
      eventBus.unsubscribe("closeConnectWalletModal", closeConnectWalletModal);
    };
  }, []);

  const showToast = (message: string, type?: ToastType) => {
    console.log(`Toast will show: ${message}, Type: ${type}`);
    setToastMessage(message);
    setToastType(type || ToastType.Common);
    setToastOpen(true);
  };
  const handleToastClose = (
    event?: Event | React.SyntheticEvent<any, Event>,
    reason?: string,
  ) => {
    if (reason === "clickaway") {
      return;
    }
    setToastOpen(false);
  };

  const handleOpenMenu = () => {};
  const handleConnectWalletModal = () => {
    setShowConnectWalletModal(true);
  };

  const handleOpenVerify = () => {};

  const handleLogin = () => {};

  const onDisconnectWallet = () => {};

  return (
    <>
      <AppBar
        position="static"
        color="transparent"
        elevation={0}
        sx={{ width: "100%", overflow: "hidden" }}
      >
        <Toolbar
          sx={{
            justifyContent: "space-between",
            padding: "20px 16px",
          }}
        >
          <IconButton edge="start" color="inherit" sx={{ p: 0, mr: 2 }}>
            <img
              src={Logo}
              alt="Logo"
              style={{ height: "50px", width: "auto" }}
            />
          </IconButton>

          {!isPortrait && (
            <Box
              sx={{ flexGrow: 1, display: "flex", justifyContent: "center" }}
            >
              <Typography
                variant="body1"
                component="a"
                href="#categories"
                sx={{ color: "inherit", mx: 2, textDecoration: "none" }}
              >
                Categories
              </Typography>
              <Typography
                variant="body1"
                component="a"
                href="#leaderboard"
                sx={{ color: "inherit", mx: 2, textDecoration: "none" }}
              >
                Leaderboard
              </Typography>
              <Typography
                variant="body1"
                component="a"
                href="#user-guide"
                sx={{ color: "inherit", textDecoration: "none" }}
              >
                User Guide
              </Typography>
            </Box>
          )}

          <Box sx={{ display: "flex", alignItems: "center", gap: 1.5 }}>
            <ConnectWalletButton
              onOpenConnectWalletModal={handleConnectWalletModal}
              onOpenVerifyWalletModal={handleOpenVerify}
              onLogin={handleLogin}
              onDisconnectWallet={onDisconnectWallet}
            />
            {isPortrait ? (
              <IconButton
                color="inherit"
                onClick={() => {}}
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
        </Toolbar>
      </AppBar>
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

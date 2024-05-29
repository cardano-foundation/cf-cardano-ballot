import React, { useEffect, useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import { AppBar, Box, Toolbar, Typography, IconButton } from "@mui/material";
import MenuOutlinedIcon from "@mui/icons-material/MenuOutlined";
import Logo from "../../../assets/logo.svg";
import { useIsPortrait } from "../../../common/hooks/useIsPortrait";
import { eventBus } from "../../../utils/EventBus";
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

const Header = () => {
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
    eventBus.subscribe("openConnectWalletModal", openConnectWalletModal);

    return () => {
      eventBus.unsubscribe("openConnectWalletModal", openConnectWalletModal);
    };
  }, []);

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

  const handleConnectWalletModal = () => {
    setShowConnectWalletModal(true);
  };

  const handleOpenVerify = () => {};

  const handleLogin = () => {};

  const onDisconnectWallet = () => {
    console.log("onDisconnectWallet");
    disconnect();
  };

  const handleClickMenu = (option: string) => {
    if (option !== location.pathname) navigate(option);
  };

  return (
    <>
      <AppBar
        position="static"
        sx={{
          width: "100%",
          maxWidth: 1440,
          my: "15px",
          mx: "auto",
          background: "transparent",
          boxShadow: "none",
        }}
      >
        <Toolbar
          sx={{
            justifyContent: "space-between",
            padding: "20px 16px",
          }}
        >
          <Box
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
              sx={{ flexGrow: 1, display: "flex", justifyContent: "center" }}
            >
              <Typography
                variant="body1"
                component="span"
                href="#categories"
                sx={{
                  color: "inherit",
                  mx: 2,
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
                sx={{ color: "inherit", mx: 2, textDecoration: "none" }}
                onClick={() => handleClickMenu(ROUTES.LEADERBOARD)}
              >
                Leaderboard
              </Typography>
              <Typography
                variant="body1"
                component="a"
                href="#user-guide"
                sx={{ color: "inherit", textDecoration: "none" }}
                onClick={() => handleClickMenu(ROUTES.USER_GUIDE)}
              >
                User Guide
              </Typography>
            </Box>
          )}

          <Box sx={{ display: "flex", alignItems: "center", gap: 1.5 }}>
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
        </Toolbar>
      </AppBar>
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

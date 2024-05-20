import React, { useEffect, useState } from "react";
import {
  AppBar,
  Box,
  IconButton,
  Toolbar,
  Typography,
  useMediaQuery,
} from "@mui/material";
import Logo from "../../../assets/logo.svg";
import { ConnectWalletButton } from "../ConnectWalletButton/ConnectWalletButton";
import { eventBus } from "../../../utils/EventBus";
import theme from "../../../common/styles/theme";
import MenuOutlinedIcon from "@mui/icons-material/MenuOutlined";
import { ConnectWallet } from "../../ConnectWallet/ConnectWallet";
import { ToastType } from "../Toast/Toast.types";
import { Toast } from "../Toast/Toast";

const Header: React.FC = () => {
  const [showConnectWalletModal, setShowConnectWalletModal] =
    useState<boolean>(false);
  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState<ToastType>("common");
  const [toastOpen, setToastOpen] = useState(false);

  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

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
      showToast(message, type || "common");
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
    setToastType(type || "common");
    setToastMessage(message);
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
      <AppBar position="static" color="transparent" elevation={0}>
        <Toolbar
          sx={{
            justifyContent: "space-between",
            alignItems: "center",
            pt: 2.5,
          }}
        >
          <div style={{ paddingTop: "5px" }}>
            <img
              src={Logo}
              alt="Cardano Summit Logo"
              style={{ marginTop: "5px" }}
            />
          </div>

          {!isMobile ? (
            <Box
              sx={{
                display: "flex",
                justifyContent: "center",
                alignItems: "center",
                marginTop: "5px",
              }}
            >
              <Typography
                variant="body1"
                component="a"
                href="#categories"
                sx={{
                  color: "inherit",
                  marginRight: "60px",
                  textDecoration: "none",
                }}
              >
                Categories
              </Typography>
              <Typography
                variant="body1"
                component="a"
                href="#leaderboard"
                sx={{
                  color: "inherit",
                  marginRight: "60px",
                  textDecoration: "none",
                }}
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
          ) : null}

          <Box sx={{ display: "flex", alignItems: "center", gap: 1.5 }}>
            <ConnectWalletButton
              onOpenConnectWalletModal={handleConnectWalletModal}
              onOpenVerifyWalletModal={handleOpenVerify}
              onLogin={handleLogin}
              onDisconnectWallet={onDisconnectWallet}
            />

            {isMobile && (
              <IconButton
                color="inherit"
                onClick={handleOpenMenu}
                sx={{
                  marginLeft: isMobile ? 0 : "auto",
                  padding: "12px",
                  width: 44,
                  height: 44,
                  display: "flex",
                  justifyContent: "center",
                  alignItems: "center",
                  borderRadius: "12px",
                  background:
                    "linear-gradient(258deg, #EE9766 0%, #40407D 187.58%, #0C7BC5 249.97%)",
                  "&:hover": {
                    background:
                      "linear-gradient(258deg, #EE9766, #EE9766, #40407D, #0C7BC5)",
                  },
                }}
              >
                <MenuOutlinedIcon
                  sx={{
                    fontSize: "1.5rem",
                    color: "background.default",
                    "&:hover": {
                      color: "text.primary",
                    },
                  }}
                />
              </IconButton>
            )}
          </Box>
        </Toolbar>
      </AppBar>
      <ConnectWallet
        showPeerConnect={showConnectWalletModal}
        handleCloseConnectWalletModal={(open) =>
          setShowConnectWalletModal(open ? open : false)
        }
      />
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

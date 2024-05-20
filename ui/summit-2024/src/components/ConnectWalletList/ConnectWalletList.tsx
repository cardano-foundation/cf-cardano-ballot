import React, { useEffect, useState } from "react";
import {
  Avatar,
  Box,
  Button,
  Divider,
  IconButton,
  List,
  ListItem,
  ListItemAvatar,
  Typography,
} from "@mui/material";
import OpenInNewOutlinedIcon from "@mui/icons-material/OpenInNewOutlined";
import QrCodeOutlinedIcon from "@mui/icons-material/QrCodeOutlined";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import PhonelinkOutlinedIcon from "@mui/icons-material/PhonelinkOutlined";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import {
  copyToClipboard,
  resolveCardanoNetwork,
  walletIcon,
} from "../../utils/utils";
import IDWLogo from "../../assets/idw.png";
import { env } from "../../common/constants/env";
import { ConnectWalletFlow, IWalletInfo } from "./ConnectWalletList.types";
import QRCode from "react-qr-code";
import { clearUserInSessionStorage } from "../../utils/session";
import { removeFromLocalStorage } from "../../utils/storage";
import {
  setConnectedPeerWallet,
  setUserVotes,
  setVoteReceipt,
  setWalletIsVerified,
} from "../../store/userSlice";
import { VoteReceipt } from "../../types/voting-app-types";
import { disconnect } from "process";
import { eventBus } from "../../utils/EventBus";
import { ToastType } from "../common/Toast/Toast.types";

type ConnectWalletModalProps = {
  description?: string;
  currentPath: ConnectWalletFlow;
  setCurrentPath: (currentPath: ConnectWalletFlow) => void;
  onConnectWallet: () => void;
  onConnectError: (code: Error) => void;
  onOpenPeerConnect: () => void;
};

const SUPPORTED_WALLETS = env.SUPPORTED_WALLETS;

const ConnectWalletList = (props: ConnectWalletModalProps) => {
  const {
    description,
    onConnectWallet,
    onConnectError,
    currentPath,
    setCurrentPath,
  } = props;
  const [peerConnectWalletInfo, setPeerConnectWalletInfo] = useState<
    IWalletInfo | undefined
  >(undefined);

  const [peerConnectOption, setPeerConnectOption] =
    useState<ConnectWalletFlow>(currentPath);

  const [toastMessage, setToastMessage] = useState("");
  const [toastType, setToastType] = useState<ToastType>("common");
  const [toastOpen, setToastOpen] = useState(false);
  const [onPeerConnectAccept, setOnPeerConnectAccept] = useState(() => () => {
    /*TODO */
  });
  const [onPeerConnectReject, setOnPeerConnectReject] = useState(() => () => {
    /*TODO */
  });

  const {
    installedExtensions,
    connect,
    dAppConnect,
    meerkatAddress,
    initDappConnect,
  } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  const availableWallets = installedExtensions.filter((installedWallet) =>
    SUPPORTED_WALLETS.includes(installedWallet),
  );

  useEffect(() => {
    if (dAppConnect.current === null) {
      const verifyConnection = (
        walletInfo: IWalletInfo,
        callback: (granted: boolean, autoconnect: boolean) => void,
      ) => {
        setPeerConnectWalletInfo(walletInfo);
        setCurrentPath(ConnectWalletFlow.ACCEPT_CONNECTION);

        if (walletInfo.requestAutoconnect) {
          //setModalMessage(`Do you want to automatically connect to wallet ${walletInfo.name} (${walletInfo.address})?`);
          setOnPeerConnectAccept(() => () => callback(true, true));
          setOnPeerConnectReject(() => () => callback(false, false));
        } else {
          // setModalMessage(`Do you want to connect to wallet ${walletInfo.name} (${walletInfo.address})?`);
          setOnPeerConnectAccept(() => () => callback(true, false));
          setOnPeerConnectReject(() => () => callback(false, false));
        }
      };

      const onApiInject = (name: string, address: string): void => {
        connect(
          name,
          () => {
            eventBus.publish("closeConnectWalletModal");
            eventBus.publish("showToast", "Wallet connected successfully");
          },
          () => {
            eventBus.publish(
              "showToast",
              "Unable to connect wallet. Please try again",
              "error",
            );
          },
        ).catch((e) => console.error(e));
      };

      const onApiEject = (name: string, address: string): void => {
        setPeerConnectWalletInfo(undefined);
        eventBus.publish("showToast", "Wallet disconnected successfully");
      };

      const onP2PConnect = (
        address: string,
        walletInfo?: IWalletInfo,
      ): void => {
        // TODO
      };

      initDappConnect(
        "Cardano Summit 2023",
        env.FRONTEND_URL,
        verifyConnection,
        onApiInject,
        onApiEject,
        [],
        onP2PConnect,
      );
    }
  }, []);

  useEffect(() => {
    setPeerConnectOption(currentPath);
  }, [currentPath]);

  const showToast = (message: string, type?: ToastType) => {
    setToastType(type || "common");
    setToastMessage(message);
    setToastOpen(true);
  };
  const handleShowConnectIdentityWallet = () => {
    setPeerConnectOption(ConnectWalletFlow.CONNECT_IDENTITY_WALLET);
    setCurrentPath(ConnectWalletFlow.CONNECT_IDENTITY_WALLET);
  };

  const handleShowConnectP2PWallet = () => {
    setPeerConnectOption(ConnectWalletFlow.CONNECT_CIP45_WALLET);
    setCurrentPath(ConnectWalletFlow.CONNECT_CIP45_WALLET);
  };

  const onDisconnectWallet = () => {
    disconnect();
    clearUserInSessionStorage();
    setPeerConnectWalletInfo(undefined);
    removeFromLocalStorage("cardano-peer-autoconnect-id");
    removeFromLocalStorage("cardano-wallet-discovery-address");
  };

  const handleAccept = () => {
    if (peerConnectWalletInfo) {
      onPeerConnectAccept();
    }
  };

  const handleCopyToClipboard = async () => {
    await copyToClipboard(meerkatAddress);
    setCurrentPath(ConnectWalletFlow.ACCEPT_CONNECTION);
  };

  const renderContent = () => {
    switch (peerConnectOption) {
      case ConnectWalletFlow.SELECT_WALLET:
        return renderSelectWallet();
      case ConnectWalletFlow.CONNECT_IDENTITY_WALLET:
        return renderCIP45ConnectWallet();
      case ConnectWalletFlow.CONNECT_CIP45_WALLET:
        return renderCIP45ConnectWallet();
      case ConnectWalletFlow.ACCEPT_CONNECTION:
        return renderAcceptConnection();
    }
  };

  const renderCIP45ConnectWallet = () => {
    return (
      <>
        <Box
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
          sx={{ mt: 4, mb: 4 }}
        >
          <Typography
            variant="body1"
            sx={{
              textAlign: "center",
              fontSize: "16px",
            }}
          >
            To connect a mobile wallet, scan the below QR code from the wallet.
            If scanning isn’t an option, you can also copy the Peer ID.{" "}
            <span
              style={{
                color: "var(--orange, #EE9766)",
                textDecorationLine: "underline",
                cursor: "pointer",
              }}
            >
              Learn more about CIP-45
            </span>
          </Typography>
          <Box
            sx={{
              backgroundColor: "white",
              display: "flex",
              justifyContent: "center",
              marginTop: "40px",
              marginBottom: "16px",
              padding: "5px",
            }}
          >
            <QRCode
              size={256}
              style={{ height: "auto", width: "200px" }}
              value={meerkatAddress}
              viewBox={"0 0 256 256"}
            />
          </Box>
          <Box
            sx={{
              display: "flex",
              alignItems: "center",
              color: "text.primary",
              fontSize: "16px",
              fontWeight: "medium",
              textTransform: "none",
              padding: "8px 16px",
              cursor: "pointer",
              borderRadius: "4px",
              "&:hover": {
                opacity: 0.9,
              },
            }}
            onClick={() => handleCopyToClipboard()}
          >
            <ContentCopyIcon sx={{ marginRight: "8px", width: "20px" }} />
            Copy Peer ID
          </Box>
        </Box>
      </>
    );
  };
  const renderAcceptConnection = () => {
    return (
      <>
        <Box
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
        >
          {peerConnectWalletInfo?.icon ? (
            <img
              src={peerConnectWalletInfo?.icon}
              alt="Wallet"
              style={{ width: "64px", marginTop: "44px" }}
            />
          ) : (
            <PhonelinkOutlinedIcon sx={{ width: 54, height: 54 }} />
          )}
          <Typography
            variant="body1"
            align="left"
            sx={{
              textAlign: "center",
              color: "text.neutralLightest",
              fontSize: "18px",
              fontStyle: "normal",
              fontWeight: "500",
              lineHeight: "22px",
              marginTop: "24px",
              marginBottom: "44px",
            }}
          >
            <span
              style={{
                textTransform: "capitalize",
                textAlign: "center",
                fontSize: "16px",
                fontStyle: "normal",
                fontWeight: 800,
                lineHeight: "24px",
              }}
            >
              {peerConnectWalletInfo?.name}{" "}
            </span>
            wallet is trying to connect
          </Typography>
          <Button
            onClick={() => handleAccept()}
            className="vote-nominee-button"
            sx={{
              textTransform: "none",
              background:
                "linear-gradient(258deg, #EE9766 0%, #40407D 187.58%, #0C7BC5 249.97%)",
              "&:hover": {
                background:
                  "linear-gradient(258deg, #EE9766, #EE9766, #40407D, #0C7BC5)",
              },
              width: "394px",
              height: "auto",
              color: "background.neutralDarkest",
              fontSize: "16px",
              fontStyle: "normal",
              fontWeight: 500,
              lineHeight: "24px",
            }}
            fullWidth
          >
            Accept connection
          </Button>
          <Button
            onClick={() => {}}
            className="vote-nominee-button"
            style={{
              marginTop: "12px",
              textTransform: "none",
              width: "394px",
              height: "auto",
              color: "#EE9766",
              fontSize: "16px",
              fontStyle: "normal",
              fontWeight: 500,
              lineHeight: "24px",
              border: "1px solid var(--orange, #EE9766)",
            }}
          >
            Deny
          </Button>
        </Box>
      </>
    );
  };

  const renderSelectWallet = () => {
    return (
      <>
        <List>
          <ListItem
            sx={{
              display: "flex",
              padding: "8px 20px 8px 12px",
              alignItems: "center",
              gap: "10px",
              borderRadius: "12px",
              border: "1px solid var(--orange, #EE9766)",
              background: "var(--neutralDarkest, #121212)",
              mt: 2,
              justifyContent: "space-between",

              cursor: "pointer",
              "&:hover": {
                bgcolor: "action.hover",
                opacity: 0.8,
              },
            }}
            onClick={() => handleShowConnectIdentityWallet()}
          >
            <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
              <ListItemAvatar>
                <Avatar src={IDWLogo} sx={{ width: 24, height: 24 }} />
              </ListItemAvatar>
              <Typography
                sx={{
                  color: "#EE9766",
                  fontSize: "16px",
                  fontWeight: 500,
                  lineHeight: "24px",
                }}
              >
                Connect Identity Wallet
              </Typography>
            </Box>
            <IconButton edge="end" size="small" sx={{ ml: "auto" }}>
              <QrCodeOutlinedIcon
                sx={{
                  width: "20px",
                  height: "20px",
                  flexShrink: 0,
                  ml: "auto",
                  color: "#EE9766",
                }}
              />
            </IconButton>
          </ListItem>
          <Divider sx={{ my: 2, color: "text.neutralLight" }} component="li">
            <Typography sx={{ color: "text.neutralLight" }}>or</Typography>
          </Divider>
          {availableWallets.length ? (
            availableWallets.map((walletName, index) => (
              <ListItem
                key={index}
                sx={{
                  mt: 2,
                  display: "flex",
                  justifyContent: "space-between",
                  alignItems: "center",
                  padding: "8px 20px 8px 12px",
                  borderRadius: "12px",
                  border: "1px solid var(--neutral, #737380)",
                  background: "var(--neutralDarkest, #121212)",
                  cursor: "pointer",
                  "&:hover": {
                    bgcolor: "action.hover",
                    opacity: 0.8,
                  },
                }}
                onClick={() =>
                  connect(walletName, onConnectWallet, onConnectError)
                }
              >
                <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
                  <ListItemAvatar>
                    <Avatar
                      src={walletIcon(walletName)}
                      sx={{ width: 24, height: 24 }}
                    />
                  </ListItemAvatar>
                  <Typography
                    sx={{
                      color: "text.primary",
                      fontSize: "16px",
                      fontWeight: 500,
                      lineHeight: "24px",
                    }}
                  >
                    Connect{" "}
                    <span style={{ textTransform: "capitalize" }}>
                      {walletName === "typhoncip30" ? "Typhon" : walletName}
                    </span>{" "}
                    Wallet
                  </Typography>
                </Box>
                <IconButton edge="end" size="small" sx={{ ml: "auto" }}>
                  <OpenInNewOutlinedIcon
                    sx={{
                      width: "20px",
                      height: "20px",
                      flexShrink: 0,
                      color: "text.neutralLight",
                      ml: "auto",
                    }}
                  />
                </IconButton>
              </ListItem>
            ))
          ) : (
            <Typography
              sx={{
                color: "text.secondary",
                fontSize: "16px",
                fontWeight: 600,
                lineHeight: "22px",
                p: 2,
              }}
            >
              No extension wallets installed
            </Typography>
          )}
          <ListItem
            sx={{
              mt: 2,
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              padding: "8px 20px 8px 12px",
              borderRadius: "12px",
              border: "1px solid var(--neutral, #737380)",
              background: "var(--neutralDarkest, #121212)",
              cursor: "pointer",
              "&:hover": {
                bgcolor: "action.hover",
                opacity: 0.8,
              },
            }}
            onClick={() => handleShowConnectP2PWallet()}
          >
            <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
              <ListItemAvatar>
                <PhonelinkOutlinedIcon sx={{ width: 24, height: 24 }} />
              </ListItemAvatar>
              <Typography
                sx={{
                  fontSize: "16px",
                  fontWeight: 500,
                  lineHeight: "24px",
                }}
              >
                Connect P2P Wallet
              </Typography>
            </Box>
            <IconButton edge="end" size="small" sx={{ ml: "auto" }}>
              <QrCodeOutlinedIcon
                sx={{
                  width: "20px",
                  height: "20px",
                  flexShrink: 0,
                  ml: "auto",
                }}
              />
            </IconButton>
          </ListItem>
        </List>
      </>
    );
  };

  return (
    <>
      {description ? (
        <Typography
          gutterBottom
          sx={{
            color: "text.neutralLightest",
            fontSize: "16px",
            fontWeight: 500,
            lineHeight: "24px",
            wordWrap: "break-word",
            textAlign: "center",
            marginTop: "16px",
          }}
        >
          {description}
        </Typography>
      ) : null}

      {renderContent()}
    </>
  );
};

export default ConnectWalletList;

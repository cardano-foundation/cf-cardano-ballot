import { useEffect, useState } from "react";
import {
  Avatar,
  Box,
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
import { checkIsMobile } from "@cardano-foundation/cardano-connect-with-wallet-core";
import {
  copyToClipboard,
  resolveCardanoNetwork,
  walletIcon,
} from "../../utils/utils";
import VeridianLogo from "../../assets/veridian.svg";
import { env } from "../../common/constants/env";
import { ConnectWalletFlow, NetworkType } from "./ConnectWalletList.types";
import QRCode from "react-qr-code";
import { eventBus, EventName } from "../../utils/EventBus";
import theme from "../../common/styles/theme";
import { CustomButton } from "../common/CustomButton/CustomButton";

type ConnectWalletListProps = {
  description?: string;
  meerkatAddress: string;
  peerConnectWalletInfo: any;
  currentPath: ConnectWalletFlow;
  setCurrentPath: (currentPath: ConnectWalletFlow) => void;
  closeModal: () => void;
  onConnectError: (code: Error) => void;
  handleOnPeerConnectAccept: () => void;
  onOpenPeerConnect: () => void;
  connectExtensionWallet: (walletName: string) => void;
};

const SUPPORTED_WALLETS = env.SUPPORTED_WALLETS;

const ConnectWalletList = (props: ConnectWalletListProps) => {
  const {
    description,
    currentPath,
    setCurrentPath,
    handleOnPeerConnectAccept,
    closeModal,
    connectExtensionWallet,
  } = props;

  const [peerConnectOption, setPeerConnectOption] =
    useState<ConnectWalletFlow>(currentPath);

  const network = resolveCardanoNetwork(env.TARGET_NETWORK);

  const { installedExtensions } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  const availableWallets = installedExtensions.filter((installedWallet) =>
    SUPPORTED_WALLETS.includes(installedWallet)
  );

  useEffect(() => {
    setPeerConnectOption(currentPath);
  }, [currentPath]);

  const handleShowConnectIdentityWallet = () => {
    setPeerConnectOption(ConnectWalletFlow.CONNECT_IDENTITY_WALLET);
    setCurrentPath(ConnectWalletFlow.CONNECT_IDENTITY_WALLET);
  };

  const handleShowSetUpIdentityWallet = () => {
    setPeerConnectOption(ConnectWalletFlow.SETUP_IDENTITY_WALLET);
    setCurrentPath(ConnectWalletFlow.SETUP_IDENTITY_WALLET);
  };

  const handleShowSetUpConnectUrl = () => {
    setPeerConnectOption(ConnectWalletFlow.SETUP_CONNECT_URL);
    setCurrentPath(ConnectWalletFlow.SETUP_CONNECT_URL);
  };

  const handleConnectWallet = () => {
    setPeerConnectOption(ConnectWalletFlow.CONNECT_CIP45_WALLET);
    setCurrentPath(ConnectWalletFlow.CONNECT_CIP45_WALLET);
  };

  const handleAccept = () => {
    handleOnPeerConnectAccept();
  };

  const handleCopyToClipboard = async () => {
    if (!props.meerkatAddress) return;
    await copyToClipboard(props.meerkatAddress);
    eventBus.publish(EventName.ShowToast, "Copied to clipboard.");
  };

  const handleCopyBootUrl = async () => {
    await copyToClipboard("booturl");
    eventBus.publish(EventName.ShowToast, "Boot URL copied to clipboard.");
  };

  const renderContent = () => {
    switch (peerConnectOption) {
      case ConnectWalletFlow.SELECT_WALLET:
        return renderSelectWallet();
      case ConnectWalletFlow.CONNECT_IDENTITY_WALLET:
        return renderCIP45ConnectWallet();
      case ConnectWalletFlow.SETUP_IDENTITY_WALLET:
        return renderSetupIdentityWallet();
      case ConnectWalletFlow.SETUP_CONNECT_URL:
        return renderSetupConnectUrl();
      case ConnectWalletFlow.CONNECT_CIP45_WALLET:
        return renderCIP45ConnectWallet();
      case ConnectWalletFlow.ACCEPT_CONNECTION:
        return renderAcceptConnection();
    }
  };

  const renderSetupIdentityWallet = () => {
    return (
      <>
        <Box
          component="div"
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
              marginBottom: "40px",
            }}
          >
            Scan the Boot URL QR code or copy the link to install and prepare
            your wallet.
          </Typography>
          <Box
            component="div"
            sx={{
              backgroundColor: "white",
              display: "flex",
              justifyContent: "center",
              marginBottom: "16px",
              padding: "5px",
            }}
          >
            <QRCode
              size={256}
              style={{ height: "auto", width: "200px" }}
              value={"https://keria-boot.pro.cf-summit-2025.eu-west-1.backend.voting.summit.cardano.org/"}
              viewBox={"0 0 256 256"}
            />
          </Box>
          <Box
            component="div"
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
            onClick={() => handleCopyBootUrl()}
          >
            <ContentCopyIcon sx={{ marginRight: "8px", width: "20px" }} />
            Copy Boot URL
          </Box>
          <CustomButton
            onClick={() => handleShowSetUpConnectUrl()}
            colorVariant="primary"
            fullWidth={true}
            sx={{
              marginTop: "24px",
            }}
          >
            Next
          </CustomButton>
        </Box>
      </>
    );
  };

  const renderSetupConnectUrl = () => {
    return (
      <>
        <Box
          component="div"
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
              marginBottom: "40px",
            }}
          >
            Scan the Connect URL QR code or copy the link.
          </Typography>
          <Box
            component="div"
            sx={{
              backgroundColor: "white",
              display: "flex",
              justifyContent: "center",
              marginBottom: "16px",
              padding: "5px",
            }}
          >
            <QRCode
              size={256}
              style={{ height: "auto", width: "200px" }}
              value={"https://keria.pro.cf-summit-2025.eu-west-1.backend.voting.summit.cardano.org"}
              viewBox={"0 0 256 256"}
            />
          </Box>
          <Box
            component="div"
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
            Copy Connect URL
          </Box>
          <CustomButton
            onClick={() => handleConnectWallet()}
            colorVariant="primary"
            fullWidth={true}
            sx={{
              marginTop: "24px",
            }}
          >
            Connect Wallet
          </CustomButton>
        </Box>
      </>
    );
  };

  const renderCIP45ConnectWallet = () => {
    return (
      <>
        <Box
          component="div"
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
              onClick={() => {
                window.open(
                  "https://developers.cardano.org/docs/governance/cardano-improvement-proposals/cip-0045/",
                  "_blank"
                );
              }}
            >
              Learn more about CIP-45
            </span>
          </Typography>
          <Box
            component="div"
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
              value={props.meerkatAddress || ""}
              viewBox={"0 0 256 256"}
            />
          </Box>
          <Box
            component="div"
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
          component="div"
          display="flex"
          flexDirection="column"
          alignItems="center"
          justifyContent="center"
        >
          {props.peerConnectWalletInfo?.icon ? (
            <img
              src={props.peerConnectWalletInfo?.icon || ""}
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
              color: theme.palette.text.neutralLightest,
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
              {props.peerConnectWalletInfo?.name}{" "}
            </span>
            wallet is trying to connect
          </Typography>
          <CustomButton
            onClick={handleAccept}
            colorVariant="primary"
            fullWidth={true}
            sx={{
              marginBottom: "12px",
            }}
          >
            Accept
          </CustomButton>
          <CustomButton
            onClick={() => {}}
            colorVariant="secondary"
            fullWidth={true}
            sx={{
              marginBottom: "12px",
            }}
          >
            Deny
          </CustomButton>
        </Box>
      </>
    );
  };
  const renderSelectWallet = () => {
    return (
      <>
        <List
          sx={{
            maxHeight: "80vh",
            overflowY: "auto",
          }}
        >
          <ListItem
            sx={{
              display: "flex",
              padding: "12px",
              alignItems: "center",
              gap: "10px",
              borderRadius: "8px",
              border: `1px solid ${theme.palette.secondary.main}`,
              mt: 2,
              justifyContent: "space-between",
              cursor: "pointer",
              color: theme.palette.secondary.main,
              transition: "color 0.3s, border 0.3s",
              "&:hover": {
                color: theme.palette.text.primary,
                border: `1px solid ${theme.palette.text.primary}`,
              },
            }}
            onClick={() => handleShowSetUpIdentityWallet()}
          >
            <Box
              component="div"
              sx={{ display: "flex", alignItems: "center", gap: 2 }}
            >
              <ListItemAvatar>
                <Avatar src={VeridianLogo} sx={{ width: 20, height: 20 }} />
              </ListItemAvatar>
              <Typography
                sx={{
                  color: "inherit",
                  fontSize: "16px",
                  fontWeight: 600,
                  lineHeight: "20px",
                }}
              >
                Set up Veridian Wallet
              </Typography>
            </Box>
            <IconButton
              edge="end"
              size="small"
              sx={{ ml: "auto", padding: 0, color: "inherit" }}
            >
              <QrCodeOutlinedIcon
                sx={{
                  width: "16px",
                  height: "16px",
                  flexShrink: 0,
                  ml: "auto",
                  color: "inherit",
                }}
              />
            </IconButton>
          </ListItem>
          <ListItem
            sx={{
              display: "flex",
              padding: "12px",
              alignItems: "center",
              gap: "10px",
              borderRadius: "8px",
              border: `1px solid ${theme.palette.secondary.main}`,
              mt: 2,
              justifyContent: "space-between",
              cursor: "pointer",
              color: theme.palette.secondary.main,
              transition: "color 0.3s, border 0.3s",
              "&:hover": {
                color: theme.palette.text.primary,
                border: `1px solid ${theme.palette.text.primary}`,
              },
            }}
            onClick={() => handleShowConnectIdentityWallet()}
          >
            <Box
              component="div"
              sx={{ display: "flex", alignItems: "center", gap: 2 }}
            >
              <ListItemAvatar>
                <Avatar src={VeridianLogo} sx={{ width: 20, height: 20 }} />
              </ListItemAvatar>
              <Typography
                sx={{
                  color: "inherit",
                  fontSize: "16px",
                  fontWeight: 600,
                  lineHeight: "20px",
                }}
              >
                Connect Veridian Wallet
              </Typography>
            </Box>
            <IconButton
              edge="end"
              size="small"
              sx={{ ml: "auto", padding: 0, color: "inherit" }}
            >
              <QrCodeOutlinedIcon
                sx={{
                  width: "16px",
                  height: "16px",
                  flexShrink: 0,
                  ml: "auto",
                  color: "inherit",
                }}
              />
            </IconButton>
          </ListItem>
          <Divider
            sx={{
              my: "24px",
              "&::before, &::after": {
                borderColor: theme.palette.text.primary,
              },
            }}
            component="li"
          >
            <Typography sx={{ color: theme.palette.text.primary }}>
              or
            </Typography>
          </Divider>

          {network !== NetworkType.MAINNET ? (
            <>
              <Box
                component="div"
                style={{
                  display: "flex",
                  justifyContent: "center",
                  width: "100%",
                }}
              >
                <Typography
                  sx={{
                    display: "flex",
                    justifyContent: "center",
                    alignItems: "center",
                    padding: "0 8px",
                    gap: "10px",
                    borderRadius: "4px",
                    background: theme.palette.background.neutralDark,
                    color: "var(--green, #6EBE78)",
                    fontSize: "12px",
                    fontStyle: "normal",
                    fontWeight: 600,
                    lineHeight: "20px",
                    width: "68px",
                  }}
                >
                  TESTNET
                </Typography>
              </Box>
            </>
          ) : null}
          {availableWallets.length ? (
            availableWallets.map((walletName, index) => (
              <ListItem
                key={index}
                sx={{
                  display: "flex",
                  padding: "12px",
                  alignItems: "center",
                  gap: "10px",
                  borderRadius: "8px",
                  border: `1px solid ${theme.palette.text.primary}`,
                  mt: 2,
                  justifyContent: "space-between",
                  cursor: "pointer",
                  color: theme.palette.text.primary,
                  transition: "color 0.3s, border 0.3s",
                  "&:hover": {
                    color: "#D3DCF5",
                    border: "1px solid #D3DCF5",
                  },
                }}
                onClick={() => connectExtensionWallet(walletName)}
              >
                <Box
                  component="div"
                  sx={{ display: "flex", alignItems: "center", gap: 2 }}
                >
                  <ListItemAvatar>
                    <Avatar
                      src={walletIcon(walletName)}
                      sx={{ width: 20, height: 20 }}
                    />
                  </ListItemAvatar>
                  <Typography
                    sx={{
                      color: "inherit",
                      fontSize: "16px",
                      fontWeight: 600,
                      lineHeight: "20px",
                    }}
                  >
                    Connect{" "}
                    <span style={{ textTransform: "capitalize" }}>
                      {walletName === "typhoncip30" ? "Typhon" : walletName}
                    </span>{" "}
                    Wallet
                  </Typography>
                </Box>
                <IconButton
                  edge="end"
                  size="small"
                  sx={{ ml: "auto", padding: 0, color: "inherit" }}
                >
                  <OpenInNewOutlinedIcon
                    sx={{
                      width: "16px",
                      height: "16px",
                      flexShrink: 0,
                      ml: "auto",
                      color: "inherit",
                    }}
                  />
                </IconButton>
              </ListItem>
            ))
          ) : (
            <>
              {!checkIsMobile() && (
                <Typography
                  sx={{
                    color: theme.palette.text.primary,
                    fontSize: "16px",
                    fontWeight: 600,
                    lineHeight: "22px",
                    p: 2,
                  }}
                >
                  No extension wallets installed
                </Typography>
              )}
            </>
          )}
          {checkIsMobile() &&
            typeof (window as any).cardano === "undefined" && (
              <Typography
                sx={{
                  color: theme.palette.text.primary,
                  fontSize: "16px",
                  fontWeight: 600,
                  lineHeight: "22px",
                  p: 2,
                }}
              >
                If you are on a mobile device, tap this copy icon{" "}
                <IconButton
                  onClick={() => copyToClipboard(window.location.origin)}
                  sx={{
                    color: theme.palette.text.primary,
                    padding: "2px",
                    margin: "0 2px",
                    verticalAlign: "middle",
                    "&:hover": {
                      backgroundColor: "rgba(255, 255, 255, 0.1)",
                    },
                  }}
                  size="small"
                >
                  <ContentCopyIcon fontSize="small" />
                </IconButton>{" "}
                to copy the URL, then open one of the supported wallets listed
                above and paste the URL into the wallet's in-app dApp browser.
              </Typography>
            )}
        </List>
        <Box
          component="div"
          style={{
            marginTop: "24px",
            display: "flex",
            justifyContent: "center",
            width: "100%",
          }}
        >
          <Typography
            onClick={() => closeModal()}
            sx={{
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              padding: "0 8px",
              gap: "10px",
              color: theme.palette.text.primary,
              fontSize: "16px",
              fontStyle: "normal",
              fontWeight: 600,
              lineHeight: "24px",
              textDecorationLine: "underline",
              cursor: "pointer",
            }}
          >
            Continue browsing and connect later
          </Typography>
        </Box>
      </>
    );
  };

  return (
    <>
      {description ? (
        <Typography
          gutterBottom
          sx={{
            color: theme.palette.text.neutralLightest,
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

import { useSelector } from "react-redux";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import {
  Avatar,
  Box,
  Button,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
} from "@mui/material";
import AccountBalanceWalletIcon from "@mui/icons-material/AccountBalanceWallet";
import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";
import ExitToAppIcon from "@mui/icons-material/ExitToApp";
import VerifiedIcon from "@mui/icons-material/Verified";
import React from "react";
import "./ConnectWalletButton.scss";
import { RootState } from "../../store";
import { getUserInSession, tokenIsExpired } from "../../utils/session";
import {
  addressSlice,
  resolveCardanoNetwork,
  walletIcon,
} from "../../utils/utils";
import { env } from "../../common/constants/env";
import { eventBus } from "../../utils/EventBus";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";

type ConnectWalletButtonProps = {
  label: string;
  disableBackdropClick?: boolean;
  onOpenConnectWalletModal: () => void;
  onOpenVerifyWalletModal: () => void;
  onDisconnectWallet: () => void;
  onLogin: () => void;
};

const ConnectWalletButton = (props: ConnectWalletButtonProps) => {
  const { onOpenConnectWalletModal, onLogin, onDisconnectWallet } = props;
  const isMobile = useIsPortrait();
  const eventCache = useSelector((state: RootState) => state.user.event);
  const walletIsVerified = useSelector(
    (state: RootState) => state.user.walletIsVerified,
  );
  const session = getUserInSession();
  const isExpired = tokenIsExpired(session?.expiresAt);

  const { stakeAddress, isConnected, enabledWallet } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  const handleConnectWallet = () => {
    if (!isConnected) {
      onOpenConnectWalletModal();
    }
  };

  const handleVerifyWallet = () => {
    eventBus.publish("openVerifyWalletModal");
  };

  return (
    <Box className="button-container">
      <Button
        sx={{ zIndex: "99", padding: isMobile ? "10px 10px" : "16px 20px" }}
        className={`main-button ${
          isConnected ? "connected-button" : "connect-button"
        }`}
        color="inherit"
        onClick={() => handleConnectWallet()}
      >
        {isConnected ? (
          <Avatar
            src={enabledWallet ? walletIcon(enabledWallet) : ""}
            style={{ width: "24px", height: "24px" }}
          />
        ) : (
          <AccountBalanceWalletIcon />
        )}
        {isConnected ? (
          <>
            {isMobile
              ? null
              : stakeAddress
              ? addressSlice(stakeAddress, 8)
              : null}
            {walletIsVerified ? (
              <VerifiedIcon
                style={{
                  width: "20px",
                  paddingBottom: "0px",
                  color: "#1C9BEF",
                }}
              />
            ) : null}
            <div className="arrow-icon">
              <KeyboardArrowDownIcon />
            </div>
          </>
        ) : (
          <>{props.label?.length ? <span>{props.label}</span> : null}</>
        )}
      </Button>
      {isConnected && (
        <Box
          className="disconnect-wrapper"
          sx={{
            width: isMobile ? "180px" : "100%",
          }}
        >
          <List>
            {!walletIsVerified && !eventCache?.finished ? (
              <ListItem
                sx={{
                  zIndex: "99",
                  cursor: walletIsVerified ? "default" : "pointer",
                }}
                className="menu-button"
                color="inherit"
                onClick={() => handleVerifyWallet()}
                disabled={eventCache?.finished}
              >
                Verify Wallet
              </ListItem>
            ) : null}
            {((!session || isExpired) && walletIsVerified) ||
            (isExpired && !walletIsVerified && eventCache.finished) ? (
              <ListItem
                sx={{ zIndex: "99" }}
                className="menu-button"
                color="inherit"
                onClick={() => onLogin()}
                disabled={session && !isExpired}
              >
                Login
              </ListItem>
            ) : null}
            <ListItem
              sx={{
                zIndex: "99",
                display: "flex",
                justifyContent: "space-between",
                width: "100%",
              }}
              className="menu-button last-button"
              color="inherit"
              onClick={onDisconnectWallet}
            >
              <ListItemText primary="Logout" />
              <ListItemIcon sx={{ minWidth: "auto" }}>
                <ExitToAppIcon />
              </ListItemIcon>
            </ListItem>
          </List>
        </Box>
      )}
    </Box>
  );
};

export { ConnectWalletButton };

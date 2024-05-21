import React from "react";
import { useSelector } from "react-redux";
import { RootState } from "../../../store";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import {
  Avatar,
  Button,
  Typography,
  Box,
  useMediaQuery,
  IconButton,
} from "@mui/material";
import AccountBalanceWalletOutlinedIcon from "@mui/icons-material/AccountBalanceWalletOutlined";
import KeyboardArrowDownIcon from "@mui/icons-material/KeyboardArrowDown";
import VerifiedIcon from "@mui/icons-material/Verified";
import {
  addressSlice,
  resolveCardanoNetwork,
  walletIcon,
} from "../../../utils/utils";
import { getUserInSession, tokenIsExpired } from "../../../utils/session";
import { env } from "../../../common/constants/env";
import { i18n } from "../../../i18n";
import theme from "../../../common/styles/theme";
import { useIsPortrait } from "../../../common/hooks/useIsPortrait";

type ConnectWalletButtonProps = {
  disableBackdropClick?: boolean;
  onOpenConnectWalletModal: () => void;
  onOpenVerifyWalletModal: () => void;
  onDisconnectWallet: () => void;
  onLogin: () => void;
};

const ConnectWalletButton = ({
  onOpenConnectWalletModal,
  onOpenVerifyWalletModal,
  onLogin,
  onDisconnectWallet,
}: ConnectWalletButtonProps) => {
  const { eventCache, walletIsVerified } = useSelector((state: RootState) => ({
    eventCache: state.user.event,
    walletIsVerified: state.user.walletIsVerified,
  }));
  const session = getUserInSession();
  const isExpired = tokenIsExpired(session?.expiresAt);
  let isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const isPortrait = useIsPortrait();
  isMobile = isMobile || isPortrait;

  const { stakeAddress, isConnected, enabledWallet } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  return (
    <Box sx={{ position: "relative" }}>
      <IconButton
        sx={{
          width: isMobile ? 44 : 181,
          height: isMobile ? 44 : "58px",
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          borderRadius: "12px",
          padding: isMobile ? "0px" : "16px 24px 16px 20px",
          gap: isMobile ? 0 : 2,
          border: "1px solid var(--orange, #EE9766)",
          borderColor: "secondary.main",
          "&:hover": {
            borderColor: "text.primary",
            color: "text.primary",
          },
          color: "secondary.main",
          textTransform: "none",
        }}
        onClick={handleConnectWallet}
      >
        {isConnected && enabledWallet ? (
          <Avatar
            src={walletIcon(enabledWallet)}
            sx={{ width: 24, height: 24 }}
          />
        ) : (
          <AccountBalanceWalletOutlinedIcon />
        )}
        <Typography variant="body2" noWrap>
          {isConnected ? (
            <>
              {stakeAddress && addressSlice(stakeAddress, 5)}
              {walletIsVerified && (
                <VerifiedIcon sx={{ ml: 1, color: "success.main" }} />
              )}
              <KeyboardArrowDownIcon />
            </>
          ) : !isMobile ? (
            i18n.t("header.connectWalletButton")
          ) : null}
        </Typography>
      </IconButton>
      {isConnected && (
        <Box
          sx={{
            position: "absolute",
            top: "100%",
            left: 0,
            width: "100%",
            display: "none",
            bgcolor: "background.paper",
            boxShadow: 1,
          }}
        >
          {!walletIsVerified && !eventCache?.finished && (
            <Button
              sx={{ width: "100%", justifyContent: "center" }}
              onClick={onOpenVerifyWalletModal}
              disabled={eventCache?.finished}
            >
              Verify
            </Button>
          )}
          {((!session || isExpired) && walletIsVerified) ||
            (isExpired && !walletIsVerified && eventCache.finished && (
              <Button
                sx={{ width: "100%", justifyContent: "center" }}
                onClick={onLogin}
                disabled={session && !isExpired}
              >
                Login
              </Button>
            ))}
          <Button
            sx={{ width: "100%", justifyContent: "center" }}
            onClick={onDisconnectWallet}
          >
            Disconnect Wallet
          </Button>
        </Box>
      )}
    </Box>
  );

  function handleConnectWallet() {
    if (!isConnected) {
      onOpenConnectWalletModal();
    }
  }
};

export { ConnectWalletButton };

import React, { useState, useRef } from "react";
import { useSelector } from "react-redux";
import { RootState } from "../../../store";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import {
  Avatar,
  Button,
  Typography,
  Box,
  IconButton,
  Menu,
  MenuItem,
  useMediaQuery,
  Fade,
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
  dropdownOptions?: { label: string; action: () => void; endIcon: any }[];
};

const ConnectWalletButton = ({
  onOpenConnectWalletModal,
  onOpenVerifyWalletModal,
  onLogin,
  onDisconnectWallet,
  dropdownOptions,
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

  const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null);
  const buttonRef = useRef<HTMLElement | null>(null);
  const menuRef = useRef<HTMLElement | null>(null);
  const { stakeAddress, isConnected, enabledWalle } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  const handleMouseEnter = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(buttonRef.current);
  };

  const handleMouseLeave = () => {
    setTimeout(() => {
      const button = buttonRef.current;
      const menu = menuRef.current;
      if (button && menu) {
        if (
          !button.contains(document.activeElement) &&
          !menu.contains(document.activeElement)
        ) {
          setAnchorEl(null);
        }
      }
    }, 100);
  };

  const handleCloseMenu = () => {
    setAnchorEl(null);
  };

  return (
    <Box component="div" sx={{ position: "relative" }}>
      <IconButton
        ref={buttonRef}
        sx={{
          cursor: "pointer",
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
          "& .MuiButtonBase-root": {
            cursor: "pointer",
          },
          color: "secondary.main",
          textTransform: "none",
        }}
        onClick={() => onOpenConnectWalletModal()}
        onMouseEnter={handleMouseEnter}
        onMouseLeave={handleMouseLeave}
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

      {dropdownOptions && (
        <Menu
          anchorEl={anchorEl}
          open={Boolean(anchorEl)}
          onClose={handleCloseMenu}
          MenuListProps={{
            onMouseEnter: handleMouseEnter,
            onMouseLeave: handleMouseLeave,
          }}
          ref={menuRef}
          TransitionComponent={Fade}
          sx={{
            "& .MuiPaper-root": {
              background: theme.palette.background.default,
              color: "navy",
              padding: "10px",
              width: "180px",
              boxShadow: "4px 4px 24px 0px rgba(115, 115, 128, 0.20)",
              borderRadius: "12px",
              marginTop: "4px",
            },
            "& .MuiBackdrop-invisible": {
              zIndex: -1,
              position: "",
            },
            "& .MuiPopover-root": {
              zIndex: -1,
              position: "",
            },
          }}
        >
          {dropdownOptions.map((option, index) => (
            <MenuItem
              key={index}
              sx={{
                color: theme.palette.text.neutralLightest,
                fontSize: "12px",
                fontStyle: "normal",
                fontWeight: 500,
                lineHeight: "20px",
                background: "transparent !important",
                "& .MuiTouchRipple-root span": {
                  backgroundColor: "transparent !important",
                  background: "transparent !important",
                },
                display: "flex",
                justifyContent: "space-between",
                alignItems: "center",
              }}
              onClick={() => {
                option.action();
                handleCloseMenu();
              }}
            >
              <span>{option.label}</span>
              {option.endIcon && option.endIcon}
            </MenuItem>
          ))}
        </Menu>
      )}

      {isConnected && (
        <Box
          component="div"
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
};

export { ConnectWalletButton };

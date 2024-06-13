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
import "./ConnectWalletButton.scss";
import { getUserInSession, tokenIsExpired } from "../../utils/session";
import {
  addressSlice,
  resolveCardanoNetwork,
  walletIcon,
} from "../../utils/utils";
import { env } from "../../common/constants/env";
import { eventBus } from "../../utils/EventBus";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";
import { useAppSelector } from "../../store/hooks";
import { getEventCache } from "../../store/reducers/eventCache";
import {
  getConnectedWallet,
  getWalletIdentifier,
  getWalletIsVerified,
} from "../../store/reducers/userCache";

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
  const eventCache = useAppSelector(getEventCache);
  const walletIsVerified = useAppSelector(getWalletIsVerified);
  const walletIdentifier = useAppSelector(getWalletIdentifier);
  const connectedWallet = useAppSelector(getConnectedWallet);

  const session = getUserInSession();
  const isExpired = tokenIsExpired(session?.expiresAt);

  const { enabledWallet, stakeAddress } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  const walletName = stakeAddress ? enabledWallet : connectedWallet.name;

  const handleConnectWallet = () => {
    if (!walletIdentifier || !walletIdentifier.length) {
      onOpenConnectWalletModal();
    }
  };

  const handleVerifyWallet = () => {
    eventBus.publish("openVerifyWalletModal");
  };

  return (
    <Box component="div" className="button-container">
      <Button
        sx={{ zIndex: "99", padding: isMobile ? "10px 10px" : "16px 20px" }}
        className={`main-button ${
          walletIdentifier?.length ? "connected-button" : "connect-button"
        }`}
        color="inherit"
        onClick={() => handleConnectWallet()}
      >
        {walletIdentifier?.length ? (
          <Avatar
            src={walletIcon(walletName)}
            style={{ width: "24px", height: "24px" }}
          />
        ) : (
          <AccountBalanceWalletIcon />
        )}
        {walletIdentifier?.length ? (
          <>
            {isMobile
              ? null
              : walletIdentifier
              ? addressSlice(walletIdentifier, 8)
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
      {walletIdentifier?.length ? (
        <Box
          component="div"
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
      ) : null}
    </Box>
  );
};

export { ConnectWalletButton };

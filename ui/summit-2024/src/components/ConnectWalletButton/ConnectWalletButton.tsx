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
import { addressSlice } from "../../utils/utils";
import { eventBus } from "../../utils/EventBus";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";
import { useAppSelector } from "../../store/hooks";
import { getEventCache } from "../../store/reducers/eventCache";
import {
  getConnectedWallet,
  getWalletIsVerified,
} from "../../store/reducers/userCache";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "../../routes";

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
  const navigate = useNavigate();
  const isMobile = useIsPortrait();
  const eventCache = useAppSelector(getEventCache);
  const walletIsVerified = useAppSelector(getWalletIsVerified);
  const connectedWallet = useAppSelector(getConnectedWallet);

  const session = getUserInSession();
  const isExpired = tokenIsExpired(session?.expiresAt);

  const handleConnectWallet = () => {
    if (!connectedWallet.address || !connectedWallet.address .length) {
      onOpenConnectWalletModal();
    }
  };

  const handleVerifyWallet = () => {
    eventBus.publish("openVerifyWalletModal");
  };

  const handleOpenVoteReceipts = () => {
    navigate(ROUTES.RECEIPTS);
  };

  return (
    <Box component="div" className="button-container">
      <Button
        sx={{ zIndex: "99", padding: isMobile ? "10px 10px" : "16px 20px" }}
        className={`main-button ${
            connectedWallet.address ?.length ? "connected-button" : "connect-button"
        }`}
        color="inherit"
        onClick={() => handleConnectWallet()}
      >
        {connectedWallet.address ?.length ? (
          <Avatar
            src={connectedWallet.icon}
            style={{ width: "24px", height: "24px" }}
          />
        ) : (
          <AccountBalanceWalletIcon />
        )}
        {connectedWallet.address ?.length ? (
          <>
            {isMobile
              ? null
              : connectedWallet.address
                    ? addressSlice(connectedWallet.address , 5)
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
      {connectedWallet.address ?.length ? (
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
              onClick={handleOpenVoteReceipts}
            >
              <ListItemText primary="Votes Receipts" />
            </ListItem>
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

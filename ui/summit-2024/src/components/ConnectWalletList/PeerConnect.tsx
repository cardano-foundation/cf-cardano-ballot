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
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { resolveCardanoNetwork, walletIcon } from "../../utils/utils";
import IDWLogo from "../../assets/idw.png";
import { env } from "../../common/constants/env";

type PeerConnectProps = {
  description: string;
  onConnectWallet: () => void;
  onConnectError: (code: Error) => void;
  onOpenPeerConnect: () => void;
};

const SUPPORTED_WALLETS = env.SUPPORTED_WALLETS;

const PeerConnect = (props: PeerConnectProps) => {
  const { description, onConnectWallet, onConnectError } = props;
  const { installedExtensions, connect } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  const availableWallets = installedExtensions.filter((installedWallet) =>
    SUPPORTED_WALLETS.includes(installedWallet),
  );

  return (
    <>
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
          onClick={() => connect("", onConnectWallet, onConnectError)}
        >
          <Box
            component="div"
            sx={{ display: "flex", alignItems: "center", gap: 2 }}
          >
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
              <Box
                component="div"
                sx={{ display: "flex", alignItems: "center", gap: 2 }}
              >
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
      </List>
    </>
  );
};

export default PeerConnect;

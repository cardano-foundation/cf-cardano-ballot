import React from "react";
import {
  Box,
  Drawer,
  IconButton,
  List,
  ListItem,
  Typography,
} from "@mui/material";
import { useNavigate, useLocation } from "react-router-dom";
import { ROUTES } from "../../../routes";
import theme from "../../../common/styles/theme";
import Logo from "../../../assets/logo.svg";
import CloseIcon from "@mui/icons-material/Close";
import { ConnectWalletButton } from "../../ConnectWalletButton/ConnectWalletButton";
import { eventBus, EventName } from "../../../utils/EventBus";
import { resetUser } from "../../../store/reducers/userCache";
import { clearUserInSessionStorage } from "../../../utils/session";
import { useAppDispatch } from "../../../store/hooks";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";
import { resolveCardanoNetwork } from "../../../utils/utils";
import { env } from "../../../common/constants/env";
import { CustomButton } from "../../common/CustomButton/CustomButton";

interface RightMenuProps {
  menuIsOpen: boolean;
  setMenuIsOpen: (isOpen: boolean) => void;
}

const RightMenu: React.FC<RightMenuProps> = ({ menuIsOpen, setMenuIsOpen }) => {
  const navigate = useNavigate();
  const location = useLocation();
  const dispatch = useAppDispatch();

  const { disconnect } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  const handleClickMenu = (option: string) => {
    if (option !== location.pathname) {
      navigate(option);
      setMenuIsOpen(false);
    }
  };

  const handleConnectWalletModal = () => {
    eventBus.publish(EventName.OpenConnectWalletModal);
  };
  const handleOpenVerify = () => {
    eventBus.publish(EventName.OpenVerifyWalletModal);
  };

  const onDisconnectWallet = () => {
    dispatch(resetUser());
    disconnect();
    clearUserInSessionStorage();
  };

  return (
    <>
      <Drawer
        anchor="right"
        open={menuIsOpen}
        onClose={() => setMenuIsOpen(false)}
        PaperProps={{ sx: { width: "100%", height: "100%" } }}
      >
        <Box
          component="div"
          sx={{
            width: "100%",
            height: "100%",
            display: "flex",
            flexDirection: "column",
            backgroundColor: theme.palette.background.default,
          }}
        >
          <Box
            component="div"
            sx={{
              display: "flex",
              justifyContent: "space-between",
              alignItems: "center",
              padding: 2,
              position: "relative",
            }}
          >
            <Box component="img" src={Logo} sx={{ height: 40 }} />
            <IconButton
              sx={{
                position: "absolute",
                right: 20,
                display: "inline-flex",
                padding: "12px",
                alignItems: "flex-start",
                borderRadius: "12px",
                backgroundColor: theme.palette.background.neutralDark,
                cursor: "pointer",
                "&:hover": {
                  backgroundColor: theme.palette.text.neutralLightest,
                  color: theme.palette.background.neutralDark,
                },
              }}
              edge="end"
              color="inherit"
              onClick={() => setMenuIsOpen(false)}
            >
              <CloseIcon />
            </IconButton>
          </Box>

          <Box
            component="div"
            sx={{
              display: "flex",
              alignItems: "center",
              width: "100%",
              px: "16px",
            }}
          >
            <ConnectWalletButton
              label="Connect Wallet"
              showAddress={true}
              onOpenConnectWalletModal={handleConnectWalletModal}
              onOpenVerifyWalletModal={handleOpenVerify}
              onDisconnectWallet={() => onDisconnectWallet()}
            />
          </Box>

          <Box
            component="div"
            sx={{
              width: "100%",
              display: "flex",
              flexDirection: "column",
              alignItems: "left",
              paddingTop: 2,
              paddingLeft: 2,
            }}
          >
            <List>
              <ListItem onClick={() => handleClickMenu(ROUTES.CATEGORIES)}>
                <Typography
                  sx={{
                    color: theme.palette.text.primary,
                    fontSize: "16px",
                    fontStyle: "normal",
                    fontWeight: 600,
                    lineHeight: "20px",
                    marginBottom: "16px",
                    cursor: "pointer",
                  }}
                >
                  Categories
                </Typography>
              </ListItem>
              <ListItem onClick={() => handleClickMenu(ROUTES.LEADERBOARD)}>
                <Typography
                  sx={{
                    color: theme.palette.text.primary,
                    fontSize: "16px",
                    fontStyle: "normal",
                    fontWeight: 600,
                    lineHeight: "24px",
                    marginBottom: "16px",
                    cursor: "pointer",
                  }}
                >
                  Leaderboard
                </Typography>
              </ListItem>
              <ListItem onClick={() => handleClickMenu(ROUTES.USER_GUIDE)}>
                <Typography
                  sx={{
                    color: theme.palette.text.primary,
                    fontSize: "16px",
                    fontStyle: "normal",
                    fontWeight: 600,
                    lineHeight: "24px",
                    marginBottom: "16px",
                    cursor: "pointer",
                  }}
                >
                  User Guide
                </Typography>
              </ListItem>
            </List>

            <Box component="div" sx={{pr: "16px"}}>
              <CustomButton
                onClick={() => handleClickMenu(ROUTES.CATEGORIES)}
                fullWidth={true}
                colorVariant="primary"
              >
                Start Voting
              </CustomButton>
            </Box>
          </Box>
        </Box>
      </Drawer>
    </>
  );
};

export { RightMenu };

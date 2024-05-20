import React from "react";
import {
  Avatar,
  IconButton,
  List,
  ListItem,
  ListItemAvatar,
  ListItemSecondaryAction,
  Typography,
} from "@mui/material";
import InstallDesktopIcon from "@mui/icons-material/InstallDesktop";
import "./SupportedWalletsList.scss";
import { openNewTab, resolveCardanoNetwork } from "../../utils/utils";
import { env } from "common/constants/env";
import { useCardano } from "@cardano-foundation/cardano-connect-with-wallet";

type ConnectWalletModalProps = {
  description: string;
};

const SUPPORTED_WALLETS = env.SUPPORTED_WALLETS;

const CHROME_STORE_LINKS = {
  typhoncip30:
    "https://chrome.google.com/webstore/detail/typhon-wallet/kfdniefadaanbjodldohaedphafoffoh",
  gerowallet:
    "https://chrome.google.com/webstore/detail/gerowallet/bgpipimickeadkjlklgciifhnalhdjhe",
  flint:
    "https://chrome.google.com/webstore/detail/flint-wallet/hnhobjmcibchnmglfbldbfabcgaknlkj",
  nami: "https://chrome.google.com/webstore/detail/nami/lpfcbjknijpeeillifnkikgncikgfhdo",
  yoroi:
    "https://chrome.google.com/webstore/detail/yoroi/ffnbelfdoeiohenkjibnmadjiehjhajb",
  lace: "https://chrome.google.com/webstore/detail/lace/gafhhkghbfjjkeiendhlofajokpaflmk",
  eternl:
    "https://chrome.google.com/webstore/detail/eternl/kmhcihpebfmpgmihbkipmjlmmioameka",
  nufi: "",
};

const SupportedWalletsList = (props: ConnectWalletModalProps) => {
  const { description } = props;
  const { installedExtensions } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  const walletIcon = (walletName) => {
    if (walletName === "typhoncip30") {
      return "/static/typhon-icon.png";
    }
    if (walletName === "gerowallet") {
      return "/static/gero-icon.png";
    } else {
      return `/static/${walletName}-icon.png`;
    }
  };

  return (
    <>
      <Typography
        className="connect-wallet-modal-description"
        gutterBottom
        style={{ wordWrap: "break-word" }}
      >
        {description}
      </Typography>
      <List>
        {SUPPORTED_WALLETS.map((walletName, index) => (
          <ListItem key={index} className="walletItem">
            <ListItemAvatar>
              <Avatar
                src={walletIcon(walletName)}
                style={{ width: "24px", height: "24px" }}
              />
            </ListItemAvatar>
            <Typography className="walletLabel">
              {!installedExtensions.includes(walletName) ? "Install " : null}{" "}
              <span className="walletName">
                {walletName === "typhoncip30" ? "typhon" : walletName}
              </span>{" "}
              Wallet
            </Typography>
            <ListItemSecondaryAction>
              <IconButton
                onClick={() => {
                  if (
                    Object.keys(CHROME_STORE_LINKS).includes(walletName) &&
                    CHROME_STORE_LINKS[walletName].length
                  ) {
                    openNewTab(CHROME_STORE_LINKS[walletName]);
                  }
                }}
                edge="end"
                aria-label="share"
              >
                <InstallDesktopIcon />
              </IconButton>
            </ListItemSecondaryAction>
          </ListItem>
        ))}
      </List>
    </>
  );
};

export default SupportedWalletsList;

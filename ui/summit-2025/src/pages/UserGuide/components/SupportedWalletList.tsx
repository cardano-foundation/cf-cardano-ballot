import {
  Avatar,
  Box,
  IconButton,
  List,
  ListItem,
  ListItemAvatar,
  Typography,
} from "@mui/material";
import OpenInNewOutlinedIcon from "@mui/icons-material/OpenInNewOutlined";
import Modal from "../../../components/common/Modal/Modal";
import { useIsPortrait } from "../../../common/hooks/useIsPortrait";
import { env } from "../../../common/constants/env";
import theme from "../../../common/styles/theme";
import { useEffect, useState } from "react";
import { eventBus, EventName } from "../../../utils/EventBus";
import typhonLogo from "../../../common/resources/images/wallets/typhon.svg";
import geroLogo from "../../../common/resources/images/wallets/gero.svg";
import flintLogo from "../../../common/resources/images/wallets/flint.svg";
import namiLogo from "../../../common/resources/images/wallets/nami.svg";
import yoroiLogo from "../../../common/resources/images/wallets/yoroi.svg";
import laceLogo from "../../../common/resources/images/wallets/lace.svg";
import eternlLogo from "../../../common/resources/images/wallets/eternl.svg";
import nufiLogo from "../../../common/resources/images/wallets/nufi.svg";

const SUPPORTED_WALLETS = env.SUPPORTED_WALLETS;

const CHROME_STORE_LINKS = {
  typhoncip30: {
    link: "https://chromewebstore.google.com/webstore/detail/typhon-wallet/kfdniefadaanbjodldohaedphafoffoh",
    logo: typhonLogo,
  },
  gerowallet: {
    link: "https://chromewebstore.google.com/webstore/detail/gerowallet/bgpipimickeadkjlklgciifhnalhdjhe",
    logo: geroLogo,
  },
  flint: {
    link: "https://chromewebstore.google.com/webstore/detail/flint-wallet/hnhobjmcibchnmglfbldbfabcgaknlkj",
    logo: flintLogo,
  },
  nami: {
    link: "https://chromewebstore.google.com/webstore/detail/nami/lpfcbjknijpeeillifnkikgncikgfhdo",
    logo: namiLogo,
  },
  yoroi: {
    link: "https://chromewebstore.google.com/webstore/detail/yoroi/ffnbelfdoeiohenkjibnmadjiehjhajb",
    logo: yoroiLogo,
  },
  lace: {
    link: "https://chromewebstore.google.com/webstore/detail/lace/gafhhkghbfjjkeiendhlofajokpaflmk",
    logo: laceLogo,
  },
  eternl: {
    link: "https://chromewebstore.google.com/webstore/detail/eternl/kmhcihpebfmpgmihbkipmjlmmioameka",
    logo: eternlLogo,
  },
  nufi: {
    link: "https://chromewebstore.google.com/detail/nufi/gpnihlnnodeiiaakbikldcihojploeca",
    logo: nufiLogo,
  },
};

const SupportedWalletsList = () => {
  const isMobile = useIsPortrait();
  const [isOpen, setIsOpen] = useState(false);

  const getWalletData = (name: string) => {
    const key = Object.keys(CHROME_STORE_LINKS).find((link) =>
      link.includes(name),
    );
    return key ? CHROME_STORE_LINKS[key] : undefined;
  };

  const handleOpenWallet = (walletName: string) => {
    const walletData = getWalletData(walletName);
    if (walletData?.link) {
      window.open(walletData.link, "_blank");
    }
  };

  const handleCloseModal = () => {
    setIsOpen(false);
  };

  useEffect(() => {
    const openModal = () => {
      setIsOpen(true);
    };
    eventBus.subscribe(EventName.OpenSupportedWalletsModal, openModal);
    return () => {
      eventBus.unsubscribe(EventName.OpenSupportedWalletsModal, openModal);
    };
  }, []);

  const renderSupportedWallets = () => {
    return (
      <>
        <List
          sx={{
            maxHeight: "80vh",
            overflowY: "auto",
          }}
        >
          {SUPPORTED_WALLETS.map((walletName, index) => (
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
              onClick={() => handleOpenWallet(walletName)}
            >
              <Box
                component="div"
                sx={{ display: "flex", alignItems: "center", gap: 2 }}
              >
                <ListItemAvatar>
                  <Avatar
                    src={getWalletData(walletName)?.logo}
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
                    color: theme.palette.text.neutralLight,
                    ml: "auto",
                  }}
                />
              </IconButton>
            </ListItem>
          ))}
        </List>
      </>
    );
  };

  return (
    <>
      <Modal
        id="login-modal"
        isOpen={isOpen}
        name="login-modal"
        title="Supported Wallets"
        onClose={() => handleCloseModal()}
        width={isMobile ? "100%" : "450px"}
      >
        {renderSupportedWallets()}
      </Modal>
    </>
  );
};

export default SupportedWalletsList;

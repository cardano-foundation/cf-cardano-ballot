import { Box, Link, Typography } from "@mui/material";
import { forwardRef, useMemo } from "react";

import { ModalContents, ModalHeader, ModalWrapper } from "@atoms";
import { useModal } from "@context";
import type { WalletOption } from "@molecules";
import { WalletOptionButton } from "@molecules";
import { openInNewTab } from "@utils";
import { To } from "react-router-dom";
import { LINKS } from "@/consts/links";

type ChooseWalletModalState = {
  pathToNavigate?: To;
};

export const ChooseWalletModal = forwardRef<HTMLDivElement>((_, ref) => {
  const { state } = useModal<ChooseWalletModalState>();

  const walletOptions: WalletOption[] = useMemo(() => {
    if (!window.cardano) return [];
    const keys = Object.keys(window.cardano);
    const resultWallets: WalletOption[] = [];
    keys.forEach((k: string) => {
      const { icon, name, supportedExtensions } = window.cardano[k];
      if (icon && name && supportedExtensions) {
        // Check if the name already exists in resultWallets
        const isNameDuplicate = resultWallets.some(
          (wallet) => wallet.label === name,
        );
        // Check if the supportedExtensions array contains an entry with cip === 95
        const isCip95Available = Boolean(
          supportedExtensions?.find((i) => i.cip === 95),
        );
        // If the name is not a duplicate and cip === 95 is available, add it to resultWallets
        if (!isNameDuplicate && isCip95Available) {
          resultWallets.push({
            icon,
            label: name,
            name: k,
            cip95Available: true,
          });
        }
      }
    });
    return resultWallets;
  }, [window]);

  return (
    <ModalWrapper dataTestId="connect-your-wallet-modal" ref={ref}>
      <ModalHeader>Connect your Wallet</ModalHeader>
      <ModalContents>
        <Typography
          sx={{
            fontSize: "16px",
            fontWeight: "500",
            marginBottom: "24px",
            textAlign: "center",
          }}
        >
          {"Choose the wallet you want to connect with:"}
        </Typography>
        <Box
          sx={{
            display: "flex",
            flexDirection: "column",
            maxHeight: "500px",
            overflow: "auto",
            width: "100%",
            padding: "8px",
          }}
        >
          {!walletOptions.length ? (
            <Typography
              color="primary"
              variant="body2"
              fontWeight={600}
              sx={{ textAlign: "center" }}
            >
              {"You don't have wallets to connect, install a wallet and refresh the page and try again"}
            </Typography>
          ) : (
            walletOptions.map(({ icon, label, name, cip95Available }) => (
              <WalletOptionButton
                dataTestId={`${name}-wallet-button`}
                key={name}
                icon={icon}
                label={label}
                name={name}
                cip95Available={cip95Available}
                pathToNavigate={state?.pathToNavigate}
              />
            ))
          )}
        </Box>
        <Typography
          sx={{
            fontSize: "11px",
            fontWeight: "500",
            marginTop: "24px",
            textAlign: "center",
          }}
        >
          {"Can’t see your wallet? Check what wallets are currently compatible with GovTool "}
          <Link
            fontSize={11}
            fontWeight={500}
            onClick={() => openInNewTab(LINKS.COMPATIBLE_WALLETS)}
            sx={{ cursor: "pointer" }}
          >
            {"here"}
          </Link>
          .
        </Typography>
      </ModalContents>
    </ModalWrapper>
  );
});

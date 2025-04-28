import { AppBar, Box, Button } from "@mui/material";

import { useCardano, useModal } from "@context";
import { WalletInfoCard } from "@/components/molecules";
import { IMAGES } from '@consts';

export const TopNav = () => {
  const { openModal } = useModal();
  const { isEnabled } = useCardano();
  return (
    <AppBar position="static" sx={{ bgcolor: 'transparent', borderRadius: 0, padding: '24px 64px', boxShadow: 'none' }}>
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
          flex: 1,
          justifyContent: "space-between",
        }}
      >
        <img
          alt="app-logo"
          src={IMAGES.appLogo}
        />
        <Box sx={{ width: '260px' }}>
          <WalletInfoCard />
        </Box>
        {!isEnabled && (
          <Button variant="contained" color="secondary" onClick={() => { openModal({ type: "chooseWallet" }); }}>
            Connect Wallet
          </Button>
        )}
      </Box>
    </AppBar>
  );
}

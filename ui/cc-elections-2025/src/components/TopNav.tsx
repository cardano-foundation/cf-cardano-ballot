import { useNavigate } from "react-router-dom";
import { AppBar, Box, Button } from "@mui/material";

import { useCardano, useModal } from "@context";
import { WalletInfoCard } from "@/components/molecules";
import { ICONS, IMAGES } from '@consts';
import Typography from "@mui/material/Typography";
import IconButton from "@mui/material/IconButton";


type TopNavProps = {
  title: string;
  navigateBack: boolean;
}

export const TopNav = ({title, navigateBack}: TopNavProps) => {
  const { openModal } = useModal();
  const { isEnabled } = useCardano();
  const navigate = useNavigate();
  return (
    <AppBar position="static" sx={{ bgcolor: 'transparent', borderRadius: 0, padding: '24px 64px', boxShadow: 'none' }}>
      <Box
        sx={{
          display: "flex",
          alignItems: "center",
        }}
      >
        <img
          alt="app-logo"
          src={IMAGES.appLogo}
        />
        <Box sx={{ display: 'flex', flex: 1, alignItems: 'center', justifyContent: 'space-between', padding: '0 32px' }}>
          <Box sx={{ display: 'flex', gap: '8px' }}>
            {navigateBack && (
              <IconButton onClick={() => navigate(-1)}>
                <img src={ICONS.arrowLeftIcon} alt="" />
              </IconButton>
            )}
            <Typography variant="h1">{title}</Typography>
          </Box>
          {!isEnabled ? (
            <Box>
              <Button variant="contained" color="secondary" onClick={() => { openModal({ type: "chooseWallet" }); }}>
                Connect Wallet
              </Button>
            </Box>
          ) : (
            <Box sx={{ width: '260px' }}>
              <WalletInfoCard />
            </Box>
          )}
        </Box>
      </Box>
    </AppBar>
  );
}

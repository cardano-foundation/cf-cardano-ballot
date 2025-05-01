import { useNavigate } from "react-router-dom";
import { AppBar, Box, Button, Link } from "@mui/material";

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
    <AppBar position="static" sx={{ backgroundColor: 'transparent', borderRadius: 0, padding: { xxs: '24px 16px', md: '24px 32px', xl: '24px 64px'}, boxShadow: 'none' }}>
      <Box
        sx={{
          display: "flex",
          flexDirection: { xxs: "column", md: "row" },
          rowGap: '16px',
          alignItems: "center",
        }}
      >
        <Link href="/">
          <img
            alt="app-logo"
            src={IMAGES.appLogo}
          />
        </Link>
        <Box sx={{
          display: 'flex',
          flex: 1,
          flexDirection: { xxs: "column", md: "row" },
          rowGap: '16px',
          alignItems: 'center',
          justifyContent: 'space-between',
          padding: { xxs: 0, md: '0 32px' },
          columnGap: '16px',
        }}>
          <Box sx={{ display: 'flex', gap: '8px' }}>
            {navigateBack && (
              <IconButton onClick={() => navigate(-1)}>
                <img src={ICONS.arrowLeftIcon} alt="" />
              </IconButton>
            )}
            <Typography
              variant="h1"
              sx={{
                fontSize: {xxs: "30px" , lg: "33px"},
                textAlign: {xxs: "center" , md: "left"},
              }}
            >
              {title}
            </Typography>
          </Box>
          {!isEnabled ? (
            <Box sx={{ flex: { md: '0 0 160px' } }}>
              <Button variant="contained" color="secondary" onClick={() => { openModal({ type: "chooseWallet" }); }}>
                Connect Wallet
              </Button>
            </Box>
          ) : (
            <Box sx={{ minWidth: '260px' }}>
              <WalletInfoCard />
            </Box>
          )}
        </Box>
      </Box>
    </AppBar>
  );
}

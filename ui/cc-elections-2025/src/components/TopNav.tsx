import { AppBar, Box, Button } from "@mui/material";
import { IMAGES, ICONS } from '../consts';

export const TopNav = () => {
  return (
    <AppBar position="static" sx={{ bgcolor: 'transparent', borderRadius: 0, padding: '40px 32px', boxShadow: 'none' }}>
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
        <Button
          variant="text"
          endIcon={<img src={ICONS.externalLinkIcon} alt="" />}
          sx={{
            color: "#212A3D",
            fontSize: '18px',
            lineHeight: 1,
            fontWeight: 400,
            padding: '12px 12px 12px 16px',
          }}
        >
          Support
        </Button>
      </Box>
    </AppBar>
  );
}

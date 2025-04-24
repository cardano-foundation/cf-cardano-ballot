import { AppBar, Box, Button } from "@mui/material";
import { IMAGES } from '../consts';

export const TopNav = () => {
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
        <Button variant="contained" color="secondary" onClick={() => null}>
          Button
        </Button>
      </Box>
    </AppBar>
  );
}

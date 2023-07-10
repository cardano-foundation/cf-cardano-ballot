import CssBaseline from '@mui/material/CssBaseline';
import Box from '@mui/material/Box';
import Typography from '@mui/material/Typography';
import Container from '@mui/material/Container';

function Copyright() {
  return (
    <Typography
      variant="body2"
      color="text.secondary"
      align="center"
    >
      {'Copyright © '}
      {new Date().getFullYear()}
      <span color="inherit">. All rights reserved.</span>
    </Typography>
  );
}

export default function Footer() {
  return (
    <Box
      sx={{
        display: 'flex',
        flexDirection: 'column',
        minHeight: '20vh',
      }}
    >
      <CssBaseline />
      <Box
        component="footer"
        sx={{
          py: 3,
          px: 2,
          mt: 'auto',
        }}
      >
        <Container maxWidth="lg">
          <Typography variant="body1">Cardano Ballot V2</Typography>
          <Copyright />
        </Container>
      </Box>
    </Box>
  );
}

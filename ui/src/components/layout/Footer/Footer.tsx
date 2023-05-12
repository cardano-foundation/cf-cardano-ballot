import Container from "@mui/material/Container";
import Typography from "@mui/material/Typography";
import Box from "@mui/material/Box";

function Copyright() {
  return (
    <Typography
      variant="body2"
      color="text.secondary"
      align="center">
      {"Copyright © "}
      {new Date().getFullYear()} {" "}
      <span
        color="inherit">
        Cardano Ballot
      </span>. All rights reserved.
    </Typography>
  );
}

export default function Footer() {
  return (
    <Container maxWidth="sm">
      <Box sx={{ my: 4 }}>
        <Copyright />
      </Box>
    </Container>
  );
}

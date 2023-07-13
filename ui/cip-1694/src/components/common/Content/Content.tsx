import React from "react";
import { PageRouter } from "../../../common/routes";
import { Box, Container } from "@mui/material";
import CssBaseline from "@mui/material/CssBaseline";

export default function Content() {
  return (
    <Box
    sx={{
      display: 'flex',
      flexDirection: 'column',
      minHeight: '60vh',
    }}
  >
      <CssBaseline />
      <Container
        component="main"
        maxWidth="lg"
        sx={{ mt: 2, mb: 1 }}
      >
        <PageRouter />
      </Container>
    </Box>
  );
}

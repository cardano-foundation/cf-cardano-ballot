import React from 'react';
import { Box, Container } from '@mui/material';
import CssBaseline from '@mui/material/CssBaseline';
import { PageRouter } from '../../../common/routes';

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

import { PageRouter } from "../../../commons/routes";
import { Box, Container } from "@mui/material";
import CssBaseline from "@mui/material/CssBaseline";

export default function Content() {
  return (
    <Box>
      <CssBaseline />
      <Container
        component="main"
        maxWidth="lg"
        sx={{ mt: 9, mb: 1 }}
      >
        <PageRouter />
      </Container>
    </Box>
  );
}

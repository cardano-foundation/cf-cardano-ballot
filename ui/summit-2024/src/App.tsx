import "./App.scss";
import { Box, CircularProgress, Container, Grid } from "@mui/material";
import { BrowserRouter } from "react-router-dom";
import Header from "./components/common/Header/Header";
import { PageRouter } from "./routes";
import { Footer } from "./components/common/Footer/Footer";
import { TermsAndConditionsModal } from "./components/LegalOptInModal/TermsAndConditionsModal";

const eventCache = {
  id: "1",
};
function App() {
  return (
    <>
      <BrowserRouter>
        <Container md={{ maxWidth: "1440px" }}>
          <Grid container spacing={1} direction="column">
            <Grid item xs>
              <Header />
            </Grid>
            <Grid item xs={6}>
              <Box className="content">
                {eventCache !== undefined && eventCache?.id.length ? (
                  <PageRouter />
                ) : (
                  <Box
                    sx={{
                      display: "flex",
                      height: "60vh",
                      alignItems: "center",
                      justifyContent: "center",
                    }}
                  >
                    <CircularProgress
                      className="app-spinner"
                      style={{
                        color: "#03021f",
                        strokeWidth: "10",
                      }}
                    />
                  </Box>
                )}
              </Box>
            </Grid>
            <Grid item xs>
              <Footer />
            </Grid>
          </Grid>
          <TermsAndConditionsModal />
        </Container>
      </BrowserRouter>
    </>
  );
}

export default App;

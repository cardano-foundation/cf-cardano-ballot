import "./App.scss";
import { Box, CircularProgress, Container, Grid } from "@mui/material";
import { BrowserRouter } from "react-router-dom";
import Header from "./components/Header/Header";
import { PageRouter } from "./routes";
import { Footer } from "./components/Footer/Footer";
import { TermsAndConditionsModal } from "./components/LegalOptInModal/TermsAndConditionsModal";
import { Cookies } from "./components/LegalOptInModal/Cookies";

const eventCache = {
  id: "1",
};
function App() {
  return (
    <>
      <BrowserRouter>
        <Container
          maxWidth="lg"
          sx={{
            padding: "0px !important",
          }}
        >
          <Grid container spacing={1} direction="column">
            <Grid container justifyContent="center">
              <Grid item xs={12}>
                <Header />
              </Grid>
            </Grid>

            <Box component="div">
              {eventCache !== undefined && eventCache?.id.length ? (
                <PageRouter />
              ) : (
                <Box
                  component="div"
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
            <Footer />
          </Grid>
          <Cookies />
          <TermsAndConditionsModal />
        </Container>
      </BrowserRouter>
    </>
  );
}

export default App;

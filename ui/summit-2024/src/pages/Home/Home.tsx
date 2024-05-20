import React from "react";
import { Box, Typography, Grid, useMediaQuery, Button } from "@mui/material";
import Shapes from "../../assets/shapes.svg";
import theme from "../../common/styles/theme";
import GLBViewer from "../../components/GLBViewer/GLBViewer";

const Home: React.FC = () => {
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  return (
    <>
      <Grid container>
        <Grid item xs={12} md={6}>
          <Typography
            variant="h4"
            sx={{
              color: "text.neutralLight",
              fontSize: isMobile ? "12px" : "16px",
              fontStyle: "normal",
              marginLeft: isMobile ? "" : "20px",
              whiteSpace: "nowrap",
              fontWeight: "500",
              lineHeight: "24px",
              marginBottom: "20px",
              marginTop: "96px",
              textAlign: isMobile ? "center" : "",
            }}
          >
            Voting Closes 9 October 2024 23:59 UTC
          </Typography>
          <Typography
            variant="h4"
            sx={{
              color: "text.neutralLightest",
              fontFamily: "Dosis",
              fontSize: isMobile ? "40px" : "88px",
              fontStyle: "normal",
              fontWeight: "700",
              lineHeight: isMobile ? "42px" : "88px",
              marginLeft: isMobile ? "" : "20px",
              whiteSpace: "nowrap",
              textAlign: isMobile ? "center" : "",
            }}
          >
            Vote for the
          </Typography>
          <Box
            sx={{
              display: "flex",
              marginLeft: "12px",
              justifyContent: isMobile ? "center" : "",
            }}
          >
            {!isMobile ? (
              <Box
                sx={{
                  marginTop: "48px",
                  marginRight: "8px",
                  position: "absolute",
                  width: "80px",
                  height: "16px",
                  flexShrink: 0,
                  borderRadius: "8px",
                  background:
                    "linear-gradient(258deg, #EE9766 0%, #40407D 87.58%, #0C7BC5 249.97%)",
                }}
              />
            ) : null}
            <Typography
              variant="h4"
              sx={{
                color: "text.neutralLightest",
                fontFamily: "Dosis",
                fontSize: isMobile ? "40px" : "88px",
                fontStyle: "normal",
                fontWeight: "700",
                display: "inline",
                whiteSpace: "nowrap",
                lineHeight: isMobile ? "42px" : "88px",
                marginLeft: isMobile ? "" : "96px",
              }}
            >
              Cardano Summit
            </Typography>
          </Box>
          <Typography
            variant="h4"
            gutterBottom
            sx={{
              color: "text.neutralLightest",
              fontFamily: "Dosis",
              fontSize: isMobile ? "40px" : "88px",
              fontStyle: "normal",
              fontWeight: "700",
              lineHeight: isMobile ? "52px" : "88px",
              marginLeft: isMobile ? "" : "20px",
              whiteSpace: "nowrap",
              textAlign: isMobile ? "center" : "",
            }}
          >
            2024 Awards
          </Typography>
          <Box
            sx={{
              display: "flex",
              justifyContent: isMobile ? "center" : "flex-start",
              marginLeft: isMobile ? "" : "20px",
              marginTop: "20px",
            }}
          >
            {isMobile ? (
              <Box
                sx={{
                  width: "80px",
                  height: "16px",
                  flexShrink: 0,
                  borderRadius: "8px",
                  background:
                    "linear-gradient(258deg, #EE9766 0%, #40407D 87.58%, #0C7BC5 249.97%)",
                }}
              />
            ) : null}
          </Box>
          <Box
            sx={{
              display: "flex",
              justifyContent: isMobile ? "center" : "flex-start",
              marginLeft: isMobile ? "" : "20px",
              marginTop: "20px",
            }}
          >
            <Button
              variant="contained"
              sx={{
                textTransform: "none",
                width: "134px",
                height: "56px",
                color: "background.neutralDarkest",
                fontSize: "16px",
                fontStyle: "normal",
                fontWeight: 500,
                lineHeight: "24px",
                marginRight: "12px",
                borderRadius: " 12px",
                background:
                  "linear-gradient(70deg, #0C7BC5 -105.24%, #40407D -53.72%, #EE9766 -0.86%, #EE9766 103.82%)",
              }}
            >
              Start Voting
            </Button>
            <Button
              onClick={() => {}}
              className="vote-nominee-button"
              style={{
                textTransform: "none",
                width: "134px",
                height: "56px",
                color: "#EE9766",
                fontSize: "16px",
                fontStyle: "normal",
                fontWeight: 500,
                lineHeight: "24px",
                border: "1px solid var(--orange, #EE9766)",
                borderRadius: " 12px",
              }}
            >
              How to Vote
            </Button>
          </Box>
        </Grid>
        <Grid item xs={12} md={6}>
          <Box
            sx={{
              height: "100%",
              backgroundImage: !isMobile ? `url(${Shapes})` : "",
              backgroundSize: "contain",
              backgroundRepeat: "no-repeat",
              backgroundPosition: "center",
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              marginTop: isMobile ? "" : "12px",
              marginLeft: isMobile ? "24px" : "90px",
            }}
          >
            <GLBViewer glbUrl="/award.glb" />
          </Box>
        </Grid>
      </Grid>
    </>
  );
};

export { Home };

import * as React from "react";
import { styled, useTheme } from "@mui/material/styles";
import { Grid, Container, Typography, Button } from "@mui/material";

const ContentStyle = styled("div")(({ theme }) => ({
  display: "flex",
  justifyContent: "flex-start",
  flexDirection: "column",
  padding: theme.spacing(2, 0),
  [theme.breakpoints.up("md")]: {
    alignItems: "flex-start",
    padding: theme.spacing(5, 0, 0, 5),
  },
}));

const QuestionStyle = styled("div")(({ theme }) => ({
  maxWidth: 600,
  display: "flex",
  flexDirection: "column",
  justifyContent: "flex-start",
  margin: theme.spacing(2, 0, 2, 2),
}));

const HeroStyleImg = styled("img")(({ theme }) => ({
  top: 0,
  width: 600,
  height: 500,
  objectFit: "cover",
  borderRadius: 16,
  [theme.breakpoints.up("md")]: {
    maxWidth: 420,
  },
}));

export default function Content() {
  const theme = useTheme();
  return (
    <Container>
      <Grid
        container
        direction={{ xs: "column", sm: "row" }}
        justifyContent={{ xs: "center", sm: "flex-start" }}
        alignItems="center"
        spacing={3}
      >
        <Grid item>
          <QuestionStyle>
            <Typography
              variant="h3"
              sx={{ color: "text.primary", textAlign: "left", fontWeight: 600 }}
            >
              What is CIP-1694 voting?
            </Typography>
            <Typography
              variant="body1"
              sx={{ color: "text.secondary", textAlign: "left" }}
            >
              Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do
              eiusmod tempor incididunt ut labore et dolore magna aliqua. Sit
              amet justo donec enim diam vulputate.
            </Typography>
          </QuestionStyle>
          <Button
            size="large"
            variant="contained"
            sx={{
              marginTop: "0px !important",
              height: { xs: "50px", sm: "60px", lg: "70px" },
              fontSize: "25px",
              fontWeight: 700,
              textTransform: "none",
              fontFamily: "Roboto Bold",
              backgroundColor: theme.palette.primary.main,
            }}
          >
            Ready to vote?
          </Button>
        </Grid>
        <Grid item>
          <ContentStyle>
            <HeroStyleImg
              src="/static/cardano-summit-hero.jpeg"
              alt="cardano-summit-2022"
            />
          </ContentStyle>
        </Grid>
      </Grid>
    </Container>
  );
}

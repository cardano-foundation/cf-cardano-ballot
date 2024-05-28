import React from "react";
import {
  Grid,
  Box,
  Typography,
  Button,
  Link,
  useMediaQuery,
} from "@mui/material";
import Logo from "../../../assets/logo.svg";
import dubaiBg from "../../../assets/dubai-bg.svg";
import { CustomButton } from "../../../components/common/CustomButton/CustomButton";
import theme from "../../../common/styles/theme";

const TicketsSection = () => {
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  return (
    <>
      <Grid
        container
        spacing={2}
        sx={{
          marginTop: "120px",
          backgroundImage: `url(${dubaiBg})`,
          backgroundSize: "cover",
          backgroundPosition: "center 72%",
          position: "relative",
          height: 340,
          overflow: "hidden",
          borderRadius: "24px",
          paddingLeft: { xs: "16px", sm: "50px" },
          paddingRight: { xs: "16px", sm: "50px" },
          mx: { xs: "auto", sm: 0 },
        }}
      >
        <Box
          sx={{
            position: "absolute",
            top: 0,
            left: 0,
            width: "100%",
            height: "100%",
            background: "rgba(18, 18, 18, 0.50)",
            backdropFilter: "blur(3px)",
            zIndex: 0,
          }}
        />
        <Grid
          item
          xs={12}
          sm={6}
          style={{
            textAlign: {
              xs: "center",
              sm: "left",
            },
            zIndex: 1,
          }}
        >
          <img src={Logo} alt="Event Logo" style={{ width: 200 }} />
        </Grid>
        {!isMobile && (
          <Grid item xs={12} sm={6} style={{ textAlign: "right", zIndex: 1 }}>
            <CustomButton
              colorVariant="primary"
              sx={{
                marginTop: { xs: "30px", md: "0px" },
              }}
            >
              Get Tickets
            </CustomButton>
          </Grid>
        )}
        <Grid
          item
          xs={12}
          style={{ textAlign: isMobile ? "center" : "left", zIndex: 1 }}
        >
          <Typography
            variant="h4"
            sx={{
              fontFamily: "Dosis",
              fontWeight: 700,
              fontSize: { xs: "32px", sm: "40px", md: "52px" },
              lineHeight: { xs: "38px", sm: "48px", md: "56px" },
            }}
          >
            A Global Blockchain
          </Typography>
          <Typography
            variant="h4"
            sx={{
              color: theme.palette.text.neutralLightest,
              fontWeight: 700,
              fontFamily: "Dosis",
              fontSize: { xs: "32px", sm: "40px", md: "52px" },
              lineHeight: { xs: "38px", sm: "48px", md: "56px" },
            }}
          >
            Event
          </Typography>
        </Grid>
        <Grid
          item
          xs={12}
          style={{ textAlign: isMobile ? "center" : "right", zIndex: 1 }}
        >
          <Typography
            variant="h4"
            sx={{
              fontFamily: "Dosis",
              fontWeight: 700,
              fontSize: { xs: "32px", sm: "40px", md: "52px" },
              lineHeight: { xs: "38px", sm: "48px", md: "56px" },
            }}
          >
            Dubai, UAE
          </Typography>

          <Typography
            variant="h4"
            sx={{
              fontFamily: "Dosis",
              fontWeight: 700,
              fontSize: { xs: "32px", sm: "40px", md: "52px" },
              lineHeight: { xs: "38px", sm: "48px", md: "56px" },
            }}
          >
            23-24 October 2024
          </Typography>
        </Grid>
        {isMobile && (
          <Grid
            item
            xs={12}
            style={{ textAlign: "center", zIndex: 1, marginBottom: "26px" }}
          >
            <CustomButton colorVariant="primary">Get Tickets</CustomButton>
          </Grid>
        )}
      </Grid>
    </>
  );
};

export { TicketsSection };

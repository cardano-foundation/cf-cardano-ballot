import React from "react";
import { Grid, Box, Typography, Button, Link } from "@mui/material";
import Logo from "../../../assets/logo.svg";
import dubaiBg from "../../../assets/dubai-bg.svg";

const TicketsSection = () => {
  return (
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
          width: { xs: 'calc(100% - 32px)', sm: 'auto' }
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
        }}
      />

      <Grid
        item
        xs={6}
        sx={{
          display: {xs: "", sm: "flex"},
          flexDirection: "column",
          alignItems: "flex-start",
          justifyContent: "center",
          padding: 2,
          marginTop: {xs: "30px", sm: "-30px", md: "0px"},
          zIndex: 2,
        }}
      >
        <img
          src={Logo}
          alt="Event Logo"
          style={{ width: 200, marginBottom: 16 }}
        />
        <Typography
          variant="h4"
          sx={{
              fontSize: { xs: "32px", sm: "40px", md: "52px" },
              lineHeight: { xs: "38px", sm: "48px", md: "56px" },
          }}
        >
          A Global Blockchain
        </Typography>
        <Typography
          variant="h4"
          sx={{
            color: "white",
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
        xs={6}
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: "flex-end",
          justifyContent: "center",
          padding: 2,
          zIndex: 2,
          paddingRight: "50px",
        }}
      >
        <Button
          variant="contained"
          sx={{
            marginTop: "30px",
            display: "inline-flex",
            padding: "16px 24px",
            justifyContent: "center",
            alignItems: "center",
            gap: "8px",
            borderRadius: " 12px",
            color: "background.neutralDarkest",
            fontSize: "16px",
            fontStyle: "normal",
            fontWeight: 500,
            lineHeight: "24px",
            textTransform: "none",
            bgcolor: "secondary.main",
            "&:hover": {
              bgcolor: "secondary.dark",
            },
          }}
        >
          <Link
            href="https://summit.cardano.org/"
            target="_blank"
            rel="noopener"
            sx={{
              textDecoration: "none",
            }}
          >
            Get Tickets
          </Link>
        </Button>
        <Typography
          variant="h6"
          sx={{
            marginTop: "80px",
            color: "text.neutralLightest",
            textAlign: "right",
            fontSize: "32px",
            lineHeight: "line-height: 40px",
          }}
        >
          Dubai, UAE
        </Typography>
        <Typography
          variant="h6"
          sx={{
            color: "text.neutralLightest",
            textAlign: "right",
            fontSize: "32px",
            lineHeight: "line-height: 40px",
          }}
        >
          23-24 October 2024
        </Typography>
      </Grid>
    </Grid>
  );
};

export { TicketsSection };

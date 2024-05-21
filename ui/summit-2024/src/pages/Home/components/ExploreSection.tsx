import React from "react";
import {
  Grid,
  Typography,
  Button,
  Card,
  CardContent,
  Box,
  useTheme,
  useMediaQuery,
} from "@mui/material";
import HowToVoteOutlinedIcon from "@mui/icons-material/HowToVoteOutlined";
import Ellipses2 from "../../../assets/ellipses2.svg";
import folderIcon from "../../../assets/folder.svg";
import trophyIcon from "../../../assets/trophy.svg";

const ExploreSection = () => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  return (
    <Grid container spacing={2} sx={{ marginTop: 8, justifyContent: "center" }}>
      {/* Text and button in a single row on mobile, normal layout on desktop */}
      <Grid
        item
        xs={12}
        md={4}
        sx={{
          display: "flex",
          flexDirection: isMobile ? "column" : "column",
          alignItems: "center",
          justifyContent: "center",
        }}
      >
        <Typography
          variant="h4"
          sx={{
            color: "text.neutralLightest",
            fontFamily: "Dosis",
            fontSize: isMobile ? "40px" : "52px",
            fontStyle: "normal",
            fontWeight: "700",
            lineHeight: isMobile ? "22px" : "56px",
            marginLeft: isMobile ? "" : "20px",
            whiteSpace: "nowrap",
          }}
        >
          Cast Your Vote for
        </Typography>
        <Typography
          variant="h4"
          sx={{
            color: "text.neutralLightest",
            fontFamily: "Dosis",
            fontSize: isMobile ? "40px" : "52px",
            fontStyle: "normal",
            fontWeight: "700",
            lineHeight: isMobile ? "22px" : "56px",
            marginLeft: isMobile ? "" : "20px",
            whiteSpace: "nowrap",
          }}
        >
          This Year’s Award
        </Typography>
        <Typography
          variant="h4"
          sx={{
            color: "text.neutralLightest",
            fontFamily: "Dosis",
            fontSize: isMobile ? "40px" : "52px",
            fontStyle: "normal",
            fontWeight: "700",
            lineHeight: isMobile ? "22px" : "56px",
            marginLeft: isMobile ? "" : "20px",
            whiteSpace: "nowrap",
          }}
        >
          Summit!
        </Typography>
        <Button
          onClick={() => {}}
          className="vote-nominee-button"
          style={{
            textTransform: "none",
            width: "151px",
            height: "56px",
            color: "#EE9766",
            fontSize: "16px",
            fontStyle: "normal",
            fontWeight: 500,
            lineHeight: "24px",
            border: "1px solid var(--orange, #EE9766)",
            borderRadius: "12px",
            marginLeft: "20px",
            marginTop: "48px",
            padding: "16px 24px 16px 20px",
          }}
          startIcon={<HowToVoteOutlinedIcon />}
        >
          User Guide
        </Button>
      </Grid>
      {/* Adjust card grid settings for mobile to stack underneath */}
      <Grid item xs={12} sm={6} md={4}>
        <Card
          sx={{
            position: "relative",
            height: "272px",
            overflow: "hidden",
            borderRadius: "24px",
            maxWidth: "404px",
          }}
        >
          {/* Card content as before */}
        </Card>
      </Grid>
      <Grid item xs={12} sm={6} md={4}>
        <Card
          sx={{
            position: "relative",
            height: "272px",
            overflow: "hidden",
            borderRadius: "24px",
            maxWidth: "404px",
          }}
        >
          {/* Card content as before */}
        </Card>
      </Grid>
    </Grid>
  );
};

export { ExploreSection };

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
import { useIsPortrait } from "../../../common/hooks/useIsPortrait";

const ExploreSection = () => {
  const isMobile = useIsPortrait();

  return (
    <Grid container spacing={2} sx={{ marginTop: 8, justifyContent: "center" }}>
      <Grid
        item
        xs={12}
        md={4}
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: isMobile ? "center" : "flex-start",
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
            textAlign: isMobile ? "center" : "left",
            lineHeight: isMobile ? "22px" : "56px",
            whiteSpace: "nowrap",
            mb: 2,
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
            textAlign: isMobile ? "center" : "left",
            lineHeight: isMobile ? "22px" : "56px",
            whiteSpace: "nowrap",
            mb: 2,
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
            textAlign: isMobile ? "center" : "left",
            lineHeight: isMobile ? "22px" : "56px",
            whiteSpace: "nowrap",
          }}
        >
          Summit!
        </Typography>
        <Button
          onClick={() => {}}
          sx={{
            textTransform: "none",
            width: "auto",
            height: "56px",
            color: "#EE9766",
            fontSize: "16px",
            fontWeight: 500,
            lineHeight: "24px",
            border: "1px solid var(--orange, #EE9766)",
            borderRadius: "12px",
            mt: 4,
            padding: "16px 24px",
            alignSelf: isMobile ? "center" : "flex-start",
          }}
          startIcon={<HowToVoteOutlinedIcon />}
        >
          User Guide
        </Button>
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
          <CardContent sx={{ position: "relative", zIndex: 2 }}>
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                marginTop: "20px",
              }}
            >
              <img
                src={folderIcon}
                style={{
                  width: "44px",
                  height: "44px",
                  marginLeft: "40px",
                  marginRight: "8px",
                }}
              />
              <Typography
                variant="h6"
                component="div"
                sx={{
                  fontFamily: "Dosis",
                  fontSize: "20px",
                  fontStyle: "normal",
                  fontWeight: 700,
                  lineHeight: "28px",
                  color: "text.neutralLightest",
                }}
              >
                Categories
              </Typography>
            </Box>
            <Typography
              variant="h2"
              component="div"
              sx={{
                color: "text.neutralLightest",
                fontSize: "68px",
                fontStyle: "normal",
                fontWeight: 500,
                lineHeight: "76px",
                marginTop: "46px",
                marginLeft: "40px",
              }}
            >
              Ambassador
            </Typography>
          </CardContent>
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
          <CardContent sx={{ position: "relative", zIndex: 2 }}>
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                marginTop: "20px",
              }}
            >
              <img
                src={trophyIcon}
                style={{
                  width: "44px",
                  height: "44px",
                  marginLeft: "40px",
                  marginRight: "8px",
                }}
              />
              <Typography
                variant="h6"
                component="div"
                sx={{
                  fontFamily: "Dosis",
                  fontSize: "20px",
                  fontStyle: "normal",
                  fontWeight: 700,
                  lineHeight: "28px",
                  color: "text.neutralLightest",
                }}
              >
                Leaderboard
              </Typography>
            </Box>
            <Typography
              variant="h2"
              component="div"
              sx={{
                color: "text.neutralLightest",
                fontSize: "16px",
                fontStyle: "normal",
                fontWeight: 500,
                lineHeight: "24px",
                marginLeft: "40px",
                marginTop: "40px",
              }}
            >
              Total votes
            </Typography>
            <Typography
              variant="h2"
              component="div"
              sx={{
                color: "text.neutralLightest",
                fontSize: "68px",
                fontStyle: "normal",
                fontWeight: 500,
                lineHeight: "76px",
                marginLeft: "40px",
              }}
            >
              1,275
            </Typography>
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  );
};

export { ExploreSection };

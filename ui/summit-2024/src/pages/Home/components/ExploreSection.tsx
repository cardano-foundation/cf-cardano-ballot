import React from "react";
import {
  Grid,
  Typography,
  Button,
  Card,
  CardContent,
  Box,
} from "@mui/material";
import HowToVoteOutlinedIcon from "@mui/icons-material/HowToVoteOutlined";
import folderIcon from "../../../assets/folder.svg";
import trophyIcon from "../../../assets/trophy.svg";
import { useIsPortrait } from "../../../common/hooks/useIsPortrait";
import { CustomButton } from "../../../components/common/CustomButton/CustomButton";
import theme from "../../../common/styles/theme";

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
          <Box sx={{
              display: {
                  xs: "block",
                    sm: "none"
              }
          }}
          >
              <Typography
                  variant="h4"
                  sx={{
                      color: theme.palette.text.neutralLightest,
                      fontFamily: "Dosis",
                      fontSize: { md: "40px", lg: "52px" },
                      lineHeight: { md: "30px", lg: "56px" },
                      fontStyle: "normal",
                      fontWeight: "700",
                      textAlign: isMobile ? "center" : "left",
                      whiteSpace: { sm: "nowrap", md: "normal" },
                      mb: 2,
                  }}
              >
                  Cast Your Vote for This
              </Typography>
              <Typography
                  variant="h4"
                  sx={{
                      color: theme.palette.text.neutralLightest,
                      fontFamily: "Dosis",
                      fontSize: { md: "40px", lg: "52px" },
                      lineHeight: { md: "30px", lg: "56px" },
                      fontStyle: "normal",
                      fontWeight: "700",
                      textAlign: isMobile ? "center" : "left",
                      whiteSpace: { sm: "nowrap", md: "normal" },
                      mb: 2,
                  }}
              >
                  Year’s Award Summit!2
              </Typography>
          </Box>
          <Box sx={{
              display: {
                  xs: "none",
                  sm: "block"
              },
              width: "100%",
          }}
          >
              <Typography
                  variant="h4"
                  sx={{
                      color: theme.palette.text.neutralLightest,
                      fontFamily: "Dosis",
                      fontSize: { md: "52px" },
                      lineHeight: { md: "56px" },
                      fontStyle: "normal",
                      fontWeight: "700",
                      textAlign: {
                          xs:  "center",
                          md: "left"
                      },
                      whiteSpace: { sm: "nowrap", md: "normal" },
                  }}
              >
                  Cast Your Vote for This Year’s
              </Typography>
              <Typography
                  variant="h4"
                  sx={{
                      color: theme.palette.text.neutralLightest,
                      fontFamily: "Dosis",
                      fontSize: { md: "52px" },
                      lineHeight: { md: "56px" },
                      fontStyle: "normal",
                      fontWeight: "700",
                      textAlign: {
                          xs:  "center",
                          md: "left"
                      },
                      whiteSpace: { sm: "nowrap", md: "normal" },
                      mb: 2,
                  }}
              >
                  Award Summit!
              </Typography>
          </Box>
          <Box sx={{
              width: "100%",
              textAlign: {
                  xs: "center",
                  md: "left"
              }
          }}
          >
              <CustomButton
                  sx={{ marginTop: "28px",  marginBottom: "32px" }}
                  colorVariant="secondary"
                  startIcon={<HowToVoteOutlinedIcon />}
              >
                  User Guide
              </CustomButton>
          </Box>


      </Grid>
      <Grid item xs={12} sm={6} md={4}>
        <Card
          sx={{
            position: "relative",
            height: "272px",
            overflow: "hidden",
            borderRadius: "24px",
            mx: { xs: "auto", sm: "inherit" },
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            width: { xs: "calc(100% - 32px)", sm: "auto" },
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
                  color: theme.palette.text.neutralLightest,
                }}
              >
                Categories
              </Typography>
            </Box>
            <Typography
              sx={{
                color: theme.palette.text.neutralLightest,
                fontSize: {
                    xs: "40px",
                    md: "68px",
                },
                fontStyle: "normal",
                fontWeight: 500,
                lineHeight: "76px",
                marginTop: "46px",
                marginLeft: isMobile ? "" : "40px",
                  whiteSpace: "nowrap",
                  maxWidth: "335px"
              }}
            >
                Ambassador, Blockchain for Good, DeFi ..
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
            mx: { xs: "auto", sm: "inherit" },
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
            width: { xs: "calc(100% - 32px)", sm: "auto" },
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
                  color: theme.palette.text.neutralLightest,
                }}
              >
                Leaderboard
              </Typography>
            </Box>
            <Typography
              variant="h2"
              component="div"
              sx={{
                color: theme.palette.text.neutralLightest,
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
                color: theme.palette.text.neutralLightest,
                  fontSize: {
                      xs: "40px",
                      md: "68px",
                  },
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

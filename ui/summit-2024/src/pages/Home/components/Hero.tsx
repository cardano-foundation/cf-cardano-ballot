import Shapes from "../../../assets/shapes.svg";
import GLBViewer from "../../../components/GLBViewer/GLBViewer";
import React from "react";
import {Box, Button, Fade, Grid, Typography, useMediaQuery} from "@mui/material";
import { useIsPortrait } from "../../../common/hooks/useIsPortrait";
import { CustomButton } from "../../../components/common/CustomButton/CustomButton";
import theme from "../../../common/styles/theme";

const Hero = () => {
    const isMobile = useMediaQuery(theme.breakpoints.down('xs'));
    const isTablet = useMediaQuery(theme.breakpoints.up('tablet'));
    const isDesktop = useMediaQuery(theme.breakpoints.up('tablet'));
  const isPortrait = useIsPortrait();
  return (
    <>
      <Grid container spacing={{ xs: 2, sm: 3, md: 4, lg: 5 }}>
        <Grid item xs={12} sm={8} tablet={4} md={6} lg={4}>
          <Typography
            variant="h4"
            sx={{
              color: "text.neutralLight",
              fontSize: { xs: "12px", sm: "14px", md: "16px", lg: "18px" },
              fontStyle: "normal",
              marginLeft: isPortrait ? "" : "20px",
              whiteSpace: "nowrap",
              fontWeight: "500",
              lineHeight: { xs: "18px", sm: "20px", md: "24px", lg: "28px" },
              marginBottom: "20px",
              marginTop: { xs: "20px", sm: "44px", md: "64px", lg: "96px" },
              textAlign: {
                  xs: "center",
                  sm: "left",
              }
            }}
          >
            Voting Closes 9 October 2024 23:59 UTC
          </Typography>
          <Typography
            variant="h4"
            sx={{
              color: "text.neutralLightest",
              fontFamily: "Dosis",
                fontSize: { xs: "40px", md: "70px", lg: "88px" },
              fontStyle: "normal",
              fontWeight: "700",
              lineHeight: { xs: "32px", md: "70px", lg: "88px" },
              marginLeft: isPortrait ? "" : "20px",
              whiteSpace: "nowrap",
                textAlign: {
                    xs: "center",
                    sm: "left"
                }
            }}
          >
            Vote for the
          </Typography>
          <Box
            sx={{
              display: "flex",
              marginLeft: {
                  sm: "",
                  md: "12px"
              },
              justifyContent: {
                xs: "center",
                sm: "left"
              }
            }}
          >
              <Box
                  sx={{
                      marginTop: {
                          xs: "4%",
                          tablet: "1.5%",
                          md: "2.5%"
                      },
                      marginRight: {
                          tablet: "55%",
                          md: "50%"
                      },
                      position: "absolute",
                      width: "80px",
                      height: "16px",
                      flexShrink: 0,
                      borderRadius: "8px",
                      background:
                          "linear-gradient(258deg, #EE9766 0%, #40407D 87.58%, #0C7BC5 249.97%)",
                      display: {
                          xs: "none",
                          tablet: "inline-block"

                      }
                  }}
              />
            <Typography
              variant="h4"
              sx={{
                color: theme.palette.text.neutralLightest,
                fontFamily: "Dosis",
                fontSize: { xs: "40px", md: "70px", lg: "88px" },
                fontStyle: "normal",
                fontWeight: "700",
                display: "inline",
                whiteSpace: "nowrap",
                  lineHeight: { xs: "32px", md: "70px", lg: "88px" },
                marginLeft: {
                    xs: "0px",
                    tablet: "96px"
                },
                  textAlign: {
                      xs: "center",
                      sm: "left"
                },
                "-webkit-justify-content": {
                    xs: "center",
                    sm: "left"
                }
              }}
            >
              Cardano Summit
            </Typography>
          </Box>
          <Typography
            variant="h4"
            gutterBottom
            sx={{
              color: theme.palette.text.neutralLightest,
              fontFamily: "Dosis",
                fontSize: {xs: "40px", md: "70px", lg: "88px" },
              fontStyle: "normal",
              fontWeight: "700",
                lineHeight: { xs: "32px", md: "70px", lg: "88px" },
              marginLeft: {
                  xs: "0",
                  tablet: "0"
              },
              whiteSpace: "nowrap",
              textAlign: {
                  xs: "center",
                  sm: "left"
              }
            }}
          >
            2024 Awards
          </Typography>

          {isMobile ? (
            <Box
              sx={{
                display: "flex",
                justifyContent: "center",
                marginTop: "20px",
              }}
            >
              <Box
                sx={{
                  marginRight: "8px",
                  marginBottom: "32px",
                  width: "80px",
                  height: "16px",
                  flexShrink: 0,
                  borderRadius: "8px",
                  background:
                    "linear-gradient(258deg, #EE9766 0%, #40407D 87.58%, #0C7BC5 249.97%)",
                }}
              />
            </Box>
          ) : null}
            <Box
                sx={{
                    display: "flex",
                    flexDirection: {
                        xs: "column",
                        sm: "row"
                    },
                    width: "100%",
                    justifyContent: {
                        xs: "center",
                        sm: "flex-start"
                    },
                    alignItems: "center",
                    marginTop: "20px",
                }}
            >
                <CustomButton
                    sx={{
                        width: {
                            xs: "90%",
                            sm: "144px"
                        },
                        margin: "10px 0",
                        marginRight: {
                            sm: "12px"
                        }
                    }}
                    colorVariant="primary"
                >
                    Start Voting
                </CustomButton>
                <CustomButton
                    colorVariant="secondary"
                    sx={{
                        width: {
                            xs: "90%",
                            sm: "144px"
                        },
                        margin: "10px 0"
                    }}
                >
                    How to Vote
                </CustomButton>
            </Box>

        </Grid>
        <Grid item xs={12} sm={4} tablet={8} md={6} lg={8}>
          <Box
            sx={{
              height: "100%",
              backgroundImage: {
                  xs: "",
                  sm: `url(${Shapes})`
              },
              backgroundSize: {
                xs: "cover",
                sm: "contain",
                md: "contain",
                lg: "contain",
              },
              backgroundRepeat: "no-repeat",
              backgroundPosition: {
                xs: "left",
                sm: "center",
                md: "center",
                lg: "right",
              },
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              marginTop: isPortrait ? "" : "12px",
              marginLeft: isPortrait ? "24px" : "90px",
              paddingLeft: { xs: 2, sm: 3, md: 4, lg: 30 },
            }}
          >
            <Fade in={true} timeout={3000}>
              <Box>
                <GLBViewer glbUrl="/compressed.glb" height={
                    isTablet ? "600px" : "350px"
                } width="auto" />
              </Box>
            </Fade>
          </Box>
        </Grid>
      </Grid>
    </>
  );
};

export { Hero };

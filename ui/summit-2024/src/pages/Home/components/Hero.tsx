import Shapes from "../../../assets/shapes.svg";
import GLBViewer from "../../../components/GLBViewer/GLBViewer";
import React from "react";
import { Box, Button, Grid, Typography } from "@mui/material";
import { useIsPortrait } from "../../../common/hooks/useIsPortrait";
import {CustomButton} from "../../../components/common/CustomButton/CustomButton";

const Hero = () => {
  const isMobile = useIsPortrait();
  return (
    <>
      <Grid container spacing={{ xs: 2, sm: 3, md: 4, lg: 5 }}>
        <Grid item xs={12} sm={8} md={6} lg={4}>
          <Typography
            variant="h4"
            sx={{
              color: "text.neutralLight",
              fontSize: { xs: "12px", sm: "14px", md: "16px", lg: "18px" },
              fontStyle: "normal",
              marginLeft: isMobile ? "" : "20px",
              whiteSpace: "nowrap",
              fontWeight: "500",
              lineHeight: { xs: "18px", sm: "20px", md: "24px", lg: "28px" },
              marginBottom: "20px",
              marginTop: { xs: "20px", sm: "44px", md: "64px", lg: "96px" },
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
              fontSize: { xs: "52px", sm: "62px", md: "70px", lg: "88px" },
              fontStyle: "normal",
              fontWeight: "700",
              lineHeight: { xs: "52px", sm: "62px", md: "70px", lg: "88px" },
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
                  marginTop: "38px",
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
                fontSize: { xs: "52px", sm: "62px", md: "70px", lg: "88px" },
                fontStyle: "normal",
                fontWeight: "700",
                display: "inline",
                whiteSpace: "nowrap",
                lineHeight: { xs: "52px", sm: "62px", md: "70px", lg: "88px" },
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
              fontSize: { xs: "52px", sm: "62px", md: "70px", lg: "88px" },
              fontStyle: "normal",
              fontWeight: "700",
              lineHeight: { xs: "52px", sm: "62px", md: "70px", lg: "88px" },
              marginLeft: isMobile ? "" : "20px",
              whiteSpace: "nowrap",
              textAlign: isMobile ? "center" : "",
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
              flexDirection: isMobile ? "column" : "row",
              justifyContent: isMobile ? "center" : "flex-start",
              marginLeft: isMobile ? "" : "20px",
              marginTop: "20px",
            }}
          >
              <CustomButton colorVariant="primary" sx={{margin: { xs: '12px 0', sm: '10px 12px' },}}>
                  Start Voting
              </CustomButton>
              <CustomButton colorVariant="secondary" sx={{margin: { xs: '12px 0', sm: '10px 12px' },}}>
                  How to Vote
              </CustomButton>
          </Box>
        </Grid>
        <Grid item xs={12} sm={4} md={6} lg={8}>
          <Box
            sx={{
              height: "100%",
              backgroundImage: !isMobile ? `url(${Shapes})` : "",
              backgroundSize: {
                xs: "cover",
                sm: "cover",
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
              marginTop: isMobile ? "" : "12px",
              marginLeft: isMobile ? "24px" : "90px",
              paddingLeft: { xs: 2, sm: 3, md: 4, lg: 30 },
            }}
          >
            <GLBViewer glbUrl="/compressed.glb" />
          </Box>
        </Grid>
      </Grid>
    </>
  );
};

export { Hero };

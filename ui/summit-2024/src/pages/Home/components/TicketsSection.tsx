import { Grid, Box, Typography, useMediaQuery, Card } from "@mui/material";
import Logo from "../../../assets/logo.svg";
import dubaiBg from "../../../assets/dubai-bg.svg";
import { CustomButton } from "../../../components/common/CustomButton/CustomButton";
import theme from "../../../common/styles/theme";

const TicketsSection = () => {
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  const handleButtonClick = () => {
    window.open("https://summit.cardano.org/registration/", "_blank");
  };

  return (
    <>
      <Grid
        container
        spacing={2}
        sx={{
          marginTop: 8,
          justifyContent: "center",
          marginLeft: {
            xs: "-1%",
          },
        }}
      >
        <Grid
          item
          xs={12}
          sx={{
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
          }}
        >
          <Grid item xs={12}>
            <Card
              sx={{
                position: "relative",
                overflow: "hidden",
                borderRadius: "24px",
                mx: { xs: "auto", sm: "inherit" },
                display: "flex",
                flexDirection: "column",
                justifyContent: "center",
                width: { xs: "calc(100% - 32px)", sm: "auto" },
                backgroundImage: `url(${dubaiBg})`,
                backgroundSize: "cover",
                backgroundPosition: "center 72%",
                padding: {
                  xs: "16px",
                  sm: "60px",
                },
              }}
            >
              <Box
                component="div"
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
              <Grid container>
                <Grid
                  item
                  xs={12}
                  sm={6}
                  sx={{
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
                  <Grid
                    item
                    xs={12}
                    sm={6}
                    style={{ textAlign: "right", zIndex: 1 }}
                  >
                    <CustomButton
                      colorVariant="primary"
                      sx={{
                        marginTop: { xs: "30px", md: "0px" },
                      }}
                      onClick={handleButtonClick}
                    >
                      Get Tickets
                    </CustomButton>
                  </Grid>
                )}
              </Grid>

              <Grid container>
                <Grid
                  item
                  xs={12}
                  sm={6}
                  style={{ textAlign: isMobile ? "center" : "left", zIndex: 1 }}
                >
                  <Typography
                    variant="h4"
                    sx={{
                      fontFamily: "Dosis",
                      fontWeight: 700,
                      fontSize: { xs: "32px", sm: "40px", md: "52px" },
                      lineHeight: { xs: "38px", sm: "48px", md: "56px" },
                      marginTop: { xs: "32px", sm: "58px" },
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
                  sm={6}
                  style={{
                    textAlign: isMobile ? "center" : "right",
                    zIndex: 1,
                  }}
                >
                  <Typography
                    variant="h4"
                    sx={{
                      fontFamily: "Dosis",
                      fontWeight: 700,
                      fontSize: { xs: "32px", sm: "40px", md: "52px" },
                      lineHeight: { xs: "38px", sm: "48px", md: "56px" },
                      marginTop: { xs: "16px", sm: "84px" },
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
              </Grid>

              {isMobile && (
                <Grid
                  item
                  xs={12}
                  sx={{
                    textAlign: "center",
                    zIndex: 1,
                    marginBottom: "26px",
                    marginTop: "32px",
                  }}
                >
                  <CustomButton
                    colorVariant="primary"
                    onClick={handleButtonClick}
                  >
                    Get Tickets
                  </CustomButton>
                </Grid>
              )}
            </Card>
          </Grid>
        </Grid>
      </Grid>
    </>
  );
};

export { TicketsSection };

import Shapes from "../../../assets/shapes.svg";
import GLBViewer from "../../../components/GLBViewer/GLBViewer";
import { Box, Fade, Grid, Typography, useMediaQuery } from "@mui/material";
import { useIsPortrait } from "../../../common/hooks/useIsPortrait";
import { CustomButton } from "../../../components/common/CustomButton/CustomButton";
import theme from "../../../common/styles/theme";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "../../../routes";
import { formatISODate } from "../../../utils/utils";
import { useAppSelector } from "../../../store/hooks";
import { getEventCache } from "../../../store/reducers/eventCache";

const Hero = () => {
  const isMobile = useMediaQuery(theme.breakpoints.down("xs"));
  const isTablet = useMediaQuery(theme.breakpoints.down("md"));
  const isDesktop = useMediaQuery(theme.breakpoints.up("tablet"));
  const eventCache = useAppSelector(getEventCache);
  const isPortrait = useIsPortrait();
  const navigate = useNavigate();
  const handleClickMenu = (option: string) => {
    navigate(option);
  };

  const showVotingButtons = !eventCache.finished; // If the event is running or has not started running yet

  const getVotingText = () : string => {  
    if (eventCache.notStarted) {  
      return "Voting Opens " + formatISODate(eventCache.eventStartDate);  
    }  
    if (eventCache.active) {  
      return "Voting Closes " + formatISODate(eventCache.eventEndDate);  
    }  
    if (eventCache.proposalsReveal) {  
      return "THE RESULTS ARE IN!!!"  
    }  
    return "Results Announced " + formatISODate(eventCache.proposalsRevealDate);  
  };  

  const getAwardHeight = (): string => {
    const isMobilePlus = useMediaQuery(theme.breakpoints.down("sm"));
    let height = "600px";
    if (isMobile) {
      height = "350px";
    } else if (isMobilePlus) {
      height = "350px";
    } else if (isTablet) {
      height = "400px";
    } else if (isDesktop) {
      height = "550px";
    }
    return height;
  };
  return (
    <>
      <Grid
        container
        sx={{
          px: "20px",
        }}
      >
        <Grid item xs={12} sm={5} tablet={5} md={6} lg={4}>
          <Typography
            variant="h4"
            sx={{
              color: theme.palette.text.neutralLightest,
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
              },
            }}
          >
            {getVotingText()}
          </Typography>
          <Typography
            variant="h4"
            sx={{
              color: theme.palette.text.neutralLightest,
              fontFamily: "Dosis",
              fontSize: { xs: "40px", md: "70px", lg: "88px" },
              fontStyle: "normal",
              fontWeight: "700",
              lineHeight: { xs: "42px", md: "88px" },
              whiteSpace: "nowrap",
              textAlign: {
                xs: "center",
                sm: "left",
                tablet: "left",
              },
            }}
          >
            Vote for the
          </Typography>
          <Box
            component="div"
            sx={{
              display: "flex",
              marginLeft: {
                sm: "",
                md: "12px",
              },
              justifyContent: {
                xs: "center",
                sm: "left",
              },
            }}
          >
            <Box
              component="div"
              sx={{
                marginTop: {
                  xs: "4%",
                  tablet: "1.5%",
                  md: "2.5%",
                },
                marginRight: {
                  tablet: "55%",
                  md: "50%",
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
                  md: "inline-block",
                },
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
                lineHeight: { xs: "42px", md: "88px" },
                marginLeft: {
                  xs: "0px",
                  md: "96px",
                },
                textAlign: {
                  xs: "center",
                  sm: "left",
                },
                "-webkit-justify-content": {
                  xs: "center",
                  sm: "left",
                },
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
              fontSize: { xs: "40px", md: "70px", lg: "88px" },
              fontStyle: "normal",
              fontWeight: "700",
              lineHeight: { xs: "42px", md: "88px" },
              marginLeft: {
                xs: "0",
                tablet: "0",
              },
              whiteSpace: "nowrap",
              textAlign: {
                xs: "center",
                sm: "left",
              },
            }}
          >
            2024 Awards
          </Typography>

          <Box
            component="div"
            sx={{
              display: "flex",
              justifyContent: {
                xs: "center",
                sm: "left",
              },
              marginTop: "20px",
            }}
          >
            <Box
              component="div"
              sx={{
                marginBottom: "28px",
                width: "80px",
                height: "16px",
                flexShrink: 0,
                borderRadius: "8px",
                background:
                  "linear-gradient(258deg, #EE9766 0%, #40407D 87.58%, #0C7BC5 249.97%)",
                display: {
                  sx: "inline-block",
                  md: "none",
                },
              }}
            />
          </Box>
          <Box
            component="div"
            sx={{
              display: "flex",
              flexDirection: {
                xs: "column",
                sm: "row",
              },
              width: "100%",
              justifyContent: {
                xs: "center",
                sm: "flex-start",
              },
              alignItems: "center",
            }}
          >
            {showVotingButtons ? ( // If the event is running or has not started running yet
              <>
                <CustomButton
                  onClick={() => handleClickMenu(ROUTES.CATEGORIES)}
                  sx={{
                    width: {
                      xs: "90%",
                      sm: "187px",
                    },
                    margin: "10px 0",
                    marginRight: {
                      sm: "12px",
                    },
                  }}
                  colorVariant="primary"
                >
                  {eventCache.notStarted
                    ? "Preview Categories"
                    : "Start Voting"}
                </CustomButton>
                <CustomButton
                  onClick={() => handleClickMenu(ROUTES.USER_GUIDE)}
                  colorVariant="secondary"
                  sx={{
                    width: {
                      xs: "90%",
                      sm: "144px",
                    },
                    margin: "10px 0",
                  }}
                >
                  How to Vote
                </CustomButton>
              </>
            ) : (
              // If the event has already finished
              <CustomButton
                onClick={() => handleClickMenu(ROUTES.LEADERBOARD)}
                sx={{
                  width: { xs: "90%", sm: "172px" },
                  margin: "10px 0",
                }}
                colorVariant="primary"
              >
                {eventCache.proposalsReveal ? "See Results" : "See Leaderboard"}
              </CustomButton>
            )}
          </Box>
        </Grid>
        <Grid item xs={12} sm={7} tablet={7} md={6} lg={8}>
          <Box
            component="div"
            sx={{
              height: "100%",
              backgroundImage: {
                xs: "",
                sm: `url(${Shapes})`,
              },
              backgroundSize: {
                xs: "contain",
              },
              backgroundRepeat: "no-repeat",
              backgroundPosition: {
                xs: "left",
                sm: "center",
                tablet: "center",
                md: "center",
                lg: "right",
              },
              display: "flex",
              justifyContent: "center",
              alignItems: "center",
              marginTop: isPortrait ? "" : "12px",
              marginLeft: isPortrait ? "24px" : "90px",
              paddingLeft: { lg: 30 },
            }}
          >
            <Fade in={true} timeout={3000}>
              <Box component="div">
                <GLBViewer
                  glbUrl="/compressed.glb"
                  height={getAwardHeight()}
                  width="auto"
                />
              </Box>
            </Fade>
          </Box>
        </Grid>
      </Grid>
    </>
  );
};

export { Hero };

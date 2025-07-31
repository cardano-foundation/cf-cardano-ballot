import GLBViewer from "../../../components/GLBViewer/GLBViewer";
import {Box, Button, Fade, Grid, Stack, Typography, useMediaQuery} from "@mui/material";
import theme from "../../../common/styles/theme";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "../../../routes";
import {calculateTotalVotes, formatISODate} from "../../../utils/utils";
import { useAppSelector } from "../../../store/hooks";
import { getEventCache } from "../../../store/reducers/eventCache";
import ArrowForwardIcon from '@mui/icons-material/ArrowForward';
import berlinBg from "@assets/berlin-bg.jpg";
import bubblesBg from "@assets/bubbles-bg.jpg";
import cflogoBg from "@assets/cf-logo-bg.svg";
import categoriesBg from "@assets/categories-bg.jpg";
import ticketsBg from "@assets/tickets-bg.jpg";
import votesBg from "@assets/votes-bg.jpg";
import summitLogo from "@assets/summit-logo.svg";
import ticketsBlurredText from "@assets/tickets-blurred-text.png";
import ticketsBlurredVerticalText from "@assets/tickets-blurred-text-vertical.png";
import {useEffect, useState} from "react";
import {ByCategoryStats} from "../../../types/voting-app-types";
import {getStats} from "../../../common/api/leaderboardService";
import {useMatomo} from "@datapunt/matomo-tracker-react";

const Hero = () => {
  const { trackEvent } = useMatomo();
  const isMobile = useMediaQuery(theme.breakpoints.down("xs"));
  const isTablet = useMediaQuery(theme.breakpoints.down("md"));
  const isDesktop = useMediaQuery(theme.breakpoints.up("tablet"));
  const eventCache = useAppSelector(getEventCache);
  const navigate = useNavigate();
  const handleClickMenu = (option: string) => {
    navigate(option);
  };

  const [stats, setStats] = useState<ByCategoryStats[]>();
  const totalVotes = calculateTotalVotes(stats);

  useEffect(() => {
    getStats().then((response) => {
      // @ts-ignore
      setStats(response.categories);
    });
  }, []);

  const getVotingText = (): string => {
    if (eventCache.notStarted) {
      return "Voting Opens " + formatISODate(eventCache.eventStartDate);
    }
    if (eventCache.active) {
      return "Voting Closes " + formatISODate(eventCache.eventEndDate);
    }
    if (eventCache.proposalsReveal) {
      return "THE RESULTS ARE IN!!!";
    }
    return "Results Announced " + formatISODate(eventCache.proposalsRevealDate);
  };

  const getHeroButtonText = (): string => {
    if (eventCache.notStarted) {
      return "Preview Categories";
    }
    if (eventCache.active) {
      return "Start Voting";
    }
    if (eventCache.proposalsReveal) {
      return "View Results";
    }
    return "View Leaderboard";
  }

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

  const getTicketsClick = () => {
    window.open("https://summit.cardano.org/", "_blank");
    trackEvent({
      category: "open-buy-summit-tickets",
      action: "click-event",
    });
  };

  return (
    <>
      <Grid
        container
        spacing={{xs: 2, sm: 3}}
        sx={{
          marginTop: {xs: "24px", sm: "24px"},
          px: "20px",
        }}
      >
        <Grid item xs={12} md={7}>
          <Box
            component="div"
            sx={{
              display: "flex",
              position: "relative",
              overflow: "hidden",
              flexDirection: "column",
              height: "100%",
              borderRadius: "24px",
              padding: {xs: "20px", sm: "84px 100px 84px 44px"},
              backgroundColor: theme.palette.background.neutralDark,
            }}
          >
            <Box
              component="div"
              sx={{
                height: "100%",
                display: "flex",
                position: "absolute",
                bottom: "-50px",
                right: "-50px",
                justifyContent: "center",
                alignItems: "center",
              }}
            >
              <Fade in={true} timeout={3000}>
                <Box component="div">
                  <GLBViewer
                    glbUrl="/award24-3d.glb"
                    height={getAwardHeight()}
                    width="auto"
                  />
                </Box>
              </Fade>
            </Box>
            <Typography
              variant="h1"
              sx={{
                color: theme.palette.text.primary,
                fontFamily: "Tomorrow",
                fontSize: { xs: "44px", tablet: "80px" },
                fontStyle: "normal",
                fontWeight: "500",
                lineHeight: { xs: "44px", tablet: "80px" },
                zIndex: 10,
              }}
            >
              Vote for the Cardano Summit 2025 Awards
            </Typography>
            <Typography
              variant="h4"
              sx={{
                color: theme.palette.text.secondary,
                fontSize: { xs: "16px", tablet: "20px" },
                fontStyle: "normal",
                whiteSpace: {xs: "normal", sm: "nowrap"},
                fontWeight: "600",
                lineHeight: { xs: "24px", tablet: "28px" },
                marginTop: "12px",
                zIndex: 10,
              }}
            >
              {getVotingText()}
            </Typography>
            <Box
              component="div"
              sx={{
                display: "flex",
                paddingTop: "44px",
                zIndex: 10,
              }}
            >
              <Button
                sx={{
                  flexGrow: 0,
                  color: theme.palette.text.primary,
                  fontSize: "16px",
                  fontStyle: "normal",
                  fontWeight: 600,
                  lineHeight: "20px",
                  cursor: "pointer",
                  padding: "12px 12px 12px 16px",
                  textTransform: "none",
                  border: `1px solid ${theme.palette.text.primary}`,
                  borderRadius: "8px",
                  "&:hover": {
                    backgroundColor: theme.palette.text.primary,
                    color: theme.palette.background.default,
                  },
                }}
                onClick={() => getHeroButtonText() === 'View Leaderboard' ? handleClickMenu(ROUTES.LEADERBOARD) : handleClickMenu(ROUTES.CATEGORIES)}
              >
                {getHeroButtonText()}
                <ArrowForwardIcon
                  sx={{
                    width: "20px",
                    height: "20px",
                    marginLeft: "8px",
                  }}
                />
              </Button>
            </Box>
          </Box>
        </Grid>
        <Grid item xs={12} md={5}>
          <Stack spacing={{xs: 2, sm: 3}} sx={{ height: "100%" }}>
            <Box
              component="div"
              sx={{
                position: "relative",
                display: "flex",
                flexDirection: "column",
                justifyContent: "center",
                alignItems: "center",
                height: "100%",
                borderRadius: "24px",
                backgroundImage: `url(${berlinBg})`,
                backgroundSize: "cover",
                backgroundPosition: "center 72%",
                overflow: "hidden",
                padding: {xs: "20px", sm: "120px 32px"}
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
                  backgroundColor: "rgba(0,0,0,0.2)",
                  zIndex: 1,
                }}
              />
              <Typography
                variant="h4"
                sx={{
                  color: "#FF6444",
                  fontFamily: "Tomorrow",
                  fontSize: "56px",
                  fontStyle: "normal",
                  fontWeight: "900",
                  lineHeight: "56px",
                  zIndex: 2,
                }}
              >
                BERLIN
              </Typography>
              <Typography
                variant="h5"
                sx={{
                  color: theme.palette.text.secondary,
                  fontSize: {xs: "16px", md: "20px"},
                  fontStyle: "normal",
                  fontWeight: "700",
                  lineHeight: {xs: "24px", md: "28px"},
                  zIndex: 2,
                }}
              >
                12-13 NOVEMBER 2025
              </Typography>
            </Box>
            <Grid container sx={{display: { xs: "none", md: "flex"}}}>
              <Grid item md={6}>
                <Box
                  component="div"
                  sx={{
                    display: "flex",
                    height: "240px",
                    borderRadius: "24px",
                    backgroundImage: `url(${bubblesBg})`,
                    backgroundSize: "cover",
                    backgroundPosition: "center 72%",
                  }}
                >
                </Box>
              </Grid>
              <Grid item md={6} sx={{paddingLeft: "24px"}}>
                <Box
                  component="div"
                  sx={{
                    display: "flex",
                    height: "240px",
                    borderRadius: "24px",
                    backgroundImage: `url(${cflogoBg})`,
                    backgroundSize: "cover",
                    backgroundPosition: "center 72%",
                  }}
                >
                </Box>
              </Grid>
            </Grid>
          </Stack>
        </Grid>
      </Grid>

      <Box
        component="div"
        sx={{
          display: "flex",
          flexDirection: "column",
          px: "20px",
          mt: {xs: "44px", sm: "80px"},
        }}
      >
        <Typography
          variant="h4"
          sx={{
            color: theme.palette.text.secondary,
            fontSize: {xs: "16px", sm: "20px"},
            fontStyle: "normal",
            fontWeight: "600",
            lineHeight: {xs: "24px", sm: "28px"},
            mb: "12px",
          }}
        >
          Your Voice. Your Impact.
        </Typography>
        <Typography
          variant="h2"
          sx={{
            color: theme.palette.text.primary,
            fontFamily: "Tomorrow",
            fontSize: {xs: "32px", sm: "44px"},
            fontStyle: "normal",
            fontWeight: "500",
            lineHeight: {xs: "32px", sm: "44px"},
            maxWidth: "702px",
            mb: {xs: "24px", sm: "40px"}
          }}
        >
          An Overview of This Year’s Ballot
        </Typography>
      </Box>

      <Grid
        container
        spacing={{xs: 2, sm: 3, md: 5}}
        sx={{
          marginTop: "4px",
          px: "20px",
        }}
      >
        <Grid item xs={12} md={6}>
          <Box
            component="div"
            sx={{
              position: "relative",
              display: "flex",
              flexDirection: "column",
              height: {xs: "300px", sm: "380px"},
              borderRadius: "24px",
              padding: {xs: "20px", sm: "40px 120px 40px 40px"},
              backgroundImage: `url(${categoriesBg})`,
              backgroundSize: "cover",
              backgroundPosition: "center 72%",
              overflow: "hidden",
            }}
          >
            <Typography
              variant="h5"
              sx={{
                color: theme.palette.text.secondary,
                fontSize: { xs: "16px", sm: "20px" },
                fontStyle: "normal",
                fontWeight: "600",
                lineHeight: {xs: "24px", sm: "28px"},
                mb: "12px",
                zIndex: 2,
              }}
            >
              What’s on the Ballot
            </Typography>
            <Typography
              variant="h4"
              sx={{
                color: "#fff",
                fontFamily: "Tomorrow",
                fontSize: {xs: "32px", sm: "44px"},
                fontStyle: "normal",
                fontWeight: "500",
                lineHeight: {xs: "32px", sm: "44px"},
                zIndex: 2,
              }}
            >
              Explore the Key Categories Shaping Cardano’s Future.
            </Typography>
            <Box
              component="div"
              sx={{
                display: "flex",
                paddingTop: "24px",
                zIndex: 2,
              }}
            >
              <Button
                sx={{
                  flexGrow: 0,
                  color: theme.palette.text.primary,
                  fontSize: "16px",
                  fontStyle: "normal",
                  fontWeight: 600,
                  lineHeight: "20px",
                  cursor: "pointer",
                  padding: "12px 12px 12px 16px",
                  textTransform: "none",
                  border: `1px solid ${theme.palette.text.primary}`,
                  borderRadius: "8px",
                  "&:hover": {
                    backgroundColor: theme.palette.text.primary,
                    color: theme.palette.background.default,
                  },
                }}
                onClick={() => handleClickMenu(ROUTES.CATEGORIES)}
              >
                View Categories
                <ArrowForwardIcon
                  sx={{
                    width: "20px",
                    height: "20px",
                    marginLeft: "8px",
                  }}
                />
              </Button>
            </Box>
            <Box
              component="div"
              sx={{
                position: "absolute",
                bottom: "20px",
                right: "40px",
                zIndex: 2,
              }}
            >
              <Typography
                variant="h5"
                sx={{
                  display: "flex",
                  color: theme.palette.text.secondary,
                  fontSize: "14px",
                  fontStyle: "normal",
                  fontWeight: "600",
                  lineHeight: "20px",
                }}
              >
                Need help?
                <Typography
                  variant="h5"
                  sx={{
                    color: theme.palette.text.secondary,
                    fontSize: "14px",
                    fontStyle: "normal",
                    fontWeight: "600",
                    lineHeight: "20px",
                    ml: "4px",
                    textDecoration: "underline",
                    cursor: "pointer",
                  }}
                  onClick={() => handleClickMenu(ROUTES.CATEGORIES)}
                >
                  Voting User Guide
                </Typography>
              </Typography>
            </Box>
            <Box
              component="div"
              sx={{
                position: "absolute",
                inset: 0,
                width: "100%",
                height: "100%",
                backgroundColor: "rgba(5, 12, 37, 0.7)",
                backdropFilter: "blur(4px)",
                zIndex: 1,
              }}
            />
          </Box>
        </Grid>
        <Grid item xs={12} md={6}>
          <Box
            component="div"
            sx={{
              position: "relative",
              display: "flex",
              flexDirection: "column",
              height: {xs: "300px", sm: "380px"},
              borderRadius: "24px",
              padding: {xs: "20px", sm: "40px"},
              backgroundImage: `url(${votesBg})`,
              backgroundSize: "cover",
              backgroundPosition: "center 72%",
              overflow: "hidden",
            }}
          >
            <Typography
              variant="h5"
              sx={{
                color: theme.palette.text.secondary,
                fontSize: {xs: "16px", sm: "20px"},
                fontStyle: "normal",
                fontWeight: "600",
                lineHeight: {xs:"24px", sm: "28px"},
                mb: "12px",
                zIndex: 2,
              }}
            >
              Total Votes So Far
            </Typography>
            <Typography
              variant="h4"
              sx={{
                color: "#fff",
                fontFamily: "Tomorrow",
                fontSize: {xs: "32px", sm: "80px"},
                fontStyle: "normal",
                fontWeight: "500",
                lineHeight: {xs: "32px", sm: "132px"},
                zIndex: 2,
              }}
            >
              {eventCache.notStarted ? "N/A" : totalVotes}
            </Typography>
            <Box
              component="div"
              sx={{
                display: "flex",
                paddingTop: "24px",
                zIndex: 2,
              }}
            >
              <Button
                sx={{
                  flexGrow: 0,
                  color: theme.palette.text.primary,
                  fontSize: "16px",
                  fontStyle: "normal",
                  fontWeight: 600,
                  lineHeight: "20px",
                  cursor: "pointer",
                  padding: "12px 12px 12px 16px",
                  textTransform: "none",
                  border: `1px solid ${theme.palette.text.primary}`,
                  borderRadius: "8px",
                  "&:hover": {
                    backgroundColor: theme.palette.text.primary,
                    color: theme.palette.background.default,
                  },
                }}
                onClick={() => handleClickMenu(ROUTES.LEADERBOARD)}
              >
                View Leaderboard
                <ArrowForwardIcon
                  sx={{
                    width: "20px",
                    height: "20px",
                    marginLeft: "8px",
                  }}
                />
              </Button>
            </Box>
            <Box
              component="div"
              sx={{
                position: "absolute",
                bottom: "20px",
                right: "40px",
                zIndex: 2,
              }}
            >
              <Typography
                variant="h5"
                sx={{
                  display: "flex",
                  color: theme.palette.text.secondary,
                  fontSize: "14px",
                  fontStyle: "normal",
                  fontWeight: "600",
                  lineHeight: "20px",
                }}
              >
                Need help?
                <Typography
                  variant="h5"
                  sx={{
                    color: theme.palette.text.secondary,
                    fontSize: "14px",
                    fontStyle: "normal",
                    fontWeight: "600",
                    lineHeight: "20px",
                    ml: "4px",
                    textDecoration: "underline",
                    cursor: "pointer",
                  }}
                  onClick={() => handleClickMenu(ROUTES.CATEGORIES)}
                >
                  Voting User Guide
                </Typography>
              </Typography>
            </Box>
            <Box
              component="div"
              sx={{
                position: "absolute",
                inset: 0,
                width: "100%",
                height: "100%",
                backgroundColor: "rgba(5, 12, 37, 0.7)",
                backdropFilter: "blur(4px)",
                zIndex: 1,
              }}
            />
          </Box>
        </Grid>
      </Grid>
      <Box
        component="div"
        sx={{
          mt: {xs: "44px", sm: "80px"},
          px: "20px",
        }}
      >
        <Box
          component="div"
          sx={{
            position: "relative",
            display: "flex",
            flexDirection: "column",
            justifyContent: "space-between",
            padding: {xs: "20px", sm: "40px"},
            height: {xs: "785px", md: "480px"},
            borderRadius: "24px",
            backgroundImage: `url(${ticketsBg})`,
            backgroundSize: "cover",
            backgroundPosition: "center 32%",
            overflow: "hidden",
          }}
        >
        <Box
            component="div"
            sx={{
              display: "flex",
              flexDirection: { xs: "column", md: "row" },
              justifyContent: "space-between",
              alignItems: "center",
              zIndex: 2,
            }}
          >
            <img src={summitLogo} alt="Cardano Summit Logo" style={{ height: "64px" }} />
            <Box
              component="div"
              sx={{
                backgroundColor: theme.palette.background.default,
                border: `0.87px solid ${theme.palette.text.primary}`,
                borderRadius: "8px",
                padding: {xs:"16px 32px", sm: "16px 24px"},
                display: "flex",
                flexDirection: { xs: "column", sm: "row" },
                alignItems: "center",
                mt: { xs: "24px", sm: "40px", md: "0px" },
              }}
            >
              <Typography
                variant="h5"
                sx={{
                  fontFamily: "Tomorrow",
                  color: theme.palette.text.primary,
                  fontSize: "32px",
                  fontStyle: "normal",
                  fontWeight: "900",
                  lineHeight: "32px",
                  mr: {xs:"0px", sm: "20px"},
                }}
              >
                BERLIN
              </Typography>
              <Typography
                variant="h5"
                sx={{
                  color: theme.palette.text.secondary,
                  fontSize: "16px",
                  fontStyle: "normal",
                  fontWeight: "600",
                  lineHeight: "32px",
                }}
              >
                12-13 November 2025
              </Typography>
            </Box>
          </Box>
          <Box
            component="div"
            sx={{
              display: "flex",
              justifyContent: "space-between",
              flexDirection: { xs: "column", md: "row" },
              alignItems: { xs: "center", md: "end" },
              zIndex: 2,
            }}
          >
            {isDesktop && <img src={ticketsBlurredText} alt="Where Blockchain Meets Enterprise" style={{ height: "128px", pointerEvents: "none" }} />}
            {!isDesktop && <img src={ticketsBlurredVerticalText} alt="Where Blockchain Meets Enterprise" style={{ height: "256px", pointerEvents: "none" }} />}
            <Box
              component="div"
              sx={{
                display: "flex",
                paddingTop: "24px",
                zIndex: 2,
              }}
            >
              <Button
                sx={{
                  flexGrow: 0,
                  color: theme.palette.text.primary,
                  fontSize: "16px",
                  fontStyle: "normal",
                  fontWeight: 600,
                  lineHeight: "20px",
                  cursor: "pointer",
                  padding: "12px 12px 12px 16px",
                  textTransform: "none",
                  border: `1px solid ${theme.palette.text.primary}`,
                  borderRadius: "8px",
                  "&:hover": {
                    backgroundColor: theme.palette.text.primary,
                    color: theme.palette.background.default,
                  },
                }}
                onClick={() => getTicketsClick()}
              >
                Get Tickets Now
                <ArrowForwardIcon
                  sx={{
                    width: "20px",
                    height: "20px",
                    marginLeft: "8px",
                  }}
                />
              </Button>
            </Box>
          </Box>
          <Box
            component="div"
            sx={{
              position: "absolute",
              inset: 0,
              width: "100%",
              height: "100%",
              backgroundColor: "rgba(5, 12, 37, 0.5)",
              zIndex: 1,
            }}
          />
        </Box>
      </Box>
    </>
  );
};

export { Hero };

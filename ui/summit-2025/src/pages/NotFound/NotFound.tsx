import React from "react";
import {Box, Button, Grid, Typography} from "@mui/material";
import { PageBase } from "../BasePage";
import theme from "../../common/styles/theme";
import cardanoLogo from "@assets/cardano-logo.svg";
import {ROUTES} from "../../routes";
import {useAppSelector} from "../../store/hooks";
import {getEventCache} from "../../store/reducers/eventCache";
import {useNavigate} from "react-router-dom";

const NotFound: React.FC = () => {
  const eventCache = useAppSelector(getEventCache);
  const navigate = useNavigate();

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

  const handleClickMenu = (option: string) => {
    navigate(option);
  };

  return (
    <PageBase title="NotFound">
      <Grid
        container
        spacing={{xs: 2, tablet: 3}}
        sx={{
          marginTop: {xs: "24px", tablet: "24px"},
          px: "20px",
        }}
      >
        <Grid item xs={3} sm={4}>
          <Box
            component="div"
            sx={{
              display: "flex",
              position: "relative",
              overflow: "hidden",
              justifyContent: "center",
              alignItems: "center",
              height: {xs:"156px", sm: "208px", md:"320px"},
              borderRadius: {xs: "12px", sm: "24px"},
              backgroundColor: "#FF6444",
            }}
          >
            <Typography
              variant="h6"
              sx={{
                fontFamily: "Tomorrow",
                color: theme.palette.background.default,
                fontSize: { xs: "88px", sm: "160px", md: "200px" },
                fontWeight: "500",
                lineHeight: { xs: "88px", sm: "160px", md: "28px", lg: "200px" },
              }}
            >
              4
            </Typography>
          </Box>
        </Grid>
        <Grid item xs={6} sm={4}>
          <Box
            component="div"
            sx={{
              display: "flex",
              position: "relative",
              overflow: "hidden",
              justifyContent: "center",
              alignItems: "center",
              height: {xs:"156px", sm: "208px", md:"320px"},
              borderRadius: {xs: "12px", sm: "24px"},
              backgroundColor: theme.palette.text.secondary,
            }}
          >
            <img src={cardanoLogo} alt="Cardano Logo" style={{ height: "168px" }} />
          </Box>
        </Grid>
        <Grid item xs={3} sm={4}>
          <Box
            component="div"
            sx={{
              display: "flex",
              position: "relative",
              overflow: "hidden",
              justifyContent: "center",
              alignItems: "center",
              height: {xs:"156px", sm: "208px", md:"320px"},
              borderRadius: {xs: "12px", sm: "24px"},
              backgroundColor: "#2867ED",
            }}
          >
            <Typography
              variant="h6"
              sx={{
                fontFamily: "Tomorrow",
                color: theme.palette.text.primary,
                fontSize: { xs: "88px", sm: "160px", md: "200px" },
                fontWeight: "500",
                lineHeight: { xs: "88px", sm: "160px", md: "28px", lg: "200px" },
              }}
            >
              4
            </Typography>
          </Box>
        </Grid>
        <Grid item xs={12}>
          <Box
            component="div"
            sx={{
              display: "flex",
              flexDirection: "column",
              position: "relative",
              overflow: "hidden",
              justifyContent: "center",
              alignItems: "center",
              borderRadius: {xs: "12px", sm: "24px"},
              padding: {xs: "20px", sm: "24px", md: "44px"},
              backgroundColor: theme.palette.background.neutralDark,
            }}
          >
            <Typography
              variant="h6"
              sx={{
                fontFamily: "Tomorrow",
                color: theme.palette.text.primary,
                fontSize: { xs: "40px", sm: "48px", md: "68px" },
                fontWeight: "900",
                lineHeight: { xs: "40px", sm: "48px", md: "68px" },
                marginBottom: {xs:"16px", sm: "24px", md: "44px"},
                textAlign: "center",
              }}
            >
              PAGE NOT FOUND
            </Typography>
            <Box
              component="div"
              sx={{
                display: "flex",
                position: "relative",
                overflow: "hidden",
                justifyContent: "center",
                alignItems: "center",
              }}
            >
              <Button
                sx={{
                  flexGrow: 0,
                  color: theme.palette.background.default,
                  backgroundColor: theme.palette.secondary.main,
                  marginRight: "12px",
                  fontSize: "16px",
                  fontStyle: "normal",
                  fontWeight: 600,
                  lineHeight: "20px",
                  cursor: "pointer",
                  padding: "12px 16px",
                  textTransform: "none",
                  borderRadius: "8px",
                  border: `1px solid ${theme.palette.secondary.main}`,
                  width: "144px",
                  "&:hover": {
                    backgroundColor: "#ff9277",
                    color: theme.palette.background.default,
                    border: `1px solid #ff9277`,
                  },
                }}
                onClick={() => getHeroButtonText() === 'View Leaderboard' ? handleClickMenu(ROUTES.LEADERBOARD) : handleClickMenu(ROUTES.CATEGORIES)}
              >
                {getHeroButtonText()}
              </Button>
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
                onClick={() =>handleClickMenu(ROUTES.LANDING)}
              >
                Back to Home
              </Button>
            </Box>
          </Box>
        </Grid>
      </Grid>
    </PageBase>
  );
};

export { NotFound };

import { Grid, Typography, Card, CardContent, Box } from "@mui/material";
import { keyframes } from "@mui/system";
import HowToVoteOutlinedIcon from "@mui/icons-material/HowToVoteOutlined";
import folderIcon from "../../../assets/folder.svg";
import trophyIcon from "../../../assets/trophy.svg";
import { useIsPortrait } from "../../../common/hooks/useIsPortrait";
import { CustomButton } from "../../../components/common/CustomButton/CustomButton";
import theme from "../../../common/styles/theme";
import guideBg from "@assets/guideCard.svg";
import { useNavigate } from "react-router-dom";
import { ROUTES } from "../../../routes";
import { useEffect, useState } from "react";
import { ByCategoryStats } from "../../../types/voting-app-types";
import { getStats } from "../../../common/api/leaderboardService";
import { calculateTotalVotes } from "../../../utils/utils";
import { useAppSelector } from "../../../store/hooks";
import { getEventCache } from "../../../store/reducers/eventCache";

const ExploreSection = () => {
  const isMobile = useIsPortrait();
  const navigate = useNavigate();

  const handleClickMenu = (option: string) => {
    navigate(option);
  };

  const eventCache = useAppSelector(getEventCache);
  const [stats, setStats] = useState<ByCategoryStats[]>();
  const totalVotes = calculateTotalVotes(stats);

  useEffect(() => {
    getStats().then((response) => {
      // @ts-ignore
      setStats(response.categories);
    });
  }, []);

  const marquee = keyframes`
      from {
        transform: translateX(0%);
      }
      to {
        transform: translateX(-100%);
      }
    `;

  const categoriesNames = eventCache.categories.map((c) => c.name).join(", ");

  return (
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
        md={4}
        sx={{
          display: "flex",
          flexDirection: "column",
          alignItems: isMobile ? "center" : "flex-start",
          justifyContent: "center",
        }}
      >
        <Box
          component="div"
          sx={{
            display: {
              xs: "block",
              sm: "none",
            },
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
            Year’s Award Summit!
          </Typography>
        </Box>
        <Box
          component="div"
          sx={{
            display: {
              xs: "none",
              sm: "block",
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
                xs: "center",
                md: "left",
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
                xs: "center",
                md: "left",
              },
              whiteSpace: { sm: "nowrap", md: "normal" },
              mb: 2,
            }}
          >
            Award Summit!
          </Typography>
        </Box>
        <Box
          component="div"
          sx={{
            width: "100%",
            textAlign: {
              xs: "center",
              md: "left",
            },
          }}
        >
          <CustomButton
            onClick={() => handleClickMenu(ROUTES.USER_GUIDE)}
            sx={{ marginTop: "28px", marginBottom: "32px" }}
            colorVariant="secondary"
            startIcon={<HowToVoteOutlinedIcon />}
          >
            User Guide
          </CustomButton>
        </Box>
      </Grid>
      <Grid item xs={12} sm={6} md={4}>
        <Card
          onClick={() => handleClickMenu(ROUTES.CATEGORIES)}
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
            backgroundImage: `url(${guideBg})`,
            backgroundSize: "180% 160%",
            backgroundPosition: "center",
            cursor: "pointer"
          }}
        >
          <CardContent
            sx={{
              position: "relative",
              zIndex: 2,
              display: "flex",
              flexDirection: "column",
              justifyContent: "space-between",
              height: "100%",
            }}
          >
            <Box
              component="div"
              sx={{
                display: "flex",
                alignItems: "center",
                marginTop: "30px",
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
            <Box
              component="div"
              sx={{
                width: "100%",
                height: "80px",
                position: "relative",
                overflow: "hidden",
                marginBottom: "20px",
              }}
            >
              <Typography
                sx={{
                  color: theme.palette.text.neutralLightest,
                  fontSize: {
                    xs: "40px",
                    md: "68px",
                  },
                  fontStyle: "normal",
                  fontWeight: 500,
                  lineHeight: "86px",
                  whiteSpace: "nowrap",
                  display: "inline-block",
                  position: "absolute",
                  minWidth: "200%",
                  animation: `${marquee} 20s linear infinite`,
                }}
              >
                {categoriesNames}
              </Typography>
            </Box>
          </CardContent>
        </Card>
      </Grid>
      <Grid item xs={12} sm={6} md={4}>
        <Card
          onClick={() => handleClickMenu(ROUTES.LEADERBOARD)}
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
            backgroundImage: `url(${guideBg})`,
            backgroundSize: "180% 160%",
            backgroundPosition: "center",
            cursor: "pointer"
          }}
        >
          <CardContent sx={{ position: "relative", zIndex: 2 }}>
            <Box
              component="div"
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
              {eventCache.notStarted ? "N/A" : totalVotes}
            </Typography>
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  );
};

export { ExploreSection };

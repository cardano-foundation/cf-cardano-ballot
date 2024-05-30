import React, { useRef, useState } from "react";
import { Box, Grid, Typography, List, ListItem } from "@mui/material";
import theme from "../../common/styles/theme";
import Ellipses from "../../assets/ellipse.svg";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";
import { UserGuideCard } from "./components/UserGuideCard";
import { userGuideMenu } from "../../__fixtures__/userGuide";
import { CustomButton } from "../../components/common/CustomButton/CustomButton";
import { ROUTES } from "../../routes";
import { useNavigate } from "react-router-dom";

const UserGuide: React.FC = () => {
  const navigate = useNavigate();
  const userGuideMenuOptions = userGuideMenu;
  const [selectedCategory, setSelectedCategory] = useState(
    userGuideMenuOptions[0].label,
  );

  const isMobile = useIsPortrait();

  const createAccountsRef = useRef<HTMLElement>(null);
  const submitVotesRef = useRef<HTMLElement>(null);
  const howVoteRef = useRef<HTMLElement>(null);

  const handleNavigate = (pathname: string) => {
    navigate(pathname);
  };

  const handleClickMenuItem = (option: string) => {
    setSelectedCategory(option);
    if (option === userGuideMenu[0].label) {
      submitVotesRef.current?.scrollIntoView({
        behavior: "smooth",
        block: "start",
      });
    } else if (option === userGuideMenu[1].label) {
      createAccountsRef.current?.scrollIntoView({
        behavior: "smooth",
        block: "start",
      });
    } else if (option === userGuideMenu[2].label) {
      howVoteRef.current?.scrollIntoView({
        behavior: "smooth",
        block: "start",
      });
    }
  };

  return (
    <>
      <Box
        sx={{
          height: "28px",
        }}
      />
      <Box sx={{ width: "100%" }}>
        <Grid container>
          <Grid item xs={12} md={2.4} lg={2}>
            {isMobile ? (
              <>
                <Box
                  sx={{
                    overflowX: "auto",
                    width: "100%",
                    maxWidth: "100vw",
                    "&::-webkit-scrollbar": {
                      display: "none",
                    },
                    scrollbarWidth: "none",
                    msOverflowStyle: "none",
                    marginTop: "14px",
                  }}
                >
                  <List
                    sx={{
                      display: "flex",
                      flexDirection: "row",
                      padding: 0,
                      margin: 0,
                    }}
                  >
                    {userGuideMenuOptions.map((category, index) => (
                      <ListItem
                        onClick={() => handleClickMenuItem(category.label)}
                        key={index}
                        sx={{
                          display: "flex",
                          marginRight: "8px",
                          whiteSpace: "nowrap",
                        }}
                      >
                        <Typography
                          sx={{
                            color:
                              category.label === selectedCategory
                                ? theme.palette.background.default
                                : theme.palette.text.neutralLightest,
                            background:
                              category.label === selectedCategory
                                ? theme.palette.secondary.main
                                : "none",
                            padding: "8px 12px",
                            borderRadius: "12px",
                            fontSize: "16px",
                            fontWeight: 500,
                            lineHeight: "24px",
                            cursor: "pointer",
                          }}
                        >
                          {category.label}
                        </Typography>
                      </ListItem>
                    ))}
                  </List>
                </Box>
              </>
            ) : (
              <>
                <List
                  sx={{
                    position: "sticky",
                    top: 74,
                    zIndex: 1100,
                    overflowY: "auto",
                    maxHeight: "calc(100vh - 74px)",
                    borderRight: "1px solid #737380",
                  }}
                >
                  {userGuideMenuOptions.map((option, index) => (
                    <ListItem
                      onClick={() => handleClickMenuItem(option.label)}
                      key={index}
                    >
                      {option.label === selectedCategory ? (
                        <>
                          <Box
                            sx={{
                              display: "flex",
                              padding: "8px 12px",
                              alignItems: "center",
                              gap: "10px",
                              alignSelf: "stretch",
                              borderRadius: "12px",
                              background: theme.palette.secondary.main,
                              color: theme.palette.background.default,
                              fontSize: "16px",
                              fontStyle: "normal",
                              fontWeight: 500,
                              lineHeight: "24px",
                              cursor: "pointer",
                              width: "100%",
                            }}
                          >
                            <Typography
                              sx={{
                                gap: "10px",
                                alignSelf: "stretch",
                                borderRadius: "12px",
                                fontSize: "16px",
                                fontStyle: "normal",
                                fontWeight: 500,
                                lineHeight: "24px",
                                cursor: "pointer",
                                width: "100%",
                              }}
                            >
                              {option.label}
                            </Typography>
                          </Box>
                        </>
                      ) : (
                        <>
                          <Typography
                            sx={{
                              color: theme.palette.text.neutralLightest,
                              fontSize: "16px",
                              fontStyle: "normal",
                              fontWeight: 500,
                              lineHeight: "24px",
                              cursor: "pointer",
                            }}
                          >
                            {option.label}
                          </Typography>
                        </>
                      )}
                    </ListItem>
                  ))}
                </List>
              </>
            )}
          </Grid>
          <Grid
            item
            xs={12}
            md={9.6}
            lg={10}
            sx={{
              background: "transparent",
              padding: {
                xs: "0px 20px",
                sm: "20px 0pxs",
              },
            }}
          >
            <Typography
              sx={{
                color: theme.palette.text.neutralLightest,
                fontFamily: "Dosis",
                fontSize: "32px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "36px",
                marginBottom: "32px",
              }}
            >
              User Guide
            </Typography>
            <Typography
              ref={submitVotesRef}
              sx={{
                marginTop: "24px",
                color: theme.palette.text.neutralLightest,
                fontFamily: "Dosis",
                fontSize: "24px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "28px",
                marginBottom: "16px",
              }}
            >
              To Submit Votes, You’ll Need:
            </Typography>
            <Grid container spacing={2}>
              {userGuideMenuOptions[0].sections.map((section) => {
                return (
                  <Grid item xs={12} sm={6}>
                    <UserGuideCard
                      number={section.number}
                      title={section.title}
                      description={section.description}
                      link={section.link}
                    />
                  </Grid>
                );
              })}
            </Grid>
            <Typography
              ref={createAccountsRef}
              sx={{
                marginTop: "24px",
                color: theme.palette.text.neutralLightest,
                fontFamily: "Dosis",
                fontSize: "24px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "28px",
                marginBottom: "16px",
              }}
            >
              Create and Verify Your Account:
            </Typography>
            <Grid container spacing={2}>
              {userGuideMenuOptions[1].sections.map((section) => {
                return (
                  <Grid item xs={12} sm={6}>
                    <UserGuideCard
                      number={section.number}
                      title={section.title}
                      description={section.description}
                      link={section.link}
                    />
                  </Grid>
                );
              })}
            </Grid>

            <Typography
              ref={howVoteRef}
              sx={{
                marginTop: "24px",
                color: theme.palette.text.neutralLightest,
                fontFamily: "Dosis",
                fontSize: "24px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "28px",
                marginBottom: "16px",
              }}
            >
              How to Submit a Vote:
            </Typography>
            <Grid container spacing={2}>
              {userGuideMenuOptions[2].sections.map((section) => {
                return (
                  <Grid item xs={12} sm={6} md={4}>
                    <UserGuideCard
                      number={section.number}
                      title={section.title}
                      description={section.description}
                      link={section.link}
                    />
                  </Grid>
                );
              })}
            </Grid>
            <Grid
              container
              spacing={2}
              justifyContent="center"
              sx={{
                marginTop: "24px",
              }}
            >
              <Grid
                item
                sx={{
                  width: {
                    xs: "100%",
                    md: "auto",
                  },
                }}
              >
                <CustomButton
                  onClick={() => handleNavigate(ROUTES.CATEGORIES)}
                  colorVariant="primary"
                  sx={{
                    width: {
                      xs: "100%",
                      md: "auto",
                    },
                  }}
                >
                  Vote Now
                </CustomButton>
              </Grid>
            </Grid>
          </Grid>
        </Grid>
        <img
          src={Ellipses}
          style={{
            position: "fixed",
            right: "0",
            top: "70%",
            transform: "translateY(-25%)",
            zIndex: "-1",
            width: "70%",
          }}
        />
      </Box>
    </>
  );
};

export { UserGuide };

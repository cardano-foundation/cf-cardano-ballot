import React, { useRef, useState } from "react";
import { Box, Grid, Typography, List, ListItem } from "@mui/material";
import theme from "../../common/styles/theme";
import Ellipses from "../../assets/ellipse.svg";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";
import { UserGuideCard } from "./components/UserGuideCard";
import { userGuideMenu } from "../../__fixtures__/userGuide";

const UserGuide: React.FC = () => {
  const userGuideMenuOptions = userGuideMenu;
  const [selectedCategory, setSelectedCategory] = useState(
    userGuideMenuOptions[0].label,
  );

  const isMobile = useIsPortrait();

  const createAccountsRef = useRef<HTMLElement>(null);
  const submitVotesRef = useRef<HTMLElement>(null);
  const howVoteRef = useRef<HTMLElement>(null);

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
    <Box sx={{ width: "100%" }}>
      <Grid container>
        <Grid
          item
          xs={12}
          md={2.4}
          lg={2}
          sx={{
            position: isMobile ? "" : "sticky",
            top: 0,
            height: isMobile ? "" : "100%",
            overflow: "auto",
          }}
        >
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
              sm: "40px",
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
            <Grid item xs={12} sm={6}>
              <UserGuideCard
                number={1}
                title="The ability to receive an SMS verification message."
                description="Securely verify your account with a one-time SMS code for Cardano Ballot. Safety and simplicity combined."
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <UserGuideCard
                number={2}
                title="A supported Cardano wallet and/or browser extension"
                description="You don't need to have any funds in your wallet to use Cardano Ballot."
                link={{
                  label: "View a list of supported wallets",
                  url: "",
                }}
              />
            </Grid>
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
            <Grid item xs={12} sm={6}>
              <UserGuideCard
                number={1}
                title="Click on 'Connect Wallet' and choose a supported wallet from the list."
                description="By default, only Flint (Desktop/Mobile) and installed supported wallets will be shown."
              />
            </Grid>

            <Grid item xs={12} sm={6}>
              <UserGuideCard
                number={2}
                title="Verify your wallet using CIP8 message signing through SMS or Discord."
                description="Once you connect your wallet you will be prompted for verification, if you choose to skip this step until later you can access this again by clicking your wallet in the top right corner, or by trying to vote via the categories page.
Protect your Cardano Ballot account with seamless verification using CIP8 message signing via SMS or Discord."
              />
            </Grid>
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
            <Grid item xs={12} sm={6} md={4}>
              <UserGuideCard
                number={1}
                title="Navigate to the “Categories” section."
                description="You can do this by either clicking on the link in at the top of the page labelled “Categories” or by clicking on the “Start Voting” button on the home page and directly below this section!"
              />
            </Grid>
            <Grid item xs={12} sm={6} md={4}>
              <UserGuideCard
                number={2}
                title="Scroll through the voting categories."
                description="On the left-hand side, you will see the proposed voting categories. Simply click on the category you wish to vote on to see the corresponding list of nominees."
              />
            </Grid>

            <Grid item xs={12} sm={4} md={4}>
              <UserGuideCard
                number={3}
                title="Browse and choose your nominee"
                description="To see additional information about a nominee click on the “Learn More” button. To select the nominee you want to vote for, simply click on their listing to make the selection."
              />
            </Grid>
            <Grid item xs={12} sm={4} md={4}>
              <UserGuideCard
                number={4}
                title="Click “Vote Now” to cast your vote."
                description="Once you have made your selection you will be able to click on the “Vote Now” button in the top right above the nominees listings."
              />
            </Grid>
            <Grid item xs={12} sm={4} md={4}>
              <UserGuideCard
                number={5}
                title="Connect and/ or verify your wallet"
                description="If you haven’t yet connected or verified your wallet you will be prompted to do so at this point. Follow the instructions from your chosen wallet."
              />
            </Grid>
            <Grid item xs={12} sm={4} md={4}>
              <UserGuideCard
                number={6}
                title="Your vote is now submitted!"
                description="Once you follow the signing process from your wallet your vote will be submitted. To see the progress status of your vote click the “Vote Receipt” button in the top right above the nominees listings."
              />
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
          transform: "translateY(-40%)",
          zIndex: "-1",
          width: "70%",
        }}
      />
    </Box>
  );
};

export { UserGuide };

import React, { useRef, useState, useEffect, ReactNode } from "react";
import {
  Box,
  Grid,
  List,
  ListItem,
  Typography,
  useMediaQuery,
} from "@mui/material";
import theme from "../../common/styles/theme";
import {ROUTES} from "../../routes";

type MenuItem = {
  label: string;
  content: ReactNode;
};

type LayoutProps = {
  menuOptions: MenuItem[];
  title?: string;
  bottom?: ReactNode;
  mode?: "scroll" | "change";
  defaultOption?: number;
  onSelectMenuOption?: (option: string) => void;
};

const Layout: React.FC<LayoutProps> = ({
  menuOptions,
  title,
  bottom,
  mode = "scroll",
  defaultOption = 0,
  onSelectMenuOption,
}) => {
  const isMobile = useMediaQuery(theme.breakpoints.down("md"));
  const [selectedOption, setSelectedOption] = useState("");
  const [userInteracted, setUserInteracted] = useState(false);

  const optionRefs = useRef<{ [key: string]: React.RefObject<HTMLDivElement> }>(
    menuOptions.reduce(
      (acc, option) => {
        if (mode === "scroll") {
          acc[option.label] = React.createRef<HTMLDivElement>();
        }
        return acc;
      },
      {} as { [key: string]: React.RefObject<HTMLDivElement> },
    ),
  );

  useEffect(() => {
    setSelectedOption(menuOptions[defaultOption].label);
  }, [menuOptions.length]);

  useEffect(() => {
    if (
      userInteracted &&
      mode === "scroll" &&
      selectedOption &&
      optionRefs.current[selectedOption]
    ) {
      const element = optionRefs.current[selectedOption].current;
      if (element) {
        const topPos = element.getBoundingClientRect().top - 100;
        window.scrollTo({ top: topPos, behavior: "smooth" });
      }
    }
  }, [selectedOption, mode, userInteracted]);

  const handleClickMenuItem = (label: string) => {
    if (!userInteracted) setUserInteracted(true);
    setSelectedOption(label);
    if (onSelectMenuOption) onSelectMenuOption(label);
  };

  const isLeaderboardPage =
      window.location.pathname === ROUTES.LEADERBOARD && !isMobile;

  return (
    <Box component="div" sx={{ width: "100%",  marginTop: isLeaderboardPage ? "0px" : "60px",
      paddingX: isLeaderboardPage ? "0px" : "16px", }}>
      <Grid container>
        <Grid item xs={12} md={2.4} lg={2} sx={{}}>
          {isMobile ? (
            <>
              <Box
                  component="div"
                  sx={{
                    width: "100%",
                    maxWidth: "100vw",
                    overflowX: 'auto',
                    overflowY: 'hidden',
                    WebkitOverflowScrolling: 'touch',
                    "&::-webkit-scrollbar": {
                      display: "none",
                    },
                    marginTop: "14px",
                    position: "fixed",
                    top: 72,
                    zIndex: 1200,
                    background: theme.palette.background.default,
                  }}
              >
                <List
                    sx={{
                      display: "flex",
                      flexDirection: "row",
                      padding: 0,
                      margin: 0,
                      whiteSpace: 'nowrap',
                    }}
                >
                  {menuOptions.map((option, index) => (
                      <ListItem
                          onClick={() => handleClickMenuItem(option.label)}
                          key={index}
                          sx={{
                            marginRight: "8px",
                            cursor: "pointer",
                          }}
                      >
                        <Typography
                            sx={{
                              color: option.label === selectedOption
                                  ? theme.palette.background.default
                                  : theme.palette.text.neutralLightest,
                              background: option.label === selectedOption
                                  ? theme.palette.secondary.main
                                  : "none",
                              padding: "8px 12px",
                              borderRadius: "12px",
                              fontSize: "16px",
                              fontWeight: 500,
                              lineHeight: "24px",
                            }}
                        >
                          {option.label}
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
                  top: 102,
                  zIndex: 1100,
                  overflowY: "auto",
                  maxHeight: "calc(100vh - 102px)",
                  borderRight: "1px solid #737380",
                }}
              >
                {
                  title ? <ListItem
                      sx={{
                        paddingLeft: "0px",
                        marginBottom: "16px",
                      }}
                  >
                    <Typography
                        sx={{
                          fontFamily: "Dosis",
                          fontSize: "32px",
                          fontStyle: "normal",
                          fontWeight: 700,
                          lineHeight: "36px",
                        }}
                    >
                      {title} ({menuOptions.length})
                    </Typography>
                  </ListItem> : null
                }

                {menuOptions.map((option, index) => (
                  <ListItem
                    onClick={() => handleClickMenuItem(option.label)}
                    key={index}
                    sx={{
                      paddingLeft: "0px",
                    }}
                  >
                    {option.label === selectedOption ? (
                      <>
                        <Box
                          component="div"
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
            paddingLeft: {
              xs: "16px",
              md: "20px",
            },
            paddingRight: {
              xs: "10px",
              md: "0px",
            },
            marginTop: isMobile ? "60px" : "10px",
          }}
        >
          {title ? (
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
              {title}
            </Typography>
          ) : null}
          {mode === "change"
            ? menuOptions.find((option) => option.label === selectedOption)
                ?.content
            : menuOptions.map((option) => (
                <div
                  key={option.label}
                  ref={
                    mode === "scroll"
                      ? optionRefs.current[option.label]
                      : undefined
                  }
                >
                  {option.content}
                </div>
              ))}
          {bottom}
        </Grid>
      </Grid>
    </Box>
  );
};

export default Layout;

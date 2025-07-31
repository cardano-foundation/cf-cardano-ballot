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

type MenuItem = {
  label: string;
  content: ReactNode;
};

type LayoutProps = {
  menuOptions: MenuItem[];
  title?: string;
  optionsTitle?: string;
  bottom?: ReactNode;
  mode?: "scroll" | "change";
  defaultOption?: number;
  onSelectMenuOption?: (option: string) => void;
};

const Layout: React.FC<LayoutProps> = ({
  menuOptions,
  title,
  optionsTitle,
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

  return (
    <Box
      component="div"
      sx={{
        width: "100%",
      }}
    >
      <Grid container>
        <Grid item xs={12} md={2.4} lg={2}>
          {isMobile ? (
            <>
              <Box
                component="div"
                sx={{
                  width: "100%",
                  maxWidth: "100vw",
                  overflowX: "auto",
                  overflowY: "hidden",
                  WebkitOverflowScrolling: "touch",
                  "&::-webkit-scrollbar": {
                    display: "none",
                  },
                  marginTop: "14px",
                  position: "fixed",
                  top: 82,
                  zIndex: 1092,
                  background: theme.palette.background.default,
                }}
              >
                <List
                  sx={{
                    display: "flex",
                    flexDirection: "row",
                    padding: 0,
                    margin: 0,
                    whiteSpace: "nowrap",
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
                          color:
                            option.label === selectedOption
                              ? theme.palette.background.default
                              : theme.palette.text.neutralLightest,
                          background:
                            option.label === selectedOption
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
                  paddingRight: "12px",
                }}
              >
                {title ? (
                  <ListItem
                    sx={{
                      paddingLeft: "10px",
                      marginBottom: "16px",
                    }}
                  >
                    <Typography
                      sx={{
                        color: theme.palette.text.primary,
                        fontFamily: "Tomorrow",
                        fontSize: "32px",
                        fontStyle: "normal",
                        fontWeight: 500,
                        lineHeight: "32px",
                      }}
                    >
                      {title}
                    </Typography>
                  </ListItem>
                ) : null}

                {menuOptions.map((option, index) => (
                  <ListItem
                    onClick={() => handleClickMenuItem(option.label)}
                    key={index}
                    sx={{
                      marginTop: "12px",
                      paddingLeft: "10px",
                      paddingRight: "0px",
                      paddingTop: "12px",
                      paddingBottom: "12px",
                      cursor: "pointer",
                      transition: "background-color 0.3s, color 0.3s",
                      color:
                          option.label === selectedOption
                              ? theme.palette.background.default
                              : theme.palette.text.primary,
                      backgroundColor:
                        option.label === selectedOption
                          ? theme.palette.secondary.main
                          : "transparent",
                      "&:hover": option.label !== selectedOption ? ({
                        backgroundColor: theme.palette.background.neutralDark,
                        color: theme.palette.text.primary,
                      }) : '',
                      display: "flex",
                      alignItems: "center",
                      width: "100%",
                      borderRadius: "8px",
                    }}
                  >
                    <Typography
                      sx={{
                        color: "inherit",
                        fontSize: "16px",
                        fontWeight: 600,
                        lineHeight: "20px",
                        flexGrow: 1,
                        textAlign: "left",
                      }}
                    >
                      {option.label}
                    </Typography>
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
              md: "44px",
            },
            paddingRight: {
              xs: "10px",
              md: "0px",
            },
            marginTop: isMobile ? "60px" : "10px",
          }}
        >
          {optionsTitle ? (
            <Typography
              sx={{
                color: '#fff',
                fontFamily: "Tomorrow",
                fontSize: "44px",
                fontStyle: "normal",
                fontWeight: 500,
                lineHeight: "44px",
                marginBottom: "32px",
                paddingTop: "16px",
              }}
            >
              {optionsTitle}
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

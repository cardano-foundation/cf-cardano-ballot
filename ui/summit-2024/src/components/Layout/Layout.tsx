import React, { useRef, useState, useEffect, ReactNode } from "react";
import { Box, Grid, List, ListItem, Typography } from "@mui/material";
import theme from "../../common/styles/theme";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";

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
  onSelectMenuOption?: (option: string) => void
};

const Layout: React.FC<LayoutProps> = ({
  menuOptions,
  title,
  bottom,
  mode = "scroll",
  defaultOption = 0,
                                         onSelectMenuOption
}) => {
  const isMobile = useIsPortrait();
  const [selectedOption, setSelectedOption] = useState("");

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
      mode === "scroll" &&
      selectedOption &&
      optionRefs.current[selectedOption]
    ) {
      optionRefs.current[selectedOption].current?.scrollIntoView({
        behavior: "smooth",
        block: "start",
      });
    }
  }, [selectedOption, mode]);

  const handleClickMenuItem = (label: string) => {
    setSelectedOption(label);
    if (onSelectMenuOption) onSelectMenuOption(label);
  };

  return (
    <Box component="div" sx={{ width: "100%" }}>
      <Grid container>
        <Grid item xs={12} md={2.4} lg={2}>
          {isMobile ? (
            <>
              <Box
                component="div"
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
                  {menuOptions.map((option, index) => (
                    <ListItem
                      onClick={() => handleClickMenuItem(option.label)}
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
                          cursor: "pointer",
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
                {menuOptions.map((option, index) => (
                  <ListItem
                    onClick={() => handleClickMenuItem(option.label)}
                    key={index}
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
            padding: {
              xs: "0px 20px",
              sm: "20px 0pxs",
            },
            marginTop: "10px",
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

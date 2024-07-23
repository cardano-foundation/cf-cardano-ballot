import React, { useState, useRef } from "react";
import { Box, Grid, Typography, List, ListItem } from "@mui/material";
import theme from "../../common/styles/theme";
import Ellipses from "../../assets/ellipse.svg";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";
import { PageBase } from "../BasePage";
import termsData from "../../common/resources/data/termsAndConditions.json";

const TermsAndConditions: React.FC = () => {
  const [selectedSection, setSelectedSection] = useState(
      termsData.title
  );
  const isMobile = useIsPortrait();

    const termsDataTitleRef = useRef<HTMLElement>(null);
    const termsDataTermsRef = useRef<HTMLElement>(null);
    const termsDataDisclaimerTitleRef = useRef<HTMLElement>(null);
    const contactUsRef = useRef<HTMLElement>(null);


  const handleClickMenuItem = (option:string) => {
    setSelectedSection(option);
    switch (option) {
        case termsData.title: {
            termsDataTitleRef.current?.scrollIntoView({
                behavior: "smooth",
                block: "start",
            });
            break;
        }
        case "terms": {
            termsDataTermsRef.current?.scrollIntoView({
                behavior: "smooth",
                block: "start",
            });
            break;
        }
        case termsData.disclaimer.title: {
            termsDataDisclaimerTitleRef.current?.scrollIntoView({
                behavior: "smooth",
                block: "start",
            });
            break;
        }
        case "contactUs": {
            contactUsRef.current?.scrollIntoView({
                behavior: "smooth",
                block: "start",
            });
            break;
        }
    }
  };

  const renderListItems = (list) => {
       return list.map((item, index) => (
          <Box component="div" key={index} sx={{mt: 1}}>
              <Typography
                  sx={{
                      color: theme.palette.text.neutralLight,
                      fontSize: "16px",
                      fontWeight: 500,
                      lineHeight: "24px",
                  }}
                  dangerouslySetInnerHTML={{
                      __html: `${item.number} ${item.content.join(" ")}`,
                  }}
              />
          </Box>
      ));
  }

  return (
    <PageBase title="Terms and Conditions">
      <Box component="div" sx={{ width: "100%" }}>
        <Grid container>
          <Grid item xs={12} md={2.4} lg={2}>
            {isMobile ? (
              <Box
                component="div"
                sx={{
                  overflowX: "auto",
                  width: "100%",
                  maxWidth: "100vw",
                  "&::-webkit-scrollbar": { display: "none" },
                  scrollbarWidth: "none",
                  msOverflowStyle: "none",
                  marginTop: "14px",
                  marginLeft: "14px",
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
                    <ListItem
                        onClick={() => handleClickMenuItem(termsData.title)}
                        key={termsData.title}
                        sx={{
                            display: "flex",
                            marginRight: "8px",
                            whiteSpace: "nowrap",
                            background:
                                termsData.title === selectedSection
                                    ? theme.palette.secondary.main
                                    : "none",
                            color:
                                termsData.title === selectedSection
                                    ? theme.palette.background.default
                                    : theme.palette.text.neutralLightest,
                            padding: "8px 12px",
                            borderRadius: "12px",
                            fontSize: "16px",
                            fontWeight: 500,
                            lineHeight: "24px",
                            cursor: "pointer",
                        }}
                    >
                        {termsData.title}
                    </ListItem>
                    <ListItem
                        onClick={() => handleClickMenuItem("terms")}
                        key={termsData.title}
                        sx={{
                            display: "flex",
                            marginRight: "8px",
                            whiteSpace: "nowrap",
                            background:
                                "terms" === selectedSection
                                    ? theme.palette.secondary.main
                                    : "none",
                            color:
                                "terms" === selectedSection
                                    ? theme.palette.background.default
                                    : theme.palette.text.neutralLightest,
                            padding: "8px 12px",
                            borderRadius: "12px",
                            fontSize: "16px",
                            fontWeight: 500,
                            lineHeight: "24px",
                            cursor: "pointer",
                        }}
                    >
                        Terms
                    </ListItem>
                    <ListItem
                        onClick={() => handleClickMenuItem(termsData.disclaimer.title)}
                        key={termsData.title}
                        sx={{
                            display: "flex",
                            marginRight: "8px",
                            whiteSpace: "nowrap",
                            background:
                                termsData.disclaimer.title === selectedSection
                                    ? theme.palette.secondary.main
                                    : "none",
                            color:
                                termsData.disclaimer.title === selectedSection
                                    ? theme.palette.background.default
                                    : theme.palette.text.neutralLightest,
                            padding: "8px 12px",
                            borderRadius: "12px",
                            fontSize: "16px",
                            fontWeight: 500,
                            lineHeight: "24px",
                            cursor: "pointer",
                        }}
                    >
                        Disclaimer
                    </ListItem>
                    <ListItem
                        onClick={() => handleClickMenuItem("contactUs")}
                        key={"contactUs"}
                        sx={{
                            display: "flex",
                            marginRight: "8px",
                            whiteSpace: "nowrap",
                            background:
                                "contactUs" === selectedSection
                                    ? theme.palette.secondary.main
                                    : "none",
                            color:
                                "contactUs" === selectedSection
                                    ? theme.palette.background.default
                                    : theme.palette.text.neutralLightest,
                            padding: "8px 12px",
                            borderRadius: "12px",
                            fontSize: "16px",
                            fontWeight: 500,
                            lineHeight: "24px",
                            cursor: "pointer",
                        }}
                    >
                        Contact Us
                    </ListItem>
                </List>
              </Box>
            ) : (
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
                  <ListItem
                      onClick={() => handleClickMenuItem(termsData.title)}
                      key={termsData.title}
                      sx={{
                          background:
                              termsData.title === selectedSection
                                  ? theme.palette.secondary.main
                                  : "none",
                          color:
                              termsData.title === selectedSection
                                  ? theme.palette.background.default
                                  : theme.palette.text.neutralLightest,
                          padding: "8px 12px",
                          fontSize: "16px",
                          fontWeight: 500,
                          lineHeight: "24px",
                          cursor: "pointer",
                      }}
                  >
                      {termsData.title}
                  </ListItem>
                  <ListItem
                      onClick={() => handleClickMenuItem("terms")}
                      key={termsData.title}
                      sx={{
                          background:
                              "terms" === selectedSection
                                  ? theme.palette.secondary.main
                                  : "none",
                          color:
                              "terms" === selectedSection
                                  ? theme.palette.background.default
                                  : theme.palette.text.neutralLightest,
                          padding: "8px 12px",
                          fontSize: "16px",
                          fontWeight: 500,
                          lineHeight: "24px",
                          cursor: "pointer",
                      }}
                  >
                      Terms
                  </ListItem>
                  <ListItem
                      onClick={() => handleClickMenuItem(termsData.disclaimer.title)}
                      key={termsData.title}
                      sx={{
                          background:
                              termsData.disclaimer.title === selectedSection
                                  ? theme.palette.secondary.main
                                  : "none",
                          color:
                              termsData.disclaimer.title === selectedSection
                                  ? theme.palette.background.default
                                  : theme.palette.text.neutralLightest,
                          padding: "8px 12px",
                          fontSize: "16px",
                          fontWeight: 500,
                          lineHeight: "24px",
                          cursor: "pointer",
                      }}
                  >
                      Disclaimer
                  </ListItem>
                  <ListItem
                      onClick={() => handleClickMenuItem("contactUs")}
                      key={"contactUs"}
                      sx={{
                          background:
                              "contactUs" === selectedSection
                                  ? theme.palette.secondary.main
                                  : "none",
                          color:
                              "contactUs" === selectedSection
                                  ? theme.palette.background.default
                                  : theme.palette.text.neutralLightest,
                          padding: "8px 12px",
                          fontSize: "16px",
                          fontWeight: 500,
                          lineHeight: "24px",
                          cursor: "pointer",
                      }}
                  >
                      Contact Us
                  </ListItem>
              </List>
            )}
          </Grid>
          <Grid item xs={12} md={9.6} lg={10} sx={{ padding: "0px 20px" }}>
            <>
              <Typography
                  ref={termsDataTitleRef}
                sx={{
                  color: theme.palette.text.neutralLightest,
                  fontFamily: "Dosis",
                  fontSize: "32px",
                  fontWeight: 700,
                  lineHeight: "36px",
                }}
              >
                {termsData.title}
              </Typography>
              <Typography
                sx={{
                  color: theme.palette.text.neutralLight,
                  fontSize: "16px",
                  fontWeight: 500,
                  lineHeight: "24px",
                  mt: 2,
                }}
              >
                {termsData.date}
              </Typography>
              {termsData.sections.map((section, index) => (
                <Box component="div" key={index} sx={{ mt: 4 }}>
                  <Typography
                    sx={{
                      color: theme.palette.text.neutralLightest,
                      fontFamily: "Dosis",
                      fontSize: "24px",
                      fontWeight: 700,
                      lineHeight: "28px",
                    }}
                  >
                    {section.title}
                  </Typography>
                  {section.content?.map((paragraph, pIndex) => (
                    <Typography
                      key={pIndex}
                      sx={{
                        color: theme.palette.text.neutralLight,
                        fontSize: "16px",
                        fontWeight: 500,
                        lineHeight: "24px",
                        mt: 1,
                      }}
                      dangerouslySetInnerHTML={{ __html: paragraph }}
                    />
                  ))}
                  {section.subsections?.map((subsection, sIndex) => (
                    <Box key={sIndex} sx={{ mt: 2 }}>
                      <Typography
                        sx={{
                          color: theme.palette.text.neutralLightest,
                          fontFamily: "Dosis",
                          fontSize: "24px",
                          fontWeight: 700,
                          lineHeight: "28px",
                        }}
                      >
                        {subsection.title}
                      </Typography>
                      {subsection.content.map((content, cIndex) => (
                        <Typography
                          key={cIndex}
                          sx={{
                            color: "var(--neutralLight, #D2D2D9)",
                            fontSize: "16px",
                            fontWeight: 500,
                            lineHeight: "24px",
                            mt: 1,
                          }}
                          dangerouslySetInnerHTML={{ __html: content }}
                        />
                      ))}
                      {subsection.definitions &&
                        Object.entries(subsection.definitions).map(
                          ([key, value], dIndex) => (
                            <Typography
                              key={dIndex}
                              sx={{
                                color: "var(--neutralLight, #D2D2D9)",
                                fontSize: "16px",
                                fontWeight: 500,
                                lineHeight: "24px",
                                mt: 1,
                              }}
                              dangerouslySetInnerHTML={{ __html: value }}
                            />
                          ),
                        )}
                    </Box>
                  ))}
                </Box>
              ))}
                <Box component="div" ref={termsDataTermsRef}>
                    {termsData.terms.map((termSection, index) => (
                        <Box component="div" key={index} sx={{ mt: 4 }}>
                            <Typography

                                sx={{
                                    color: theme.palette.text.neutralLightest,
                                    fontFamily: "Dosis",
                                    fontSize: "24px",
                                    fontWeight: 700,
                                    lineHeight: "28px",
                                }}
                            >
                                {termSection.title}
                            </Typography>
                            {renderListItems(termSection.list)}
                        </Box>
                    ))}
                </Box>

              {/* Disclaimer Section */}
              {termsData.disclaimer && (
                <Box component="div" sx={{ mt: 4 }}>
                  <Typography
                      ref={termsDataDisclaimerTitleRef}
                    sx={{
                      color: theme.palette.text.neutralLightest,
                      fontFamily: "Dosis",
                      fontSize: "24px",
                      fontWeight: 700,
                      lineHeight: "28px",
                    }}
                  >
                    {termsData.disclaimer.title}
                  </Typography>
                  {termsData.disclaimer.content.map((content, index) => (
                    <Typography
                      key={index}
                      sx={{
                        color: theme.palette.text.neutralLight,
                        fontSize: "16px",
                        fontWeight: 500,
                        lineHeight: "24px",
                        mt: 1,
                      }}
                      dangerouslySetInnerHTML={{ __html: content }}
                    />
                  ))}
                </Box>
              )}

              {/* Liability Section */}
              {termsData.liability && (
                <Box component="div" sx={{ mt: 4 }}>
                  <Typography
                    sx={{
                      color: theme.palette.text.neutralLightest,
                      fontFamily: "Dosis",
                      fontSize: "24px",
                      fontWeight: 700,
                      lineHeight: "28px",
                    }}
                  >
                    {termsData.liability.title}
                  </Typography>
                  {termsData.liability.content.map((content, index) => (
                    <Typography
                      key={index}
                      sx={{
                        color: theme.palette.text.neutralLight,
                        fontSize: "16px",
                        fontWeight: 500,
                        lineHeight: "24px",
                        mt: 1,
                      }}
                      dangerouslySetInnerHTML={{ __html: content }}
                    />
                  ))}
                </Box>
              )}

              {/* Miscellaneous Section */}
              {termsData.miscellaneous && (
                <Box component="div" sx={{ mt: 4 }}>
                  <Typography
                    sx={{
                      color: theme.palette.text.neutralLightest,
                      fontFamily: "Dosis",
                      fontSize: "24px",
                      fontWeight: 700,
                      lineHeight: "28px",
                    }}
                  >
                    {termsData.miscellaneous.title}
                  </Typography>
                  {renderListItems(termsData.miscellaneous.list)}
                </Box>
              )}

              {/* Contact Us Section */}
              {termsData.contactus && (
                <Box component="div" sx={{ mt: 4 }}>
                  <Typography
                      ref={contactUsRef}
                    sx={{
                      color: theme.palette.text.neutralLightest,
                      fontFamily: "Dosis",
                      fontSize: "24px",
                      fontWeight: 700,
                      lineHeight: "28px",
                    }}
                  >
                    {termsData.contactus.title}
                  </Typography>
                  <Typography
                    sx={{
                      color: theme.palette.text.neutralLight,
                      fontSize: "16px",
                      fontWeight: 500,
                      lineHeight: "24px",
                      mt: 1,
                    }}
                    dangerouslySetInnerHTML={{
                      __html: termsData.contactus.content,
                    }}
                  />
                </Box>
              )}
            </>
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
    </PageBase>
  );
};

export { TermsAndConditions };

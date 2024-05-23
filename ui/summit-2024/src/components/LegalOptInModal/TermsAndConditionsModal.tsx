import * as React from "react";
import {
  Box,
  Button,
  Checkbox,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Typography,
} from "@mui/material";
import { useState } from "react";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";
import { useLocalStorage } from "../../common/hooks/useLocalStorage";
import { CB_TERMS_AND_PRIVACY } from "../../common/constants/local";
import { TabsSegment } from "../common/TabPanel/TabsSegment";
import termsData from "../../common/resources/data/termsAndConditions.json";
import privacyData from "../../common/resources/data/privacyPolicy.json";
import theme from "../../common/styles/theme";
import { ExtraDetails, List } from "./TermsAndConditionsModal.type";

const TermsAndConditionsModal = () => {
  const isMobile = useIsPortrait();
  const [currentTab, setCurrentTab] = useState(0);
  const [isChecked, setIsChecked] = useState(false);
  const [termsAndConditionsChecked, setTermsAndConditionsChecked] =
    useLocalStorage(CB_TERMS_AND_PRIVACY, false);

  const tabs = ["Terms & Conditions", "Privacy Policy"];

  const handleCheckboxChange = (event) => {
    setIsChecked(event.target.checked);
  };
  const handleAccept = () => {
    setTermsAndConditionsChecked(true);
  };
  const renderContent = () => {
    const renderListItems = (list: List[]) =>
      list.map((item, index) => (
        <Box key={index} sx={{ mt: 1 }}>
          <Typography
            sx={{
              color: "var(--neutralLight, #D2D2D9)",
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
    // TODO: fix text styles based on figma reqs
    switch (currentTab) {
      case 0: {
        return (
          <>
            <Typography
              sx={{
                color: "var(--neutralLightest, #FAF9F6)",
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
                color: "var(--neutralLight, #D2D2D9)",
                fontSize: "16px",
                fontWeight: 500,
                lineHeight: "24px",
                mt: 2,
              }}
            >
              {termsData.date}
            </Typography>
            {termsData.sections.map((section, index) => (
              <Box key={index} sx={{ mt: 4 }}>
                <Typography
                  sx={{
                    color: "var(--neutralLightest, #FAF9F6)",
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
                      color: "var(--neutralLight, #D2D2D9)",
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
                        color: "var(--neutralLightest, #FAF9F6)",
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
            {termsData.terms.map((termSection, index) => (
              <Box key={index} sx={{ mt: 4 }}>
                <Typography
                  sx={{
                    color: "var(--neutralLightest, #FAF9F6)",
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

            {/* Disclaimer Section */}
            {termsData.disclaimer && (
              <Box sx={{ mt: 4 }}>
                <Typography
                  sx={{
                    color: "var(--neutralLightest, #FAF9F6)",
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
                      color: "var(--neutralLight, #D2D2D9)",
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
              <Box sx={{ mt: 4 }}>
                <Typography
                  sx={{
                    color: "var(--neutralLightest, #FAF9F6)",
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
                      color: "var(--neutralLight, #D2D2D9)",
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
              <Box sx={{ mt: 4 }}>
                <Typography
                  sx={{
                    color: "var(--neutralLightest, #FAF9F6)",
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
              <Box sx={{ mt: 4 }}>
                <Typography
                  sx={{
                    color: "var(--neutralLightest, #FAF9F6)",
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
                    color: "var(--neutralLight, #D2D2D9)",
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
        );
      }
      case 1: {
        const renderExtras = (extras: ExtraDetails) => {
          return Object.entries(extras).map(([key, value], index) => (
            <Typography
              key={index}
              sx={{
                color: "var(--neutralLight, #D2D2D9)",
                fontSize: "16px",
                fontStyle: "normal",
                fontWeight: 500,
                lineHeight: "24px",
                mt: 1,
              }}
              dangerouslySetInnerHTML={{ __html: value }}
            />
          ));
        };
        return (
          <>
            <Typography
              sx={{
                color: "var(--neutralLightest, #FAF9F6)",
                fontFamily: "Dosis",
                fontSize: "32px",
                fontStyle: "normal",
                fontWeight: 700,
                lineHeight: "36px",
              }}
            >
              {privacyData.title}
            </Typography>
            <Typography
              sx={{
                color: "var(--neutralLight, #D2D2D9)",
                fontSize: "16px",
                fontStyle: "normal",
                fontWeight: 500,
                lineHeight: "24px",
              }}
            >
              {privacyData.date}
            </Typography>
            {privacyData.description.map((paragraph, index) => (
              <Typography
                key={index}
                sx={{
                  color: "var(--neutralLight, #D2D2D9)",
                  fontSize: "16px",
                  fontStyle: "normal",
                  fontWeight: 500,
                  lineHeight: "24px",
                }}
              >
                {paragraph}
              </Typography>
            ))}

            {privacyData.sections.map((section, index) => (
              <Box key={index} sx={{ mt: 2 }}>
                <Typography
                  sx={{
                    color: "var(--neutralLightest, #FAF9F6)",
                    fontFamily: "Dosis",
                    fontSize: "24px",
                    fontStyle: "normal",
                    fontWeight: 700,
                    lineHeight: "28px",
                  }}
                >
                  {section.title}
                </Typography>

                {section.subsections.map((subsection, subIndex) => (
                  // TODO: contact subsection styles
                  <Box key={subIndex} sx={{ mt: 1 }}>
                    <Typography
                      sx={{
                        color: "var(--neutralLightest, #FAF9F6)",
                        fontFamily: "Dosis",
                        fontSize: "24px",
                        fontStyle: "normal",
                        fontWeight: 700,
                        lineHeight: "28px",
                      }}
                    >
                      {subsection.title}
                    </Typography>
                    {subsection.content.map((content, contentIndex) => (
                      <Typography
                        key={contentIndex}
                        sx={{
                          color: "var(--neutralLight, #D2D2D9)",
                          fontSize: "16px",
                          fontStyle: "normal",
                          fontWeight: 500,
                          lineHeight: "24px",
                          mt: 1,
                        }}
                      >
                        {content}
                      </Typography>
                    ))}
                    {subsection.extras && renderExtras(subsection.extras)}
                  </Box>
                ))}
              </Box>
            ))}
          </>
        );
      }
    }
  };

  const checkedIcon = (
    <Box
      sx={{
        width: 20,
        height: 20,
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "var(--orange, #EE9766)", // Background color for checked state
        borderRadius: "4px",
      }}
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="20"
        height="20"
        viewBox="0 0 20 20"
        fill="none"
      >
        <path
          d="M7.875 15.167L2.917 10.208 4.125 9.021 7.875 12.771 15.854 4.792 17.063 5.979 7.875 15.167Z"
          fill="#121212"
        />
      </svg>
    </Box>
  );

  // Plain icon for the unchecked state
  const icon = (
    <Box
      sx={{
        width: 20,
        height: 20,
        backgroundColor: "transparent", // Make unchecked state transparent
        borderRadius: "4px",
        border: "2px solid white", // White border for unchecked state
      }}
    />
  );

  return (
    <Dialog
      open={!termsAndConditionsChecked}
      keepMounted
      disableBackdropClick
      onClose={() => setTermsAndConditionsChecked(false)}
      scroll={"paper"}
      maxWidth={isMobile ? "sm" : "md"}
      sx={{
        backdropFilter: "blur(10px)",
        "& .MuiDialog-paper": { borderRadius: "16px" },
      }}
      aria-labelledby="terms-modal-title"
      aria-describedby="terms-modal-description"
    >
      <DialogTitle sx={{ backgroundColor: theme.palette.background.default }}>
        <TabsSegment
          tabs={tabs}
          currentTab={currentTab}
          setCurrentTab={(tab) => setCurrentTab(tab)}
        />
      </DialogTitle>
      <DialogContent
        dividers
        sx={{ backgroundColor: theme.palette.background.default }}
      >
        <DialogContentText sx={{ width: isMobile ? "auto" : "800px" }}>
          {renderContent()}
        </DialogContentText>
      </DialogContent>
      <DialogActions
        sx={{
          justifyContent: "space-between",
          alignItems: "center",
          padding: theme.spacing(2),
          backgroundColor: theme.palette.background.default,
        }}
      >
        <Box sx={{ display: "flex", alignItems: "center" }}>
          <Checkbox
            icon={icon}
            checkedIcon={checkedIcon}
            checked={isChecked}
            onChange={handleCheckboxChange}
          />
          <Typography
            sx={{
              color: theme.palette.text.neutralLightest,
              marginLeft: theme.spacing(1),
            }}
          >
            I agree to the Terms of Service
          </Typography>
        </Box>

        <Box sx={{ display: "flex", gap: theme.spacing(2) }}>
          <Button
            variant="outlined"
            sx={{
              display: "flex",
              padding: "16px 24px",
              justifyContent: "center",
              alignItems: "center",
              borderRadius: "12px",
              borderColor: "var(--orange, #EE9766)",
              color: "var(--orange, #EE9766)",
              textTransform: "none",
            }}
          >
            Decline
          </Button>
          <Button
            onClick={() => handleAccept()}
            variant="contained"
            disabled={!isChecked}
            sx={{
              display: "flex",
              padding: "16px 24px",
              justifyContent: "center",
              alignItems: "center",
              borderRadius: "12px",
              background: isChecked
                ? "linear-gradient(70deg, #0C7BC5 -105.24%, #40407D -53.72%, #EE9766 -0.86%, #EE9766 103.82%)"
                : "var(--neutral, #737380)",
              color: "white",
              textTransform: "none",
            }}
          >
            Accept
          </Button>
        </Box>
      </DialogActions>
    </Dialog>
  );
};

export { TermsAndConditionsModal };

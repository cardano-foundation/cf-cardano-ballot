import {
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
  DialogTitle,
  Typography,
  useMediaQuery,
} from "@mui/material";
import { useEffect, useState } from "react";
import { useLocalStorage } from "../../common/hooks/useLocalStorage";
import { CB_TERMS_AND_PRIVACY } from "../../common/constants/local";
import termsData from "../../common/resources/data/termsAndConditions.json";
import privacyData from "../../common/resources/data/privacyPolicy.json";
import theme from "../../common/styles/theme";
import { ExtraDetails, List } from "./TermsAndConditionsModal.type";
import { CustomCheckBox } from "../common/CustomCheckBox/CustomCheckBox";
import AnimatedSwitch from "../AnimatedSwitch/AnimatedSwitch";
import { eventBus, EventName } from "../../utils/EventBus";

const TermsAndConditionsModal = () => {
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));

  const tabs = [
    isMobile ? "T&Cs" : "Terms & Conditions",
    isMobile ? "Privacy" : "Privacy Policy",
  ];

  const [currentTab, setCurrentTab] = useState(tabs[0]);

  const [forceOpenModal, setForceOpenModal] = useState(false);
  const [termsAndConditionsChecked, setTermsAndConditionsChecked] =
    useLocalStorage(CB_TERMS_AND_PRIVACY, false);
  const [isChecked, setIsChecked] = useState(termsAndConditionsChecked);

  useEffect(() => {
    const openModal = () => {
      setForceOpenModal(true);
    };
    eventBus.subscribe(EventName.OpenTermsModal, openModal);
    return () => {
      eventBus.unsubscribe(EventName.OpenTermsModal, openModal);
    };
  }, []);

  useEffect(() => {
    const searchParams = new URLSearchParams(location.search);
    const terms = searchParams.get("terms") === "true";
    setForceOpenModal(terms);
  }, [location]);

  const handleSetCurrentTab = (option: string) => {
    setCurrentTab(option);
  };

  const handleDecline = () => {
    eventBus.publish(
      EventName.ShowToast,
      "To use the app, you must accept the Terms and Conditions and Privacy Policy",
    );
  };

  const handleAccept = () => {
    setTermsAndConditionsChecked(true);
    setForceOpenModal(false);
    setIsChecked(false);
  };
  const renderContent = () => {
    const renderListItems = (list: List[]) =>
      list.map((item, index) => (
        <Box component="div" key={index} sx={{ mt: 1 }}>
          <Typography
            sx={{
              color: theme.palette.text.primary,
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
      case tabs[0]: {
        return (
          <>
            <Typography
              sx={{
                color: theme.palette.text.primary,
                fontFamily: "Tomorrow",
                fontSize: "32px",
                fontWeight: 700,
                lineHeight: "36px",
              }}
            >
              {termsData.title}
            </Typography>
            <Typography
              sx={{
                color: theme.palette.text.primary,
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
                    color: theme.palette.text.primary,
                    fontFamily: "Tomorrow",
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
                      color: theme.palette.text.primary,
                      fontSize: "16px",
                      fontWeight: 500,
                      lineHeight: "24px",
                      mt: 1,
                    }}
                    dangerouslySetInnerHTML={{ __html: paragraph }}
                  />
                ))}
                {section.subsections?.map((subsection, sIndex) => (
                  <Box component="div" key={sIndex} sx={{ mt: 2 }}>
                    <Typography
                      sx={{
                        color: theme.palette.text.primary,
                        fontFamily: "Tomorrow",
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
                        ([_, value], dIndex) => (
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
              <Box component="div" key={index} sx={{ mt: 4 }}>
                <Typography
                  sx={{
                    color: theme.palette.text.primary,
                    fontFamily: "Tomorrow",
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
              <Box component="div" sx={{ mt: 4 }}>
                <Typography
                  sx={{
                    color: theme.palette.text.primary,
                    fontFamily: "Tomorrow",
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
                      color: theme.palette.text.primary,
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
                    color: theme.palette.text.primary,
                    fontFamily: "Tomorrow",
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
                      color: theme.palette.text.primary,
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
                    color: theme.palette.text.primary,
                    fontFamily: "Tomorrow",
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
                  sx={{
                    color: theme.palette.text.primary,
                    fontFamily: "Tomorrow",
                    fontSize: "24px",
                    fontWeight: 700,
                    lineHeight: "28px",
                  }}
                >
                  {termsData.contactus.title}
                </Typography>
                <Typography
                  sx={{
                    color: theme.palette.text.primary,
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
      case tabs[1]: {
        const renderExtras = (extras: ExtraDetails) => {
          return Object.entries(extras).map(([_, value], index) => (
            <Typography
              key={index}
              sx={{
                color: theme.palette.text.primary,
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
                color: theme.palette.text.primary,
                fontFamily: "Tomorrow",
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
                color: theme.palette.text.primary,
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
                  color: theme.palette.text.primary,
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
              <Box component="div" key={index} sx={{ mt: 2 }}>
                <Typography
                  sx={{
                    color: theme.palette.text.primary,
                    fontFamily: "Tomorrow",
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
                  <Box component="div" key={subIndex} sx={{ mt: 1 }}>
                    <Typography
                      sx={{
                        color: theme.palette.text.primary,
                        fontFamily: "Tomorrow",
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
                          color: theme.palette.text.primary,
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

  return (
    <Dialog
      open={!termsAndConditionsChecked || forceOpenModal}
      keepMounted
      // @ts-ignore
      disableBackdropClick
      onClose={() =>
        setTermsAndConditionsChecked(false) && setForceOpenModal(false)
      }
      scroll={"paper"}
      maxWidth={isMobile ? "sm" : "md"}
      sx={{
        backdropFilter: "blur(10px)",
        "& .MuiDialog-paper": { borderRadius: "24px" },
      }}
      aria-labelledby="terms-modal-title"
      aria-describedby="terms-modal-description"
    >
      <DialogTitle
        sx={{
          backgroundColor: theme.palette.background.default,
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          padding: "28px",
        }}
      >
        <AnimatedSwitch
          defaultValue={tabs[0]}
          optionA={tabs[0]}
          optionB={tabs[1]}
          onClickOption={(option: string) => handleSetCurrentTab(option)}
        />
      </DialogTitle>
      <DialogContent
        sx={{ backgroundColor: theme.palette.background.default }}
      >
        <DialogContentText sx={{ width: isMobile ? "auto" : "800px" }}>
          {renderContent()}
        </DialogContentText>
      </DialogContent>
      <DialogActions
        sx={{
          borderTop: `1px solid ${theme.palette.background.neutralDark}`,
          justifyContent: { xs: "center", sm: "space-between" },
          alignItems: "center",
          padding: "28px",
          backgroundColor: theme.palette.background.default,
          flexDirection: { xs: "column", sm: "row" },
        }}
      >
        <Box
          component="div"
          sx={{
            display: "flex",
            alignItems: "center",
            width: "100%",
            mb: { xs: 2, sm: 0 },
          }}
        >
          <CustomCheckBox isChecked={isChecked} setIsChecked={setIsChecked} />
          <Typography
            sx={{
              color: theme.palette.text.primary,
              marginLeft: theme.spacing(1),
              flexGrow: 1,
              fontSize: "16px",
              fontWeight: 600,
              lineHeight: "24px",
            }}
          >
            I agree to the Terms of Service
          </Typography>
        </Box>

        <Box
          component="div"
          sx={{
            display: "flex",
            flexDirection: { xs: "column", sm: "row" },
            gap: theme.spacing(2),
            width: "100%",
            justifyContent: "end",
          }}
        >
          <Button
            onClick={() => handleDecline()}
            variant="outlined"
            sx={{
              color: theme.palette.text.primary,
              fontSize: "16px",
              fontStyle: "normal",
              fontWeight: 600,
              lineHeight: "24px",
              cursor: "pointer",
              padding: "12px 16px",
              textTransform: "none",
              border: `1px solid ${theme.palette.text.primary}`,
              borderRadius: "8px",
              "&:hover": {
                backgroundColor: theme.palette.text.primary,
                color: theme.palette.background.default,
              },
            }}
          >
            Decline
          </Button>
          <Button
            onClick={handleAccept}
            variant="contained"
            disabled={!isChecked}
            sx={{
              color: theme.palette.background.default,
              fontSize: "16px",
              fontStyle: "normal",
              fontWeight: 600,
              lineHeight: "20px",
              cursor: "pointer",
              padding: "12px 16px",
              textTransform: "none",
              borderRadius: "8px",
              "&:hover": {
                backgroundColor: "#FF9277",
                color: theme.palette.background.default,
              },
              backgroundColor: theme.palette.secondary.main,
              "&:disabled": {
                backgroundColor: "#A0AEDB",
                color: theme.palette.text.neutralLight,
              }
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

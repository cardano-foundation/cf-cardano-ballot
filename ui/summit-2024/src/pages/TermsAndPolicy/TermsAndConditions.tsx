import Layout from "../../components/Layout/Layout";
import { Box, Typography } from "@mui/material";
import theme from "../../common/styles/theme";
import termsData from "../../common/resources/data/termsAndConditions.json";
import { PageBase } from "../BasePage";

const TermsAndConditions = () => {
  const renderListItems = (list) => {
    return list.map((item, index) => (
      <Box component="div" key={index} sx={{ mt: 1 }}>
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
  };

  const optionsForScroll = [
    {
      label: "Terms and Conditions",
      content: (
        <>
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
                <Box component="div" key={sIndex} sx={{ mt: 2 }}>
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
        </>
      ),
    },
    {
      label: "Terms",
      content: (
        <>
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
        </>
      ),
    },
    {
      label: "Disclaimer",
      content: (
        <>
          {termsData.disclaimer && (
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
        </>
      ),
    },
    {
      label: "Contact Us",
      content: (
        <>
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
        </>
      ),
    },
  ];

  return (
    <PageBase title="Terms and Conditions">
      <Layout
        title="Terms and Conditions"
        menuOptions={optionsForScroll}
        mode="scroll"
      />
    </PageBase>
  );
};

export default TermsAndConditions;

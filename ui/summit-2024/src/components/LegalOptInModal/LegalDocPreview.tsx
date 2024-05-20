import * as React from "react";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Typography,
} from "@mui/material";
import termsData from "../../common/resources/data/termsAndConditions.json";
import privacyData from "../../common/resources/data/privacyPolicy.json";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";

const LegalDocPreview = () => {
  const [expandedTerms, setExpandedTerms] = React.useState(true);
  const [privacyTerms, setPrivacyTerms] = React.useState(true);
  return (
    <>
      <Accordion
        expanded={expandedTerms}
        onChange={() => setExpandedTerms(!expandedTerms)}
      >
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel1a-content"
          id="panel1a-header"
        >
          <Typography variant="h6">{termsData.title}</Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Typography variant="body1" sx={{ mb: 1, mt: 2 }}>
            {termsData.date}
          </Typography>
          {termsData.sections.map((section, index) => {
            return (
              <div key={index}>
                <Typography
                  variant="subtitle2"
                  sx={{ mt: 1, fontWeight: "bold" }}
                >
                  {section.title}
                </Typography>
                {section.content &&
                  section.content.map((paragraph, paragraphIndex) => (
                    <Typography
                      key={paragraphIndex}
                      variant="body1"
                      sx={{ mt: 1 }}
                      dangerouslySetInnerHTML={{ __html: paragraph }}
                    />
                  ))}
                {section.subsections &&
                  section.subsections.map((subsection, subIndex) => (
                    <div key={`${subsection.title}-${subIndex}`}>
                      <Typography
                        variant="subtitle2"
                        sx={{ mt: 1, fontWeight: "bold" }}
                      >
                        {subsection.title}
                      </Typography>

                      <Typography variant="body1" sx={{ mt: 1 }}>
                        {subsection.content}
                      </Typography>

                      {subsection.definitions && (
                        <div>
                          {Object.entries(subsection.definitions).map(
                            ([definition, text], termIndex) => (
                              <Typography
                                key={definition}
                                variant="body1"
                                sx={{ mt: 1, ml: 4 }}
                                dangerouslySetInnerHTML={{ __html: text }}
                              />
                            ),
                          )}
                        </div>
                      )}
                    </div>
                  ))}
              </div>
            );
          })}
        </AccordionDetails>
        <AccordionSummary aria-controls="panel1a-content" id="panel1a-header">
          <Typography variant="h6">{termsData.title}</Typography>
        </AccordionSummary>
        <AccordionDetails>
          {termsData.terms.map((term, index) => {
            return (
              <div key={index}>
                <Typography
                  variant="subtitle2"
                  sx={{ mt: 1, fontWeight: "bold" }}
                >
                  {term.title}
                </Typography>
                {term.list.map((item, indexTerm) => (
                  <div key={`${term.title}-${indexTerm}`}>
                    {item.content.map((paragraph, indexParagraph) => (
                      <Typography
                        key={`${item.number}-${indexParagraph}`}
                        variant="body1"
                        sx={{ mt: 1 }}
                        dangerouslySetInnerHTML={{ __html: paragraph }}
                      />
                    ))}
                  </div>
                ))}
              </div>
            );
          })}

          <Typography variant="subtitle2" sx={{ mt: 1, fontWeight: "bold" }}>
            {termsData.disclaimer.title}
          </Typography>
          <Typography variant="body1">
            {termsData.disclaimer.content}
          </Typography>

          <Typography variant="subtitle2" sx={{ mt: 1, fontWeight: "bold" }}>
            {termsData.liability.title}
          </Typography>
          <Typography variant="body1">{termsData.liability.content}</Typography>

          <Typography variant="subtitle2" sx={{ mt: 1, fontWeight: "bold" }}>
            {termsData.miscellaneous.title}
          </Typography>
          {termsData.miscellaneous.list.map((mis, index) => (
            <Typography key={index} variant="body1" sx={{ mt: 1 }}>
              {mis.number} {mis.content}
            </Typography>
          ))}

          <Typography variant="subtitle2" sx={{ mt: 1, fontWeight: "bold" }}>
            {termsData.contactus.title}
          </Typography>
          <Typography
            variant="body1"
            dangerouslySetInnerHTML={{ __html: termsData.contactus.content }}
          />
        </AccordionDetails>
      </Accordion>

      <div style={{ height: 8 }} />

      <Accordion
        expanded={privacyTerms}
        onChange={() => setPrivacyTerms(!privacyTerms)}
      >
        <AccordionSummary
          expandIcon={<ExpandMoreIcon />}
          aria-controls="panel2a-content"
          id="panel2a-header"
        >
          <Typography variant="h6">{privacyData.title}</Typography>
        </AccordionSummary>
        <AccordionDetails>
          <Typography variant="body1" sx={{ mb: 5, mt: 2 }}>
            {privacyData.date}
          </Typography>
          {privacyData.description.map((paragraph, index) => (
            <Typography key={index} variant="body1" sx={{ mt: 4 }}>
              {paragraph}
            </Typography>
          ))}

          {privacyData.sections.map((section, index) => {
            return (
              <div key={`${section.title}-${index}`}>
                <Typography
                  variant="subtitle2"
                  sx={{ mt: 1, fontWeight: "bold" }}
                >
                  {section.title}
                </Typography>
                {section.subsections &&
                  section.subsections.map((subsection, subIndex) => (
                    <div key={`${subsection.title}-${subIndex}`}>
                      <Typography
                        variant="subtitle2"
                        sx={{ mt: 1, fontWeight: "bold" }}
                      >
                        {subsection.title}
                      </Typography>

                      {subsection.content.map((paragraph, paragraphIndex) => (
                        <Typography
                          key={paragraphIndex}
                          variant="body1"
                          sx={{ mt: 1 }}
                          dangerouslySetInnerHTML={{ __html: paragraph }}
                        />
                      ))}

                      {subsection.extras && (
                        <div>
                          {Object.entries(subsection.extras).map(
                            ([extra, text], termIndex) => (
                              <Typography
                                key={extra}
                                variant="body1"
                                sx={{ mt: 1, ml: 4 }}
                                dangerouslySetInnerHTML={{ __html: text }}
                              />
                            ),
                          )}
                        </div>
                      )}
                    </div>
                  ))}
              </div>
            );
          })}
        </AccordionDetails>
      </Accordion>
    </>
  );
};

export { LegalDocPreview };

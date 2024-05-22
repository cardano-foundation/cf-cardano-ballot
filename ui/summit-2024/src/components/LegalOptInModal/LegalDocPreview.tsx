import React, { useState, useEffect } from "react";
import {
  Accordion,
  AccordionDetails,
  AccordionSummary,
  Typography,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";

const LegalDocPreview = () => {
  const [termsData, setTermsData] = useState(null);
  const [privacyData, setPrivacyData] = useState(null);

  useEffect(() => {
    fetch("/privacyPolicy.json")
      .then((response) => response.json())
      .then((data) => setPrivacyData(data))
      .catch((error) =>
        console.error("Error loading the privacy policy data:", error),
      );

    fetch("/termsAndConditions.json")
      .then((response) => response.json())
      .then((data) => setTermsData(data))
      .catch((error) =>
        console.error("Error loading the terms and conditions data:", error),
      );
  }, []);

  console.log("termsData");
  console.log(termsData);
  console.log("privacyData");
  console.log(privacyData);

  if (!termsData || !privacyData) return <span>Loading...</span>;

  return (
    <>
      <Accordion expanded={true}>
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
          {/* Render sections and content */}
        </AccordionDetails>
      </Accordion>
      {/* Repeat similar structure for privacyData */}
    </>
  );
};

export { LegalDocPreview };

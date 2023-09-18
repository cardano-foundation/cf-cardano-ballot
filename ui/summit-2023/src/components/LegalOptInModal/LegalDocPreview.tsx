import * as React from 'react';
import {
    Accordion,
    AccordionDetails,
    AccordionSummary,
    Typography,
} from '@mui/material';
import termsData from '../../common/resources/data/termsAndConditions.json';
import privacyData from '../../common/resources/data/privacyPolicy.json';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

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
                    <Typography variant="subtitle1" sx={{ mt: 4 }}>
                        {termsData.subtitle}
                    </Typography>
                    {termsData.sections.map((section, index) => {
                        return (
                            <div key={index}>
                                <Typography variant="subtitle2" sx={{ mt: 1 }}>
                                    {section.title}
                                </Typography>
                                <Typography variant="body1">
                                    {section.content}
                                </Typography>
                            </div>
                        );
                    })}
                    {termsData.terms.map((term, index) => {
                        return (
                            <div key={index}>
                                <Typography variant="subtitle2" sx={{ mt: 1 }}>
                                    {term.title}
                                </Typography>
                                {term.list.map((t) => {
                                    return (
                                        <Typography
                                            key={t.number}
                                            variant="body1"
                                        >
                                            {t.number} {t.content}
                                        </Typography>
                                    );
                                })}
                            </div>
                        );
                    })}

                    <Typography variant="subtitle2" sx={{ mt: 1 }}>
                        {termsData.disclaimer.title}
                    </Typography>
                    <Typography variant="body1">
                        {termsData.disclaimer.content}
                    </Typography>

                    <Typography variant="subtitle2" sx={{ mt: 1 }}>
                        {termsData.liability.title}
                    </Typography>
                    <Typography variant="body1">
                        {termsData.liability.content}
                    </Typography>

                    <Typography variant="subtitle2" sx={{ mt: 1 }}>
                        {termsData.miscellaneous.title}
                    </Typography>
                    {termsData.miscellaneous.list.map((mis, index) => (
                        <Typography key={index} variant="body1" sx={{ mt: 1 }}>
                            {mis.number} {mis.content}
                        </Typography>
                    ))}

                    <Typography variant="subtitle2" sx={{ mt: 1 }}>
                        {termsData.contactus.title}
                    </Typography>
                    <Typography variant="body1">
                        {termsData.contactus.content}
                    </Typography>
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
                    <Typography variant="body1" sx={{ mt: 4 }}>
                        {privacyData.description}
                    </Typography>
                    <Typography variant="body2" sx={{ mt: 2 }}>
                        {privacyData.subdescription}
                    </Typography>

                    {privacyData.sections.map((section, index) => {
                        return (
                            <div key={index}>
                                <Typography variant="subtitle2" sx={{ mt: 1 }}>
                                    {section.title}
                                </Typography>
                                <Typography variant="body1">
                                    {section.content}
                                </Typography>
                            </div>
                        );
                    })}
                </AccordionDetails>
            </Accordion>
        </>
    );
};

export { LegalDocPreview };

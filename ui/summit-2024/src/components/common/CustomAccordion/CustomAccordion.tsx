import React, { useRef, useState } from "react";
import { Box, Typography, SxProps, Theme } from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import ExpandLessIcon from "@mui/icons-material/ExpandLess";
import theme from "../../../common/styles/theme";

interface CustomAccordionProps {
  titleClose: string;
  titleOpen?: string;
  description: string;
  sx?: SxProps<Theme>;
  children: React.ReactNode;
}

const CustomAccordion: React.FC<CustomAccordionProps> = ({
  titleClose,
  titleOpen,
  children,
  sx,
}) => {
  const [expanded, setExpanded] = useState(false);
  const contentRef = useRef<HTMLDivElement>(null);

  const toggleAccordion = () => {
    setExpanded(!expanded);
    if (!expanded) {
      setTimeout(() => {
        contentRef.current?.scrollIntoView({ behavior: "smooth" });
      }, 0);
    }
  };

  return (
    <Box sx={sx}>
      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          padding: "8px 16px",
          cursor: "pointer",
          background: theme.palette.background.neutralDark,
          borderRadius: "12px",
        }}
        onClick={toggleAccordion}
      >
        <Typography
          sx={{
            color: theme.palette.background.neutralLightest,
            fontSize: "12px",
            fontStyle: "normal",
            fontWeight: 500,
            linHeight: "20px",
          }}
        >
          {expanded && titleOpen ? titleOpen : titleClose}
        </Typography>
        {expanded ? <ExpandLessIcon /> : <ExpandMoreIcon />}
      </Box>
      <Box
        ref={contentRef}
        sx={{
          marginTop: "20px",
        }}
      >
        {expanded && <>{children}</>}
      </Box>
    </Box>
  );
};

export { CustomAccordion };

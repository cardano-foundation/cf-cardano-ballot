import React from "react";
import { Paper, Typography, Box, Link } from "@mui/material";
import theme from "../../../common/styles/theme";
import guideBg from "../../../assets/bg/guideCard.svg";
import { CustomCardProps } from "./UserGuideCard.type";

const UserGuideCard = ({
  number,
  title,
  description,
  link,
}: CustomCardProps) => {
  return (
    <Paper
      elevation={3}
      sx={{
        width: "100%",
        minHeight: "276px",
        height: "100%",
        flexShrink: 0,
        borderRadius: "24px",
        border: theme.palette.background.default,
        backdropFilter: "blur(5px)",
        position: "relative",
        display: "flex",
        flexDirection: "column",
        padding: "28px",
        backgroundImage: `url(${guideBg})`,
        backgroundSize: "180% 160%",
        backgroundPosition: "center",
      }}
    >
      <Box
        sx={{
          maxWidth: "419px",
        }}
      >
        <Box sx={{ display: "flex", alignItems: "center", paddingLeft: 1 }}>
          <Box
            sx={{
              width: "40px",
              height: "40px",
              borderRadius: "50%",
              border: `2px solid ${theme.palette.text.neutralLightest}`,
              color: "white",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              marginRight: 2,
              padding: "8px 0px",
            }}
          >
            {number}
          </Box>
        </Box>
        <Typography
          sx={{
            marginTop: "24px",
            color: theme.palette.text.neutralLightest,
            fontFamily: "Dosis",
            fontSize: "24px",
            fontStyle: "normal",
            fontWeight: 700,
            lineHeight: "28px",
          }}
        >
          {title}
        </Typography>
        <Typography
          sx={{
            marginTop: "12px",
            color: theme.palette.text.neutralLight,
            fontSize: "16px",
            fontStyle: "normal",
            fontWeight: 500,
            lineHeight: "24px",
            marginBottom: "24px",
          }}
        >
          {description}
        </Typography>
        {link && (
          <Typography
            sx={{
              color: theme.palette.secondary.main,
              fontSize: "16px",
              fontStyle: "normal",
              fontWeight: 500,
              lineHeight: "24px",
              textDecorationLine: "underline",
              marginBottom: "24px",
            }}
          >
            <Link
              href={link.url}
              target="_blank"
              rel="noopener"
              sx={{
                color: theme.palette.secondary.main,
              }}
            >
              {link.label}
            </Link>
          </Typography>
        )}
      </Box>
    </Paper>
  );
};

export { UserGuideCard };

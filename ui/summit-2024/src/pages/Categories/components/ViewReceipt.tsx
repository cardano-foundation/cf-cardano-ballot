import React from "react";
import { Box, IconButton, Paper, Typography } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import theme from "../../../common/styles/theme";
import InfoIcon from "@mui/icons-material/Info";
import RefreshIcon from "@mui/icons-material/Refresh";
import { STATE, ViewReceiptProps } from "./ViewReceipt.type";

const ViewReceipt: React.FC<ViewReceiptProps> = ({ state }) => {
  const getContent = () => {
    switch (state) {
      case STATE.NOT_READY: {
        return {
          leftIcon: <InfoIcon />,
          title: "Vote Not Ready for Verification",
          description:
            "Although your vote has been successfully submitted, you may have to wait up to 30 minutes for this to be visible on chain. Please check back later.",
          iconBottom: (
            <RefreshIcon
              sx={{
                cursor: "pointer",
              }}
            />
          ),
          labelBottom: "Refresh Status",
        };
      }
      default:
        return;
    }
  };
  const content = getContent();
  return (
    <>
      <div
        style={{
          backgroundColor: theme.palette.background.default,
          height: "100%",
        }}
      >
        <Box
          sx={{
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            padding: 1,
            width: "450px",
          }}
        >
          <Typography
            variant="h6"
            sx={{
              flexGrow: 1,
              color: theme.palette.background.neutralLightest,
              textAlign: "center",
              fontFamily: "Dosis",
              fontSize: "28px",
              fontWeight: 700,
              lineHeight: "32px",
            }}
          >
            Vote Receipt
          </Typography>
          <IconButton
            sx={{
              display: "inline-flex",
              padding: "12px",
              borderRadius: "12px",
              background: theme.palette.background.neutralDark,
            }}
          >
            <CloseIcon sx={{ color: "#fff" }} />
          </IconButton>
        </Box>
        <Box
          sx={{
            margin: "28px",
          }}
        >
          <Paper
            sx={{
              display: "flex",
              flexDirection: "column",
              alignItems: "center",
              alignSelf: "stretch",
              color: theme.palette.text.neutralLight,
              fontFamily: "Roboto",
              fontSize: "12px",
              fontWeight: 500,
              lineHeight: "20px",
              borderRadius: "12px",
              background: theme.palette.background.neutralDark,
            }}
          >
            <Box
              sx={{
                display: "flex",
                flexDirection: "row",
                justifyContent: "space-between",
                width: "100%",
                borderBottom: `1px solid ${theme.palette.background.disabled}`,
              }}
            >
              <Box
                sx={{
                  display: "flex",
                  justifyContent: "center",
                  alignItems: "center",
                  width: "60px",
                  borderRight: `1px solid ${theme.palette.background.disabled}`,
                }}
              >
                {content?.leftIcon}
              </Box>

              <Box
                sx={{
                  width: "305px",
                  display: "flex",
                  flexDirection: "column",
                  justifyContent: "space-between",
                  my: "20px",
                }}
              >
                <Typography
                  sx={{
                    color: theme.palette.background.neutralLightest,
                    fontSize: "16px",
                    fontStyle: "normal",
                    fontWeight: 500,
                    lineHeight: "24px",
                  }}
                >
                  {content?.title}
                </Typography>
                <Typography
                  sx={{
                    color: theme.palette.background.neutralLight,
                    fontSize: "12px",
                    fontStyle: "normal",
                    fontWeight: 500,
                    lineHeight: "20px",
                    marginRight: "12px",
                    marginTop: "4px",
                  }}
                >
                  {content?.description}
                </Typography>
              </Box>
            </Box>
            <Box
              sx={{
                display: "flex",
                alignItems: "center",
                alignSelf: "center",
                padding: "12px 24px",
              }}
            >
              {content?.iconBottom}
              <Typography
                sx={{
                  marginLeft: 1,
                  color: theme.palette.background.neutralLight,
                  fontSize: "12px",
                  fontStyle: "normal",
                  fontWeight: 500,
                  lineHeight: "20px",
                  cursor: "pointer",
                }}
              >
                {content?.labelBottom}
              </Typography>
            </Box>
          </Paper>
        </Box>
      </div>
    </>
  );
};

export { ViewReceipt };

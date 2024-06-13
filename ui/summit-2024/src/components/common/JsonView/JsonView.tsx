import React from "react";
import { Box, SxProps, Theme, Tooltip, Typography } from "@mui/material";
import { copyToClipboard } from "../../../utils/utils";
import CheckCircleOutlineIcon from "@mui/icons-material/CheckCircleOutline";
import ContentCopyIcon from "@mui/icons-material/ContentCopy";
import LaunchIcon from "@mui/icons-material/Launch";
import theme from "../../../common/styles/theme";
import {eventBus, EventName} from "../../../utils/EventBus";

interface JsonRendererProps {
  data: string;
  sx?: SxProps<Theme>;
}

const JsonView: React.FC<JsonRendererProps> = ({ data, sx }) => {
  const [copied, setCopied] = React.useState(false);

  const handleCopy = async () => {
    await copyToClipboard(data);
    setCopied(true);
    eventBus.publish(EventName.ShowToast, "Copied to clipboard successfully");
    setTimeout(() => setCopied(false), 1000);
  };

  return (
    <>
      <Box
        component="div"
        sx={{
          width: "100%",
          padding: "12px",
          background: theme.palette.background.neutralDark,
          borderRadius: "4px 4px 0px 0px",
          ...(sx && sx),
        }}
      >
        <Typography
          sx={{
            fontFamily: "monospace",
            whiteSpace: "pre-wrap",
            color: theme.palette.text.neutralLight,
            fontSize: "12px",
            fontWeight: 500,
            lineHeight: "20px",
            fontStyle: "normal",
            wordWrap: "break-word",
          }}
        >
          {data}
        </Typography>
        <Tooltip title="Copy to clipboard">
          <Box
            component="span"
            sx={{
              position: "absolute",
              top: 60,
              right: 25,
              cursor: "pointer",
            }}
          >
            {copied ? (
              <CheckCircleOutlineIcon
                sx={{
                  width: "20px",
                  height: "20px",
                }}
              />
            ) : (
              <ContentCopyIcon
                onClick={handleCopy}
                sx={{
                  width: "20px",
                  height: "20px",
                }}
              />
            )}
          </Box>
        </Tooltip>
      </Box>
      <Box
        component="div"
        sx={{
          borderTop: `1px solid ${theme.palette.background.disabled}`,
          width: "100%",
          background: theme.palette.background.neutralDark,
          display: "flex",
          alignItems: "center",
          justifyContent: "center",
          padding: "10px 24px",
          cursor: "pointer",
        }}
      >
        <Typography
          sx={{
            color: theme.palette.text.neutralLightest,
            fontSize: "12px",
            fontWeight: 500,
            lineHeight: "20px",
            fontStyle: "normal",
          }}
        >
          Verify
        </Typography>
        <LaunchIcon
          sx={{
            width: "16px",
            height: "16px",
            marginLeft: "8px",
          }}
        />
      </Box>
    </>
  );
};

export { JsonView };

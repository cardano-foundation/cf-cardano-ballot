import {Paper, Typography, Box, Button} from "@mui/material";
import theme from "../../../common/styles/theme";
import { CustomCardProps } from "./UserGuideCard.type";
import { eventBus, EventName } from "../../../utils/EventBus";

const UserGuideCard = ({
  number,
  title,
  description,
  link,
}: CustomCardProps) => {
  const handleOpenSupportedWallets = () => {
    eventBus.publish(EventName.OpenSupportedWalletsModal);
  };

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
        padding: "24px",
          backgroundColor: '#3E4C73'
      }}
    >
      <Box
        component="div"
        sx={{
          maxWidth: "419px",
        }}
      >
        <Box
          component="div"
          sx={{ display: "flex", alignItems: "center", paddingLeft: 1 }}
        >
          <Box
            component="div"
            sx={{
              width: "40px",
              height: "40px",
              borderRadius: "8px",
              border: `1px solid ${theme.palette.text.neutralLightest}`,
              color: "white",
              display: "flex",
              alignItems: "center",
              justifyContent: "center",
              marginRight: 2,
              padding: "8px 0px",
              fontWeight: 500,
              fontFamily: "Tomorrow",
              fontSize: "24px",
            }}
          >
            {number}
          </Box>
        </Box>
        <Typography
          sx={{
            marginTop: "24px",
            color: theme.palette.text.primary,
            fontFamily: "Tomorrow",
            fontSize: "24px",
            fontStyle: "normal",
            fontWeight: 500,
            lineHeight: "24px",
          }}
        >
          {title}
        </Typography>
        <Typography
          sx={{
            marginTop: "12px",
            color: theme.palette.text.primary,
            fontSize: "16px",
            fontStyle: "normal",
            fontWeight: 600,
            lineHeight: "24px",
            marginBottom: "24px",
          }}
        >
          {description}
        </Typography>
        {link && (
          <Button
            sx={{
              color: theme.palette.text.primary,
              fontSize: "16px",
              fontStyle: "normal",
              fontWeight: 500,
              lineHeight: "24px",
              marginBottom: "24px",
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
            onClick={() => handleOpenSupportedWallets()}
          >
            {link.label}
          </Button>
        )}
      </Box>
    </Paper>
  );
};

export { UserGuideCard };

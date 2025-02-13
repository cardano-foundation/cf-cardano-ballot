import { Paper, Typography, Box } from "@mui/material";
import theme from "../../../common/styles/theme";
import guideBg from "@assets/guideCard.svg";
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
        padding: "28px",
        backgroundImage: `url(${guideBg})`,
        backgroundSize: "180% 160%",
        backgroundPosition: "center",
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
              cursor: "pointer",
            }}
            onClick={() => handleOpenSupportedWallets()}
          >
            {link.label}
          </Typography>
        )}
      </Box>
    </Paper>
  );
};

export { UserGuideCard };

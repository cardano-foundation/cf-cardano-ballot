import {
  IconButton,
  Snackbar,
  useTheme,
  Box,
  Typography,
  Stack,
} from "@mui/material";
import ErrorIcon from "@mui/icons-material/Error";
import CloseIcon from "@mui/icons-material/Close";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import { ToastProps, ToastType } from "./Toast.types";

const Toast = ({ message, isOpen, type, onClose }: ToastProps) => {
  const theme = useTheme();

  const getStyles = () => {
    switch (type) {
      case ToastType.Verified:
        return {
          backgroundColor: "#030617",
          color: theme.palette.text.primary,
          Icon: CheckCircleIcon,
        };
      case ToastType.Error:
        return {
          backgroundColor: "#030617",
          // @ts-ignore
          color: theme.palette.error.text,
          Icon: ErrorIcon,
        };
      case ToastType.Common:
        return {
          backgroundColor: "#030617",
          color: theme.palette.text.primary,
          Icon: CheckCircleIcon,
        };
      default:
        return {};
    }
  };

  const { backgroundColor, color, Icon } = getStyles();

  return (
    <Snackbar
      open={isOpen}
      onClose={onClose}
      anchorOrigin={{ vertical: "top", horizontal: "center" }}
      autoHideDuration={3000}
      ContentProps={{
        sx: {
          backgroundColor: backgroundColor,
          color: color,
          fontWeight: "600",
          fontSize: "16px",
          lineHeight: "24px",
          padding:"4px 20px"
        },
      }}
      message={
        <Stack direction="row" spacing={1} alignItems="center">
          {/* @ts-ignore */}
          <Icon sx={{width:"20px", height: "20px"}} />
          <Typography variant="body2">{message}</Typography>
        </Stack>
      }
      action={
        <Box component="div" sx={{ display: "flex", alignItems: "center" }}>
          <Box
            component="div"
            sx={{
              backgroundColor: color,
              width: "1px",
              height: 24,
              borderRadius: "12px",
            }}
          />
          <IconButton size="small" onClick={onClose} color="inherit">
            <CloseIcon fontSize="small" sx={{ width: "20px" }} />
          </IconButton>
        </Box>
      }
    />
  );
};

export { Toast };

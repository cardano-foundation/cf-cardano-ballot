import React from 'react';
import { IconButton, Snackbar, useTheme, Box, Typography, Stack } from '@mui/material';
import DoNotDisturbAltIcon from '@mui/icons-material/DoNotDisturbAlt';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import CloseIcon from '@mui/icons-material/Close';
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';
import { ToastProps, ToastType } from './Toast.types';

const Toast = ({ message, isOpen, type, onClose }: ToastProps) => {
  const theme = useTheme();

  const getStyles = () => {
    switch (type) {
      case ToastType.Verified:
        return {
          backgroundColor: theme.palette.background.default,
          color: theme.palette.text.primary,
          Icon: VerifiedUserIcon,
        };
      case ToastType.Error:
        return {
          backgroundColor: theme.palette.primary.dark,
          color: theme.palette.error.text,
          Icon: DoNotDisturbAltIcon,
        };
      case ToastType.Common:
        return {
          backgroundColor: theme.palette.background.default,
          color: theme.palette.text.primary,
          Icon: CheckCircleOutlineIcon,
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
              bgcolor: backgroundColor,
              color: color,
              fontWeight: '400',
              fontSize: '16px',
            },
          }}
          message={
            <Stack direction="row" spacing={1} alignItems="center">
              <Icon />
              <Typography variant="body2">{message}</Typography>
            </Stack>
          }
          action={
            <Box sx={{ display: 'flex', alignItems: 'center' }}>
              <Box sx={{ backgroundColor: color, width: "1px", height: 24, borderRadius: "10px" }} />
              <IconButton size="small" onClick={onClose} color="inherit">
                <CloseIcon fontSize="small" sx={{ width: "20px"}}/>
              </IconButton>
            </Box>
          }
      />
  );
};

export { Toast };

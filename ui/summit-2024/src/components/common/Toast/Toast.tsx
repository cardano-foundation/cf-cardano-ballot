import React from 'react';
import { IconButton, Snackbar } from '@mui/material';
import DoNotDisturbAltIcon from '@mui/icons-material/DoNotDisturbAlt';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import CloseIcon from '@mui/icons-material/Close';
import WarningIcon from '@mui/icons-material/Warning';
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';
import { ToastProps, ToastStylesProps } from './Toast.types';

const Toast = (props: ToastProps) => {
  const { message, isOpen, type, onClose } = props;

  const getStyles = (): ToastStylesProps => {
    switch (type) {
      case 'verified':
        return {
          backgroundColor: '#03021f',
          color: '#F5F9FF',
          icon: <VerifiedUserIcon />,
        };
      case 'error':
        return {
          backgroundColor: '#c20024',
          color: '#F5F9FF',
          icon: <DoNotDisturbAltIcon />,
        };
      case 'warn':
        return {
          backgroundColor: '#FD873C',
          color: '#652701',
          icon: <WarningIcon />,
        };
      case 'common':
        return {
          backgroundColor: '#03021f',
          color: '#F5F9FF',
          icon: <CheckCircleOutlineIcon />,
        };
      default:
        return {};
    }
  };

  const toastStyles = getStyles();

  return (
    <>
      <Snackbar
        ContentProps={{
          sx: {
            background: toastStyles.backgroundColor,
            color: toastStyles.color,
            fontWeight: '400',
            fontSize: '16px',
          },
        }}
        open={isOpen}
        onClose={onClose}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
        autoHideDuration={3000}
        message={
          <span style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            {toastStyles.icon} {message}
          </span>
        }
        action={
          <>
            <div style={{ background: 'lightgray', width: '1px', height: '24px', marginRight: '8px' }}></div>
            <IconButton
              size="small"
              aria-label="close"
              color="inherit"
              onClick={onClose}
            >
              <CloseIcon fontSize="small" />
            </IconButton>
          </>
        }
      />
    </>
  );
};

export { Toast };

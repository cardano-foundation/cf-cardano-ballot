import React from 'react';
import toast from 'react-hot-toast';
import cn from 'classnames';
import { IconButton } from '@mui/material';
import CheckCircleOutlineOutlinedIcon from '@mui/icons-material/CheckCircleOutlineOutlined';
import CloseIcon from '@mui/icons-material/Close';
import styles from './Toast.module.scss';

export const Toast = ({
  message,
  icon,
  error = false,
}: {
  message: string;
  icon?: React.ReactNode;
  error?: boolean;
}) => (
  <div
    data-testid="toast"
    className={cn(styles.toast, { [styles.error]: error })}
  >
    {icon || <CheckCircleOutlineOutlinedIcon className={styles.toastIcon} />}
    <span className={styles.message}>{message}</span>
    <span className={styles.divider} />
    <IconButton
      className={styles.toastCloseBtn}
      aria-label="close"
      onClick={() => toast.dismiss()}
      data-testid="toast-close-button"
    >
      <CloseIcon className={styles.toastClose} />
    </IconButton>
  </div>
);

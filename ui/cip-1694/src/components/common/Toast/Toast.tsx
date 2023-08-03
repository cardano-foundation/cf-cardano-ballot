import React from 'react';
import toast from 'react-hot-toast';
import { IconButton } from '@mui/material';
import CheckCircleOutlineOutlinedIcon from '@mui/icons-material/CheckCircleOutlineOutlined';
import CloseIcon from '@mui/icons-material/Close';
import styles from './Toast.module.scss';

export const Toast = ({ message, icon }: { message: string; icon?: React.ReactNode }) => (
  <div className={styles.toast}>
    {icon || <CheckCircleOutlineOutlinedIcon className={styles.toastIcon} />}
    <span>{message}</span>
    <span className={styles.divider} />
    <IconButton
      aria-label="close"
      onClick={() => toast.dismiss()}
    >
      <CloseIcon className={styles.toastClose} />
    </IconButton>
  </div>
);

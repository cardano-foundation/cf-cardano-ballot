import React from 'react';
import Dialog from '@mui/material/Dialog';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import Button from '@mui/material/Button';
import CloseIcon from '@mui/icons-material/Close';
import styles from './MobileModal.module.scss';

type VoteSubmittedModalProps = {
  name: string;
  id: string;
  openStatus: boolean;
  title: string;
  children: string | React.ReactNode;
  onCloseFn: () => void;
};

export const MobileModal = (props: VoteSubmittedModalProps) => {
  const { name, id, title, children, onCloseFn, openStatus } = props;

  return (
    <Dialog
      open={openStatus}
      aria-labelledby={name}
      PaperProps={{ sx: { width: '100vw', borderRadius: '0px', margin: '0px', height: '100vh', minHeight: '100vh' } }}
    >
      <DialogTitle
        className={styles.dialogTitle}
        id={id}
      >
        {title}
        <Button
          className={styles.closeButton}
          size="large"
          variant="outlined"
          onClick={onCloseFn}
        >
          <CloseIcon className={styles.closeIcon} />
        </Button>
      </DialogTitle>
      <DialogContent className={styles.dialogContent}>{children}</DialogContent>
    </Dialog>
  );
};

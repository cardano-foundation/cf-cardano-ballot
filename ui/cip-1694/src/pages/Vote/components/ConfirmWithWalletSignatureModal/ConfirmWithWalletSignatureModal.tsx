import React from 'react';
import Dialog from '@mui/material/Dialog';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import CloseIcon from '@mui/icons-material/Close';
import DialogTitle from '@mui/material/DialogTitle';
import { Box, Button, IconButton, Typography, CircularProgress } from '@mui/material';
import Grid from '@mui/material/Grid';
import styles from './ConfirmWithWalletSignatureModal.module.scss';

type ConfirmWithWalletSignatureModalProps = {
  name: string;
  id: string;
  openStatus: boolean;
  isConfirming: boolean;
  showCloseBtn: boolean;
  title: string;
  description: string | React.ReactNode;
  onConfirm: () => void;
  onCloseFn: () => void;
};

export const ConfirmWithWalletSignatureModal = (props: ConfirmWithWalletSignatureModalProps) => {
  const { name, id, openStatus, title, description, onConfirm, onCloseFn, isConfirming, showCloseBtn } = props;

  return (
    <Dialog
      open={!!openStatus}
      aria-labelledby={name}
      PaperProps={{ sx: { width: '410px', maxWidth: '410px', borderRadius: '16px' } }}
      data-testid="confirm-with-signature-modal"
    >
      <DialogTitle
        sx={{ padding: { xs: '20px', md: '30px 30px 20px 30px' } }}
        className={styles.dialogTitle}
        id={id}
        data-testid="confirm-with-signature-title"
      >
        {title}
        {showCloseBtn && (
          <IconButton
            aria-label="close"
            onClick={onCloseFn}
            className={styles.closeBtn}
            data-testid="confirm-with-signature-close"
          >
            <CloseIcon className={styles.closeIcon} />
          </IconButton>
        )}
      </DialogTitle>
      <DialogContent
        sx={{ padding: { xs: '20px', md: '0px 30px 30px 30px' } }}
        className={styles.dialogContent}
      >
        <DialogContentText component={'div'}>
          <Grid
            container
            direction="column"
            justifyContent="center"
            alignItems="center"
            gap={'25px'}
          >
            <Grid
              item
              width="100%"
            >
              <Typography
                className={styles.description}
                component="div"
                variant="h5"
                data-testid="confirm-with-signature-description"
              >
                {description}
              </Typography>
            </Grid>
            <Grid
              item
              width="100%"
            >
              <Box width="100%">
                <Button
                  className={styles.button}
                  size="large"
                  variant="contained"
                  onClick={() => onConfirm()}
                  disabled={isConfirming}
                  data-testid="confirm-with-signature-cta"
                >
                  <span className={styles.buttonContent}>
                    Confirm
                    {isConfirming && (
                      <CircularProgress
                        size={20}
                        className={styles.loader}
                        color="inherit"
                      />
                    )}
                  </span>
                </Button>
              </Box>
            </Grid>
          </Grid>
        </DialogContentText>
      </DialogContent>
    </Dialog>
  );
};

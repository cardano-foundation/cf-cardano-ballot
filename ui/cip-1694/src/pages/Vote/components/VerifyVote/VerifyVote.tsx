import React from 'react';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import Dialog from '@mui/material/Dialog';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { Box, Button, Typography } from '@mui/material';
import Grid from '@mui/material/Grid';
import IconButton from '@mui/material/IconButton';
import CloseIcon from '@mui/icons-material/Close';
import styles from './VerifyVote.module.scss';
import { QRCode } from '../QRCode/QRCode';

type VoteSubmittedModalProps = {
  name: string;
  id: string;
  openStatus: boolean;
  onCloseFn: () => void;
};

export const VerifyVoteModal = (props: VoteSubmittedModalProps) => {
  const { name, id, openStatus, onCloseFn } = props;

  return (
    <Dialog
      open={openStatus}
      aria-labelledby={name}
      PaperProps={{ sx: { width: '400px', borderRadius: '16px' } }}
    >
      <DialogTitle
        className={styles.dialogTitle}
        id={id}
      >
        Vote verified <CheckCircleOutlineIcon className={styles.checkIcon} />
        <IconButton
          aria-label="close"
          onClick={onCloseFn}
          className={styles.closeBtn}
        >
          <CloseIcon className={styles.closeIcon} />
        </IconButton>
      </DialogTitle>
      <DialogContent className={styles.dialogContent}>
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
              >
                Your vote has been successfully verified. Click the link or scan the QR code to view the transaction.
              </Typography>
            </Grid>
            <Grid
              item
              width="100%"
              container
            >
              <Grid
                item
                container
                direction="row"
                gap="18px"
                wrap="nowrap"
                sx={{ height: '160px' }}
              >
                <Grid
                  xs={6}
                  item
                  sx={{ height: '160px' }}
                >
                  <Button
                    className={styles.viewTxBtn}
                    size="large"
                    variant="outlined"
                    onClick={() => console.log('show tx details...')}
                  >
                    View transaction details
                  </Button>
                </Grid>
                <Grid
                  xs={6}
                  item
                >
                  <QRCode data="qrCodeData" />
                </Grid>
              </Grid>
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
                  onClick={onCloseFn}
                  sx={{}}
                >
                  Done
                </Button>
              </Box>
            </Grid>
          </Grid>
        </DialogContentText>
      </DialogContent>
    </Dialog>
  );
};

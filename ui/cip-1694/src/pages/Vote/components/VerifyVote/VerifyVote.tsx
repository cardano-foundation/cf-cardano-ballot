import React, { useEffect, useState } from 'react';
import cn from 'classnames';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import Dialog from '@mui/material/Dialog';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { Box, Button, TextareaAutosize, Typography } from '@mui/material';
import Grid from '@mui/material/Grid';
import IconButton from '@mui/material/IconButton';
import CloseIcon from '@mui/icons-material/Close';
import { VoteReceipt } from 'types/backend-services-types';
import styles from './VerifyVote.module.scss';
import { Loader } from '../Loader/Loader';
import { QRCode } from '../QRCode/QRCode';

type VoteSubmittedModalProps = {
  name: string;
  id: string;
  openStatus: boolean;
  onCloseFn: () => void;
};

export const VerifyVoteModal = (props: VoteSubmittedModalProps) => {
  const { name, id, openStatus, onCloseFn } = props;
  const [isVerified, setIsVerified] = useState(false);
  const [isVerifying, setIsVerifying] = useState(false);
  const [coseSignature, setCoseSignature] = useState<VoteReceipt['coseSignature']>('');

  useEffect(() => {
    if (openStatus) {
      setIsVerifying(false);
      setIsVerified(false);
      setCoseSignature('');
    }
  }, [openStatus]);

  const onVerify = () => {
    // TODO: implement verification
    setIsVerifying(true);
    setTimeout(() => {
      setIsVerified(true);
      setIsVerifying(false);
    }, 2000);
  };

  const isDisabled = !coseSignature || isVerifying;

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
        {isVerified ? (
          <>
            Vote verified <CheckCircleOutlineIcon className={styles.checkIcon} />
          </>
        ) : (
          'Verify your vote'
        )}
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
                {isVerified
                  ? 'Your vote has been successfully verified. Click the link or scan the QR code to view the transaction.'
                  : 'To authenticate your vote, please copy and paste your Cose Signature into the input field below. After this, click on the "Verify" button to complete the verification process.'}
              </Typography>
            </Grid>
            <Grid
              item
              width="100%"
              container
            >
              {!isVerified ? (
                <TextareaAutosize
                  disabled={isVerifying || isVerified}
                  className={styles.textArea}
                  onChange={(e) => setCoseSignature(e.target.value)}
                  maxRows={4}
                  aria-label="cose signature"
                  placeholder="Paste your cose signature here"
                />
              ) : (
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
              )}
            </Grid>
            <Grid
              item
              width="100%"
            >
              <Box width="100%">
                <Button
                  disabled={isDisabled}
                  className={cn(styles.button, { [styles.disabled]: isDisabled, [styles.loading]: isVerifying })}
                  size="large"
                  variant="contained"
                  onClick={isVerified ? onCloseFn : onVerify}
                  sx={{}}
                >
                  {isVerifying ? <Loader /> : isVerified ? 'Done' : 'Verify'}
                </Button>
              </Box>
            </Grid>
          </Grid>
        </DialogContentText>
      </DialogContent>
    </Dialog>
  );
};

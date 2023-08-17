import React from 'react';
import cn from 'classnames';
import GppGoodOutlinedIcon from '@mui/icons-material/GppGoodOutlined';
import Dialog from '@mui/material/Dialog';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { Box, Button, Typography, useMediaQuery, useTheme } from '@mui/material';
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
  const theme = useTheme();
  const isSmallScreen = useMediaQuery(theme.breakpoints.up('sm'));
  const { name, id, openStatus, onCloseFn } = props;

  const showTxDetails = () => {
    // TODO: implement
    console.log('show tx details...');
  };
  const qrCodeData = 'qrCodeData';

  return (
    <Dialog
      open={!!openStatus}
      aria-labelledby={name}
      PaperProps={{ sx: { width: '400px', borderRadius: '16px' } }}
    >
      <DialogTitle
        className={styles.dialogTitle}
        padding={{ xs: '20px', sm: '30px 30px 20px 30px' }}
        gap={{ xs: '8px', sm: '12px' }}
        id={id}
      >
        Vote verified{' '}
        <span className={styles.iconCircle}>
          <GppGoodOutlinedIcon className={styles.checkIcon} />
        </span>
        <IconButton
          aria-label="close"
          onClick={onCloseFn}
          className={styles.closeBtn}
        >
          <CloseIcon className={styles.closeIcon} />
        </IconButton>
      </DialogTitle>
      <DialogContent
        sx={{ padding: { xs: '20px', sm: '0px 30px 30px 30px' } }}
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
                direction={{ xs: 'column-reverse', sm: 'row' }}
                gap={{ xs: '24px', sm: '18px' }}
                wrap="nowrap"
                height={{ xs: 'auto', sm: '160px' }}
                justifyContent={'center'}
                alignItems={'center'}
              >
                <Grid
                  xs={6}
                  item
                  height={{ xs: 'auto', sm: '160px' }}
                  width={{ xs: '100%', sm: 'auto' }}
                >
                  <Button
                    className={cn(styles.viewTxBtn, { [styles.xs]: !isSmallScreen })}
                    size="large"
                    variant="outlined"
                    onClick={() => showTxDetails()}
                  >
                    View transaction details
                  </Button>
                </Grid>
                <Grid
                  xs={6}
                  item
                >
                  <QRCode
                    options={{ ...(isSmallScreen && { height: 160, width: 160 }) }}
                    data={qrCodeData}
                  />
                </Grid>
              </Grid>
            </Grid>
            <Grid
              item
              width="100%"
              marginTop={{ xs: '-12px', sm: '0xp' }}
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

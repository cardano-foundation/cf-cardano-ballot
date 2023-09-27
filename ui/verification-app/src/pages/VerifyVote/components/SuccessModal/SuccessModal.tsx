import React, { useLayoutEffect, useState } from 'react';
import cn from 'classnames';
import Dialog from '@mui/material/Dialog';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import CheckIcon from '@mui/icons-material/Check';
import Link from '@mui/material/Link';
import { Fade, Grid, Slide } from '@mui/material';
import { Box } from '@mui/system';
import { QRCode } from 'common/components/QRCode/QRCode';
import styles from './SuccessModal.module.scss';

type SuccessModalProps = {
  opened: boolean;
  explorerLink: string;
};

export const SuccessModal = ({ opened, explorerLink }: SuccessModalProps) => {
  const [isRendered, setIsRendered] = useState(false);
  const [isCheckboxAnimated, setIsCheckboxAnimated] = useState(false);

  useLayoutEffect(() => {
    setIsRendered(true);
  }, []);

  return (
    <Dialog
      disableEscapeKeyDown
      aria-labelledby="dialog-title"
      aria-describedby="dialog-description"
      open={opened}
      maxWidth="xl" // To set width more then 600px
      sx={{ '& .MuiBackdrop-root': { bgcolor: '#F5F9FF' } }}
      PaperProps={{
        sx: {
          padding: '50px',
          alignItems: 'center !important',
          width: '450px',
          height: '607px',
          boxSizing: 'border-box',
          borderRadius: '16px',
          bgcolor: '#F5F9FF',
          boxShadow: '2px 5px 50px 0px rgba(57, 72, 108, 0.20)',
          justifyContent: 'center !important',
        },
      }}
    >
      <Box position="relative" display="flex" alignItems="flex-end" flex="1">
        <Fade
          in={isRendered}
          timeout={1000}
          onEntered={() => setIsCheckboxAnimated(true)}
        >
          <Grid
            className={cn(styles.checkBox, {
              [styles.checkBoxAnimated]: isCheckboxAnimated,
            })}
            container
            justifyContent={'center'}
            alignItems={'center'}
          >
            <Grid
              alignItems={'center'}
              justifyContent={'center'}
              container
              item
              sx={{
                backgroundColor: '#43E4B733',
                width: '100px',
                height: '100px',
                borderRadius: '50%',
              }}
            >
              <Grid
                container
                item
                sx={{
                  backgroundColor: '#43E4B7',
                  width: '80px',
                  height: '80px',
                  borderRadius: '50%',
                }}
                alignItems={'center'}
                justifyContent={'center'}
              >
                <CheckIcon
                  sx={{ width: '56px', height: '56px', color: '#061D3C' }}
                  // classes={{ root: styles.pulse }}
                />
              </Grid>
            </Grid>
          </Grid>
        </Fade>
        <Slide in={isCheckboxAnimated} timeout={1000} direction="up">
          <div>
            <DialogTitle
              id="dialog-title"
              justifyContent="center"
              display="flex"
              sx={{
                padding: '0 0 20px 0',
                color: '#061D3C',
                fontSize: 28,
                fontFamily: 'Roboto',
                fontWeight: '600',
                lineHeight: '28px',
              }}
            >
              Vote verified
            </DialogTitle>

            <DialogContent
              sx={{
                padding: '0',
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
              }}
            >
              <DialogContentText
                id="dialog-description"
                sx={{
                  textAlign: 'center',
                  lineHeight: '22px',
                  pb: '25px',
                  color: '#39486C',
                  fontSize: '16px',
                  fontFamily: 'Roboto',
                  fontWeight: '400',
                  wordWrap: 'break-word',
                }}
              >
                Your vote has been successfully verified. Click the link or scan
                the QR code to view the transaction.
              </DialogContentText>
              <Grid sx={{ pb: '16px' }}>
                <QRCode data={explorerLink} />
              </Grid>
              <Link
                target="_blank"
                href={explorerLink}
                sx={{
                  color: '#1D439B',
                  textDecorationColor: '#1D439B',
                  lineHeight: '22px',
                }}
              >
                View transaction details
              </Link>
            </DialogContent>
          </div>
        </Slide>
      </Box>
    </Dialog>
  );
};

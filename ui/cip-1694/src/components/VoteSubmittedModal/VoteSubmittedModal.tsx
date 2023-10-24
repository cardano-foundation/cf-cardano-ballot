import React from 'react';
import Dialog from '@mui/material/Dialog';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { Box, Button, Typography } from '@mui/material';
import Grid from '@mui/material/Grid';
import IconButton from '@mui/material/IconButton';
import CloseIcon from '@mui/icons-material/Close';
import styles from './VoteSubmittedModal.module.scss';

type VoteSubmittedModalProps = {
  name: string;
  id: string;
  openStatus: boolean;
  title: string;
  description: string | React.ReactNode;
  onCloseFn: () => void;
};

export const VoteSubmittedModal = (props: VoteSubmittedModalProps) => {
  const { name, id, openStatus, title, description, onCloseFn } = props;

  return (
    <Dialog
      onClose={onCloseFn}
      open={!!openStatus}
      aria-labelledby={name}
      PaperProps={{ sx: { width: '400px', borderRadius: '16px' } }}
      data-testid="vote-submitted-modal"
    >
      <DialogTitle
        sx={{ padding: { xs: '20px', md: '30px 30px 20px 30px' } }}
        className={styles.dialogTitle}
        id={id}
        data-testid="vote-submitted-title"
      >
        {title}
        <IconButton
          aria-label="close"
          onClick={onCloseFn}
          className={styles.closeBtn}
          data-testid="vote-submitted-close"
        >
          <CloseIcon className={styles.closeIcon} />
        </IconButton>
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
                data-testid="vote-submitted-description"
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
                  onClick={() => onCloseFn()}
                  sx={{}}
                  data-testid="vote-submitted-cta"
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

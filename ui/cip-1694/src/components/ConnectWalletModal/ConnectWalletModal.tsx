import React from 'react';
import Dialog from '@mui/material/Dialog';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { Box, Typography } from '@mui/material';
import Grid from '@mui/material/Grid';
import IconButton from '@mui/material/IconButton';
import CloseIcon from '@mui/icons-material/Close';
import { ConnectWalletList } from '@cardano-foundation/cardano-connect-with-wallet';
import { env } from '../../env';
import { connectWalletListCustomCss } from './utils';
import styles from './ConnectWalletModal.module.scss';

type ConnectWalletModalProps = {
  name: string;
  id: string;
  openStatus: boolean;
  title: string;
  description: string;
  onConnectWallet: () => void;
  onConnectWalletError: (walletName: string, error: Error) => void;
  onCloseFn: () => void;
};

export const ConnectWalletModal = (props: ConnectWalletModalProps) => {
  const { name, id, openStatus, title, description, onConnectWallet, onConnectWalletError, onCloseFn } = props;
  const supportedWallets = env.SUPPORTED_WALLETS;

  return (
    <Dialog
      data-testid="connected-wallet-modal"
      open={!!openStatus}
      aria-labelledby={name}
      PaperProps={{ sx: { width: '400px', borderRadius: '16px' } }}
    >
      <DialogTitle
        sx={{ padding: { xs: '20px', md: '30px 30px 20px 30px' } }}
        className={styles.dialogTitle}
        id={id}
        data-testid="connected-wallet-modal-title"
      >
        {title}
        <IconButton
          aria-label="close"
          onClick={onCloseFn}
          className={styles.closeBtn}
          data-testid="connected-wallet-modal-close"
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
                data-testid="connected-wallet-modal-description"
              >
                {description}
              </Typography>
            </Grid>
            <Grid
              item
              width="100%"
            >
              <Box width="100%">
                <ConnectWalletList
                  supportedWallets={supportedWallets}
                  onConnect={onConnectWallet}
                  onConnectError={onConnectWalletError}
                  customCSS={connectWalletListCustomCss}
                />
              </Box>
            </Grid>
          </Grid>
        </DialogContentText>
      </DialogContent>
    </Dialog>
  );
};

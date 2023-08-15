import React from 'react';
import Dialog from '@mui/material/Dialog';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { Box, Typography } from '@mui/material';
import Grid from '@mui/material/Grid';
import { useTheme } from '@mui/material/styles';
import IconButton from '@mui/material/IconButton';
import CloseIcon from '@mui/icons-material/Close';
import { ConnectWalletList } from '@cardano-foundation/cardano-connect-with-wallet';
import styles from './ConnectWalletModal.module.scss';
import { env } from '../../env';

type ConnectWalletModalProps = {
  name: string;
  id: string;
  openStatus: boolean;
  title: string;
  description: string;
  onConnectWallet: () => void;
  onCloseFn: () => void;
};

export const ConnectWalletModal = (props: ConnectWalletModalProps) => {
  const theme = useTheme();
  const { name, id, openStatus, title, description, onConnectWallet, onCloseFn } = props;
  const supportedWallets = env.SUPPORTED_WALLETS;

  return (
    <Dialog
      data-testid="connected-wallet-modal"
      open={!!openStatus}
      aria-labelledby={name}
      PaperProps={{ sx: { width: '400px', borderRadius: '16px' } }}
    >
      <DialogTitle
        sx={{ padding: { xs: '20px', ms: '30px 30px 20px 30px' } }}
        className={styles.dialogTitle}
        id={id}
      >
        {title}
        <IconButton
          aria-label="close"
          onClick={onCloseFn}
          className={styles.closeBtn}
        >
          <CloseIcon className={styles.closeIcon} />
        </IconButton>
      </DialogTitle>
      <DialogContent
        sx={{ padding: { xs: '20px', ms: '0px 30px 30px 30px' } }}
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
                  primaryColor={theme.palette.primary.main}
                  onConnect={onConnectWallet}
                  customCSS={`
                    display: flex;
                    flex-direction: column;
                    gap: 10px;
                    max-width: 100%;
                    width: 100%;
                    span {
                      background: #F5F9FF !important;
                      border-radius: 8px !important;
                      border: 1px solid #BBB !important;
                      color: #39486C !important;
                      font-size: 16px !important;
                      font-style: normal;
                      font-weight: 400 !important;
                      height: 54px !important;
                      line-height: 22px !important;
                      padding: 20px;
                      gap: 14px;
                      : hover {
                        border: 1px solid #1D439B !important;
                        background: rgba(29, 67, 155, 0.10) !important;
                        box-shadow: 2px 2px 5px 0px rgba(29, 67, 155, 0.25);
                      }
                    }
                  `}
                />
              </Box>
            </Grid>
          </Grid>
        </DialogContentText>
      </DialogContent>
    </Dialog>
  );
};

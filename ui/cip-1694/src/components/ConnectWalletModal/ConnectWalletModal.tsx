import React from 'react';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { Box, Button, styled } from '@mui/material';
import Grid from '@mui/material/Grid';
import { useTheme } from '@mui/material/styles';
import { ConnectWalletList } from '@cardano-foundation/cardano-connect-with-wallet';

const ConnectGrid = styled(Grid)(({ theme }) => ({
  width: '100%',
  ...theme.typography.body2,
  '& [role="separator"]': {
    margin: theme.spacing(0, 2),
  },
}));

type ConnectWalletModalProps = {
  name: string;
  id: string;
  openStatus: boolean;
  title: string;
  action: boolean;
  onConnectWallet: () => void;
  onCloseFn: () => void;
  buttonLabel: string;
};

const ConnectWalletModal = (props: ConnectWalletModalProps) => {
  const theme = useTheme();
  const { name, id, openStatus, title, action, onConnectWallet, onCloseFn, buttonLabel } = props;

  return (
    <Dialog
      open={openStatus}
      onClose={onCloseFn}
      aria-labelledby={name}
    >
      <DialogTitle id={id}>{title}</DialogTitle>
      <DialogContent>
        <DialogContentText component={'div'}>
          <ConnectGrid
            container
            direction="column"
            justifyContent="center"
            alignItems="center"
          >
            <Grid
              item
              xs={6}
            >
              <Box>
                <ConnectWalletList
                  borderRadius={15}
                  supportedWallets={['flint', 'nami', 'eternl', 'typhon', 'yoroi']}
                  alwaysVisibleWallets={['flint']}
                  primaryColor={theme.palette.primary.main}
                  onConnect={onConnectWallet}
                  customCSS={`
                    width: 170px;
                    button {
                        padding: 6px;
                        font-weight: 700;
                        line-height: 1.7142857142857142;
                        font-size: 0.875rem;
                        font-family: Helvetica Light,sans-serif;
                    }
                    span {
                        padding: 16px;
                        font-family: Helvetica Light,sans-serif;
                        font-size: 0.875rem;
                    }
                  `}
                />
              </Box>
            </Grid>
          </ConnectGrid>
        </DialogContentText>
      </DialogContent>
      {action && (
        <DialogActions>
          <Button
            autoFocus
            onClick={onCloseFn}
          >
            {buttonLabel}
          </Button>
        </DialogActions>
      )}
    </Dialog>
  );
};

export default ConnectWalletModal;

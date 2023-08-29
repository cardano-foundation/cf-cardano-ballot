import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  Button,
  IconButton,
  Drawer,
  List,
  ListItem,
  useTheme,
  useMediaQuery,
  Grid,
  Avatar,
  Snackbar,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import CloseIcon from '@mui/icons-material/Close';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import KeyboardArrowDownIcon from '@mui/icons-material/KeyboardArrowDown';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import DoNotDisturbAltIcon from '@mui/icons-material/DoNotDisturbAlt';
import './Header.scss';
import { i18n } from '../../../i18n';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { addressSlice, walletIcon } from '../../../utils/utils';
import Modal from '../Modal/Modal';
import ConnectWalletList from '../../ConnectWalletList/ConnectWalletList';
import { VerifyWallet } from '../../VerifyWallet';

const Header: React.FC = () => {
  const [drawerOpen, setDrawerOpen] = useState(false);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  const { stakeAddress, isConnected, disconnect, enabledWallet } = useCardano();
  const [openAuthDialog, setOpenAuthDialog] = useState<boolean>(false);
  const [verifyModalIsOpen, setVerifyModalIsOpen] = useState<boolean>(false);
  const [toastMessage, setToastMessage] = useState('');
  const [toastIsError, setToastIsError] = useState(false);
  const [toastOpen, setToastOpen] = useState(false);

  const handleCloseAuthDialog = () => {
    setOpenAuthDialog(false);
  };

  const showToast = (message: string, error?: boolean) => {
    setToastIsError(!!error);
    setToastOpen(true);
    setToastMessage(message);
  };

  const onConnectWallet = () => {
    setOpenAuthDialog(false);
    showToast('Wallet connected successfully');
  };
  const onConnectWalletError = () => {
    setOpenAuthDialog(false);
    showToast('Unable to connect wallet. Please try again', true);
  };

  const onDisconnectWallet = () => {
    disconnect();
    showToast('Wallet disconnected successfully');
  };

  const handleConnectWallet = () => {
    if (!isConnected) {
      setOpenAuthDialog(true);
    }
  };

  const handleOpenVerify = () => {
    setVerifyModalIsOpen(true);
  };

  const handleCloseVerify = () => {
    setVerifyModalIsOpen(false);
  };
  const onVerify = () => {
    showToast('Your wallet has been verified');
    handleCloseVerify();
  };

  const handleToastClose = (event?: Event | React.SyntheticEvent<any, Event>, reason?: string) => {
    if (reason === 'clickaway') {
      return;
    }
    setToastOpen(false);
  };

  const drawerItems = (
    <List>
      <ListItem style={{ justifyContent: 'space-between' }}>
        <Button
          className="connect-button"
          color="inherit"
          onClick={() => setOpenAuthDialog(true)}
        >
          {i18n.t('header.connectWalletButton')}
        </Button>
        <IconButton
          className="close-button"
          onClick={() => setDrawerOpen(false)}
        >
          <CloseIcon className="close-icon" />
        </IconButton>
      </ListItem>
      <ListItem
        onClick={() => setDrawerOpen(false)}
        component={NavLink}
        to="/categories"
        className="list-item"
        style={{ marginTop: '20px' }}
      >
        {i18n.t('header.menu.categories')}
      </ListItem>
      <ListItem
        onClick={() => setDrawerOpen(false)}
        component={NavLink}
        to="/leaderboard"
        className="list-item"
      >
        {i18n.t('header.menu.leaderboard')}
      </ListItem>
      <ListItem
        onClick={() => setDrawerOpen(false)}
        component={NavLink}
        to="/user-guide"
        className="list-item"
      >
        {i18n.t('header.menu.userGuide')}
      </ListItem>
    </List>
  );

  return (
    <>
      <AppBar
        position={isMobile ? 'static' : 'sticky'}
        style={{ background: 'transparent', boxShadow: 'none', color: 'black' }}
      >
        <Toolbar>
          {isMobile ? (
            <>
              <img
                src="/static/cardano-ballot.png"
                alt="Cardano Logo"
                style={{ height: isMobile ? '29px' : '40px' }}
              />
              <div style={{ flexGrow: 1 }}></div>
              <IconButton
                edge="end"
                color="inherit"
                className="menuButton"
                onClick={() => setDrawerOpen(true)}
              >
                <MenuIcon className="close-icon" />
              </IconButton>
            </>
          ) : (
            <Grid
              container
              alignItems="center"
              justifyContent="space-between"
            >
              <Grid item>
                <img
                  src="/static/cardano-ballot.png"
                  alt="Cardano Logo"
                  style={{ flexGrow: 1, height: '40px' }}
                />
              </Grid>
              <Grid item>
                <NavLink
                  to="/categorias"
                  className="nav-link"
                >
                  {i18n.t('header.menu.categories')}
                </NavLink>
                <NavLink
                  to="/leaderboard"
                  className="nav-link"
                >
                  {i18n.t('header.menu.leaderboard')}
                </NavLink>
                <NavLink
                  to="/user-guide"
                  className="nav-link"
                >
                  {i18n.t('header.menu.userGuide')}
                </NavLink>
              </Grid>
              <Grid item>
                <div className="button-container">
                  <Button
                    className={isConnected ? 'connected-button' : 'connect-button'}
                    color="inherit"
                    onClick={() => handleConnectWallet()}
                  >
                    {isConnected && enabledWallet ? (
                      <Avatar
                        src={walletIcon(enabledWallet)}
                        style={{ width: '24px', height: '24px' }}
                      />
                    ) : (
                      <AccountBalanceWalletIcon />
                    )}
                    {isConnected ? (
                      <>
                        {stakeAddress ? addressSlice(stakeAddress, 5) : null}
                        <div className="arrow-icon">
                          <KeyboardArrowDownIcon />
                        </div>
                      </>
                    ) : (
                      <>
                        <span> {i18n.t('header.connectWalletButton')}</span>
                      </>
                    )}
                  </Button>
                  {isConnected && (
                    <div className="disconnect-wrapper">
                      <Button
                        className="connect-button verify-button"
                        color="inherit"
                        onClick={handleOpenVerify}
                      >
                        Verify
                      </Button>
                      <Button
                        className="connect-button disconnect-button"
                        color="inherit"
                        onClick={onDisconnectWallet}
                      >
                        Disconnect wallet
                      </Button>
                    </div>
                  )}
                </div>
              </Grid>
            </Grid>
          )}
        </Toolbar>
      </AppBar>

      <Drawer
        anchor="right"
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
        PaperProps={{
          style: isMobile ? { width: '100%' } : {},
        }}
      >
        {drawerItems}
      </Drawer>

      <Modal
        id="connect-wallet-modal"
        isOpen={openAuthDialog}
        name="connect-wallet-modal"
        title="Connect wallet"
        onClose={handleCloseAuthDialog}
      >
        <ConnectWalletList
          description="In order to vote, first you will need to connect your wallet."
          onConnectWallet={onConnectWallet}
          onConnectError={onConnectWalletError}
        />
      </Modal>
      <Modal
        id="verify-wallet-modal"
        isOpen={verifyModalIsOpen}
        name="verify-wallet-modal"
        title="Verify your wallet"
        onClose={handleCloseVerify}
        disableBackdropClick={true}
      >
        <VerifyWallet onVerify={() => onVerify()} />
      </Modal>
      <Snackbar
        className={`header-toast ${toastIsError ? 'header-toast-error' : ''}`}
        open={toastOpen}
        autoHideDuration={3000}
        onClose={handleToastClose}
        anchorOrigin={{ vertical: 'top', horizontal: 'center' }}
        message={
          <span style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            {toastIsError ? <DoNotDisturbAltIcon /> : <CheckCircleOutlineIcon />} {toastMessage}
          </span>
        }
        action={
          <>
            <div style={{ background: 'lightgray', width: '1px', height: '24px', marginRight: '8px' }}></div>
            <IconButton
              size="small"
              aria-label="close"
              color="inherit"
              onClick={handleToastClose}
            >
              <CloseIcon fontSize="small" />
            </IconButton>
          </>
        }
      />
    </>
  );
};

export default Header;

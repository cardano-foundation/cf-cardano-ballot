import React, { useEffect, useMemo, useState } from 'react';
import { NavLink, useLocation } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  IconButton,
  Drawer,
  List,
  ListItem,
  useTheme,
  useMediaQuery,
  Grid,
  Snackbar,
  Typography,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import CloseIcon from '@mui/icons-material/Close';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import DoNotDisturbAltIcon from '@mui/icons-material/DoNotDisturbAlt';
import './Header.scss';
import { i18n } from '../../../i18n';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import Modal from '../Modal/Modal';
import ConnectWalletList from '../../ConnectWalletList/ConnectWalletList';
import { VerifyWallet } from '../../VerifyWallet';
import { eventBus } from '../../../utils/EventBus';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../../store';
import { NetworkType } from '@cardano-foundation/cardano-connect-with-wallet-core';
import { ConnectWalletButton } from '../ConnectWalletButton/ConnectWalletButton';
import { useToggle } from 'common/hooks/useToggle';
import { CustomButton } from '../Button/CustomButton';
import { getSlotNumber } from 'common/api/voteService';
import { buildCanonicalLoginJson, submitLogin } from 'common/api/loginService';
import { saveUserInSession } from '../../../utils/session';
import { setWalletIsLoggedIn } from '../../../store/userSlice';
import { getSignedMessagePromise } from '../../../utils/utils';

const Header: React.FC = () => {
  const dispatch = useDispatch();

  const [drawerOpen, setDrawerOpen] = useState(false);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const walletIsVerified = useSelector((state: RootState) => state.user.walletIsVerified);

  const { isConnected, stakeAddress, signMessage } = useCardano({ limitNetwork: 'testnet' as NetworkType });

  const [openAuthDialog, setOpenAuthDialog] = useState<boolean>(false);
  const [loginModal, toggleLoginModal] = useToggle(false);
  const [verifyModalIsOpen, setVerifyModalIsOpen] = useState<boolean>(false);
  const [verifyDiscordModalIsReady, toggleVerifyDiscordModalIsOpen] = useToggle(false);
  const [toastMessage, setToastMessage] = useState('');
  const [toastIsError, setToastIsError] = useState(false);
  const [toastOpen, setToastOpen] = useState(false);
  const location = useLocation();

  const signMessagePromisified = useMemo(() => getSignedMessagePromise(signMessage), [signMessage]);

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const action = queryParams.get('action');
    const secret = queryParams.get('secret');

    if (action === 'verification' && secret.includes('|')) {
      toggleVerifyDiscordModalIsOpen();
    }
  }, []);

  useEffect(() => {
    const openConnectWalletModal = () => {
      setOpenAuthDialog(true);
    };
    eventBus.subscribe('openConnectWalletModal', openConnectWalletModal);

    return () => {
      eventBus.unsubscribe('openConnectWalletModal', openConnectWalletModal);
    };
  }, []);

  useEffect(() => {
    const openLoginModal = () => {
      toggleLoginModal();
    };
    eventBus.subscribe('openLoginModal', openLoginModal);

    return () => {
      eventBus.unsubscribe('openLoginModal', openLoginModal);
    };
  }, []);

  useEffect(() => {
    const openVerifyWalletModal = () => {
      setVerifyModalIsOpen(true);
    };
    eventBus.subscribe('openVerifyWalletModal', openVerifyWalletModal);

    return () => {
      eventBus.unsubscribe('openVerifyWalletModal', openVerifyWalletModal);
    };
  }, []);

  const showToast = (message: string, error?: boolean) => {
    setToastIsError(!!error);
    setToastOpen(true);
    setToastMessage(message);
  };

  useEffect(() => {
    const showToastListener = (message: string, error: boolean) => {
      showToast(message, error);
    };
    eventBus.subscribe('showToast', showToastListener);

    return () => {
      eventBus.unsubscribe('showToast', showToastListener);
    };
  }, []);

  const handleCloseAuthDialog = () => {
    setOpenAuthDialog(false);
  };

  const onConnectWallet = () => {
    setOpenAuthDialog(false);
    showToast('Wallet connected successfully');
  };
  const onConnectWalletError = () => {
    setOpenAuthDialog(false);
    showToast('Unable to connect wallet. Please try again', true);
  };

  const handleConnectWallet = () => {
    if (!isConnected) {
      setOpenAuthDialog(true);
    }
  };

  const handleOpenVerify = () => {
    if (!walletIsVerified && isConnected) {
      setVerifyModalIsOpen(true);
    }
  };

  const handleCloseVerify = () => {
    setVerifyModalIsOpen(false);
  };

  const onVerify = () => {
    showToast('Your wallet has been verified');
    handleCloseVerify();
  };

  const onError = (error: string | undefined) => {
    showToast(error, true);
  };

  const handleToastClose = (event?: Event | React.SyntheticEvent<any, Event>, reason?: string) => {
    if (reason === 'clickaway') {
      return;
    }
    setToastOpen(false);
  };

  const handleLogin = async () => {
    const absoluteSlot = (await getSlotNumber())?.absoluteSlot;
    const canonicalVoteInput = buildCanonicalLoginJson({
      stakeAddress,
      slotNumber: absoluteSlot.toString(),
    });
    try {
      const requestVoteObject = await signMessagePromisified(canonicalVoteInput);
      submitLogin(requestVoteObject)
        .then((response) => {
          const session = {
            accessToken: response.accessToken,
            expiresAt: response.expiresAt,
          };
          saveUserInSession(session);
          dispatch(setWalletIsLoggedIn({ isLoggedIn: true }));
          eventBus.publish('showToast', 'Login successfully');
          toggleLoginModal();
        })
        .catch((e) => eventBus.publish('showToast', e.message, true));
    } catch (e) {
      eventBus.publish('showToast', e.message, true);
    }
  };

  const drawerItems = (
    <List>
      <ListItem style={{ justifyContent: 'space-between' }}>
        <ConnectWalletButton
          onOpenConnectWalletModal={handleConnectWallet}
          onOpenVerifyWalletModal={handleOpenVerify}
        />
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
        position={'static'}
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
                <NavLink to="/">
                  <img
                    src="/static/cardano-ballot.png"
                    alt="Cardano Logo"
                    style={{ flexGrow: 1, height: '40px' }}
                  />
                </NavLink>
              </Grid>
              <Grid item>
                <NavLink
                  to="/categories"
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
                <ConnectWalletButton
                  onOpenConnectWalletModal={handleConnectWallet}
                  onOpenVerifyWalletModal={handleOpenVerify}
                />
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
        width={isMobile ? 'auto' : '400px'}
      >
        <ConnectWalletList
          description="In order to vote, first you will need to connect your wallet."
          onConnectWallet={onConnectWallet}
          onConnectError={onConnectWalletError}
        />
      </Modal>
      <Modal
        id="verify-wallet-modal"
        isOpen={verifyModalIsOpen || verifyDiscordModalIsReady}
        name="verify-wallet-modal"
        title="Verify your wallet"
        onClose={handleCloseVerify}
        disableBackdropClick={true}
        width={isMobile ? 'auto' : '400px'}
      >
        <VerifyWallet
          method={verifyDiscordModalIsReady ? 'discord' : undefined}
          onVerify={() => onVerify()}
          onError={(error) => onError(error)}
        />
      </Modal>
      <Modal
        id="login-modal"
        isOpen={isConnected && walletIsVerified && loginModal}
        name="login-modal"
        title="Login with your wallet"
        onClose={toggleLoginModal}
        disableBackdropClick={true}
        width={isMobile ? 'auto' : '500px'}
      >
        <Typography
          variant="h6"
          sx={{ color: '#24262E', fontSize: '18px', fontStyle: 'normal', fontWeight: '400', lineHeight: '22px' }}
        >
          The session has expired. In order to see your votes, please, login again with your wallet.
        </Typography>
        <CustomButton
          styles={{
            background: '#ACFCC5',
            color: '#03021F',
            margin: '24px 0px',
          }}
          label="Login with wallet"
          onClick={() => handleLogin()}
          fullWidth={true}
        />
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

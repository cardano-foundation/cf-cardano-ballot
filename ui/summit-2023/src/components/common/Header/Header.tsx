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
  Typography,
  Button,
  Box,
  Tooltip,
} from '@mui/material';
import MenuIcon from '@mui/icons-material/Menu';
import CloseIcon from '@mui/icons-material/Close';
import FileCopyIcon from '@mui/icons-material/FileCopy';
import './Header.scss';
import { i18n } from '../../../i18n';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import Modal from '../Modal/Modal';
import ConnectWalletList from '../../ConnectWalletList/ConnectWalletList';
import { VerifyWallet } from '../../VerifyWallet';
import { eventBus } from '../../../utils/EventBus';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../../store';
import { ConnectWalletButton } from '../ConnectWalletButton/ConnectWalletButton';
import { useToggle } from 'common/hooks/useToggle';
import { CustomButton } from '../Button/CustomButton';
import { getSlotNumber, getUserVotes } from 'common/api/voteService';
import { buildCanonicalLoginJson, submitLogin } from 'common/api/loginService';
import { clearUserInSessionStorage, saveUserInSession } from '../../../utils/session';
import { setConnectedPeerWallet, setUserVotes, setWalletIsLoggedIn } from '../../../store/userSlice';
import { copyToClipboard, getSignedMessagePromise, resolveCardanoNetwork } from '../../../utils/utils';
import { Toast } from '../Toast/Toast';
import { ToastType } from '../Toast/Toast.types';
import { env } from 'common/constants/env';
import { parseError } from 'common/constants/errors';
import { IWalletInfo } from 'components/ConnectWalletList/ConnectWalletList.types';
import { removeFromLocalStorage } from 'utils/storage';
import QRCode from 'react-qr-code';

const Header: React.FC = () => {
  const dispatch = useDispatch();

  const [drawerOpen, setDrawerOpen] = useState(false);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const isTablet = useMediaQuery(theme.breakpoints.down('md'));
  const walletIsVerified = useSelector((state: RootState) => state.user.walletIsVerified);

  const { stakeAddress, isConnected, disconnect, connect, dAppConnect, meerkatAddress, initDappConnect, signMessage } =
    useCardano({
      limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
    });

  const [openAuthDialog, setOpenAuthDialog] = useState<boolean>(false);
  const [loginModal, toggleLoginModal] = useToggle(false);
  const [loginModalMessage, setLoginModalMessage] = useState<string>('');
  const [verifyModalIsOpen, setVerifyModalIsOpen] = useState<boolean>(false);
  const [verifyDiscordModalIsReady, toggleVerifyDiscordModalIsOpen] = useToggle(false);
  const [toastMessage, setToastMessage] = useState('');
  const [toastType, setToastType] = useState<ToastType>('common');
  const [toastOpen, setToastOpen] = useState(false);
  const eventCache = useSelector((state: RootState) => state.user.event);

  const [cip45ModalIsOpen, setCip45ModalIsOpen] = useState<boolean>(false);
  const [startPeerConnect, setStartPeerConnect] = useState(false);
  const [peerConnectWalletInfo, setPeerConnectWalletInfo] = useState<IWalletInfo>(undefined);
  const [onPeerConnectAccept, setOnPeerConnectAccept] = useState(() => () => {
    /*TODO */
  });
  const [onPeerConnectReject, setOnPeerConnectReject] = useState(() => () => {
    /*TODO */
  });

  const location = useLocation();

  const signMessagePromisified = useMemo(() => getSignedMessagePromise(signMessage), [signMessage]);

  useEffect(() => {
    const queryParams = new URLSearchParams(location.search);
    const action = queryParams.get('action');
    const secret = queryParams.get('secret');

    if (action === 'verification' && secret.includes('|')) {
      toggleVerifyDiscordModalIsOpen();
      setVerifyModalIsOpen(true);
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
    const openLoginModal = (message?: string) => {
      if (message && message.length) setLoginModalMessage(message);
      else setLoginModalMessage('');
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

  const showToast = (message: string, type?: ToastType) => {
    setToastType(type || 'common');
    setToastMessage(message);
    setToastOpen(true);
  };

  useEffect(() => {
    const showToastListener = (message: string, type?: ToastType) => {
      showToast(message, type || 'common');
    };
    eventBus.subscribe('showToast', showToastListener);

    return () => {
      eventBus.unsubscribe('showToast', showToastListener);
    };
  }, []);

  const handleAccept = () => {
    if (peerConnectWalletInfo) {
      onPeerConnectAccept();
      connect(peerConnectWalletInfo.name).then(() => {
        setStartPeerConnect(false);
        setOpenAuthDialog(false);
        setCip45ModalIsOpen(false);
      });
    }
  };

  const handleReject = () => {
    onPeerConnectReject();
    setStartPeerConnect(false);
  };

  const onDisconnectWallet = () => {
    disconnect();
    clearUserInSessionStorage();
    showToast('Wallet disconnected successfully');
    setPeerConnectWalletInfo(undefined);
    removeFromLocalStorage('cardano-peer-autoconnect-id');
    removeFromLocalStorage('cardano-wallet-discovery-address');
  };

  useEffect(() => {
    if (dAppConnect.current === null) {
      const verifyConnection = (
        walletInfo: IWalletInfo,
        callback: (granted: boolean, autoconnect: boolean) => void
      ) => {
        setPeerConnectWalletInfo(walletInfo);
        setStartPeerConnect(true);

        if (walletInfo.requestAutoconnect) {
          //setModalMessage(`Do you want to automatically connect to wallet ${walletInfo.name} (${walletInfo.address})?`);
          setOnPeerConnectAccept(() => () => callback(true, true));
          setOnPeerConnectReject(() => () => callback(false, false));
        } else {
          // setModalMessage(`Do you want to connect to wallet ${walletInfo.name} (${walletInfo.address})?`);
          setOnPeerConnectAccept(() => () => callback(true, false));
          setOnPeerConnectReject(() => () => callback(false, false));
        }
      };

      const onApiInject = (name: string, address: string): void => {
        connect(
          name,
          () => {
            dispatch(setConnectedPeerWallet({ peerWallet: true }));
            showToast('Peer wallet connected successfully');
          },
          () => {
            dispatch(setConnectedPeerWallet({ peerWallet: false }));
            showToast('Peer wallet connected failed', 'error');
          }
        );
      };

      const onApiEject = (name: string, address: string): void => {
        onDisconnectWallet();
        setPeerConnectWalletInfo(undefined);
        showToast('Peer wallet disconnected successfully');
      };

      const onP2PConnect = (address: string, walletInfo?: IWalletInfo): void => {
        // TODO
      };

      initDappConnect(
        'Cardano Summit 2023',
        env.FRONTEND_URL,
        verifyConnection,
        onApiInject,
        onApiEject,
        [],
        onP2PConnect
      );
    }
  }, []);
  const handleCloseAuthDialog = () => {
    setOpenAuthDialog(false);
  };

  const onConnectWallet = () => {
    setOpenAuthDialog(false);
    showToast('Wallet connected successfully');
  };
  const onConnectWalletError = (error: Error) => {
    setOpenAuthDialog(false);
    if (process.env.NODE_ENV === 'development') {
      console.log(error.message);
    }
    showToast('Unable to connect wallet. Please, Review your wallet configuration and try again', 'error');
  };

  const handleConnectWallet = () => {
    if (!isConnected) {
      setOpenAuthDialog(true);
    }
  };

  const handleOpenVerify = () => {
    if (isConnected && !walletIsVerified && !eventCache?.finished) {
      setVerifyModalIsOpen(true);
    }
  };

  const handleCloseVerify = () => {
    setVerifyModalIsOpen(false);
  };

  const onVerify = () => {
    showToast('Your Wallet has been verified', 'verified');
    handleCloseVerify();
  };

  const onError = (errorMessage: string) => {
    showToast(errorMessage, 'error');
  };

  const handleToastClose = (event?: Event | React.SyntheticEvent<any, Event>, reason?: string) => {
    if (reason === 'clickaway') {
      return;
    }
    setToastOpen(false);
  };

  const handleLogin = async () => {
    try {
      const absoluteSlot = (await getSlotNumber())?.absoluteSlot;
      const canonicalVoteInput = buildCanonicalLoginJson({
        stakeAddress,
        slotNumber: absoluteSlot.toString(),
      });
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

          getUserVotes(session?.accessToken)
            .then((userVotes) => {
              if (userVotes) {
                dispatch(setUserVotes({ userVotes }));
              }
            })
            .catch((e) => {
              eventBus.publish('showToast', parseError(e.message), 'error');
            });
        })
        .catch((e) => eventBus.publish('showToast', 'Login failed', 'error'));
    } catch (e) {
      eventBus.publish('showToast', parseError(e.message), 'error');
    }
  };

  const handleOpenPeerConnect = () => {
    setOpenAuthDialog(false);
    setCip45ModalIsOpen(true);
  };

  const drawerItems = (
    <List>
      <ListItem style={{ justifyContent: 'space-between' }}>
        <ConnectWalletButton
          onOpenConnectWalletModal={handleConnectWallet}
          onOpenVerifyWalletModal={handleOpenVerify}
          onLogin={handleLogin}
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
        <Toolbar sx={{ pl: 0, pt: 2 }}>
          {isTablet ? (
            <>
              <NavLink to="/">
                <img
                  src="/static/cardano-ballot.png"
                  alt="Cardano Ballot Logo"
                  style={{ height: isMobile ? '35px' : '40px' }}
                />
              </NavLink>
              <div style={{ flexGrow: 1 }}></div>
              <IconButton
                edge="end"
                color="inherit"
                className="menu-button"
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
                    alt="Cardano Ballot Logo"
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
                  onLogin={handleLogin}
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
        title="Connect Wallet"
        onClose={handleCloseAuthDialog}
        width={isMobile ? 'auto' : '400px'}
      >
        <ConnectWalletList
          description="In order to vote, first you will need to connect your Wallet."
          onConnectWallet={onConnectWallet}
          onConnectError={(error: Error) => onConnectWalletError(error)}
          onOpenPeerConnect={() => handleOpenPeerConnect()}
        />
      </Modal>
      <Modal
        id="verify-wallet-modal"
        isOpen={verifyModalIsOpen}
        name="verify-wallet-modal"
        title="Verify your wallet"
        onClose={handleCloseVerify}
        disableBackdropClick={true}
        width={isMobile ? '100%' : '400px'}
      >
        <VerifyWallet
          method={verifyDiscordModalIsReady ? 'discord' : undefined}
          onVerify={() => onVerify()}
          onError={(error) => onError(error)}
        />
      </Modal>
      <Modal
        id="login-modal"
        isOpen={isConnected && loginModal}
        name="login-modal"
        title="Login with your Wallet"
        onClose={toggleLoginModal}
        disableBackdropClick={true}
        width={isMobile ? 'auto' : '500px'}
      >
        <Typography
          variant="h6"
          sx={{ color: '#24262E', fontSize: '18px', fontStyle: 'normal', fontWeight: '400', lineHeight: '22px' }}
        >
          {loginModalMessage.length
            ? loginModalMessage
            : 'The session has expired. In order to see your votes, please, login again with your Wallet.'}
        </Typography>
        <CustomButton
          styles={{
            background: '#ACFCC5',
            color: '#03021F',
            margin: '24px 0px',
          }}
          label="Login with Wallet"
          onClick={() => handleLogin()}
          fullWidth={true}
        />
      </Modal>

      <Modal
        id="cip45-wallet-modal"
        isOpen={cip45ModalIsOpen}
        name="cip45-wallet-modal"
        title="Connect Peer Wallet"
        onClose={() => setCip45ModalIsOpen(false)}
        disableBackdropClick={true}
      >
        {!startPeerConnect ? (
          <>
            <Typography
              variant="body1"
              align="left"
              sx={{
                width: '344px',
                color: '#434656',
                fontSize: '16px',
                fontStyle: 'normal',
                fontWeight: '400',
                lineHeight: '22px',
              }}
            >
              To connect your mobile wallet, simply use your wallet's app to scan the QR code below. If scanning isn't
              an option, you can also copy the Peer ID.{' '}
              <span style={{ fontSize: '14px', fontStyle: 'italic', cursor: 'pointer' }}>
                {' '}
                <a
                  href="https://github.com/cardano-foundation/CIPs/pull/395"
                  target="_blank"
                  rel="noreferrer"
                >
                  Learn more about CIP-45
                </a>
              </span>
            </Typography>
            <div style={{ display: 'flex', justifyContent: 'center', width: '100%', marginTop: '24px' }}>
              <QRCode
                size={256}
                style={{ height: 'auto', width: '200px' }}
                value={meerkatAddress}
                viewBox={'0 0 256 256'}
              />
            </div>
            <div
              onClick={() => {
                copyToClipboard(meerkatAddress)
                  .then(() => eventBus.publish('showToast', 'Copied to clipboard'))
                  .catch(() => eventBus.publish('showToast', 'Copied to clipboard failed', 'error'));
              }}
              style={{
                display: 'flex',
                justifyContent: 'center',
                alignItems: 'center',
                width: '100%',
                marginTop: '24px',
                cursor: 'pointer',
              }}
            >
              <FileCopyIcon
                fontSize="small"
                style={{ color: '#434656', cursor: 'pointer' }}
              />
              <Typography
                variant="body1"
                align="center"
                sx={{
                  color: '#434656',
                  fontSize: '16px',
                  fontStyle: 'normal',
                  fontWeight: '600',
                  lineHeight: '18.75px',
                  cursor: 'pointer',
                  marginLeft: '8px',
                }}
              >
                Copy Peer ID
              </Typography>
            </div>
            <Box
              sx={{
                display: 'flex',
                justifyContent: 'center',
                marginTop: '8px',
              }}
            >
              <Tooltip title="Currently, only the beta version of the Eternl wallet v1.11.15 is fully supporting CIP-45. Mobile support will be available soon.">
                <span style={{ fontSize: '14px', fontStyle: 'italic', cursor: 'pointer' }}>
                  {' '}
                  <a
                    href="https://beta.eternl.io/"
                    target="_blank"
                    rel="noreferrer"
                  >
                    Using Eternl Wallet
                  </a>
                </span>
              </Tooltip>
            </Box>
            <Button
              onClick={() => {
                setStartPeerConnect(false);
                setOpenAuthDialog(true);
              }}
              className="vote-nominee-button"
              style={{
                display: 'flex',
                width: '344px',
                padding: '12px',
                justifyContent: 'center',
                alignItems: 'center',
                gap: '10px',
                borderRadius: '8px',
                background: 'transparent',
                border: '1px solid #DAEEFB',
                color: '#03021F',
                fontSize: '16px',
                fontStyle: 'normal',
                fontWeight: '600',
                lineHeight: 'normal',
                textTransform: 'none',
                marginTop: '24px',
                marginBottom: '28px',
              }}
            >
              Cancel
            </Button>
          </>
        ) : (
          <>
            <Box
              display="flex"
              flexDirection="column"
              alignItems="center"
              justifyContent="center"
            >
              <img
                src={peerConnectWalletInfo?.icon}
                alt="Wallet"
                style={{ width: '64px', marginTop: '44px' }}
              />
              <Typography
                variant="body1"
                align="left"
                sx={{
                  textAlign: 'center',
                  color: '#434656',
                  fontSize: '18px',
                  fontStyle: 'normal',
                  fontWeight: '500',
                  lineHeight: '22px',
                  marginTop: '24px',
                  marginBottom: '44px',
                }}
              >
                <span style={{ textTransform: 'capitalize', fontStyle: 'italic', fontWeight: '600' }}>
                  {peerConnectWalletInfo?.name}{' '}
                </span>
                wallet is trying to connect
              </Typography>
              <Button
                onClick={handleAccept}
                className="vote-nominee-button"
                style={{
                  display: 'flex',
                  padding: '12px',
                  justifyContent: 'center',
                  alignItems: 'center',
                  gap: '10px',
                  borderRadius: '8px',
                  background: '#ACFCC5',
                  color: '#03021F',
                  fontSize: '16px',
                  fontStyle: 'normal',
                  fontWeight: '600',
                  lineHeight: 'normal',
                  textTransform: 'none',
                }}
                fullWidth
              >
                Accept connection
              </Button>
              <Button
                onClick={handleReject}
                className="vote-nominee-button"
                style={{
                  display: 'flex',
                  width: '344px',
                  padding: '12px',
                  justifyContent: 'center',
                  alignItems: 'center',
                  gap: '10px',
                  borderRadius: '8px',
                  background: 'transparent',
                  border: '1px solid #DAEEFB',
                  color: '#03021F',
                  fontSize: '16px',
                  fontStyle: 'normal',
                  fontWeight: '600',
                  lineHeight: 'normal',
                  textTransform: 'none',
                  marginTop: '10px',
                }}
              >
                Deny
              </Button>
            </Box>
          </>
        )}
      </Modal>
      <Toast
        isOpen={toastOpen}
        type={toastType}
        message={toastMessage}
        onClose={handleToastClose}
      />
    </>
  );
};

export default Header;

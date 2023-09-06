import React, { useState, useEffect, useMemo } from 'react';
import { useParams } from 'react-router-dom';
import { v4 as uuidv4 } from 'uuid';
import {
  useTheme,
  useMediaQuery,
  Typography,
  IconButton,
  Grid,
  Card,
  CardContent,
  Button,
  Drawer,
  Container,
  Box,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import ViewModuleIcon from '@mui/icons-material/ViewModule';
import ViewListIcon from '@mui/icons-material/ViewList';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import { Fade } from '@mui/material';
import './Nominees.scss';
import { eventBus } from '../../utils/EventBus';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import CloseIcon from '@mui/icons-material/Close';
import xIcon from '../../common/resources/images/x-icon.svg';
import linkedinIcon from '../../common/resources/images/linkedin-icon.svg';
import { ROUTES } from '../../routes';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import { buildCanonicalVoteInputJson, castAVoteWithDigitalSignature, getSlotNumber } from '../../common/api/voteService';
import { getSignedMessagePromise } from '../../utils/utils';
import { buildCanonicalLoginJson, submitLogin } from 'common/api/loginService';
import { saveUserInSession } from '../../utils/session';
import { setWalletIsLoggedIn } from '../../store/userSlice';

const Nominees = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const eventCache = useSelector((state: RootState) => state.user.event);
  const walletIsVerified = useSelector((state: RootState) => state.user.walletIsVerified);
  const walletIsLoggedIn = useSelector((state: RootState) => state.user.walletIsLoggedIn);
  const dispatch = useDispatch();
  const categories_ids = eventCache.categories.map((e) => e.id);
  if (!categories_ids.includes(id)) navigate(ROUTES.NOT_FOUND);

  const category = eventCache.categories.filter((c) => c.id === id)[0];

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const [listView, setListView] = useState<'grid' | 'list'>('grid');
  const [isVisible, setIsVisible] = useState(true);
  const [drawerOpen, setDrawerOpen] = useState(false);

  const { isConnected, stakeAddress, signMessage } = useCardano();

  const signMessagePromisified = useMemo(() => getSignedMessagePromise(signMessage), [signMessage]);

  const handleListView = (viewType: 'grid' | 'list') => {
    if (listView === viewType) return;

    setIsVisible(false);
    setTimeout(() => {
      setListView(viewType);
      setIsVisible(true);
    }, 300);
  };

  const login = async () => {
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
        })
        .catch((e) => eventBus.publish('showToast', e.message, true));
    } catch (e) {
      eventBus.publish('showToast', e.message, true);
    }
  };

  useEffect(() => {
    if (isMobile) {
      setListView('list');
    }
  }, [isMobile]);

  const castVote = async (optionId: string) => {
    const absoluteSlot = (await getSlotNumber())?.absoluteSlot;
    const canonicalVoteInput = buildCanonicalVoteInputJson({
      voteId: uuidv4(),
      categoryId: id,
      proposalId: optionId,
      stakeAddress,
      slotNumber: absoluteSlot.toString(),
    });
    try {
      const requestVoteObject = await signMessagePromisified(canonicalVoteInput);
      await castAVoteWithDigitalSignature(requestVoteObject);
      eventBus.publish('showToast', 'Vote submitted successfully');
    } catch (e) {
      eventBus.publish('showToast', e.message, true);
    }
  };

  const handleNomineeButton = (nomineeId: string) => {
    if (isConnected) {
      if (!walletIsVerified) {
        eventBus.publish('openVerifyWalletModal');
      } else {
        castVote(nomineeId);
      }
    } else {
      eventBus.publish('openConnectWalletModal');
    }
  };

  const renderNomineeButtonLabel = () => {
    if (isConnected) {
      if (!walletIsVerified) {
        return 'Verify your wallet';
      } else {
        return 'Vote for nominee';
      }
    } else {
      return (
        <>
          <AccountBalanceWalletIcon /> Connect wallet
        </>
      );
    }
  };

  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <Typography
          className="nominees-title"
          variant="h4"
        >
          {id}
        </Typography>
        {!isMobile && (
          <div>
            <IconButton onClick={() => handleListView('grid')}>
              <ViewModuleIcon />
            </IconButton>
            <IconButton onClick={() => handleListView('list')}>
              <ViewListIcon />
            </IconButton>
          </div>
        )}
      </div>

      <Typography
        className="nominees-description"
        style={{ width: isMobile ? '360px' : '414px' }}
        variant="body1"
        gutterBottom
      >
        To commemorate the special commitment and work of a Cardano Ambassador.
      </Typography>

      <Box
        sx={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          backgroundColor: walletIsLoggedIn ? 'rgba(5, 97, 34, 0.07)' : 'rgba(253, 135, 60, 0.07)',
          padding: '10px 20px',
          borderRadius: '8px',
          border: walletIsLoggedIn ? '1px solid #056122' : '1px solid #FD873C',
          color: 'white',
          width: '100%',
          marginBottom: '20px',
        }}
      >
        <div style={{ display: 'flex', alignItems: 'center' }}>
          {walletIsLoggedIn ? (
            <VerifiedUserIcon sx={{ marginRight: '8px', width: '24px', height: '24px', color: '#056122' }} />
          ) : (
            <WarningAmberIcon sx={{ marginRight: '8px', width: '24px', height: '24px', color: '#FD873C' }} />
          )}

          <Typography
            variant="h6"
            style={{ color: '#24262E', fontSize: '18px', fontStyle: 'normal', fontWeight: '600', lineHeight: '22px' }}
          >
            To see you vote receipt, please sign with your wallet
          </Typography>
        </div>
        <Button
          onClick={() => login()}
          variant="contained"
          color="primary"
          sx={{
            display: 'inline-flex',
            padding: '16px 24px',
            justifyContent: 'center',
            alignItems: 'center',
            gap: '10px',
            borderRadius: '8px',
            background: '#03021F',
            color: '#F6F9FF',
            fontSize: '16px',
            fontStyle: 'normal',
            fontWeight: '600',
            lineHeight: 'normal',
            textTransform: 'none',
          }}
        >
          {walletIsLoggedIn ? 'View vote receipt' : 'Login with wallet'}
        </Button>
      </Box>
      <Grid
        container
        spacing={3}
        style={{ justifyContent: 'center' }}
      >
        {/* TODO: update types from backend*/}
        {/* eslint-disable-next-line @typescript-eslint/ban-ts-comment */}
        {/* @ts-ignore */}
        {category?.proposals.map((item) => (
          <Grid
            item
            xs={!isMobile && listView === 'grid' ? 4 : 12}
            key={item.id}
          >
            <Fade in={isVisible}>
              <Card
                className={'nominee-card'}
                style={{
                  padding: '8px',
                  width: listView === 'list' ? '100%' : '414px',
                  height: !isMobile && listView === 'list' ? 'auto' : isMobile ? '440px' : '390px',
                }}
              >
                <CardContent>
                  <Typography
                    className="nominee-title"
                    variant="h5"
                  >
                    {item.id}
                  </Typography>
                  <Grid container>
                    <Grid
                      item
                      xs={!isMobile && listView === 'list' ? 10 : 12}
                    >
                      <Typography
                        className="nominee-description"
                        variant="body2"
                      >
                        {item.presentationName}
                      </Typography>
                    </Grid>
                    {!isMobile && listView === 'list' ? (
                      <Grid
                        item
                        xs={2}
                      >
                        <Button
                          className={`${isConnected ? 'vote-nominee-button' : 'connect-wallet-button'}`}
                          style={{ width: 'auto' }}
                          onClick={() => handleNomineeButton(item.id)}
                        >
                          {renderNomineeButtonLabel()}
                        </Button>
                      </Grid>
                    ) : null}
                  </Grid>

                  <Button
                    className="read-more-button"
                    fullWidth
                    style={{
                      width: !isMobile && listView === 'list' ? '146px' : '98%',
                      marginTop: !isMobile && listView === 'list' ? '15px' : '28px',
                    }}
                    onClick={() => setDrawerOpen(true)}
                  >
                    Read more
                  </Button>
                  {isMobile || listView === 'grid' ? (
                    <Button
                      className={`${isConnected ? 'vote-nominee-button' : 'connect-wallet-button'}`}
                      fullWidth
                      onClick={() => handleNomineeButton(item.id)}
                    >
                      {renderNomineeButtonLabel()}
                    </Button>
                  ) : null}
                </CardContent>
              </Card>
            </Fade>
          </Grid>
        ))}
      </Grid>
      <Drawer
        anchor="right"
        open={drawerOpen}
        onClose={() => setDrawerOpen(false)}
      >
        <Grid
          container
          p={1}
        >
          <Grid
            item
            xs={11}
          />
          <Grid
            item
            xs={1}
          >
            <IconButton
              className="closeButton"
              onClick={() => setDrawerOpen(false)}
              aria-label="close"
              style={{ float: 'right' }}
            >
              <CloseIcon />
            </IconButton>
          </Grid>
        </Grid>

        <Container style={{ margin: '5px' }}>
          <Typography
            variant="h5"
            gutterBottom
            className="nominee-slide-title"
          >
            Nominee
          </Typography>

          <Typography
            variant="subtitle1"
            gutterBottom
            className="nominee-slide-subtitle"
          >
            Company Name
          </Typography>

          <Grid
            container
            spacing={1}
            marginTop={1}
            marginBottom={2}
          >
            <Grid item>
              <IconButton
                className="nominee-social-button"
                aria-label="X"
              >
                <img
                  src={xIcon}
                  alt="X"
                  style={{ width: '20px' }}
                />
              </IconButton>
            </Grid>
            <Grid item>
              <IconButton
                className="nominee-social-button"
                aria-label="Linkedin"
              >
                <img
                  src={linkedinIcon}
                  alt="Linkedin"
                  style={{ width: '20px' }}
                />
              </IconButton>
            </Grid>
          </Grid>

          <Typography
            variant="body2"
            paragraph
            style={{ maxWidth: '490px' }}
            className="nominee-slide-description"
          >
            Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et
            dolore magna aliqua. Habitant morbi tristique senectus et netus. In massa tempor nec feugiat nisl pretium
            fusce id. Scelerisque felis imperdiet proin fermentum leo vel orci. Tortor condimentum lacinia quis vel eros
            donec ac. Malesuada bibendum arcu vitae elementum curabitur vitae nunc sed velit. Nunc aliquet bibendum enim
            facilisis gravida neque convallis a. Egestas pretium aenean pharetra magna ac placerat vestibulum. Volutpat
            maecenas volutpat blandit aliquam etiam.
          </Typography>

          <Button
            className="visit-web-button"
            href={'#'}
            fullWidth
          >
            Visit Website
          </Button>
        </Container>
      </Drawer>
    </>
  );
};

export { Nominees };

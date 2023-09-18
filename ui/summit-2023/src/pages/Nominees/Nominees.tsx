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
  Container,
  Box,
  Tooltip,
  Accordion,
  AccordionSummary,
  AccordionDetails,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import ViewModuleIcon from '@mui/icons-material/ViewModule';
import ViewListIcon from '@mui/icons-material/ViewList';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';
import NotificationsIcon from '@mui/icons-material/Notifications';
import QrCodeIcon from '@mui/icons-material/QrCode';
import RefreshIcon from '@mui/icons-material/Refresh';
import InfoIcon from '@mui/icons-material/Info';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import { Fade } from '@mui/material';
import './Nominees.scss';
import { CategoryContent } from '../Categories/Category.types';
import { ProposalContent } from './Nominees.type';
import SUMMIT2023CONTENT from '../../common/resources/data/summit2023Content.json';
import { eventBus } from '../../utils/EventBus';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import CloseIcon from '@mui/icons-material/Close';
import { ROUTES } from '../../routes';
import { useDispatch, useSelector } from 'react-redux';
import { RootState } from '../../store';
import {
  buildCanonicalVoteInputJson,
  castAVoteWithDigitalSignature,
  getSlotNumber,
  getVoteReceipt,
} from '../../common/api/voteService';
import { copyToClipboard, getSignedMessagePromise } from '../../utils/utils';
import { buildCanonicalLoginJson, submitLogin } from 'common/api/loginService';
import { getUserInSession, saveUserInSession, tokenIsExpired } from '../../utils/session';
import { setVoteReceipt, setWalletIsLoggedIn } from '../../store/userSlice';
import { FinalityScore } from '../../types/voting-ledger-follower-types';
import { ProposalPresentation } from '../../types/voting-ledger-follower-types';
import SidePage from '../../components/common/SidePage/SidePage';
import { useToggle } from 'common/hooks/useToggle';
import ReadMore from './ReadMore';
import Modal from '../../components/common/Modal/Modal';
import QRCode from 'react-qr-code';
import { NetworkType } from '@cardano-foundation/cardano-connect-with-wallet-core';
import { CustomButton } from '../../components/common/Button/CustomButton';

const Nominees = () => {
  const { categoryId } = useParams();
  const navigate = useNavigate();
  const eventCache = useSelector((state: RootState) => state.user.event);
  const walletIsVerified = useSelector((state: RootState) => state.user.walletIsVerified);
  const walletIsLoggedIn = useSelector((state: RootState) => state.user.walletIsLoggedIn);
  const receipts = useSelector((state: RootState) => state.user.receipts);
  const receipt = receipts && Object.keys(receipts).length && receipts[categoryId] ? receipts[categoryId] : undefined;

  const dispatch = useDispatch();

  const categories = eventCache?.categories;

  const summit2023Category: CategoryContent = SUMMIT2023CONTENT.categories.find(
    (category) => category.id === categoryId
  );
  const summit2023CategoryNominees: ProposalContent[] = summit2023Category.proposals;
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const [listView, setListView] = useState<'grid' | 'list'>('grid');
  const [isVisible, setIsVisible] = useState(true);
  const [isToggleReadMore, toggleReadMore] = useToggle(false);
  const [isViewVoteReceipt, toggleViewVoteReceipt] = useToggle(false);
  const [isViewFinalReceipt, toggleViewFinalReceipt] = useToggle(false);
  const [confirmVoteModal, toggleConfirmVoteModal] = useToggle(false);
  const [selectedNominee, setSelectedNominee] = useState({});
  const [selectedNomineeToVote, setSelectedNomineeToVote] = useState(undefined);
  const [nominees, setNominees] = useState<ProposalPresentation[]>([]);

  const { isConnected, stakeAddress, signMessage } = useCardano({ limitNetwork: 'testnet' as NetworkType });

  const signMessagePromisified = useMemo(() => getSignedMessagePromise(signMessage), [signMessage]);

  const loadNominees = () => {
    if (categoryId) {
      categories?.map((category) => {
        if (category.id === categoryId) {
          setNominees(category?.proposals || []);
        }
      });
    } else {
      navigate(ROUTES.NOT_FOUND);
    }
  };

  useEffect(() => {
    loadNominees();
  }, [categories]);

  const handleListView = (viewType: 'grid' | 'list') => {
    if (listView === viewType) return;

    setIsVisible(false);
    setTimeout(() => {
      setListView(viewType);
      setIsVisible(true);
    }, 300);
  };

  const viewVoteReceipt = async (toast?: boolean, toggle?: boolean) => {
    const session = getUserInSession();

    if (receipt && toggle) {
      toggleViewVoteReceipt();
    }

    if (!tokenIsExpired(session?.expiresAt)) {
      await getVoteReceipt(categoryId, session?.accessToken)
        .then((r) => {
          dispatch(setVoteReceipt({ categoryId: categoryId, receipt: r }));
          if (toggle !== false) toggleViewVoteReceipt();
        })
        .catch((e) => {
          if (toast !== false) {
            eventBus.publish('showToast', e.message, true);
          }
        });
    } else {
      if (toast !== false) {
        eventBus.publish('showToast', 'Please, login before get receipt', true);
      }
    }
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
          viewVoteReceipt(false, true);
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
      categoryId: categoryId,
      proposalId: optionId,
      stakeAddress,
      slotNumber: absoluteSlot.toString(),
    });
    try {
      const requestVoteObject = await signMessagePromisified(canonicalVoteInput);
      toggleConfirmVoteModal();
      eventBus.publish('showToast', 'Vote sent');
      await castAVoteWithDigitalSignature(requestVoteObject);
      eventBus.publish('showToast', 'Vote submitted successfully');
    } catch (e) {
      eventBus.publish('showToast', e.message, true);
    }
  };

  const handleNomineeButton = (nominee) => {
    if (isConnected) {
      if (!walletIsVerified) {
        eventBus.publish('openVerifyWalletModal');
      } else {
        toggleConfirmVoteModal();
        setSelectedNomineeToVote(nominee);
      }
    } else {
      eventBus.publish('openConnectWalletModal');
    }
  };

  const handleVoteNomineeButton = () => {
    if (isConnected) {
      if (!walletIsVerified) {
        eventBus.publish('openVerifyWalletModal');
      } else {
        castVote(selectedNomineeToVote.id);
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

  const handleCopyToClipboard = (text: string) => {
    copyToClipboard(text)
      .then(() => eventBus.publish('showToast', 'Copied to clipboard'))
      .catch(() => eventBus.publish('showToast', 'Copied to clipboard failed', true));
  };
  const getAssuranceTheme = () => {
    // TODO

    const finalityScore: FinalityScore = receipt?.finalityScore;

    switch (finalityScore) {
      case 'VERY_HIGH':
        return {
          backgroundColor: 'rgba(16, 101, 147, 0.07)',
          color: '#056122',
        };
      case 'HIGH':
        return {
          backgroundColor: 'rgba(16, 101, 147, 0.07)',
          color: '#056122',
        };
      case 'MEDIUM':
        return {
          backgroundColor: 'rgba(16, 101, 147, 0.07)',
          color: '#652701',
        };
      case 'LOW':
        return {
          backgroundColor: 'rgba(16, 101, 147, 0.07)',
        };
      case 'FINAL':
        return {
          backgroundColor: 'rgba(16, 101, 147, 0.07)',
          color: '#056122',
        };
      default:
        return {
          backgroundColor: 'rgba(16, 101, 147, 0.07)',
          color: '#106593',
        };
    }
  };

  const handleReadMore = (nominee) => {
    setSelectedNominee(nominee);
    toggleReadMore();
  };

  const handleViewVoteReceipt = () => {
    if (walletIsLoggedIn) {
      viewVoteReceipt(true, true);
    } else {
      login();
    }
  };

  return (
    <>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <Typography
          className="nominees-title"
          variant="h2"
          fontSize={{
            xs: '28px',
            md: '32px',
          }}
          lineHeight={{
            xs: '28px',
            md: '32px',
          }}
        >
          {summit2023Category.presentationName}
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
        {summit2023Category.desc}
      </Typography>

      {walletIsVerified ? (
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
              {walletIsLoggedIn
                ? 'You have successfully cast a vote for Nominee in the Ambassador category '
                : 'To see you vote receipt, please sign with your wallet'}
            </Typography>
          </div>
          <CustomButton
            styles={{
              background: '#03021F',
              color: '#F6F9FF',
              width: 'auto',
            }}
            label={walletIsLoggedIn ? 'View vote receipt' : 'Login with wallet'}
            onClick={() => handleViewVoteReceipt()}
            fullWidth={true}
          />
        </Box>
      ) : null}

      <Grid
        container
        spacing={3}
        style={{ justifyContent: 'center' }}
      >
        {nominees.map((nominee, index) => (
          <Grid
            item
            xs={12}
            sm={6}
            md={!isMobile && listView === 'grid' ? 4 : 12}
            key={nominee.id}
          >
            <Fade in={isVisible}>
              <Card
                className={'nominee-card'}
                style={{
                  padding: '8px',
                  width: listView === 'list' ? '100%' : '414px',
                  height: 'auto',
                }}
              >
                <CardContent>
                  <Typography
                    className="nominee-title"
                    variant="h2"
                  >
                    {nominee.id === summit2023CategoryNominees[index].id
                      ? summit2023CategoryNominees[index].presentationName
                      : ''}
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
                        {nominee.id === summit2023CategoryNominees[index].id
                          ? summit2023CategoryNominees[index].desc
                          : ''}
                      </Typography>
                    </Grid>
                    {!receipt && !isMobile && listView === 'list' ? (
                      <Grid
                        item
                        xs={2}
                      >
                        <CustomButton
                          styles={
                            isConnected
                              ? {
                                  background: '#ACFCC5',
                                  color: '#03021F',
                                  width: 'auto',
                                }
                              : {
                                    background: '#03021F',
                                    color: '#F6F9FF',
                                  width: 'auto',
                                }
                          }
                          label={renderNomineeButtonLabel() as string}
                          onClick={() => handleNomineeButton(nominee)}
                        />
                      </Grid>
                    ) : null}
                  </Grid>

                  <CustomButton
                    styles={{
                      background: 'transparent !important',
                      color: '#03021F',
                      border: '1px solid #daeefb',
                      width: !isMobile && listView === 'list' ? '146px' : '100%',
                      marginTop: !isMobile && listView === 'list' ? '15px' : '28px',
                    }}
                    label="Read more"
                    onClick={() =>
                      handleReadMore(
                        nominee.id === summit2023CategoryNominees[index].id && summit2023CategoryNominees[index]
                      )
                    }
                    fullWidth={true}
                  />

                  {!receipt && (isMobile || listView === 'grid') ? (
                    <CustomButton
                      styles={
                        isConnected
                          ? {
                              background: '#ACFCC5',
                              color: '#03021F',
                              marginTop: !isMobile && listView === 'list' ? '15px' : '18px',
                            }
                          : {
                                background: '#03021F',
                                color: '#F6F9FF',
                              marginTop: !isMobile && listView === 'list' ? '15px' : '18px',
                            }
                      }
                      label={renderNomineeButtonLabel() as string}
                      onClick={() => handleNomineeButton(nominee)}
                      fullWidth={true}
                    />
                  ) : null}
                </CardContent>
              </Card>
            </Fade>
          </Grid>
        ))}
      </Grid>

      <SidePage
        anchor="right"
        open={isToggleReadMore}
        setOpen={toggleReadMore}
      >
        <ReadMore
          nominee={selectedNominee}
          closeSidePage={toggleReadMore}
        />
      </SidePage>

      <SidePage
        anchor="right"
        open={isViewVoteReceipt && receipt !== undefined}
        setOpen={toggleViewVoteReceipt}
      >
        <>
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
                onClick={toggleViewVoteReceipt}
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
              sx={{
                textAlign: 'center',
                color: '#03021F',
                fontSize: '28px',
                fontStyle: 'normal',
                fontWeight: '600',
                lineHeight: '36px',
              }}
            >
              Vote Receipt
            </Typography>

            {receipt?.finalityScore === 'FINAL' ? (
              <Box
                sx={{
                  display: 'flex',
                  flexDirection: 'column',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  padding: '10px 20px',
                  borderRadius: '8px',
                  border: '1px solid #106593',
                  color: 'white',
                  width: '100%',
                  marginBottom: '20px',
                  backgroundColor: 'rgba(5, 97, 34, 0.07)',
                }}
              >
                <div style={{ width: '100%', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <VerifiedUserIcon sx={{ marginRight: '8px', width: '24px', height: '24px', color: '#056122' }} />
                    <Typography
                      variant="h6"
                      style={{
                        color: '#24262E',
                        fontSize: '18px',
                        fontStyle: 'normal',
                        fontWeight: '600',
                        lineHeight: '22px',
                      }}
                    >
                      Verified:
                      <Tooltip title="Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.">
                        <InfoIcon
                          style={{
                            color: '#434656A6',
                            width: '22px',
                            marginLeft: '3px',
                            marginBottom: '5px',
                            verticalAlign: 'middle',
                            cursor: 'pointer',
                          }}
                        />
                      </Tooltip>
                    </Typography>
                  </div>
                  <QrCodeIcon
                    sx={{
                      display: 'inline-flex',
                      justifyContent: 'center',
                      alignItems: 'center',
                      color: '#24262E',
                      cursor: 'pointer',
                      width: '36px',
                      height: '36px',
                      background: 'rgba(67, 70, 86, 0.10);',
                      borderRadius: '18px',
                      padding: '6px',
                    }}
                    onClick={toggleViewFinalReceipt}
                  />
                </div>

                <Typography
                  variant="body1"
                  sx={{
                    color: '#434656',
                    fontSize: '16px',
                    fontStyle: 'normal',
                    fontWeight: '400',
                    lineHeight: '22px',
                    wordWrap: 'break-word',
                    maxWidth: '406px',
                  }}
                >
                  Your vote has been successfully submitted. You might have to wait up to 30 minutes for this to be
                  visible on chain. Please check back later to verify your vote.
                </Typography>
              </Box>
            ) : (
              <Box
                sx={{
                  display: 'flex',
                  flexDirection: 'column',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  padding: '10px 20px',
                  borderRadius: '8px',
                  border: '1px solid #106593',
                  color: 'white',
                  width: '100%',
                  marginBottom: '20px',
                  backgroundColor: getAssuranceTheme()?.backgroundColor,
                }}
              >
                <div style={{ width: '100%', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    <NotificationsIcon sx={{ marginRight: '8px', width: '24px', height: '24px', color: '#106593' }} />
                    <Typography
                      variant="h6"
                      style={{
                        color: '#24262E',
                        fontSize: '18px',
                        fontStyle: 'normal',
                        fontWeight: '600',
                        lineHeight: '22px',
                      }}
                    >
                      {receipt?.finalityScore ? (
                        <>
                          Assurance: <span style={{ color: getAssuranceTheme()?.color }}>{receipt?.finalityScore}</span>
                        </>
                      ) : (
                        'Vote not ready for verification'
                      )}
                      <Tooltip title="Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.">
                        <InfoIcon
                          style={{
                            color: '#434656A6',
                            width: '22px',
                            marginLeft: '3px',
                            marginBottom: '5px',
                            verticalAlign: 'middle',
                            cursor: 'pointer',
                          }}
                        />
                      </Tooltip>
                    </Typography>
                  </div>
                  <RefreshIcon
                    onClick={() => viewVoteReceipt(true, false)}
                    sx={{
                      display: 'inline-flex',
                      justifyContent: 'center',
                      alignItems: 'center',
                      color: '#24262E',
                      cursor: 'pointer',
                      width: '36px',
                      height: '36px',
                      background: 'rgba(67, 70, 86, 0.10);',
                      borderRadius: '18px',
                      padding: '6px',
                    }}
                  />
                </div>

                <Typography
                  variant="body1"
                  sx={{
                    color: '#434656',
                    fontSize: '16px',
                    fontStyle: 'normal',
                    fontWeight: '400',
                    lineHeight: '22px',
                    wordWrap: 'break-word',
                    maxWidth: '406px',
                  }}
                >
                  Your vote has been successfully submitted. You might have to wait up to 30 minutes for this to be
                  visible on chain. Please check back later to verify your vote.
                </Typography>
              </Box>
            )}
            <Box
              sx={{
                display: 'flex',
                flexDirection: 'column',
                marginBottom: '20px',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', marginBottom: '10px' }}>
                <Typography
                  variant="h6"
                  sx={{
                    marginRight: '8px',
                    color: '#24262E',
                    fontSize: '18px',
                    fontStyle: 'normal',
                    fontWeight: '600',
                    lineHeight: '22px',
                  }}
                >
                  Event
                </Typography>
                <Tooltip title="Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.">
                  <InfoIcon
                    style={{
                      color: '#434656A6',
                      width: '22px',
                      marginLeft: '3px',
                      marginBottom: '5px',
                      verticalAlign: 'middle',
                      cursor: 'pointer',
                    }}
                  />
                </Tooltip>
              </div>
              <Typography
                variant="body1"
                align="left"
                sx={{ cursor: 'pointer' }}
                onClick={() => handleCopyToClipboard(receipt?.event)}
              >
                {receipt?.event}
              </Typography>
            </Box>
            <Box
              sx={{
                display: 'flex',
                flexDirection: 'column',
                marginBottom: '20px',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', marginBottom: '10px' }}>
                <Typography
                  variant="h6"
                  sx={{
                    marginRight: '8px',
                    color: '#24262E',
                    fontSize: '18px',
                    fontStyle: 'normal',
                    fontWeight: '600',
                    lineHeight: '22px',
                  }}
                >
                  Proposal
                </Typography>
                <Tooltip title="Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.">
                  <InfoIcon
                    style={{
                      color: '#434656A6',
                      width: '22px',
                      marginLeft: '3px',
                      marginBottom: '5px',
                      verticalAlign: 'middle',
                      cursor: 'pointer',
                    }}
                  />
                </Tooltip>
              </div>
              <Typography
                variant="body1"
                align="left"
                sx={{ cursor: 'pointer' }}
                onClick={() => handleCopyToClipboard(receipt?.proposal)}
              >
                {receipt?.proposal}
              </Typography>
            </Box>
            <Box
              sx={{
                display: 'flex',
                flexDirection: 'column',
                marginBottom: '20px',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', marginBottom: '10px' }}>
                <Typography
                  variant="h6"
                  sx={{
                    marginRight: '8px',
                    color: '#24262E',
                    fontSize: '18px',
                    fontStyle: 'normal',
                    fontWeight: '600',
                    lineHeight: '22px',
                  }}
                >
                  Voter Staking Address
                </Typography>
                <Tooltip title="Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.">
                  <InfoIcon
                    style={{
                      color: '#434656A6',
                      width: '22px',
                      marginLeft: '3px',
                      marginBottom: '5px',
                      verticalAlign: 'middle',
                      cursor: 'pointer',
                    }}
                  />
                </Tooltip>
              </div>
              <Typography
                variant="body1"
                align="left"
                sx={{ wordWrap: 'break-word', maxWidth: '490px', cursor: 'pointer' }}
                onClick={() => handleCopyToClipboard(receipt?.voterStakingAddress)}
              >
                {receipt?.voterStakingAddress}
              </Typography>
            </Box>
            <Box
              sx={{
                display: 'flex',
                flexDirection: 'column',
                marginBottom: '20px',
              }}
            >
              <div style={{ display: 'flex', alignItems: 'center', marginBottom: '10px' }}>
                <Typography
                  variant="h6"
                  sx={{
                    marginRight: '8px',
                    color: '#24262E',
                    fontSize: '18px',
                    fontStyle: 'normal',
                    fontWeight: '600',
                    lineHeight: '22px',
                  }}
                >
                  Status
                </Typography>
                <Tooltip title="Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.">
                  <InfoIcon
                    style={{
                      color: '#434656A6',
                      width: '22px',
                      marginLeft: '3px',
                      marginBottom: '5px',
                      verticalAlign: 'middle',
                      cursor: 'pointer',
                    }}
                  />
                </Tooltip>
              </div>
              <Typography
                variant="body1"
                align="left"
                sx={{ cursor: 'pointer' }}
                onClick={() => handleCopyToClipboard(receipt?.status)}
              >
                {receipt?.status}
              </Typography>
            </Box>
            <Accordion className="accordion-button">
              <AccordionSummary
                sx={{
                  display: 'flex',
                  width: isMobile ? 'auto' : '490px',
                  padding: '4px 16px',
                  alignItems: 'center',
                  gap: '10px',
                  borderRadius: '8px',
                  background: 'rgba(16, 101, 147, 0.07)',
                }}
                expandIcon={<ExpandMoreIcon style={{ color: '#106593' }} />}
              >
                <Typography
                  sx={{
                    color: '#106593',
                    fontSize: '16px',
                    fontStyle: 'normal',
                    fontWeight: '600',
                    lineHeight: 'normal',
                  }}
                >
                  Show Advanced Information
                </Typography>
              </AccordionSummary>
              <AccordionDetails>
                <Box
                  sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    marginBottom: '20px',
                    marginTop: '20px',
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', marginBottom: '10px' }}>
                    <Typography
                      variant="h6"
                      sx={{
                        marginRight: '8px',
                        color: '#24262E',
                        fontSize: '18px',
                        fontStyle: 'normal',
                        fontWeight: '600',
                        lineHeight: '22px',
                      }}
                    >
                      ID
                    </Typography>
                    <Tooltip title="Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.">
                      <InfoIcon
                        style={{
                          color: '#434656A6',
                          width: '22px',
                          marginLeft: '3px',
                          marginBottom: '5px',
                          verticalAlign: 'middle',
                          cursor: 'pointer',
                        }}
                      />
                    </Tooltip>
                  </div>
                  <Typography
                    variant="body1"
                    align="left"
                    sx={{ cursor: 'pointer' }}
                    onClick={() => handleCopyToClipboard(receipt?.id)}
                  >
                    {receipt?.id}
                  </Typography>
                </Box>
                <Box
                  sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    marginBottom: '20px',
                    marginTop: '20px',
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', marginBottom: '10px' }}>
                    <Typography
                      variant="h6"
                      sx={{
                        marginRight: '8px',
                        color: '#24262E',
                        fontSize: '18px',
                        fontStyle: 'normal',
                        fontWeight: '600',
                        lineHeight: '22px',
                      }}
                    >
                      Voted at Slot
                    </Typography>
                    <Tooltip title="Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.">
                      <InfoIcon
                        style={{
                          color: '#434656A6',
                          width: '22px',
                          marginLeft: '3px',
                          marginBottom: '5px',
                          verticalAlign: 'middle',
                          cursor: 'pointer',
                        }}
                      />
                    </Tooltip>
                  </div>
                  <Typography
                    variant="body1"
                    align="left"
                    sx={{ cursor: 'pointer' }}
                    onClick={() => handleCopyToClipboard(receipt?.votedAtSlot)}
                  >
                    {receipt?.votedAtSlot}
                  </Typography>
                </Box>
                <Box
                  sx={{
                    display: 'flex',
                    flexDirection: 'column',
                    marginBottom: '20px',
                    marginTop: '20px',
                  }}
                >
                  <div style={{ display: 'flex', alignItems: 'center', marginBottom: '10px' }}>
                    <Typography
                      variant="h6"
                      sx={{
                        marginRight: '8px',
                        color: '#24262E',
                        fontSize: '18px',
                        fontStyle: 'normal',
                        fontWeight: '600',
                        lineHeight: '22px',
                      }}
                    >
                      Vote Proof
                    </Typography>
                    <Tooltip title="Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.">
                      <InfoIcon
                        style={{
                          color: '#434656A6',
                          width: '22px',
                          marginLeft: '3px',
                          marginBottom: '5px',
                          verticalAlign: 'middle',
                          cursor: 'pointer',
                        }}
                      />
                    </Tooltip>
                  </div>
                  <Typography
                    variant="body1"
                    align="left"
                    sx={{
                      wordWrap: 'break-word',
                      maxWidth: '406px',
                      cursor: 'pointer',
                    }}
                    onClick={() => handleCopyToClipboard(JSON.stringify(receipt?.merkleProof || '', null, 4))}
                  >
                    {receipt?.merkleProof ? JSON.stringify(receipt?.merkleProof || '', null, 4) : 'Not available yet'}
                  </Typography>
                </Box>
              </AccordionDetails>
            </Accordion>
          </Container>
        </>
      </SidePage>
      <Modal
        isOpen={isViewFinalReceipt}
        id="final-receipt"
        title="Vote verified"
        onClose={toggleViewFinalReceipt}
      >
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
          Your vote has been successfully verified. Click the link or scan the QR code to view the transaction.
        </Typography>
        <div style={{ display: 'flex', justifyContent: 'center', width: '100%', marginTop: '24px' }}>
          <QRCode
            size={256}
            style={{ height: 'auto', width: '200px' }}
            value="heeeeeey"
            viewBox={'0 0 256 256'}
          />
        </div>
        <CustomButton
          styles={{
            background: '#ACFCC5',
            color: '#03021F',
            width: 'auto',
          }}
          label="Done"
          onClick={toggleViewFinalReceipt}
        />
      </Modal>

      <Modal
        isOpen={confirmVoteModal}
        id="confirm-vote"
        title="Confirm Vote"
        onClose={toggleConfirmVoteModal}
      >
        <CustomButton
          styles={{
            background: '#ACFCC5',
            color: '#03021F',
            margin: '20px 0px',
          }}
          label={`Vote for ${selectedNomineeToVote?.id}`}
          fullWidth={true}
          onClick={() => handleVoteNomineeButton()}
        />
        <CustomButton
          styles={{
            background: 'transparent !important',
            color: '#03021F',
          }}
          label="Cancel"
          fullWidth={true}
          onClick={toggleConfirmVoteModal}
        />
      </Modal>
    </>
  );
};

export { Nominees };

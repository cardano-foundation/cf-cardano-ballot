import React, { useState, useEffect, useMemo, ReactElement } from 'react';
import { useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import { v4 as uuidv4 } from 'uuid';
import { i18n } from 'i18n';
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
  Button,
} from '@mui/material';
import { useNavigate } from 'react-router-dom';
import ViewModuleIcon from '@mui/icons-material/ViewModule';
import ViewListIcon from '@mui/icons-material/ViewList';
import GppBadOutlinedIcon from '@mui/icons-material/GppBadOutlined';
import WarningAmberIcon from '@mui/icons-material/WarningAmber';
import VerifiedUserIcon from '@mui/icons-material/VerifiedUser';
import NotificationsIcon from '@mui/icons-material/Notifications';
import QrCodeIcon from '@mui/icons-material/QrCode';
import RefreshIcon from '@mui/icons-material/Refresh';
import InfoIcon from '@mui/icons-material/Info';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import labelVoted from '../../common/resources/images/checkmark-green.png';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import CloseIcon from '@mui/icons-material/Close';
import { Fade } from '@mui/material';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import QRCode from 'react-qr-code';
import { CategoryContent } from '../Categories/Category.types';
import SUMMIT2023CONTENT from '../../common/resources/data/summit2023Content.json';
import { eventBus } from '../../utils/EventBus';
import { ROUTES } from '../../routes';
import { RootState } from '../../store';
import {
  buildCanonicalVoteInputJson,
  castAVoteWithDigitalSignature,
  getSlotNumber,
  getUserVotes,
  getVoteReceipt,
} from '../../common/api/voteService';
import { copyToClipboard, getSignedMessagePromise, resolveCardanoNetwork, shortenString } from '../../utils/utils';
import { buildCanonicalLoginJson, submitLogin } from 'common/api/loginService';
import { getUserInSession, saveUserInSession, tokenIsExpired } from '../../utils/session';
import { setUserVotes, setVoteReceipt, setWalletIsLoggedIn } from '../../store/userSlice';
import SidePage from '../../components/common/SidePage/SidePage';
import { useToggle } from 'common/hooks/useToggle';
import ReadMore from './ReadMore';
import Modal from '../../components/common/Modal/Modal';
import { CustomButton } from '../../components/common/Button/CustomButton';
import { env } from 'common/constants/env';
import { parseError } from 'common/constants/errors';
import { categoryAlreadyVoted } from '../Categories';
import { ProposalPresentationExtended } from '../../store/types';
import { verifyVote } from 'common/api/verificationService';
import './Nominees.scss';

const Nominees = () => {
  const dispatch = useDispatch();
  const { categoryId } = useParams();
  const navigate = useNavigate();
  const eventCache = useSelector((state: RootState) => state.user.event);
  const walletIsVerified = useSelector((state: RootState) => state.user.walletIsVerified);
  const receipts = useSelector((state: RootState) => state.user.receipts);
  const receipt = receipts && Object.keys(receipts).length && receipts[categoryId] ? receipts[categoryId] : undefined;
  const userVotes = useSelector((state: RootState) => state.user.userVotes);
  const winners = useSelector((state: RootState) => state.user.winners);

  const categoryVoted = categoryAlreadyVoted(categoryId, userVotes);
  const categories = eventCache?.categories;

  const summit2023Category: CategoryContent = SUMMIT2023CONTENT.categories.find(
    (category) => category.id === categoryId
  );

  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const isBigScreen = useMediaQuery(theme.breakpoints.down('xl'));

  const [viewMode, setViewMode] = useState<'grid' | 'list'>('grid');
  const [isVisible, setIsVisible] = useState(true);
  const [isToggleReadMore, toggleReadMore] = useToggle(false);
  const [isViewVoteReceipt, toggleViewVoteReceipt] = useToggle(false);
  const [isViewFinalReceipt, toggleViewFinalReceipt] = useToggle(false);
  const [confirmVoteModal, toggleConfirmVoteModal] = useToggle(false);
  const [selectedNominee, setSelectedNominee] = useState({});
  const [selectedNomineeToVote, setSelectedNomineeToVote] = useState(undefined);
  const [nominees, setNominees] = useState<ProposalPresentationExtended[]>([]);

  const session = getUserInSession();
  const isExpired = !session || tokenIsExpired(session?.expiresAt);

  const { isConnected, stakeAddress, signMessage } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  const votedNominee = nominees.find((nominee) => nominee.id === selectedNomineeToVote?.id);

  const signMessagePromisified = useMemo(() => getSignedMessagePromise(signMessage), [signMessage]);

  const loadNominees = () => {
    if (categoryId) {
      categories?.map((category) => {
        if (category.id === categoryId) {
          setNominees(category?.proposals || []);
        }
      });
    } else {
      navigate(ROUTES.PAGE_NOT_FOUND);
    }
  };

  useEffect(() => {
    loadNominees();
  }, [categories]);

  const handleListView = (viewType: 'grid' | 'list') => {
    if (viewMode === viewType) return;

    setIsVisible(false);
    setTimeout(() => {
      setViewMode(viewType);
      setIsVisible(true);
    }, 300);
  };

  const viewVoteReceipt = async (toast?: boolean, toggle?: boolean) => {
    if (receipt && toggle) {
      toggleViewVoteReceipt();
    }

    if (!tokenIsExpired(session?.expiresAt)) {
      await getVoteReceipt(categoryId, session?.accessToken)
        .then((r) => {
          const extendedReceipt = {
            ...r,
            presentationName: nominees.find((nominee) => nominee.id === r.proposal).presentationName,
          };
          dispatch(setVoteReceipt({ categoryId: categoryId, receipt: extendedReceipt }));
          if (toggle !== false) toggleViewVoteReceipt();
        })
        .catch((e) => {
          if (toast !== false) {
            eventBus.publish('showToast', parseError(e.message), 'error');
          }
        });
    } else {
      if (toast !== false) {
        eventBus.publish('showToast', 'Please, login before get receipt', 'error');
      }
    }
  };

  const login = async () => {
    try {
      const absoluteSlot = (await getSlotNumber())?.absoluteSlot;
      const canonicalVoteInput = buildCanonicalLoginJson({
        stakeAddress,
        slotNumber: absoluteSlot.toString(),
      });
      const requestVoteObject = await signMessagePromisified(canonicalVoteInput);
      submitLogin(requestVoteObject)
        .then((response) => {
          const newSession = {
            accessToken: response.accessToken,
            expiresAt: response.expiresAt,
          };
          saveUserInSession(newSession);
          dispatch(setWalletIsLoggedIn({ isLoggedIn: true }));
          eventBus.publish('showToast', 'Login successfully');
          getUserVotes(newSession?.accessToken)
            .then((uVotes) => {
              if (uVotes) {
                dispatch(setUserVotes({ userVotes: uVotes }));
              }
            })
            .catch((e) => {
              eventBus.publish('showToast', parseError(e.message), 'error');
            });
          viewVoteReceipt(false, true);
        })
        .catch((e) => eventBus.publish('showToast', parseError(e.message), 'error'));
    } catch (e) {
      eventBus.publish('showToast', e.message, 'error');
    }
  };

  useEffect(() => {
    if (isMobile) {
      setViewMode('list');
    }
  }, [isMobile]);

  const castVote = async (optionId: string) => {
    if (eventCache?.finished) {
      eventBus.publish('showToast', 'The event already ended', 'error');
      return;
    }

    try {
      const absoluteSlot = (await getSlotNumber())?.absoluteSlot;
      const canonicalVoteInput = buildCanonicalVoteInputJson({
        voteId: uuidv4(),
        categoryId: categoryId,
        proposalId: optionId,
        stakeAddress,
        slotNumber: absoluteSlot.toString(),
      });
      const requestVoteObject = await signMessagePromisified(canonicalVoteInput);

      eventBus.publish('showToast', 'Vote submitted');
      toggleConfirmVoteModal();
      await castAVoteWithDigitalSignature(requestVoteObject);
      eventBus.publish('showToast', 'Vote submitted successfully');
      if (session && !tokenIsExpired(session?.expiresAt)) {
        getVoteReceipt(categoryId, session?.accessToken)
          .then((r) => {
            dispatch(setVoteReceipt({ categoryId: categoryId, receipt: r }));
          })
          .catch((e) => {
            if (process.env.NODE_ENV === 'development') {
              console.log(`Failed to fetch vote receipt, ${parseError(e.message)}`);
            }
          });
        getUserVotes(session?.accessToken)
          .then((response) => {
            if (response) {
              dispatch(setUserVotes({ userVotes: response }));
            }
          })
          .catch((e) => {
            if (process.env.NODE_ENV === 'development') {
              console.log(`Failed to fetch user votes, ${parseError(e.message)}`);
            }
          });
      } else {
        eventBus.publish('openLoginModal', 'Login to see your vote receipt.');
      }
    } catch (e) {
      eventBus.publish('showToast', e.message && e.message.length ? parseError(e.message) : 'Action failed', 'error');
    }
  };

  const handleNomineeButton = (nominee) => {
    if (eventCache?.finished) return;

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
    if (eventCache?.finished) return;

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
        return 'Verify your Wallet';
      } else {
        return 'Vote for Nominee';
      }
    } else {
      return (
        <>
          <AccountBalanceWalletIcon /> Connect Wallet
        </>
      );
    }
  };

  const handleCopyToClipboard = (text: string) => {
    copyToClipboard(text)
      .then(() => eventBus.publish('showToast', i18n.t('toast.copy')))
      .catch(() => eventBus.publish('showToast', i18n.t('toast.copyError'), 'error'));
  };
  const getStatusTheme = () => {
    const finalityScore = receipt?.finalityScore;

    if (receipt?.status === 'FULL') {
      switch (finalityScore) {
        case 'VERY_HIGH':
          return {
            label: 'VERY HIGH',
            backgroundColor: 'rgba(16, 101, 147, 0.07)',
            border: 'border: 1px solid #106593',
            color: '#056122',
            icon: <NotificationsIcon sx={{ marginRight: '8px', width: '24px', height: '24px', color: '#106593' }} />,
            description: i18n.t('nominees.receipt.status.FULL.VERY_HIGH.description'),
            status: 'FULL',
          };
        case 'HIGH':
          return {
            label: 'HIGH',
            backgroundColor: 'rgba(16, 101, 147, 0.07)',
            border: 'border: 1px solid #106593',
            color: '#056122',
            icon: <NotificationsIcon sx={{ marginRight: '8px', width: '24px', height: '24px', color: '#106593' }} />,
            description: i18n.t('nominees.receipt.status.FULL.HIGH.description'),
            status: 'FULL',
          };
        case 'MEDIUM':
          return {
            label: 'MEDIUM',
            backgroundColor: 'rgba(16, 101, 147, 0.07)',
            border: 'border: 1px solid #106593',
            color: '#652701',
            icon: <NotificationsIcon sx={{ marginRight: '8px', width: '24px', height: '24px', color: '#106593' }} />,
            description: i18n.t('nominees.receipt.status.FULL.MEDIUM.description'),
            status: 'FULL',
          };
        case 'LOW':
          return {
            label: 'LOW',
            backgroundColor: 'rgba(16, 101, 147, 0.07)',
            border: 'border: 1px solid #106593',
            color: '#C20024',
            icon: <NotificationsIcon sx={{ marginRight: '8px', width: '24px', height: '24px', color: '#106593' }} />,
            description: i18n.t('nominees.receipt.status.FULL.LOW.description'),
            status: 'FULL',
          };
        case 'FINAL':
          return {
            label: 'FINAL',
            backgroundColor: 'rgba(5, 97, 34, 0.07)',
            border: '1px solid #056122',
            icon: <VerifiedUserIcon sx={{ marginRight: '8px', width: '24px', height: '24px', color: '#056122' }} />,
            description: '',
            status: 'FULL',
          };
        default:
          return {
            backgroundColor: 'rgba(16, 101, 147, 0.07)',
            color: '#24262E',
            icon: <NotificationsIcon sx={{ marginRight: '8px', width: '24px', height: '24px', color: '#106593' }} />,
            description: i18n.t('nominees.receipt.status.FULL.DEFAULT.description'),
            status: 'FULL',
          };
      }
    } else if (receipt?.status === 'PARTIAL') {
      return {
        label: 'Vote in progress',
        backgroundColor: 'rgba(253, 135, 60, 0.07)',
        border: '1px solid #FD873C',
        color: '#24262E',
        icon: <WarningAmberIcon sx={{ marginRight: '8px', width: '24px', height: '24px', color: '#652701' }} />,
        description: i18n.t('nominees.receipt.status.PARTIAL.description'),
        status: 'PARTIAL',
      };
    } else if (receipt?.status === 'ROLLBACK') {
      return {
        label: 'There’s been a rollback',
        backgroundColor: 'rgba(194, 0, 36, 0.07)',
        border: '1px solid #C20024',
        color: '#24262E',
        icon: <GppBadOutlinedIcon sx={{ marginRight: '8px', width: '24px', height: '24px', color: '#C20024' }} />,
        description: i18n.t('nominees.receipt.status.ROLLBACK.description'),
        status: 'ROLLBACK',
      };
    } else {
      // BASIC
      return {
        label: 'Vote not ready for verification',
        backgroundColor: 'rgba(16, 101, 147, 0.07)',
        border: '1px solid #106593',
        color: '#24262E',
        icon: <NotificationsIcon sx={{ marginRight: '8px', width: '24px', height: '24px', color: '#106593' }} />,
        description: i18n.t('nominees.receipt.status.BASIC.description'),
      };
    }
  };

  const handleReadMore = (nominee) => {
    setSelectedNominee(nominee);
    toggleReadMore();
  };

  const handleViewVoteReceipt = () => {
    if (isConnected && !isExpired) {
      viewVoteReceipt(true, true);
    } else {
      login();
    }
  };

  const nomineeAlreadyVoted = (nominee) => {
    let alreadyVoted = false;
    if (
      !tokenIsExpired(session?.expiresAt) &&
      userVotes?.length &&
      userVotes?.find((c) => c.categoryId === categoryId) &&
      userVotes?.find((p) => p.proposalId === nominee.id)
    ) {
      alreadyVoted = true;
    }
    return alreadyVoted;
  };
  const nomineeIsWinner = (nominee) => {
    let isWinner = false;
    if (
      winners?.length &&
      winners?.find((c) => c.categoryId === categoryId) &&
      winners?.find((p) => p.proposalId === nominee.id)
    ) {
      isWinner = true;
    }
    return isWinner;
  };

  const sortNominees = (nomineesList) => {
    return [...nomineesList].sort((a, b) => {
      const aIsWinner = nomineeIsWinner(a);
      const bIsWinner = nomineeIsWinner(b);

      const aAlreadyVoted = nomineeAlreadyVoted(a);
      const bAlreadyVoted = nomineeAlreadyVoted(b);

      if (aIsWinner && !bIsWinner) return -1;
      if (!aIsWinner && bIsWinner) return 1;

      if (aAlreadyVoted && !bAlreadyVoted) return -1;
      if (!aAlreadyVoted && bAlreadyVoted) return 1;

      return 0;
    });
  };

  const verifyVoteProof = () => {
    if (receipt) {
      const body = {
        rootHash: receipt.merkleProof.rootHash,
        steps: receipt.merkleProof.steps,
        voteCoseSignature: receipt.coseSignature,
        voteCosePublicKey: receipt.cosePublicKey,
      };
      verifyVote(body)
        .then((result) => {
          if ('verified' in result && result.verified) {
            eventBus.publish('showToast', i18n.t('toast.voteVerified'), 'verified');
          } else {
            eventBus.publish('showToast', i18n.t('toast.voteNotVerified'), 'error');
          }
        })
        .catch((e) => {
          eventBus.publish('showToast', parseError(e.message), 'error');
        });
    }
  };

  const renderResponsiveList = (): ReactElement => {
    return (
      <>
        <Grid
          container
          spacing={3}
          style={{ justifyContent: 'center' }}
        >
          {sortNominees(nominees).map((nominee, index) => {
            const voted = nomineeAlreadyVoted(nominee);
            const isWinner = nomineeIsWinner(nominee);
            return (
              <Grid
                item
                xs={12}
                key={nominee.id}
              >
                <Fade in={isVisible}>
                  <Card
                    className={'nominee-card'}
                    style={{
                      padding: '8px',
                      width: '100%',
                      height: 'auto',
                    }}
                  >
                    <CardContent>
                      <Box sx={{ position: 'relative' }}>
                        {voted ? (
                          <Tooltip title={i18n.t('nominees.alreadyVoted')}>
                            <img
                              height={40}
                              width={102}
                              src={labelVoted}
                              alt={i18n.t('nominees.alreadyVoted')}
                              style={{
                                position: 'absolute',
                                float: 'right',
                                right: 0,
                                zIndex: 99,
                                opacity: 1,
                              }}
                            />
                          </Tooltip>
                        ) : null}
                      </Box>
                      <Typography
                        variant="h6"
                        sx={{
                          fontSize: {
                            xs: '28px',
                            sm: '28px',
                            md: '32px',
                          },
                          fontWeight: 600,
                        }}
                      >
                        {nominee.presentationName}
                        {isWinner ? (
                          <Tooltip title={i18n.t('nominees.winner')}>
                            <EmojiEventsIcon
                              sx={{ fontSize: '40px', position: 'absolute', marginLeft: '4px', color: '#efb810' }}
                            />
                          </Tooltip>
                        ) : null}
                      </Typography>
                      <Grid container>
                        <Grid
                          item
                          sm={12}
                          md={10}
                        >
                          <Typography
                            className="nominee-description"
                            variant="body2"
                          >
                            {shortenString(nominee.desc, 210)}
                          </Typography>
                        </Grid>
                        {!eventCache?.finished && !categoryVoted ? (
                          <Grid
                            item
                            sm={12}
                            md={2}
                            width={{ sm: '100%', md: 'auto' }}
                          >
                            <CustomButton
                              styles={
                                isConnected
                                  ? {
                                      background: '#ACFCC5',
                                      color: '#03021F',
                                      width: '100%',
                                    }
                                  : {
                                      background: '#03021F',
                                      color: '#F6F9FF',
                                      width: '100%',
                                    }
                              }
                              label={renderNomineeButtonLabel() as string}
                              onClick={() => handleNomineeButton(nominee)}
                            />
                          </Grid>
                        ) : null}
                      </Grid>
                      <Grid
                        item
                        sm={12}
                        md={2}
                        width={{ sm: '100%', md: 'auto' }}
                      >
                        <CustomButton
                          styles={{
                            background: 'transparent !important',
                            color: '#03021F',
                            border: '1px solid #daeefb',
                            width: '100%',
                            marginTop: '15px',
                          }}
                          label="Read More"
                          onClick={() => handleReadMore(nominee)}
                        />
                      </Grid>
                    </CardContent>
                  </Card>
                </Fade>
              </Grid>
            );
          })}
        </Grid>
      </>
    );
  };
  const renderResponsiveGrid = (): ReactElement => {
    return (
      <>
        <div>
          <Grid
            container
            spacing={2}
            justifyContent="center"
          >
            {sortNominees(nominees).map((nominee) => {
              const voted = nomineeAlreadyVoted(nominee);
              const isWinner = nomineeIsWinner(nominee);

              return (
                <Grid
                  item
                  xs={12}
                  sm={6}
                  md={4}
                  key={nominee.id}
                >
                  <div style={{ height: 'auto', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Card
                      sx={{
                        width: { xs: '90vw', sm: '50vw' },
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        borderRadius: '16px',
                      }}
                    >
                      <CardContent sx={{ padding: '24px' }}>
                        {voted ? (
                          <Box sx={{ position: 'relative' }}>
                            <Tooltip title={i18n.t('nominees.alreadyVoted')}>
                              <img
                                height={40}
                                width={102}
                                src={labelVoted}
                                alt={i18n.t('nominees.alreadyVoted')}
                                style={{
                                  position: 'absolute',
                                  float: 'right',
                                  right: 0,
                                  zIndex: 99,
                                  opacity: 1,
                                }}
                              />
                            </Tooltip>
                          </Box>
                        ) : null}
                        <Typography
                          variant="h6"
                          sx={{
                            fontSize: {
                              xs: '28px',
                              sm: '28px',
                              md: '32px',
                            },
                            fontWeight: 600,
                            width: voted ? '250px' : '100%',
                          }}
                        >
                          {nominee.presentationName}
                          {isWinner ? (
                            <Tooltip title={i18n.t('nominees.winner')}>
                              <EmojiEventsIcon
                                sx={{ fontSize: '40px', position: 'absolute', marginLeft: '4px', color: '#efb810' }}
                              />
                            </Tooltip>
                          ) : null}
                        </Typography>
                        <Grid container>
                          <Grid
                            item
                            xs={12}
                          >
                            <Typography
                              className="nominee-description"
                              variant="body2"
                              sx={{ minHeight: '115px', height: '115px', mt: '10px' }}
                            >
                              {shortenString(nominee.desc, 150)}
                            </Typography>
                          </Grid>
                          <Grid
                            item
                            xs={12}
                          >
                            <CustomButton
                              styles={{
                                background: 'transparent !important',
                                color: '#03021F',
                                border: '1px solid #daeefb',
                                width: '100%',
                              }}
                              label={i18n.t('nominees.readMore')}
                              onClick={() => handleReadMore(nominee)}
                              fullWidth={true}
                            />

                            {!eventCache?.finished && !categoryVoted ? (
                              <CustomButton
                                styles={
                                  isConnected
                                    ? {
                                        background: '#ACFCC5',
                                        color: '#03021F',
                                        marginTop: '18px',
                                      }
                                    : {
                                        background: '#03021F',
                                        color: '#F6F9FF',
                                        marginTop: '18px',
                                      }
                                }
                                label={renderNomineeButtonLabel() as string}
                                onClick={() => handleNomineeButton(nominee)}
                                fullWidth={true}
                              />
                            ) : null}
                          </Grid>
                        </Grid>
                      </CardContent>
                    </Card>
                  </div>
                </Grid>
              );
            })}
          </Grid>
        </div>
      </>
    );
  };

  const showBanner =
      (isConnected && isExpired) ||
    (!isExpired && categoryVoted && ( eventCache?.finished || (receipt && categoryId === receipt?.category)));

  return (
    <>
      <div
        data-testid="nominees-page"
        className="nominees-page"
        style={{ padding: isBigScreen ? '0px' : '0px 10px' }}
      >
        <div
          style={{
            display: 'flex',
            justifyContent: 'space-between',
            alignItems: 'center',
            marginTop: '20px',
            marginBottom: 20,
          }}
        >
          <Typography
            variant="h2"
            fontSize={{
              xs: '28px',
              md: '32px',
              lg: '48px',
            }}
            lineHeight={{
              xs: '28px',
              md: '32px',
            }}
            sx={{
              color: '#24262E',
              fontStyle: 'normal',
              fontWeight: '600',
            }}
          >
            {summit2023Category.presentationName}
          </Typography>
          {!isMobile && (
            <div>
              <IconButton
                onClick={() => handleListView('grid')}
                className={viewMode === 'grid' ? 'selected' : 'un-selected'}
              >
                <ViewModuleIcon />
              </IconButton>
              <IconButton
                onClick={() => handleListView('list')}
                className={viewMode === 'list' ? 'selected' : 'un-selected'}
              >
                <ViewListIcon />
              </IconButton>
            </div>
          )}
        </div>

        <Typography
          className="nominees-description"
          variant="body1"
          gutterBottom
          sx={{ marginBottom: '50px' }}
        >
          {summit2023Category.desc}
        </Typography>

        {showBanner ? (
          <Box
            sx={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              backgroundColor: !isExpired ? 'rgba(5, 97, 34, 0.07)' : 'rgba(253, 135, 60, 0.07)',
              padding: '10px 20px',
              borderRadius: '8px',
              border: !isExpired ? '1px solid #056122' : '1px solid #FD873C',
              color: 'white',
              width: '100%',
              marginBottom: '20px',
            }}
          >
            <div style={{ display: 'flex', alignItems: 'center' }}>
              {!isExpired ? (
                <VerifiedUserIcon sx={{ marginRight: '8px', width: '24px', height: '24px', color: '#056122' }} />
              ) : (
                <WarningAmberIcon sx={{ marginRight: '8px', width: '24px', height: '24px', color: '#FD873C' }} />
              )}

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
                {!isExpired
                  ? `${i18n.t('nominees.successfullyVoteCast')} ${summit2023Category.presentationName} category.`
                  : `${i18n.t('nominees.signIn')}`
                }
              </Typography>
            </div>
            <CustomButton
              styles={{
                background: '#03021F',
                color: '#F6F9FF',
                width: 'auto',
              }}
              label={!isExpired ? i18n.t('nominees.viewReceipt') : i18n.t('nominees.loginWithWallet')}
              onClick={() => handleViewVoteReceipt()}
              fullWidth={true}
            />
          </Box>
        ) : null}

        {isMobile || viewMode === 'grid' ? renderResponsiveGrid() : renderResponsiveList()}
      </div>

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
              {i18n.t('nominees.receipt.voteReceipt')}
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
                      {i18n.t('nominees.receipt.verified')}:
                      <Tooltip title={i18n.t('nominees.verifiedOnChainTooltip')}>
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
                  {i18n.t('nominees.receipt.status.BASIC.description')}
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
                  border: getStatusTheme()?.border,
                  color: getStatusTheme()?.color,
                  width: '100%',
                  marginBottom: '20px',
                  backgroundColor: getStatusTheme()?.backgroundColor,
                }}
              >
                <div style={{ width: '100%', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <div style={{ display: 'flex', alignItems: 'center' }}>
                    {getStatusTheme()?.icon}
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
                          Assurance: <span style={{ color: getStatusTheme()?.color }}>{getStatusTheme()?.label}</span>
                        </>
                      ) : (
                        <>{getStatusTheme()?.label}</>
                      )}
                      <Tooltip title={i18n.t('nominees.assuranceTooltip')}>
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
                  {getStatusTheme()?.description}
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
                  {i18n.t('nominees.receipt.event')}:
                </Typography>
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
                  {i18n.t('nominees.receipt.nominee')}:
                </Typography>
                <Tooltip title={i18n.t('nominees.nomineeSelectedTooltip')}>
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
                sx={{ cursor: 'pointer', wordWrap: 'break-word' }}
                onClick={() => handleCopyToClipboard(`${receipt?.presentationName} [${receipt?.proposal}]`)}
              >
                {receipt?.presentationName} [{receipt?.proposal}]
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
                  {i18n.t('nominees.receipt.nominee')}:
                </Typography>
                <Tooltip title={i18n.t('nominees.stakeAddressTooltip')}>
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
                  {i18n.t('nominees.receipt.statusTitle')}:
                </Typography>
                <Tooltip title={i18n.t('nominees.currentStatusTooltip')}>
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
                  {i18n.t('nominees.receipt.showAdvancedInfo')}:
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
                    <Tooltip title={i18n.t('nominees.uniqueIDTooltip')}>
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
                      {i18n.t('nominees.receipt.votedAtSlot')}:
                    </Typography>
                    <Tooltip title={i18n.t('nominees.slotTooltip')}>
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
                      {i18n.t('nominees.receipt.voteProof')}:
                    </Typography>
                    <Tooltip title={i18n.t('nominees.voteProofTooltip')}>
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

                  <Box
                    onClick={() => handleCopyToClipboard(JSON.stringify(receipt?.merkleProof || '', null, 4))}
                    sx={{
                      width: '460px',
                      overflowX: 'auto',
                      whiteSpace: 'pre',
                      padding: '16px',
                    }}
                  >
                    <Typography
                      component="pre"
                      variant="body2"
                      sx={{ pointer: 'cursor' }}
                    >
                      {receipt?.merkleProof ? JSON.stringify(receipt?.merkleProof || '', null, 4) : i18n.t('nominees.notAvailable')}
                    </Typography>
                  </Box>
                  {receipt?.merkleProof ? (
                    <CustomButton
                      styles={{
                        background: '#ACFCC5',
                        color: '#03021F',
                        width: 'auto',
                      }}
                      label={i18n.t('nominees.verifyVoteProof')}
                      onClick={verifyVoteProof}
                    />
                  ) : null}
                </Box>
              </AccordionDetails>
            </Accordion>
          </Container>
        </>
      </SidePage>
      <Modal
        isOpen={isViewFinalReceipt}
        id="final-receipt"
        title={i18n.t('nominees.voteVerified')}
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
          {i18n.t('nominees.successfullyVerified')}
        </Typography>
        <div
            onClick={() => handleCopyToClipboard(`https://beta.explorer.cardano.org/en/transaction/${receipt?.merkleProof?.transactionHash}`)}
            style={{ display: 'flex', justifyContent: 'center', width: '100%', marginTop: '24px', cursor: 'pointer' }}>
          <QRCode
            size={256}
            style={{ height: 'auto', width: '200px' }}
            value={`https://beta.explorer.cardano.org/en/transaction/${receipt?.merkleProof?.transactionHash}`}
            viewBox={'0 0 256 256'}
          />
        </div>
        <CustomButton
          styles={{
            background: '#ACFCC5',
            color: '#03021F',
            width: 'auto',
          }}
          label={i18n.t('nominees.done')}
          onClick={toggleViewFinalReceipt}
        />
      </Modal>

      <Modal
        isOpen={confirmVoteModal}
        id="confirm-vote"
        title={i18n.t('nominees.reviewVote')}
        onClose={toggleConfirmVoteModal}
      >
        <Typography
          sx={{
            color: '#39486C',
            fontSize: '16px',
            fontStyle: 'normal',
            fontWeight: '400',
            lineHeight: '22px',
          }}
        >
          {i18n.t('nominees.confirmVoteFor')}{' '}{votedNominee?.presentationName} [{selectedNomineeToVote?.id}]
        </Typography>
        <Box
          display="flex"
          justifyContent="space-between"
          sx={{ marginTop: '24px' }}
        >
          <Button
            onClick={toggleConfirmVoteModal}
            sx={{
              display: 'flex',
              width: '162px',
              padding: '16px 24px',
              justifyContent: 'center',
              alignItems: 'center',
              gap: '10px',
              borderRadius: '8px',
              border: '1px solid #DAEEFB',
              textTransform: 'none',
              color: '#434656',
              fontSize: '16px',
              fontStyle: 'normal',
              fontWeight: '600',
              lineHeight: 'normal',
              '&:hover': { backgroundColor: 'inherit' },
            }}
          >
            {i18n.t('nominees.cancel')}
          </Button>
          <Button
            onClick={() => handleVoteNomineeButton()}
            sx={{
              display: 'flex',
              width: '162px',
              padding: '16px 24px',
              justifyContent: 'center',
              alignItems: 'center',
              gap: '10px',
              borderRadius: '8px',
              background: '#ACFCC5',
              textTransform: 'none',
              color: '#03021F',
              fontSize: '16px',
              fontStyle: 'normal',
              fontWeight: '600',
              lineHeight: 'normal',
              '&:hover': { backgroundColor: '#ACFCC5' },
            }}
          >
            {i18n.t('nominees.confirmVote')}
          </Button>
        </Box>
      </Modal>
    </>
  );
};

export { Nominees };

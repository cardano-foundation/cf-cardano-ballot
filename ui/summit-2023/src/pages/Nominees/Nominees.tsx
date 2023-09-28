import React, { useState, useEffect, useMemo, ReactElement } from 'react';
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
import EmojiEventsIcon from '@mui/icons-material/EmojiEvents';
import labelVoted from '../../common/resources/images/checkmark-green.png';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import { Fade } from '@mui/material';
import './Nominees.scss';
import { CategoryContent } from '../Categories/Category.types';
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
  getUserVotes,
  getVoteReceipt,
} from '../../common/api/voteService';
import { copyToClipboard, getSignedMessagePromise, resolveCardanoNetwork, shortenString } from '../../utils/utils';
import { buildCanonicalLoginJson, submitLogin } from 'common/api/loginService';
import { getUserInSession, saveUserInSession, tokenIsExpired } from '../../utils/session';
import { setUserVotes, setVoteReceipt, setWalletIsLoggedIn } from '../../store/userSlice';
import { FinalityScore } from '../../types/voting-ledger-follower-types';
import SidePage from '../../components/common/SidePage/SidePage';
import { useToggle } from 'common/hooks/useToggle';
import ReadMore from './ReadMore';
import Modal from '../../components/common/Modal/Modal';
import QRCode from 'react-qr-code';
import { CustomButton } from '../../components/common/Button/CustomButton';
import { env } from 'common/constants/env';
import { parseError } from 'common/constants/errors';
import { categoryAlreadyVoted } from '../Categories';
import { ProposalPresentationExtended } from '../../store/types';
import { verifyVote } from 'common/api/verificationService';

const Nominees = () => {
  const { categoryId } = useParams();
  const navigate = useNavigate();
  const eventCache = useSelector((state: RootState) => state.user.event);
  const walletIsVerified = useSelector((state: RootState) => state.user.walletIsVerified);
  const walletIsLoggedIn = useSelector((state: RootState) => state.user.walletIsLoggedIn);
  const receipts = useSelector((state: RootState) => state.user.receipts);
  const receipt = receipts && Object.keys(receipts).length && receipts[categoryId] ? receipts[categoryId] : undefined;
  const userVotes = useSelector((state: RootState) => state.user.userVotes);
  const winners = useSelector((state: RootState) => state.user.winners);

  const categoryVoted = categoryAlreadyVoted(categoryId, userVotes);

  const dispatch = useDispatch();

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

  const { isConnected, stakeAddress, signMessage } = useCardano({
    limitNetwork: resolveCardanoNetwork(env.TARGET_NETWORK),
  });

  const signMessagePromisified = useMemo(() => getSignedMessagePromise(signMessage), [signMessage]);

  const loadNominees = () => {
    if (categoryId) {
      categories?.map((category) => {
        if (category.id === categoryId) {
          setNominees(category?.proposals || []);
        }
      });
    } else {
      navigate(ROUTES.PAGENOTFOUND);
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
        eventBus.publish('openLoginModal', 'Login to see your vote receipt');
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
        return 'Verify your wallet';
      } else {
        return 'Vote for nominee';
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
      .then(() => eventBus.publish('showToast', 'Copied to clipboard'))
      .catch(() => eventBus.publish('showToast', 'Copied to clipboard failed', 'error'));
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
    if (isConnected && walletIsLoggedIn && !tokenIsExpired(session?.expiresAt)) {
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
            eventBus.publish('showToast', 'Vote proof verified', 'verified');
          } else {
            eventBus.publish('showToast', 'Vote proof not verified', 'error');
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
                          <Tooltip title="Already Voted">
                            <img
                              height={40}
                              width={102}
                              src={labelVoted}
                              alt="Already Voted"
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
                        className="nominee-title"
                        variant="h2"
                      >
                        {nominee.presentationName}
                        {isWinner ? (
                          <Tooltip title="Winner">
                            <EmojiEventsIcon
                              sx={{ fontSize: '40px', position: 'absolute', marginLeft: '4px', color: '#efb810' }}
                            />
                          </Tooltip>
                        ) : null}
                      </Typography>
                      <Grid container>
                        <Grid
                          item
                          xs={10}
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
                          width: '146px',
                          marginTop: '15px',
                        }}
                        label="Read more"
                        onClick={() => handleReadMore(nominee)}
                        fullWidth={true}
                      />
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
        <div style={{ width: '100%' }}>
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
                  sm={12}
                  md={4}
                  lg={4}
                  key={nominee.id}
                >
                  <div style={{ height: 'auto', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                    <Card
                      sx={{
                        height: 'auto',
                        width: { xs: '380px', sm: '414px' },
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        borderRadius: '16px',
                      }}
                    >
                      <CardContent>
                        <Box sx={{ position: 'relative', marginTop: '12px' }}>
                          {voted ? (
                            <Tooltip title="Already Voted">
                              <img
                                height={40}
                                width={102}
                                src={labelVoted}
                                alt="Already Voted"
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
                          className="nominee-title"
                          variant="h4"
                          sx={{ mb: 1, fontWeight: 'bold', wordWrap: 'break-word', width: voted ? '260px' : '100%' }}
                        >
                          {nominee.presentationName}
                          {isWinner ? (
                            <Tooltip title="Winner">
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
                              sx={{ height: '110px' }}
                            >
                              {shortenString(nominee.desc, 210)}
                            </Typography>
                          </Grid>
                        </Grid>

                        <CustomButton
                          styles={{
                            background: 'transparent !important',
                            color: '#03021F',
                            border: '1px solid #daeefb',
                            width: '100%',
                            marginTop: '28px',
                          }}
                          label="Read more"
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

  return (
    <>
      <div
        data-testid="nominees-page"
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
          variant="body1"
          gutterBottom
          sx={{ marginBottom: '50px' }}
        >
          {summit2023Category.desc}
        </Typography>

        {isConnected &&
        (categoryVoted || (isConnected && eventCache?.finished) || (receipt && categoryId === receipt?.category)) ? (
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
                style={{
                  color: '#24262E',
                  fontSize: '18px',
                  fontStyle: 'normal',
                  fontWeight: '600',
                  lineHeight: '22px',
                }}
              >
                {walletIsLoggedIn && !tokenIsExpired(session?.expiresAt)
                  ? `You have successfully cast a vote for ${receipt?.presentationName} in the ${summit2023Category.presentationName} category.`
                  : 'To see you vote receipt, please sign with your Wallet'}
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
                      <Tooltip title="The submitted vote has been successfully verified on-chain.">
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
                      <Tooltip title="Assurance levels will update according to the finality of the transaction on-chain.">
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
                <Tooltip title="Cardano Summit 2023 Awards.">
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
                <Tooltip title="Identifies the nominee selected for this category.">
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
                  Voter Staking Address
                </Typography>
                <Tooltip title="The stake address associated with the Cardano wallet casting the vote.">
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
                <Tooltip title="The current status of your vote receipt based on the current assurance level.">
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
                    <Tooltip title="This is a unique identifier associated with the vote submitted.">
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
                    <Tooltip title="The time of the vote submission represented in Cardano blockchain epoch slots.">
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
                    <Tooltip title="This is required to verify a vote was included on-chain.">
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
                      {receipt?.merkleProof ? JSON.stringify(receipt?.merkleProof || '', null, 4) : 'Not available yet'}
                    </Typography>
                  </Box>
                  {receipt?.merkleProof ? (
                    <CustomButton
                      styles={{
                        background: '#ACFCC5',
                        color: '#03021F',
                        width: 'auto',
                      }}
                      label="Verify vote proof"
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
          label={`Vote for ${nominees.find((nominee) => nominee.id === selectedNomineeToVote?.id)
            ?.presentationName} [${selectedNomineeToVote?.id}]`}
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

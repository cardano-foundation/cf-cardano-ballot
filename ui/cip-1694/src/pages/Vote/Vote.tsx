import React, { useEffect, useState } from 'react';
import { useDispatch } from 'react-redux';
import { v4 as uuidv4 } from 'uuid';
import toast from 'react-hot-toast';
import cn from 'classnames';
import { Grid, Typography, Button } from '@mui/material';
import DoneIcon from '@mui/icons-material/Done';
import CloseIcon from '@mui/icons-material/Close';
import DoDisturbIcon from '@mui/icons-material/DoDisturb';
import ReceiptIcon from '@mui/icons-material/ReceiptLongOutlined';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import CountDownTimer from 'components/CountDownTimer/CountDownTimer';
import { setIsConnectWalletModalVisible, setIsVoteSubmittedModalVisible } from 'common/store/userSlice';
import { Account, ChainTip } from 'types/backend-services-types';
import { OptionCard } from '../../components/OptionCard/OptionCard';
import { OptionItem } from '../../components/OptionCard/OptionCard.types';
import SidePage from '../../components/common/SidePage/SidePage';
import { buildCanonicalVoteInputJson } from '../../common/utils/voteUtils';
import { voteService } from '../../common/api/voteService';
import { EVENT_ID } from '../../common/constants/appConstants';
import { useToggle } from '../../common/hooks/useToggle';
import { HttpError } from '../../common/handlers/httpHandler';
import VoteReceipt from './VoteReceipt';
import styles from './Vote.module.scss';

const errorsMap = {
  INVALID_VOTING_POWER: 'To cast a vote, Voting Power should be more than 0',
  EXPIRED_SLOT: "CIP-93's envelope slot is expired!",
  VOTE_CANNOT_BE_CHANGED: 'You have already voted! Vote cannot be changed for this stake address',
};

const items: OptionItem[] = [
  {
    label: 'Yes',
    icon: <DoneIcon sx={{ fontSize: 52, color: '#39486C' }} />,
  },
  {
    label: 'No',
    icon: <CloseIcon sx={{ fontSize: 52, color: '#39486C' }} />,
  },
  {
    label: 'Abstain',
    icon: <DoDisturbIcon sx={{ fontSize: 52, color: '#39486C' }} />,
  },
];

const Vote = () => {
  const { stakeAddress, isConnected, signMessage } = useCardano();
  const [isDisabled, setIsDisabled] = useState<boolean>(false);
  const [showVoteReceipt, setShowVoteReceipt] = useState<boolean>(false);
  const [optionId, setOptionId] = useState('');
  const [isToggledReceipt, toggleReceipt] = useToggle(false);
  const dispatch = useDispatch();

  const initialise = () => {
    optionId === '' && setIsDisabled(true);
    !isConnected && showVoteReceipt && setShowVoteReceipt(true);
  };

  useEffect(() => {
    initialise();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const notify = (message: string) => toast(message);

  const onChangeOption = (option: string) => {
    if (option !== null) {
      setOptionId(option);
      setIsDisabled(false);
    } else {
      setIsDisabled(true);
    }
  };

  const handleSubmit = async () => {
    if (!EVENT_ID) {
      console.log('EVENT_ID is not provided');
      return;
    }

    if (!isConnected) {
      dispatch(setIsConnectWalletModalVisible({ isVisible: true }));
      return;
    }

    let absoluteSlot: ChainTip['absoluteSlot'];
    try {
      ({ absoluteSlot } = await voteService.getSlotNumber());
    } catch (error) {
      console.log('Failed to fecth slot number', error?.message);
    }

    let votingPower: Account['votingPower'];
    try {
      ({ votingPower } = await voteService.getVotingPower(EVENT_ID, stakeAddress));
    } catch (error) {
      if (error instanceof Error || error instanceof HttpError) {
        console.log('Failed to fetch votingPower', error?.message);
      } else console.log('Failed to fetch votingPower', error);
      return;
    }

    const canonicalVoteInput = buildCanonicalVoteInputJson({
      option: optionId?.toUpperCase(),
      voter: stakeAddress,
      voteId: uuidv4(),
      slotNumber: absoluteSlot.toString(),
      votePower: votingPower,
    });
    signMessage(canonicalVoteInput, async (signature, key) => {
      const requestVoteObject = {
        cosePublicKey: key || '',
        coseSignature: signature,
      };

      try {
        await voteService.castAVoteWithDigitalSignature(requestVoteObject);
        dispatch(setIsVoteSubmittedModalVisible({ isVisible: true }));
        setOptionId('');
        setShowVoteReceipt(true);
      } catch (error) {
        if (error instanceof HttpError && error.code === 400) {
          notify(errorsMap[error?.message as keyof typeof errorsMap] || error?.message);
          setOptionId('');
          setIsDisabled(true);
        } else if (error instanceof Error) {
          notify(error?.message);
          console.log('Failed to cast e vote', error);
        }
      }
    });
  };

  return (
    <div className={styles.vote}>
      <Grid
        container
        direction="column"
        justifyContent="left"
        alignItems="left"
        spacing={0}
      >
        <Grid item>
          <Typography
            variant="h5"
            className={styles.title}
          >
            CIP-1694 vote
          </Typography>
        </Grid>
        <Grid item>
          <Typography
            sx={{
              mb: '24px',
            }}
          >
            <CountDownTimer />
          </Typography>
        </Grid>
        <Grid item>
          <Typography
            variant="h5"
            className={styles.description}
          >
            Do you want CIP-1694 that will allow On-Chain Governance, implemented on the Cardano Blockchain?
          </Typography>
        </Grid>
        <Grid item>
          <OptionCard
            disabled={showVoteReceipt}
            items={items}
            onChangeOption={onChangeOption}
          />
        </Grid>
        <Grid item>
          <Grid
            container
            direction="row"
            justifyContent={'center'}
          >
            <Grid item>
              {!showVoteReceipt ? (
                <Button
                  className={cn(styles.button, { [styles.disabled]: isDisabled && isConnected })}
                  size="large"
                  variant="contained"
                  disabled={isDisabled && isConnected}
                  onClick={() => handleSubmit()}
                  sx={{}}
                >
                  {!isConnected ? 'Connect wallet to vote' : 'Submit Your Vote'}
                </Button>
              ) : (
                <Button
                  className={cn(styles.button, styles.secondary)}
                  variant="contained"
                  onClick={() => toggleReceipt()}
                  aria-label="Receipt"
                  startIcon={<ReceiptIcon />}
                >
                  Vote receipt
                </Button>
              )}
            </Grid>
          </Grid>
        </Grid>
      </Grid>
      <SidePage
        anchor="right"
        open={isToggledReceipt}
        setOpen={toggleReceipt}
      >
        <VoteReceipt />
      </SidePage>
    </div>
  );
};

export default Vote;

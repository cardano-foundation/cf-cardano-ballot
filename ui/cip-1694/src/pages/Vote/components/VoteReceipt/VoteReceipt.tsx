import React, { useCallback, useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import toast from 'react-hot-toast';
import pick from 'lodash/pick';
import Grid from '@mui/material/Grid';
import { IconButton, Typography, debounce, Accordion, AccordionSummary, AccordionDetails } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import BlockIcon from '@mui/icons-material/Block';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';
import { VoteVerificationRequest } from 'types/voting-verification-app-types';
import * as verificationService from 'common/api/verificationService';
import { RootState } from 'common/store';
import { setIsVerifyVoteModalVisible } from 'common/store/userSlice';
import { Toast } from 'components/common/Toast/Toast';
import {
  AdvancedFullFieldsToDisplayArrayKeys,
  FieldsToDisplayArrayKeys,
  advancedFieldsToDisplay,
  advancedFullFieldsToDisplay,
  generalFieldsToDisplay,
} from './utils';
import { ReceiptItem } from './components/ReceiptItem/ReceipItem';
import { ReceiptInfo } from './components/ReceiptInfo/ReceiptInfo';
import styles from './VoteReceipt.module.scss';

type VoteReceiptProps = {
  setOpen: () => void;
  fetchReceipt: (props: { cb?: () => void; refetch?: boolean }) => void;
};

export const VoteReceipt = ({ setOpen, fetchReceipt }: VoteReceiptProps) => {
  const dispatch = useDispatch();
  const receipt = useSelector((state: RootState) => state.user.receipt);
  const [isVerified, setIsVerified] = useState(false);

  const verifyVote = useCallback(async () => {
    try {
      const {
        merkleProof: { rootHash = '', steps = [] } = {},
        coseSignature: voteCoseSignature,
        cosePublicKey: voteCosePublicKey,
      } = receipt;

      const verified = await verificationService.verifyVote({
        rootHash,
        voteCoseSignature,
        voteCosePublicKey,
        steps: steps as unknown as VoteVerificationRequest['steps'],
      });
      if (typeof verified === 'boolean') {
        setIsVerified(verified);
      }
    } catch (error) {
      console.log('Failed to verify vote', error?.message);
      toast(
        <Toast
          message="Unable to verify vote receipt. Please try again"
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
    }
  }, [receipt]);

  useEffect(() => {
    if (!isVerified && receipt?.status === 'FULL') {
      verifyVote();
    }
  }, [isVerified, receipt?.status, verifyVote]);

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const debouncedToast = useCallback(debounce(toast, 300), []);

  const onItemClick = useCallback(() => {
    debouncedToast(<Toast message="Copied to clipboard" />);
  }, [debouncedToast]);

  const fieldsToDisplay = pick(receipt, generalFieldsToDisplay);
  const moreFieldsToDisplay = pick(
    {
      ...receipt,
      voteProof: {
        rootHash: receipt?.merkleProof?.rootHash,
        steps: receipt?.merkleProof?.steps,
        coseSignature: receipt.coseSignature,
        cosePublicKey: receipt.cosePublicKey,
      },
    },
    receipt.status === 'FULL' ? advancedFullFieldsToDisplay : advancedFieldsToDisplay
  );

  return (
    <Grid
      data-testid="vote-receipt"
      container
      direction="column"
      justifyContent="center"
      alignItems="center"
      spacing={0}
      maxWidth="100%"
      sx={{ padding: { xs: '20px', md: '30px' }, paddingTop: '50px', width: '550px' }}
    >
      <IconButton
        aria-label="close"
        onClick={setOpen}
        className={styles.closeBtn}
        data-testid="vote-receipt-close-button"
      >
        <CloseIcon className={styles.closeIcon} />
      </IconButton>
      <Grid
        item
        spacing={0}
        container
        justifyContent="center"
      >
        <Typography
          className={styles.title}
          variant="h4"
          sx={{ marginBottom: '28px' }}
        >
          Vote Receipt
        </Typography>
      </Grid>
      <Grid
        item
        width="100%"
        wrap="nowrap"
        direction="row"
        container
        gap="12px"
      >
        <ReceiptInfo
          fetchReceipt={fetchReceipt}
          showVerifiedModal={() => dispatch(setIsVerifyVoteModalVisible({ isVisible: true }))}
          receipt={receipt}
          isVerified={isVerified}
        />
      </Grid>
      <Grid
        item
        container
        width="100%"
        direction="column"
        justifyContent="center"
        alignItems="start"
        spacing={0}
      >
        {Object.entries(fieldsToDisplay).map(([key, value]: [FieldsToDisplayArrayKeys, string]) => (
          <ReceiptItem
            key={key}
            {...{ name: key, value, onItemClick }}
          />
        ))}
        <Accordion
          TransitionProps={{ unmountOnExit: true }}
          className={styles.accordion}
        >
          <AccordionSummary
            className={styles.showMoreBtn}
            expandIcon={<ExpandMoreIcon className={styles.arrowIcon} />}
            aria-controls="panel1a-content"
            id="panel1a-header"
          >
            <Typography style={{ fontSize: '16px', fontWeight: '600' }}>Show advanced information</Typography>
          </AccordionSummary>
          <AccordionDetails sx={{ padding: '0px' }}>
            {Object.entries(moreFieldsToDisplay).map(([key, value]: [AdvancedFullFieldsToDisplayArrayKeys, string]) => (
              <ReceiptItem
                key={key}
                {...{ name: key, value, onItemClick }}
              />
            ))}
          </AccordionDetails>
        </Accordion>
      </Grid>
    </Grid>
  );
};

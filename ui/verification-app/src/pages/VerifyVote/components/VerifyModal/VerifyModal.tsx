import React, { useState, useCallback } from 'react';
import toast from 'react-hot-toast';
import cn from 'classnames';
import BlockIcon from '@mui/icons-material/Block';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { Button } from '@mui/material';
import * as verificationService from 'common/api/verificationService';
import { Toast } from 'common/components/Toast/Toast';
import { Loader } from 'common/components/Loader/Loader';
import { VerifyVoteSection } from './components/VerifyVoteSection/VerifyVoteSection';
import styles from '../../VerifyVote.module.scss';
import { ChoseExplorerSection } from './components/ChoseExplorerSection/ChoseExplorerSection';
import { SECTIONS, VerifyModalProps, ERRORS, voteProof as voteProofType } from './types';
import { ctas, descriptions, errors, titles } from './utils';

export const VerifyModal = ({ opened, onConfirm }: VerifyModalProps) => {
  const [activeSection, setActiveSection] = useState(SECTIONS.VERIFY);
  const [voteProof, setVoteProof] = useState<string>('');
  const [isLoading, setIsLoading] = useState(false);
  const [explorer, setExplorer] = useState<string>('');
  const [txHash, setTxHash] = useState<string>('');

  const handleVerify = useCallback(async () => {
    try {
      const {
        transactionHash,
        rootHash = '',
        steps = [],
        coseSignature,
        cosePublicKey,
      }: voteProofType = JSON.parse(voteProof);

      setIsLoading(true);
      const verified = await verificationService.verifyVote({
        rootHash,
        voteCoseSignature: coseSignature,
        voteCosePublicKey: cosePublicKey,
        steps,
      });
      if ('verified' in verified && typeof verified?.verified === 'boolean') {
        setTxHash(transactionHash);
        setActiveSection(SECTIONS.CHOSE_EXPLORER);
      }
    } catch (error) {
      if (process.env.NODE_ENV === 'development') {
        console.log('Failed to verify vote', error?.message);
      }

      const message = error?.message?.endsWith('is not valid JSON')
        ? errors[ERRORS.JSON]
        : errors[error?.message as ERRORS] || errors[ERRORS.VERIFY];

      toast(
        <Toast
          message={message}
          error
          icon={<BlockIcon style={{ fontSize: '19px', color: '#F5F9FF' }} />}
        />
      );
    }
    setIsLoading(false);
  }, [voteProof]);

  const handleNext = useCallback(() => {
    onConfirm(`${explorer}${txHash}`);
  }, [explorer, onConfirm, txHash]);

  return (
    <Dialog
      disableEscapeKeyDown
      aria-labelledby="dialog-title"
      aria-describedby="dialog-description"
      open={opened}
      maxWidth="xl" // To set width more then 600px
      sx={{ '& .MuiBackdrop-root': { bgcolor: '#F5F9FF' } }}
      PaperProps={{
        sx: {
          width: '750px',
          borderRadius: '16px',
          bgcolor: '#F5F9FF',
          boxShadow: '2px 5px 50px 0px rgba(57, 72, 108, 0.20)',
        },
      }}
    >
      <DialogTitle
        id="dialog-title"
        sx={{
          padding: '50px 50px 20px 50px',
          lineHeight: '32.8px',
          color: '#061D3C',
          fontSize: 28,
          fontFamily: 'Roboto',
          fontWeight: '600',
        }}
      >
        {titles[activeSection]}
      </DialogTitle>

      <DialogContent sx={{ padding: '0px 50px 25px 50px !important' }}>
        <DialogContentText
          id="dialog-description"
          sx={{
            pb: '25px',
            color: '#39486C',
            fontSize: '16px',
            fontFamily: 'Roboto',
            fontWeight: '400',
            wordWrap: 'break-word',
            lineHeight: '22px',
          }}
        >
          {descriptions[activeSection]}
        </DialogContentText>

        {activeSection === SECTIONS.VERIFY && (
          <VerifyVoteSection
            voteProof={voteProof}
            setVoteProof={setVoteProof}
          />
        )}

        {activeSection === SECTIONS.CHOSE_EXPLORER && (
          <ChoseExplorerSection
            setExplorer={setExplorer}
            explorer={explorer}
          />
        )}
      </DialogContent>

      <DialogActions
        sx={{ padding: '0px 50px 50px 50px !important' }}
        className={styles.actionsArea}
        style={{ justifyContent: 'start' }}
      >
        <Button
          variant="contained"
          onClick={activeSection === SECTIONS.VERIFY ? handleVerify : handleNext}
          className={cn(styles.verifyButton, { [styles.loading]: isLoading })}
          disabled={(activeSection === SECTIONS.VERIFY ? !voteProof : !explorer) || isLoading}
        >
          {isLoading ? <Loader /> : ctas[activeSection]}
        </Button>
      </DialogActions>
    </Dialog>
  );
};

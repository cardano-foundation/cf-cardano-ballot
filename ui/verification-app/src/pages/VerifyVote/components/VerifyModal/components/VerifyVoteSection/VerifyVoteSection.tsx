import React from 'react';
import cn from 'classnames';
import TextField from '@mui/material/TextField';
import styles from './VerifyVoteSection.module.scss';

type VerifyProps = {
  voteProof: string;
  setVoteProof: (vote: string) => void;
};

export const VerifyVoteSection = ({ voteProof, setVoteProof }: VerifyProps) => (
  <TextField
    value={voteProof}
    onChange={(e) => setVoteProof(e.target.value)}
    InputLabelProps={{ shrink: false }}
    placeholder="Paste your vote proof here"
    multiline
    maxRows={8}
    fullWidth
    InputProps={{ disableUnderline: true }}
    variant="standard"
    classes={{
      root: cn(styles.root, { [styles.filled]: voteProof }),
    }}
  />
);

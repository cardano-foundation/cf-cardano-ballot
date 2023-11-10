import React from 'react';
import Grid from '@mui/material/Grid';
import cn from 'classnames';
import { TextField, Typography } from '@mui/material';
import styles from './VoteContextInput.module.scss';

type VoteContextInputProps = {
  onChange: (text: string) => void;
  voteContext?: string;
  maxChar?: number;
  disabled: boolean;
};

const MAX_CHARS = 32_000;

export const VoteContextInput = ({ onChange, voteContext, maxChar = MAX_CHARS, disabled }: VoteContextInputProps) => (
  <Grid
    display="flex"
    justifyContent="space-between"
    alignItems="center"
    flexDirection="column"
    gap="20px"
    container
    data-testid="vote-context"
  >
    <Grid
      display="flex"
      justifyContent="space-between"
      alignItems="center"
      item
      width="100%"
    >
      <Typography
        variant="h5"
        className={styles.label}
        lineHeight={{ xs: '16px', md: '18px' }}
        fontSize={{ xs: '16px', md: '18px' }}
        data-testid="vote-context-label"
      >
        Do you have any additional comments or details about your ballot decision?
      </Typography>
    </Grid>
    <Grid
      display="flex"
      justifyContent="space-between"
      alignItems="center"
      item
      width="100%"
    >
      <TextField
        data-testid="vote-context-input"
        value={!disabled ? voteContext : ''}
        onChange={(e) => onChange((e.target.value || '').slice(0, maxChar))}
        InputLabelProps={{ shrink: false }}
        placeholder={disabled ? 'Please select an option first' : `${maxChar} characters max*`}
        multiline
        maxRows={8}
        fullWidth
        InputProps={{ disableUnderline: true }}
        variant="standard"
        classes={{
          root: cn(styles.root, { [styles.filled]: voteContext }),
        }}
        disabled={disabled}
      />
    </Grid>
  </Grid>
);

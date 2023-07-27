import React from 'react';
import { CopyToClipboard } from 'react-copy-to-clipboard';
import Grid from '@mui/material/Grid';
import { IconButton, Tooltip, Typography } from '@mui/material';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import ContentCopyRoundedIcon from '@mui/icons-material/ContentCopyRounded';
import styles from './VoteReceipt.module.scss';
import { FieldsToDisplayArrayKeys, labelTransformerMap, valueTransformerMap } from './utils';

type VoteReceiptProps = {
  onItemClick: (value: string) => void;
  name: FieldsToDisplayArrayKeys;
  value: string;
};

export const ReceiptItem = ({ name, value, onItemClick }: VoteReceiptProps) => (
  <>
    <Grid item>
      <Typography
        className={styles.label}
        variant="h4"
        sx={{ marginBottom: '8px', marginTop: '24px' }}
      >
        <Grid
          container
          gap={'10px'}
          alignItems="center"
        >
          <span>{labelTransformerMap[name] || name}</span>
          <Tooltip
            classes={{ tooltip: styles.tooltip }}
            title={
              <Grid
                container
                direction="column"
                alignItems="left"
                gap={'8px'}
              >
                <Typography
                  className={styles.tooltipTitle}
                  variant="h4"
                >
                  {labelTransformerMap[name] || name}
                </Typography>
                <Typography
                  className={styles.tooltipDescription}
                  variant="h4"
                >
                  Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et
                  dolore magna aliqua.
                </Typography>
              </Grid>
            }
          >
            <IconButton className={styles.labelButton}>
              <InfoOutlinedIcon className={styles.labelIcon} />
            </IconButton>
          </Tooltip>
        </Grid>
      </Typography>
    </Grid>
    <CopyToClipboard
      text={value}
      onCopy={onItemClick}
    >
      <Grid
        container
        alignItems="center"
        gap="8px"
        className={styles.input}
        item
      >
        <Typography
          className={styles.inputText}
          variant="h4"
        >
          {valueTransformerMap[name]?.(value) || value}
        </Typography>
        <IconButton
          data-receiptkey={name}
          className={styles.inputIconButton}
        >
          <ContentCopyRoundedIcon className={styles.inputIcon} />
        </IconButton>
      </Grid>
    </CopyToClipboard>
  </>
);

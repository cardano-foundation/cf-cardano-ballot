import React from 'react';
import { CopyToClipboard } from 'react-copy-to-clipboard';
import cn from 'classnames';
import Grid from '@mui/material/Grid';
import { IconButton, Tooltip, Typography } from '@mui/material';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import ContentCopyRoundedIcon from '@mui/icons-material/ContentCopyRounded';
import {
  AdvancedFullFieldsToDisplayArrayKeys,
  FieldsToDisplayArrayKeys,
  labelTransformerMap,
  valueTransformerMap,
} from '../../utils';
import styles from '../../VoteReceipt.module.scss';

type VoteReceiptProps = {
  onItemClick: (value: string) => void;
  name: FieldsToDisplayArrayKeys | AdvancedFullFieldsToDisplayArrayKeys;
  value: string;
  dataTestId?: string;
  description?: string;
};

export const ReceiptItem = ({
  name,
  value,
  onItemClick,
  dataTestId = 'receipt-item',
  description,
}: VoteReceiptProps) => (
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
          <span data-testid={`${dataTestId}-title`}>{labelTransformerMap[name]}</span>
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
                  className={styles.tooltipDescription}
                  variant="h4"
                >
                  {description}
                </Typography>
              </Grid>
            }
          >
            <IconButton
              data-id={name}
              data-testid={`${dataTestId}-info-icon`}
              className={styles.labelButton}
            >
              <InfoOutlinedIcon className={styles.labelIcon} />
            </IconButton>
          </Tooltip>
        </Grid>
      </Typography>
    </Grid>
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
        data-testid={`${dataTestId}-value`}
      >
        {name === 'id' || name === 'voterStakingAddress' ? (
          <Tooltip
            classes={{ tooltip: cn(styles.tooltip, styles.tooltipFullWidth) }}
            title={
              <Typography
                className={styles.tooltipDescription}
                variant="h4"
              >
                {value}
              </Typography>
            }
          >
            <span>{valueTransformerMap[name]?.(value) || value}</span>
          </Tooltip>
        ) : (
          valueTransformerMap[name]?.(value) || value
        )}
      </Typography>
      {name === 'voteProof' && (
        <CopyToClipboard
          data-testid="copy-vote-proof-cta"
          text={JSON.stringify(value)}
          onCopy={onItemClick}
        >
          <IconButton
            data-receiptkey={name}
            className={styles.inputIconButton}
          >
            <ContentCopyRoundedIcon className={styles.inputIcon} />
          </IconButton>
        </CopyToClipboard>
      )}
    </Grid>
  </>
);

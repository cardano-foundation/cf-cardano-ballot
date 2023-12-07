import React from 'react';
import Grid from '@mui/material/Grid';
import { Button, IconButton, useMediaQuery, useTheme } from '@mui/material';
import QrCodeIcon from '@mui/icons-material/QrCode';
import ReplayIcon from '@mui/icons-material/Replay';
import InfoOutlinedIcon from '@mui/icons-material/InfoOutlined';
import { FinalityScore, Status, VoteReceipt } from 'types/voting-app-types';
import { Tooltip } from 'components/Tooltip/Tooltip';
import { InfoPanelTypes, InfoPanel } from '../../../InfoPanel/InfoPanel';
import styles from './ReceiptInfo.module.scss';

const StatusToInfoPanelType: Partial<Record<Status, InfoPanelTypes>> = {
  PARTIAL: InfoPanelTypes.WARNING,
  ROLLBACK: InfoPanelTypes.ERROR,
};

const InfoPanelTitle = ({ title, children }: { title: string; children?: React.ReactElement }) => (
  <Grid
    container
    gap="8px"
    alignItems="center"
    color="#061D3C"
  >
    {title} {children}
    <Tooltip title="Assurance levels will update according to the finality of the transaction on-chain.">
      <IconButton sx={{ margin: '-8px' }}>
        <InfoOutlinedIcon style={{ color: '#39486CA6', fontSize: '19px' }} />
      </IconButton>
    </Tooltip>
  </Grid>
);

const StatusToInfoPanelTitle: Record<Status | 'VERIFIED', React.FC<{ children?: React.ReactElement }>> = {
  PARTIAL: () => <InfoPanelTitle title="Ballot in progress" />,
  ROLLBACK: () => <InfoPanelTitle title="There’s been a rollback" />,
  VERIFIED: () => <InfoPanelTitle title="Verified" />,
  BASIC: () => <InfoPanelTitle title="Ballot not ready for verification" />,
  FULL: ({ children }: { children: React.ReactElement }) => (
    <InfoPanelTitle title="Assurance:">{children}</InfoPanelTitle>
  ),
};

const StatusToInfoPanelDescription: Partial<Record<Status | 'VERIFIED', string>> = {
  PARTIAL:
    'Your transaction has been sent and is awaiting confirmation from the Cardano network (this could be 5-10 minutes). Once this has been confirmed you’ll be able to verify your ballot.',
  ROLLBACK:
    'Don’t worry there’s nothing for you to do. We will automatically resubmit your ballot. Please check back later (up to 30 minutes) to see your ballot status.',
  BASIC:
    'Your ballot has been successfully submitted. You might have to wait up to 30 minutes for this to be visible on-chain. Please check back later to verify your ballot.',
  VERIFIED: '',
};

const HIGHT_ASSURANCE =
  'Your ballot is currently being verified. While in HIGH, the chance of a rollback is very unlikely. Check back later to see if verification has completed.';

const FullStatusToInfoPanelDescription: Record<FinalityScore, string> = {
  LOW: 'Your ballot is currently being verified. While in LOW, there is the highest chance of a rollback. Check back later to see if verification has completed.',
  MEDIUM:
    'Your ballot is currently being verified. While in MEDIUM, the chance of rollback is still possible. Check back later to see if verification has completed.',
  HIGH: HIGHT_ASSURANCE,
  VERY_HIGH: HIGHT_ASSURANCE,
  FINAL: HIGHT_ASSURANCE,
};

type ReceiptInfoProps = {
  isVerified: boolean;
  fetchReceipt: () => void;
  receipt: VoteReceipt;
};

export const ReceiptInfo = ({ isVerified, fetchReceipt, receipt }: ReceiptInfoProps) => {
  const theme = useTheme();
  const isSmallScreen = useMediaQuery(theme.breakpoints.up('sm'));

  if (!receipt) return null;

  const status = isVerified ? 'VERIFIED' : receipt.status;
  const finalityScore = ['VERY_HIGH', 'FINAL'].includes(receipt.finalityScore) ? 'HIGH' : receipt.finalityScore;
  const Title = StatusToInfoPanelTitle[status];

  return (
    <InfoPanel
      type={isVerified ? InfoPanelTypes.SUCCESS : StatusToInfoPanelType[receipt.status] || InfoPanelTypes.DEFAULT}
      title={<Title>{status === 'FULL' && <span data-finalityscore={finalityScore}>{finalityScore}</span>}</Title>}
      description={
        isVerified ? '' : StatusToInfoPanelDescription[status] || FullStatusToInfoPanelDescription[finalityScore]
      }
      isSm={isSmallScreen}
      cta={
        <Button
          className={styles.ctaButton}
          size="large"
          variant="outlined"
          onClick={!isVerified ? () => fetchReceipt() : undefined}
          data-testid="refetch-receipt-button"
        >
          {isVerified ? <QrCodeIcon className={styles.ctaIcon} /> : <ReplayIcon className={styles.ctaIcon} />}
        </Button>
      }
    />
  );
};

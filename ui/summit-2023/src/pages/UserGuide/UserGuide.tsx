import { Grid, Typography } from '@mui/material';
import React from 'react';
import { GuideTile } from './components/GuideTile';
import styles from './UserGuide.module.scss';
import LooksOneIcon from '@mui/icons-material/LooksOneOutlined';
import LooksTwoIcon from '@mui/icons-material/LooksTwoOutlined';
import LooksThreeIcon from '@mui/icons-material/Looks3Outlined';
import LooksFourIcon from '@mui/icons-material/Looks4Outlined';
import LooksFiveIcon from '@mui/icons-material/Looks5Outlined';
import LooksSixIcon from '@mui/icons-material/Looks6Outlined';
import { i18n } from 'i18n';

const UserGuide = () => {
  return (
    <div
      data-testid="userguide-page"
      className={styles.userguide}
    >
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginTop: '10px',
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
          {i18n.t('userGuide.title')}
        </Typography>
      </div>

      <Typography
        className={styles.sectionTitle}
        variant="h3"
        gutterBottom
      >
        {i18n.t('userGuide.requirements.title')}
      </Typography>

      <Grid
        container
        spacing={4}
        gridRow={{ md: 6, xs: 12 }}
        gap={{ md: '46px', xs: '25px' }}
        sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' } }}
      >
        <GuideTile
          stepNumber={<LooksOneIcon fontSize="large" />}
          height={470}
          media="image"
          graphic={'/static/sms_verification.png'}
          stepTitle={i18n.t('userGuide.requirements.sms.title')}
          stepHint={i18n.t('userGuide.requirements.sms.hint')}
        />
        <GuideTile
          stepNumber={<LooksTwoIcon fontSize="large" />}
          height={470}
          media="image"
          graphic={'/static/list_of_wallets.png'}
          stepTitle={i18n.t('userGuide.requirements.discord.title')}
          stepHint={i18n.t('userGuide.requirements.discord.hint')}
        />
      </Grid>

      <Typography
        className={styles.sectionTitle}
        variant="h3"
        gutterBottom
      >
        {i18n.t('userGuide.createVerify.title')}
      </Typography>

      <Grid
        container
        spacing={3}
        gridRow={{ md: 6, xs: 12 }}
        gap={{ md: '46px', xs: '25px' }}
        sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' } }}
      >
        <GuideTile
          stepNumber={<LooksOneIcon fontSize="large" />}
          height={470}
          media="video"
          graphic={'/static/connect_wallet.mov'}
          stepTitle={i18n.t('userGuide.createVerify.steps.1.title')}
          stepHint={i18n.t('userGuide.createVerify.steps.1.hint')}
        />
        <GuideTile
          stepNumber={<LooksTwoIcon fontSize="large" />}
          height={470}
          media="video"
          graphic={'/static/verify_wallet.mov'}
          stepTitle={i18n.t('userGuide.createVerify.steps.2.title')}
          stepHint={i18n.t('userGuide.createVerify.steps.2.hint')}
        />
      </Grid>

      <Typography
        className={styles.sectionTitle}
        variant="h3"
        gutterBottom
      >
        {i18n.t('userGuide.submitVote.title')}
      </Typography>

      <Grid
        container
        spacing={2}
        gridRow={{ xs: 12, md: 6 }}
        sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' } }}
      >
        <GuideTile
          stepNumber={<LooksOneIcon fontSize="large" />}
          width={'35%'}
          height={446}
          media="image"
          graphic={'/static/categories.png'}
          stepTitle={i18n.t('userGuide.submitVote.steps.1')}
        />
        <GuideTile
          stepNumber={<LooksTwoIcon fontSize="large" />}
          width={'35%'}
          height={446}
          media="image"
          graphic={'/static/categories_card.png'}
          stepTitle={i18n.t('userGuide.submitVote.steps.2')}
        />
        <GuideTile
          stepNumber={<LooksThreeIcon fontSize="large" />}
          width={'35%'}
          height={446}
          media="image"
          graphic={'/static/view_nominees.png'}
          stepTitle={i18n.t('userGuide.submitVote.steps.3')}
        />
      </Grid>
      <Grid
        container
        spacing={2}
        gridRow={{ xs: 12, md: 6 }}
        sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' } }}
      >
        <GuideTile
          stepNumber={<LooksFourIcon fontSize="large" />}
          width={'35%'}
          height={446}
          media="image"
          graphic={'/static/vote_for_nominee.png'}
          stepTitle={i18n.t('userGuide.submitVote.steps.4')}
        />
        <GuideTile
          stepNumber={<LooksFiveIcon fontSize="large" />}
          width={'35%'}
          height={446}
          media="image"
          graphic={'/static/sign_with_wallet.png'}
          stepTitle={i18n.t('userGuide.submitVote.steps.5')}
        />
        <GuideTile
          stepNumber={<LooksSixIcon fontSize="large" />}
          width={'35%'}
          height={446}
          media="image"
          graphic={'/static/submit.png'}
          stepTitle={i18n.t('userGuide.submitVote.steps.6')}
        />
      </Grid>
    </div>
  );
};

export { UserGuide };

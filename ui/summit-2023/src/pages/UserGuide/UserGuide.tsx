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
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <Typography
          className={styles.title}
          variant="h2"
          fontSize={{
            xs: '28px',
            md: '32px',
          }}
          lineHeight={{
            xs: '28px',
            md: '32px',
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
        spacing={3}
        gridRow={{ md: 6, xs: 12 }}
        gap={{ md: '46px', xs: '25px' }}
        sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' } }}
      >
        <GuideTile
          stepNumber={<LooksOneIcon fontSize="large" />}
          width={530}
          height={470}
          graphic={'/static/CardanoBallot-category-1.png'}
          featureImg={'/static/sms_verification_message.png'}
          stepTitle={i18n.t('userGuide.requirements.sms.title')}
          stepHint={i18n.t('userGuide.requirements.sms.hint')}
          featureImgStyle={{ height: '210px', padding: '10% 0% 10% 110%', margin: '15%' }}
        />
        <GuideTile
          stepNumber={<LooksTwoIcon fontSize="large" />}
          width={530}
          height={470}
          graphic={'/static/CardanoBallot-category-2.png'}
          featureImg={'/static/cardano_wallet_extension.png'}
          stepTitle={i18n.t('userGuide.requirements.discord.title')}
          stepHint={i18n.t('userGuide.requirements.discord.hint')}
          featureImgStyle={{ height: '210px', padding: '10% 0% 10% 150%', margin: '15%' }}
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
          width={530}
          height={470}
          graphic={'/static/CardanoBallot-category-3.png'}
          featureImg={'/static/connect_wallet.png'}
          stepTitle={i18n.t('userGuide.createVerify.steps.1.title')}
          stepHint={i18n.t('userGuide.createVerify.steps.1.hint')}
          featureImgStyle={{ height: '135px', padding: '7% 0% 10% 12%', margin: '15%' }}
        />
        <GuideTile
          stepNumber={<LooksTwoIcon fontSize="large" />}
          width={530}
          height={470}
          graphic={'/static/CardanoBallot-category-4.png'}
          featureImg={'/static/verify_wallet.png'}
          stepTitle={i18n.t('userGuide.createVerify.steps.2.title')}
          stepHint={i18n.t('userGuide.createVerify.steps.2.hint')}
          featureImgStyle={{ height: '125px', padding: '10% 0% 10% 60%', margin: '20%' }}
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
        spacing={3}
        gridRow={{ md: 6, xs: 12 }}
        gap={{ md: '46px', xs: '25px' }}
        sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' } }}
      >
        <GuideTile
          stepNumber={<LooksOneIcon fontSize="large" />}
          width={414}
          height={446}
          graphic={'/static/CardanoBallot-category-5.png'}
          featureImg={'/static/categories.png'}
          stepTitle={i18n.t('userGuide.submitVote.steps.1')}
          featureImgStyle={{ height: '100px', padding: '10% 0% 10% 60%', margin: '35%' }}
        />
        <GuideTile
          stepNumber={<LooksTwoIcon fontSize="large" />}
          width={414}
          height={446}
          graphic={'/static/CardanoBallot-category-6.png'}
          featureImg={'/static/categories_card.png'}
          stepTitle={i18n.t('userGuide.submitVote.steps.2')}
          featureImgStyle={{ height: '250px', padding: '5% 0% 5% 65%', margin: '5%' }}
        />
        <GuideTile
          stepNumber={<LooksThreeIcon fontSize="large" />}
          width={414}
          height={446}
          graphic={'/static/CardanoBallot-category-7.png'}
          featureImg={'/static/view_nominees.png'}
          stepTitle={i18n.t('userGuide.submitVote.steps.3')}
          featureImgStyle={{ width: '340px', padding: '10% 0% 10% 15%', margin: '25%' }}
        />
      </Grid>
      <Grid
        container
        spacing={1}
        gridRow={{ md: 6, xs: 12 }}
        gap={{ md: '46px', xs: '25px' }}
        sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' } }}
      >
        <GuideTile
          stepNumber={<LooksFourIcon fontSize="large" />}
          width={414}
          height={446}
          graphic={'/static/CardanoBallot-category-8.png'}
          featureImg={'/static/vote_for_nominee.png'}
          stepTitle={i18n.t('userGuide.submitVote.steps.4')}
          featureImgStyle={{ width: '340px', padding: '10% 0% 10% 15%', margin: '25%' }}
        />
        <GuideTile
          stepNumber={<LooksFiveIcon fontSize="large" />}
          width={414}
          height={446}
          graphic={'/static/CardanoBallot-category-9.png'}
          featureImg={'/static/sign_with_wallet.png'}
          stepTitle={i18n.t('userGuide.submitVote.steps.5')}
          featureImgStyle={{ height: '115px', padding: '7% 0% 10% 12%', margin: '20%' }}
        />
        <GuideTile
          stepNumber={<LooksSixIcon fontSize="large" />}
          width={414}
          height={446}
          graphic={'/static/CardanoBallot-category-10.png'}
          featureImg={'/static/submit.png'}
          stepTitle={i18n.t('userGuide.submitVote.steps.6')}
          featureImgStyle={{ height: '115px', padding: '7% 0% 10% 12%', margin: '20%' }}
        />
      </Grid>
    </div>
  );
};

export { UserGuide };

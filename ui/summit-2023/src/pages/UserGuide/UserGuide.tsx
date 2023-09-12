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
          User Guide
        </Typography>
      </div>

      <Typography
        className={styles.sectionTitle}
        variant="h3"
        gutterBottom
      >
        To submit votes using Cardano Ballot, you’ll need
      </Typography>

      <Grid
        container
        spacing={3}
        gridRow={{ md: 6, xs: 12 }}
        gap={{ md: '46px', xs: '25px' }}
        sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' } }}
      >
        <GuideTile
          stepNumber={<LooksOneIcon fontSize="large"/>}
          width={530}
          height={470}
          graphic={'/static/CardanoBallot-category-1.png'}
          stepTitle={'The ability to receive an SMS verification message.'}
          stepHint={'Securely verify your account with a one-time SMS code for Cardano Ballot. Safety and simplicity combined.'}
        />
        <GuideTile
          stepNumber={<LooksTwoIcon fontSize="large" />}
          width={530}
          height={470}
          graphic={'/static/CardanoBallot-category-2.png'}
          stepTitle={'A supported Cardano wallet and/or browser extension'}
          stepHint={'You dont need to have any funds in your wallet to use Cardano Ballot. View a list of supported wallets'}
        />
      </Grid>

      <Typography
        className={styles.sectionTitle}
        variant="h3"
        gutterBottom
      >
        Creating and Verifying your Cardano Ballot Account
      </Typography>

      <Grid
        container
        spacing={3}
        gridRow={{ md: 6, xs: 12 }}
        gap={{ md: '46px', xs: '25px' }}
        sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' } }}
      >
        <GuideTile
          stepNumber={<LooksOneIcon fontSize="large"/>}
          width={530}
          height={470} 
          graphic={'/static/CardanoBallot-category-3.png'}
          stepTitle={'Click on "Connect Wallet" and choose a supported wallet from the list.'}
          stepHint={'By default, only Flint (Desktop/Mobile) and installed supported wallets will be shown.'}
        />
        <GuideTile
          stepNumber={<LooksTwoIcon fontSize="large" />}
          width={530}
          height={470} 
          graphic={'/static/CardanoBallot-category-4.png'}
          stepTitle={'Verify your wallet using CIP8 message signing through SMS or Discord.'}
          stepHint={'Seamless wallet verification with CIP8 message signing via SMS or Discord. Protect your Cardano Ballot account effortlessly.'}
        />
      </Grid>

      <Typography
        className={styles.sectionTitle}
        variant="h3"
        gutterBottom
      >
        How to submit a vote on Cardano Ballot
      </Typography>

      <Grid
        container
        spacing={3}
        gridRow={{ md: 6, xs: 12 }}
        gap={{ md: '46px', xs: '25px' }}
        sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' } }}
      >
        <GuideTile
          stepNumber={<LooksOneIcon fontSize="large"/>}
          width={414}
          height={446} 
          graphic={'/static/CardanoBallot-category-5.png'}
          stepTitle={'Navigate to Cardano Ballot’s categoy section.'}
        />
        <GuideTile
          stepNumber={<LooksTwoIcon fontSize="large" />}
          width={414}
          height={446} 
          graphic={'/static/CardanoBallot-category-6.png'}
          stepTitle={'Verify your wallet using CIP8 message signing through SMS or Discord.'}
        />
        <GuideTile
          stepNumber={<LooksThreeIcon fontSize="large" />}
          width={414}
          height={446} 
          graphic={'/static/CardanoBallot-category-7.png'}
          stepTitle={'Verify your wallet using CIP8 message signing through SMS or Discord.'}
        />
      </Grid>
      <Grid
        container
        spacing={3}
        gridRow={{ md: 6, xs: 12 }}
        gap={{ md: '46px', xs: '25px' }}
        sx={{ flexWrap: { md: 'nowrap', xs: 'wrap' } }}
      >
        <GuideTile
          stepNumber={<LooksFourIcon fontSize="large"/>}
          width={414}
          height={446} 
          graphic={'/static/CardanoBallot-category-8.png'}
          stepTitle={'Review the nominees and click on "Vote for nominee".'}
        />
        <GuideTile
          stepNumber={<LooksFiveIcon fontSize="large" />}
          width={414}
          height={446} 
          graphic={'/static/CardanoBallot-category-9.png'}
          stepTitle={'To finalize your vote, select "Sign with wallet".'}
        />
        <GuideTile
          stepNumber={<LooksSixIcon fontSize="large" />}
          width={414}
          height={446} 
          graphic={'/static/CardanoBallot-category-10.png'}
          stepTitle={'Click on "Submit your vote" to complete the process.'}
        />
      </Grid>
    </div>
  );
};

export { UserGuide };

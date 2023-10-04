import { Grid, Typography, useMediaQuery, useTheme } from '@mui/material';
import React, { useEffect, useState } from 'react';
import { GuideTile } from './components/GuideTile';
import styles from './UserGuide.module.scss';
import { i18n } from 'i18n';
import SvgIcon from '@mui/material/SvgIcon';
import { ReactComponent as StepOneIcon } from '../../common/resources/images/step1.svg';
import { ReactComponent as StepTwoIcon } from '../../common/resources/images/step2.svg';
import { ReactComponent as StepThreeIcon } from '../../common/resources/images/step3.svg';
import { ReactComponent as StepFourIcon } from '../../common/resources/images/step4.svg';
import { ReactComponent as StepFiveIcon } from '../../common/resources/images/step5.svg';
import { ReactComponent as StepSixIcon } from '../../common/resources/images/step6.svg';
import { eventBus } from '../../utils/EventBus';
import Modal from '../../components/common/Modal/Modal';
import SupportedWalletsList from '../../components/SupportedWalletsList/SupportedWalletsList';

const UserGuide = () => {
  const [openSupportedWalletsModal, setOpenSupportedWalletsModal] = useState<boolean>(false);
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));

  useEffect(() => {
    const openConnectWalletModal = () => {
      setOpenSupportedWalletsModal(true);
    };
    eventBus.subscribe('openSupportedWalletModal', openConnectWalletModal);

    return () => {
      eventBus.unsubscribe('openSupportedWalletModal', openConnectWalletModal);
    };
  }, []);

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

      <Grid container>
        <Grid
          item
          xs={12}
          sm={6}
        >
          <GuideTile
            stepNumber={
              <SvgIcon
                component={StepOneIcon}
                inheritViewBox
                fontSize="large"
              />
            }
            height={479}
            media="image"
            graphic={'/static/sms_verification.png'}
            stepTitle={i18n.t('userGuide.requirements.sms.title')}
            stepHint={i18n.t('userGuide.requirements.sms.hint')}
          />
        </Grid>
        <Grid
          item
          xs={12}
          sm={6}
        >
          <GuideTile
            stepNumber={
              <SvgIcon
                component={StepTwoIcon}
                inheritViewBox
                fontSize="large"
              />
            }
            height={479}
            media="image"
            graphic={'/static/list_of_wallets.png'}
            stepTitle={i18n.t('userGuide.requirements.wallets.title')}
            stepHint={i18n.t('userGuide.requirements.wallets.hint')}
            link={i18n.t('userGuide.requirements.wallets.link')}
          />
        </Grid>
      </Grid>
      <Typography
        className={styles.sectionTitle}
        variant="h3"
        gutterBottom
      >
        {i18n.t('userGuide.createVerify.title')}
      </Typography>

      <Grid container>
        <Grid
          item
          xs={12}
          sm={6}
        >
          <GuideTile
            stepNumber={
              <SvgIcon
                component={StepOneIcon}
                inheritViewBox
                fontSize="large"
              />
            }
            height={528}
            media="video"
            graphic={'/static/connect_wallet.mov'}
            stepTitle={i18n.t('userGuide.createVerify.steps.1.title')}
            stepHint={i18n.t('userGuide.createVerify.steps.1.hint')}
          />
        </Grid>
        <Grid
          item
          xs={12}
          sm={6}
        >
          <GuideTile
            stepNumber={
              <SvgIcon
                component={StepTwoIcon}
                inheritViewBox
                fontSize="large"
              />
            }
            height={528}
            media="video"
            graphic={'/static/verify_wallet.mov'}
            stepTitle={i18n.t('userGuide.createVerify.steps.2.title')}
            stepHint={i18n.t('userGuide.createVerify.steps.2.hint')}
          />
        </Grid>
      </Grid>

      <Typography
        className={styles.sectionTitle}
        variant="h3"
        gutterBottom
      >
        {i18n.t('userGuide.submitVote.title')}
      </Typography>

      <Grid container>
        <Grid
          item
          xs={12}
          sm={6}
          md={4}
        >
          <GuideTile
            stepNumber={
              <SvgIcon
                component={StepOneIcon}
                inheritViewBox
                fontSize="large"
              />
            }
            height={446}
            media="image"
            graphic={'/static/categories.png'}
            stepTitle={i18n.t('userGuide.submitVote.steps.1')}
          />
        </Grid>
        <Grid
          item
          xs={12}
          sm={6}
          md={4}
        >
          <GuideTile
            stepNumber={
              <SvgIcon
                component={StepTwoIcon}
                inheritViewBox
                fontSize="large"
              />
            }
            height={446}
            media="image"
            graphic={'/static/categories_card.png'}
            stepTitle={i18n.t('userGuide.submitVote.steps.2')}
          />
        </Grid>
        <Grid
          item
          xs={12}
          sm={6}
          md={4}
        >
          <GuideTile
            stepNumber={
              <SvgIcon
                component={StepThreeIcon}
                inheritViewBox
                fontSize="large"
              />
            }
            height={446}
            media="image"
            graphic={'/static/view_nominees.png'}
            stepTitle={i18n.t('userGuide.submitVote.steps.3')}
          />
        </Grid>
        <Grid
          item
          xs={12}
          sm={6}
          md={4}
        >
          <GuideTile
            stepNumber={
              <SvgIcon
                component={StepFourIcon}
                inheritViewBox
                fontSize="large"
              />
            }
            height={446}
            media="image"
            graphic={'/static/vote_for_nominee.png'}
            stepTitle={i18n.t('userGuide.submitVote.steps.4')}
          />
        </Grid>
        <Grid
          item
          xs={12}
          sm={6}
          md={4}
        >
          <GuideTile
            stepNumber={
              <SvgIcon
                component={StepFiveIcon}
                inheritViewBox
                fontSize="large"
              />
            }
            height={446}
            media="image"
            graphic={'/static/sign_with_wallet.png'}
            stepTitle={i18n.t('userGuide.submitVote.steps.5')}
          />
        </Grid>
        <Grid
          item
          xs={12}
          sm={6}
          md={4}
        >
          <GuideTile
            stepNumber={
              <SvgIcon
                component={StepSixIcon}
                inheritViewBox
                fontSize="large"
              />
            }
            height={446}
            media="image"
            graphic={'/static/submit.png'}
            stepTitle={i18n.t('userGuide.submitVote.steps.6')}
          />
        </Grid>
      </Grid>

      <Modal
        id="supported-wallet-modal"
        isOpen={openSupportedWalletsModal}
        name="supported-wallet-modal"
        title="Supported Wallets"
        onClose={() => setOpenSupportedWalletsModal(false)}
        width={isMobile ? 'auto' : '400px'}
      >
        <SupportedWalletsList description="In order to vote, first you will need to connect your Wallet." />
      </Modal>
    </div>
  );
};

export { UserGuide };

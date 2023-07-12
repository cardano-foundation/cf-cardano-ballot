import React, { useEffect, useState } from 'react';
import { Grid, Typography } from '@mui/material';
import { useTheme, styled } from '@mui/material/styles';
import { useNavigate, useLocation } from 'react-router-dom';
import AccountBalanceWalletIcon from '@mui/icons-material/AccountBalanceWallet';
import { useCardano, ConnectWalletButton, getWalletIcon } from '@cardano-foundation/cardano-connect-with-wallet';
import CountDownTimer from '../../CountDownTimer/CountDownTimer';

const LogoImg = styled('img')(() => ({
  width: '230px',
  left: '137px',
  top: '20px',
}));

const HeaderStyle = styled('header')(({ theme }) => ({
  top: 0,
  left: 0,
  zIndex: 9,
  width: '100vw',
  height: 'auto',
  display: 'flex',
  position: 'static',
  alignItems: 'center',
  padding: theme.spacing(2),
  justifyContent: 'space-between',
}));

export default function Header() {
  const theme = useTheme();
  const navigate = useNavigate();
  const location = useLocation();
  const { stakeAddress, enabledWallet, disconnect } = useCardano();
  const [walletIcon, setWalletIcon] = useState('');

  useEffect(() => {
    const init = async () => {
      if (enabledWallet && enabledWallet.length) {
        setWalletIcon(getWalletIcon(enabledWallet));
      }
    };
    init();
  }, [enabledWallet, stakeAddress]);

  const handleLogoClick = () => {
    navigate('/');
  };

  const onConnectWallet = (walletName: any) => {
    console.log(walletName);
  };

  const connectWalletButton = (
    <>
      <ConnectWalletButton
        primaryColor={theme.palette.primary.main}
        onConnect={(walletName) => onConnectWallet(walletName)}
        onDisconnect={() => {
          disconnect();
          setWalletIcon('');
        }}
        alwaysVisibleWallets={['lace']}
        supportedWallets={['flint', 'eternl', 'nami', 'typhon', 'yoroi', 'nufi', 'gerowallet', 'lace']}
        beforeComponent={
          walletIcon.length ? (
            <img
              height={22}
              width={22}
              style={{ marginRight: '8px' }}
              src={walletIcon}
              alt=""
            />
          ) : (
            <AccountBalanceWalletIcon
              style={{ marginRight: '8px' }}
              height={22}
              width={22}
            />
          )
        }
        customCSS={`
            width: 170px;
            button {
                padding: 6px;
                font-weight: 700;
                line-height: 1.7142857142857142;
                font-size: 0.875rem;
                font-family: Helvetica Light,sans-serif;
            }
            span {
                padding: 16px;
                font-family: Helvetica Light,sans-serif;
                font-size: 0.875rem;
            }
        `}
      />
    </>
  );

  return (
    <HeaderStyle>
      <Grid
        container
        direction={{ xs: 'column', sm: 'row' }}
        justifyContent={{ sm: 'center', md: 'space-between' }}
        alignItems="center"
      >
        <Grid
          item
          xs={12}
          sm={'auto'}
        >
          <LogoImg
            src="/static/Cardano_Ballot_black.png"
            onClick={handleLogoClick}
            sx={{ cursor: 'pointer', ml: { xs: 0, sm: 1 } }}
          />
        </Grid>
        <Grid
          item
          xs={12}
          sm={'auto'}
        >
          {location.pathname === '/vote' ? (
            <Typography
              variant="body2"
              color="text.secondary"
              align="center"
              component={'div'}
            >
              {connectWalletButton}
            </Typography>
          ) : (
            <Typography
              variant="body2"
              color="text.secondary"
              align="center"
              component={'span'}
            >
              Time left to vote: <CountDownTimer />
            </Typography>
          )}
        </Grid>
      </Grid>
    </HeaderStyle>
  );
}

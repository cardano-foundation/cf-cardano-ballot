import React, { useMemo, useRef, useState } from 'react';

import { Checkbox, FormControlLabel, Grid, List, ListItem, ListItemAvatar, Typography } from '@mui/material';
import CallIcon from '@mui/icons-material/Call';
import { MuiTelInput, matchIsValidTel, MuiTelInputCountry } from 'mui-tel-input';
import './VerifyWallet.scss';
import discordLogo from '../../common/resources/images/discord-icon.svg';
import { confirmPhoneNumberCode, startVerification, verifyDiscord } from 'common/api/verificationService';
import { env } from 'common/constants/env';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { useDispatch, useSelector } from 'react-redux';
import { setUserStartsVerification, setWalletIsVerified } from '../../store/userSlice';
import { PhoneNumberCodeConfirmation, VerificationStarts } from '../../store/types';
import { RootState } from '../../store';
import { NetworkType } from '@cardano-foundation/cardano-connect-with-wallet-core';
import { useLocation } from 'react-router-dom';
import { CustomButton } from '../common/Button/CustomButton';
import { capitalizeFirstLetter, getSignedMessagePromise } from '../../utils/utils';
import { eventBus } from '../../utils/EventBus';
import { SignedWeb3Request } from '../../types/voting-app-types';

// TODO: env.
const excludedCountries: MuiTelInputCountry[] | undefined = [];

type VerifyWalletProps = {
  method?: string;
  onVerify: () => void;
  onError: (error?: string) => void;
};
const VerifyWallet = (props: VerifyWalletProps) => {
  const { onVerify, onError, method } = props;

  const [verifyOption, setVerifyOption] = useState<string | undefined>(method || undefined);
  const [defaultCountryCode] = useState<MuiTelInputCountry | undefined>('ES');
  const [phone, setPhone] = useState<string>('');
  const [codes, setCodes] = useState(Array(6).fill(''));
  const [phoneCodeIsBeenSending, setPhoneCodeIsBeenSending] = useState<boolean>(false);
  const [phoneCodeIsBeenConfirming, setPhoneCodeIsBeenConfirming] = useState<boolean>(false);
  const [phoneCodeIsSent, setPhoneCodeIsSent] = useState<boolean>(false);
  const [checkImNotARobot, setCheckImNotARobot] = useState<boolean>(false);
  const [isPhoneInputDisabled] = useState<boolean>(false);
  const dispatch = useDispatch();
  const { stakeAddress, signMessage } = useCardano({ limitNetwork: 'testnet' as NetworkType });
  const userVerification = useSelector((state: RootState) => state.user.userVerification);
  const userStartsVerificationByStakeAddress =
    Object.keys(userVerification).length !== 0 && userVerification[stakeAddress];
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

  const location = useLocation();

  const queryParams = new URLSearchParams(location.search);
  const action = queryParams.get('action');
  const secret = queryParams.get('secret');

  inputRefs.current = [];

  const signMessagePromisified = useMemo(() => getSignedMessagePromise(signMessage), [signMessage]);

  const reset = (timout?: boolean) => {
    function clear() {
      setVerifyOption(undefined);
      setPhoneCodeIsSent(false);
      setPhone('');
      setCodes(Array(6).fill(''));
    }
    if (timout) {
      setTimeout(() => {
        clear();
      }, 2000);
    } else {
      clear();
    }
  };

  const handleSelectOption = (option: string) => {
    setVerifyOption(option);
  };

  const handleChangePhone = (phoneNumber: string) => {
    setPhone(phoneNumber);
  };

  const handleSendCode = async () => {
    if (matchIsValidTel(phone) && checkImNotARobot) {
      setPhoneCodeIsBeenSending(true);
      startVerification(env.EVENT_ID, stakeAddress, phone.trim().replace(' ', ''))
        .then((response: VerificationStarts) => {
          dispatch(setUserStartsVerification({ stakeAddress, verificationStarts: response }));
          setPhoneCodeIsSent(true);
          setCheckImNotARobot(false);
          setPhoneCodeIsBeenSending(false);
        })
        .catch((error) => {
          onError(error.message);
          setPhoneCodeIsBeenSending(false);
        });
    }
  };

  const handleVerifyPhoneCode = () => {
    setPhoneCodeIsBeenConfirming(true);
    confirmPhoneNumberCode(
      env.EVENT_ID,
      stakeAddress,
      phone.trim().replace(' ', ''),
      userStartsVerificationByStakeAddress.requestId,
      codes.join('')
    ).then((response: PhoneNumberCodeConfirmation) => {
      dispatch(setWalletIsVerified({ isVerified: response.verified }));
      if (response.verified) {
        onVerify();
        reset();
        setPhoneCodeIsBeenConfirming(false);
      } else {
        onError('SMS verification failed');
        setPhoneCodeIsBeenConfirming(false);
      }
    });
  };

  const handleVerifyDiscord = async () => {
    if (action === 'verification' && secret) {
      signMessagePromisified(secret)
        .then((signedMessaged: SignedWeb3Request) => {
          verifyDiscord(env.EVENT_ID, stakeAddress, secret, signedMessaged)
            .then((response: { verified: boolean }) => {
              dispatch(setWalletIsVerified({ isVerified: response.verified }));
              if (response.verified) {
                onVerify();
                reset();
              } else {
                onError('Discord verification failed');
              }
            })
            .catch((e) => eventBus.publish('showToast', capitalizeFirstLetter(e.message), true));
        })
        .catch((e) => eventBus.publish('showToast', capitalizeFirstLetter(e.message), true));
    }
  };

  const renderSelectOption = () => {
    return (
      <>
        <Typography
          className="connect-wallet-modal-description"
          gutterBottom
          style={{ wordWrap: 'break-word' }}
        >
          To verify your address please proceed with one of the options.
        </Typography>
        <List>
          <ListItem
            className="optionItem"
            onClick={() => handleSelectOption('discord')}
          >
            <ListItemAvatar>
              <img
                className="option-icon"
                src={discordLogo}
                style={{ width: '24px', height: '24px' }}
              />
            </ListItemAvatar>
            <Typography className="optionLabel">Verify with Discord</Typography>
          </ListItem>
          <ListItem
            className="optionItem"
            onClick={() => handleSelectOption('sms')}
          >
            <ListItemAvatar>
              <CallIcon
                className="option-icon"
                style={{ width: '24px', height: '24px' }}
              />
            </ListItemAvatar>
            <Typography className="optionLabel">Verify with SMS</Typography>
          </ListItem>
        </List>
      </>
    );
  };

  const renderConfirmCode = () => {
    const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>, index: number) => {
      const value = event.target.value;

      if (!(value && /^[0-9]$/.test(value)) && value !== '') return;

      const updatedCodes = [...codes];
      updatedCodes[index] = value;
      setCodes(updatedCodes);

      if (value && /^[0-9]$/.test(value) && index < 5) {
        inputRefs.current[index + 1]?.focus();
      } else if (!value && index > 0) {
        inputRefs.current[index]?.focus();
      }
    };

    const handleCancelConfirmChode = () => {
      setPhoneCodeIsSent(false);
      setCodes(Array(6).fill(''));
    };

    return (
      <>
        <Typography
          className="verify-wallet-modal-description"
          gutterBottom
          style={{ wordWrap: 'break-word' }}
        >
          Confirm the verification code that’s been sent to <span>{phone}</span>
        </Typography>
        <div style={{ display: 'flex', justifyContent: 'center', gap: '8px', marginTop: '28px' }}>
          {[...Array(6)].map((_, index) => (
            <input
              key={index}
              value={codes[index]}
              ref={(el) => (inputRefs.current[index] = el)}
              type="text"
              maxLength={1}
              onChange={(e) => handleInputChange(e, index)}
              onKeyDown={(e) => {
                const target = e.target as HTMLInputElement;
                if (e.key === 'Backspace' && target.value === '') {
                  if (index > 0) {
                    inputRefs.current[index - 1]?.focus();
                  }
                }
              }}
              className="confirm-phone-code-input"
            />
          ))}
        </div>
        <Typography
          style={{ marginTop: '28px' }}
          className="didnt-receive-label"
        >
          I didn’t receive a code
        </Typography>
        <Grid
          container
          spacing={2}
          style={{ marginTop: '28px' }}
        >
          <Grid
            item
            xs={6}
          >
            <CustomButton
              styles={{
                background: 'transparent !important',
                color: '#03021F',
                border: '1px solid #daeefb',
              }}
              label="Cancel"
              onClick={() => handleCancelConfirmChode()}
              fullWidth={true}
            />
          </Grid>
          <Grid
            item
            xs={6}
          >
            <CustomButton
              styles={
                codes.filter((code) => code !== '').length === 6 && !phoneCodeIsBeenConfirming
                  ? {
                      background: '#ACFCC5',
                      color: '#03021F',
                    }
                  : {
                      background: '#6C6F89',
                      color: '#F6F9FF !important',
                    }
              }
              disabled={codes.length < 6 || phoneCodeIsBeenConfirming}
              label="Verify"
              onClick={() => handleVerifyPhoneCode()}
              fullWidth={true}
            />
          </Grid>
        </Grid>
      </>
    );
  };

  const renderVerifyPhoneNumber = () => {
    return (
      <>
        <Typography
          className="connect-wallet-modal-description"
          gutterBottom
          style={{ wordWrap: 'break-word' }}
        >
          To verify your address please confirm your phone number.
        </Typography>
        <MuiTelInput
          className="phone-number-input"
          defaultCountry={defaultCountryCode}
          excludedCountries={excludedCountries}
          value={phone}
          onChange={handleChangePhone}
          disabled={isPhoneInputDisabled}
        />
        <FormControlLabel
          style={{ marginTop: '4px' }}
          control={
            <Checkbox
              value={checkImNotARobot}
              onChange={(event, checked) => setCheckImNotARobot(checked)}
              name="notRobot"
              color="primary"
              sx={{
                color: checkImNotARobot ? '#056122' : '',
                '&.Mui-checked': {
                  color: '#000',
                },
              }}
            />
          }
          label="I am not a robot"
        />
        <Grid
          container
          spacing={2}
          style={{ marginTop: '4px' }}
        >
          <Grid
            item
            xs={6}
          >
            <CustomButton
              styles={{
                background: 'transparent !important',
                color: '#03021F',
                border: '1px solid #daeefb',
                marginRight: '20px',
              }}
              label="Cancel"
              onClick={() => reset()}
              fullWidth={true}
            />
          </Grid>
          <Grid
            item
            xs={6}
          >
            <CustomButton
              styles={
                matchIsValidTel(phone) && checkImNotARobot && !phoneCodeIsBeenSending
                  ? {
                      background: '#ACFCC5',
                      color: '#03021F',
                      paddingLeft: '20px',
                    }
                  : {
                      background: '#6C6F89',
                      color: '#F6F9FF !important',
                    }
              }
              label="Send code"
              disabled={!matchIsValidTel(phone) || !checkImNotARobot || phoneCodeIsBeenSending}
              onClick={() => handleSendCode()}
              fullWidth={true}
            />
          </Grid>
        </Grid>
      </>
    );
  };

  const renderVerifyDiscord = () => {
    return (
      <>
        <Typography
          className="verify-wallet-modal-description"
          gutterBottom
          style={{ wordWrap: 'break-word' }}
        >
          To verify your address you need to sign a secret message. You will get the secret from our friendly Discord
          bot.
        </Typography>
        <Typography
          className="verify-wallet-modal-description"
          gutterBottom
          style={{ wordWrap: 'break-word', marginTop: '16px' }}
        >
          1. Join our Discord Server and accept our terms and conditions by reacting with a 🚀 to the message in the
          verification channel.
        </Typography>
        <Typography
          className="verify-wallet-modal-description"
          gutterBottom
          style={{ wordWrap: 'break-word', marginTop: '16px' }}
        >
          2. Open the chat with the bot.
        </Typography>
        <Typography
          className="verify-wallet-modal-description"
          gutterBottom
          style={{ wordWrap: 'break-word', marginTop: '16px' }}
        >
          3. Click on the button.
        </Typography>
        <Typography
          className="verify-wallet-modal-description"
          gutterBottom
          style={{ wordWrap: 'break-word', marginTop: '16px' }}
        >
          4. You will be redirected to voting app.
        </Typography>
        <CustomButton
          styles={{
            background: '#ACFCC5',
            color: '#03021F',
            margin: '24px 0px',
          }}
          label="Sign and verify"
          onClick={() => handleVerifyDiscord()}
          disabled={!secret}
          fullWidth={true}
        />
        <CustomButton
          styles={{
            background: 'transparent !important',
            color: '#03021F',
            border: '1px solid #daeefb',
            margin: '24px 0px',
          }}
          label="Cancel"
          onClick={() => reset()}
          fullWidth={true}
        />
      </>
    );
  };

  const renderVerify = () => {
    if (verifyOption !== undefined) {
      if (verifyOption === 'sms') {
        if (phoneCodeIsSent) {
          return renderConfirmCode();
        } else {
          return renderVerifyPhoneNumber();
        }
      } else {
        return renderVerifyDiscord();
      }
    } else {
      return renderSelectOption();
    }
  };

  return renderVerify();
};

export { VerifyWallet };

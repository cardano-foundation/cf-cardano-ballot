import React, { useRef, useState } from 'react';

import {
  Button,
  Checkbox,
  FormControlLabel,
  Grid,
  List,
  ListItem,
  ListItemAvatar,
  TextField,
  Typography,
} from '@mui/material';
import CallIcon from '@mui/icons-material/Call';
import { MuiTelInput, matchIsValidTel, MuiTelInputCountry } from 'mui-tel-input';
import './VerifyWallet.scss';
import discordLogo from '../../common/resources/images/discord-icon.svg';
import { confirmPhoneNumberCode, startVerification } from 'common/api/verificationService';
import { env } from 'common/constants/env';
import { useCardano } from '@cardano-foundation/cardano-connect-with-wallet';
import { useDispatch, useSelector } from 'react-redux';
import { setUserStartsVerification, setWalletIsVerified } from '../../store/userSlice';
import { PhoneNumberCodeConfirmation, VerificationStarts } from '../../store/types';
import { RootState } from '../../store';
import { NetworkType } from '@cardano-foundation/cardano-connect-with-wallet-core';

// TODO: env.
const excludedCountries: MuiTelInputCountry[] | undefined = [];

type VerifyWalletProps = {
  onVerify: () => void;
  onError: (error?: string) => void;
};
const VerifyWallet = (props: VerifyWalletProps) => {
  const { onVerify, onError } = props;

  const [verifyOption, setVerifyOption] = useState<string | undefined>(undefined);
  const [defaultCountryCode] = useState<MuiTelInputCountry | undefined>('ES');
  const [discordKey, setDiscordKey] = useState<string>('');
  const [phone, setPhone] = useState<string>('');
  const [codes, setCodes] = useState(Array(6).fill(''));
  const [phoneCodeIsBeenSending, setPhoneCodeIsBeenSending] = useState<boolean>(false);
  const [phoneCodeIsBeenConfirming, setPhoneCodeIsBeenConfirming] = useState<boolean>(false);
  const [phoneCodeIsSent, setPhoneCodeIsSent] = useState<boolean>(false);
  const [checkImNotARobot, setCheckImNotARobot] = useState<boolean>(false);
  const [isPhoneInputDisabled] = useState<boolean>(false);
  const { stakeAddress } = useCardano({ limitNetwork: 'testnet' as NetworkType });
  const dispatch = useDispatch();
  const userVerification = useSelector((state: RootState) => state.user.userVerification);
  const userStartsVerificationByStakeAddress =
    Object.keys(userVerification).length !== 0 && userVerification[stakeAddress];
  const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

  inputRefs.current = [];

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

  const handleVerifyDiscordKey = () => {
    if (discordKey !== '') {
      // TODO: verify key
      onVerify();
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
          style={{ marginTop: '28px' }}
        >
          <Grid
            item
            xs={6}
          >
            <Button
              onClick={() => setPhoneCodeIsSent(false)}
              className="verify-number-button-cancel"
              fullWidth
            >
              Back
            </Button>
          </Grid>
          <Grid
            item
            xs={6}
          >
            <Button
              onClick={() => handleVerifyPhoneCode()}
              disabled={codes.length < 6 || phoneCodeIsBeenConfirming}
              className={`verify-number-button-continue ${
                codes.filter((code) => code !== '').length === 6 && !phoneCodeIsBeenConfirming
                  ? 'verify-number-button-valid'
                  : ''
              }`}
              fullWidth
            >
              Verify
            </Button>
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
          style={{ marginTop: '4px' }}
        >
          <Grid
            item
            xs={6}
          >
            <Button
              onClick={() => reset()}
              className="verify-number-button-cancel"
              fullWidth
            >
              Cancel
            </Button>
          </Grid>
          <Grid
            item
            xs={6}
          >
            <Button
              onClick={() => handleSendCode()}
              disabled={!matchIsValidTel(phone) || !checkImNotARobot || phoneCodeIsBeenSending}
              className={`verify-number-button-continue ${
                matchIsValidTel(phone) && checkImNotARobot && !phoneCodeIsBeenSending
                  ? 'verify-number-button-valid'
                  : ''
              }`}
              fullWidth
            >
              Send code
            </Button>
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
          2. Copy your Stake Address.
        </Typography>
        <Typography
          className="verify-wallet-modal-description"
          gutterBottom
          style={{ wordWrap: 'break-word', marginTop: '16px' }}
        >
          3. Send your Stake Address as a private message to our WalletVerificationBot.
        </Typography>
        <Typography
          className="verify-wallet-modal-description"
          gutterBottom
          style={{ wordWrap: 'break-word', marginTop: '16px' }}
        >
          4. Get the secret key from the chat with our WalletVerificationBot.
        </Typography>
        <TextField
          value={discordKey}
          className="secret-key-input"
          label="Enter Secret Key"
          onChange={(e) => setDiscordKey(e.target.value)}
        />
        <Grid
          container
          style={{ marginTop: '35px' }}
        >
          <Grid
            item
            xs={6}
          >
            <Button
              onClick={() => reset()}
              className="verify-number-button-cancel"
              fullWidth
            >
              Cancel
            </Button>
          </Grid>
          <Grid
            item
            xs={6}
          >
            <Button
              onClick={() => handleVerifyDiscordKey()}
              disabled={!discordKey.length}
              className={`verify-number-button-continue ${discordKey.length ? 'verify-number-button-valid' : ''}`}
              fullWidth
            >
              Verify
            </Button>
          </Grid>
        </Grid>
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

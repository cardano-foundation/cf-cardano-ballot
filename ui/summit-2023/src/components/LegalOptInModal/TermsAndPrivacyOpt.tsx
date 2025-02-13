import * as React from 'react';
import Dialog from '@mui/material/Dialog';
import DialogActions from '@mui/material/DialogActions';
import DialogContent from '@mui/material/DialogContent';
import DialogContentText from '@mui/material/DialogContentText';
import DialogTitle from '@mui/material/DialogTitle';
import { Checkbox, FormControlLabel, FormGroup, Grid, Link, Typography, useMediaQuery, useTheme } from '@mui/material';
import { useEffect, useState } from 'react';
import { useLocalStorage } from '../../common/hooks/useLocalStorage';
import { CB_TERMS_AND_PRIVACY } from '../../common/constants/local';
import VisibilityIcon from '@mui/icons-material/Visibility';
import VisibilityOffIcon from '@mui/icons-material/VisibilityOff';
import { LegalDocPreview } from './LegalDocPreview';
import { ROUTES } from '../../routes';
import styles from './TermsAndPolicyOpt.module.scss';
import { CustomButton } from '../common/Button/CustomButton';

export const LINK_TERMS = 'Terms & Conditions';
export const LINK_PRIVACY = 'Privacy & Policy';

const TermsOptInModal = (props) => {
  const theme = useTheme();
  const isMobile = useMediaQuery(theme.breakpoints.down('sm'));
  const { open, setOpen } = props;
  const [_, setValue] = useLocalStorage(CB_TERMS_AND_PRIVACY, undefined);
  const [checked, setChecked] = useState(false);
  const [highlightCheckbox, setHighlightCheckbox] = useState(false);
  const [showConditionsPreview, setShowConditionsPreview] = useState(true);

  const descriptionElementRef = React.useRef(null);

  useEffect(() => {
    const path = window.location.href;

    if (
      path.includes(ROUTES.TERMS_AND_CONDITIONS) ||
      path.includes(ROUTES.PRIVACY_POLICY) ||
      path.includes(ROUTES.PAGE_NOT_FOUND)
    )
      setOpen(false);

    if (open) {
      const { current: descriptionElement } = descriptionElementRef;
      if (descriptionElement !== null) {
        descriptionElement.focus();
      }
    }
  }, [open]);

  const onClose = () => {
    setOpen(false);
  };
  const handleCheckbox = (check) => {
    setChecked(check);
    if (checked) {
      setHighlightCheckbox(false);
    }
  };
  const handleAgreeClose = () => {
    if (checked) {
      setValue(true);
      onClose();
    } else {
      setHighlightCheckbox(true);
    }
  };

  return (
    <Dialog
      open={open}
      keepMounted
      scroll={'paper'}
      maxWidth={isMobile ? 'sm' : 'md'}
      sx={{ backdropFilter: 'blur(10px)' }}
      PaperProps={{ sx: { borderRadius: '16px' } }}
      aria-labelledby="cardano-ballot-terms-modal"
      aria-describedby="cardano-ballot-terms-modal-description"
    >
      <DialogTitle
        id="cardano-ballot-terms-modal"
        className={styles.modalTitle}
      >
        <Grid
          container
          spacing={0}
          direction="row"
          justifyContent="space-between"
        >
          <img
            src="/static/cardano-ballot.png"
            alt="Cardano Logo"
            style={{ height: isMobile ? '29px' : '40px' }}
          />
          <Typography
            variant="h6"
            className={styles.modalTitle}
          >
            T&Cs and Privacy Policy
          </Typography>
        </Grid>
      </DialogTitle>
      <DialogContent dividers={true}>
        <DialogContentText
          component={'div'}
          id="scroll-dialog-description"
          ref={descriptionElementRef}
          className={styles.modalDescription}
        >
          <div style={{ marginBottom: 8, marginLeft: 8 }}>
            <Grid
              container
              rowSpacing={1}
              columnSpacing={{ xs: 1, sm: 2, md: 3 }}
            >
              <Grid
                item
                xs={12}
              >
                <div
                  onClick={() => setShowConditionsPreview(!showConditionsPreview)}
                  style={{
                    display: 'flex',
                    alignItems: 'center',
                    flexWrap: 'wrap',
                    cursor: 'pointer',
                  }}
                >
                  {showConditionsPreview ? <VisibilityOffIcon /> : <VisibilityIcon />}
                  <span
                    style={{
                      marginLeft: 4,
                    }}
                  >
                    {showConditionsPreview ? 'Hide' : 'Show'} full description
                  </span>
                </div>
              </Grid>
            </Grid>
          </div>
          {!showConditionsPreview ? null : <LegalDocPreview />}
          {showConditionsPreview ? null : (
            <Grid
              container
              sx={{ justifyContent: 'space-around' }}
            >
              <Grid
                item
                xs={12}
                sx={{ flex: 1 }}
              >
                <FormGroup>
                  <FormControlLabel
                    control={
                      <Checkbox
                        checked={checked}
                        color="primary"
                        onChange={(e) => handleCheckbox(e.target.checked)}
                        sx={{
                          color: highlightCheckbox ? '#056122' : '',
                          marginBottom: 0.5,
                          '&.Mui-checked': {
                            color: '#000',
                          },
                        }}
                      />
                    }
                    label={
                      <>
                        I have read and agree to the Cardano Ballot
                        <Link
                          href={ROUTES.TERMS_AND_CONDITIONS}
                          target="_blank"
                          sx={{ marginX: 1 }}
                        >
                          {LINK_TERMS}
                        </Link>
                        and
                        <Link
                          href={ROUTES.PRIVACY_POLICY}
                          target="_blank"
                          sx={{ marginX: 1 }}
                        >
                          {LINK_PRIVACY}
                        </Link>
                      </>
                    }
                  />
                </FormGroup>
              </Grid>
            </Grid>
          )}
        </DialogContentText>
      </DialogContent>
      <DialogActions style={{ display: 'flex', justifyContent: 'space-between' }}>
        <Grid container>
          <Grid
            item
            xs={isMobile ? 12 : 7}
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'flex-start',
              marginLeft: isMobile ? '0px' : '12px',
            }}
          >
            <FormGroup>
              <FormControlLabel
                control={
                  <Checkbox
                    checked={checked}
                    onChange={(e) => handleCheckbox(e.target.checked)}
                    sx={{ color: highlightCheckbox ? 'red' : '', marginBottom: 0.5 }}
                  />
                }
                label={
                  <>
                    I have read and agree to the Cardano Ballot
                    <Link
                      href={ROUTES.TERMS_AND_CONDITIONS}
                      target="_blank"
                      sx={{ marginX: 1 }}
                    >
                      {LINK_TERMS}
                    </Link>
                    and
                    <Link
                      href={ROUTES.PRIVACY_POLICY}
                      target="_blank"
                      sx={{ marginX: 1 }}
                    >
                      {LINK_PRIVACY}
                    </Link>
                  </>
                }
                className={styles.modalDescription}
              />
            </FormGroup>
          </Grid>
          <Grid
            item
            xs={isMobile ? 12 : 4}
            style={{
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'flex-end',
              padding: isMobile ? '6px' : '0px',
            }}
          >
            <CustomButton
              styles={
                checked
                  ? {
                      background: '#ACFCC5',
                      color: '#03021F',
                    }
                  : {
                      background: '#6C6F89',
                      color: '#F6F9FF !important',
                    }
              }
              label="Accept"
              onClick={handleAgreeClose}
              fullWidth={isMobile}
              disabled={!checked}
            />
          </Grid>
        </Grid>
      </DialogActions>
    </Dialog>
  );
};

export { TermsOptInModal };

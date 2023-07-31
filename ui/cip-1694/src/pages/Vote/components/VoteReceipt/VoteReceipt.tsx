import React, { useCallback, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import toast from 'react-hot-toast';
import pick from 'lodash/pick';
import { CopyToClipboard } from 'react-copy-to-clipboard';
import cn from 'classnames';
import Grid from '@mui/material/Grid';
import { Button, IconButton, Typography, debounce } from '@mui/material';
import QrCodeIcon from '@mui/icons-material/QrCode';
import CloseIcon from '@mui/icons-material/Close';
import CheckCircleOutlineOutlinedIcon from '@mui/icons-material/CheckCircleOutlineOutlined';
import { setIsVerifyVoteModalVisible } from 'common/store/userSlice';
import { RootState } from 'common/store';
import styles from './VoteReceipt.module.scss';
import { ReceiptItem } from './ReceipItem';
import { FieldsToDisplayArrayKeys, fieldsToDisplay } from './utils';

type VoteReceiptProps = {
  setOpen: () => void;
};

const Toast = ({ message }: { message: string }) => (
  <div className={styles.toast}>
    <CheckCircleOutlineOutlinedIcon className={styles.toastIcon} />
    <span>{message}</span>
    <span className={styles.divider} />
    <IconButton
      aria-label="close"
      onClick={() => toast.dismiss()}
    >
      <CloseIcon className={styles.toastClose} />
    </IconButton>
  </div>
);

export const VoteReceipt = ({ setOpen }: VoteReceiptProps) => {
  const receipt = useSelector((state: RootState) => state.user.receipt);
  const [isVerified] = useState(false);
  const dispatch = useDispatch();

  // eslint-disable-next-line react-hooks/exhaustive-deps
  const debouncedToast = useCallback(debounce(toast, 300), []);

  const onItemClick = useCallback(() => {
    debouncedToast(<Toast message="Copied to clipboard" />);
    dispatch(setIsVerifyVoteModalVisible({ isVisible: true }));
  }, [debouncedToast, dispatch]);

  const recordFieldsToDisplay = pick(receipt, fieldsToDisplay);

  return (
    <Grid
      container
      direction="column"
      justifyContent="center"
      alignItems="center"
      spacing={0}
      sx={{ padding: '30px', paddingTop: '50px', width: '550px' }}
    >
      <IconButton
        aria-label="close"
        onClick={setOpen}
        className={styles.closeBtn}
      >
        <CloseIcon className={styles.closeIcon} />
      </IconButton>
      <Grid
        item
        spacing={0}
        container
        justifyContent="center"
      >
        <Typography
          className={styles.title}
          variant="h4"
          sx={{ marginBottom: '28px' }}
        >
          Vote Receipt
        </Typography>
      </Grid>
      <Grid
        item
        width="100%"
        wrap="nowrap"
        direction="row"
        container
        gap="12px"
      >
        {isVerified ? (
          <>
            <Button
              disabled
              className={cn(styles.button, styles.success)}
              size="large"
              variant="contained"
            >
              Verified
            </Button>
            <Button
              className={styles.qrButton}
              size="large"
              variant="outlined"
              onClick={() => console.log('show verified modal...')}
            >
              <QrCodeIcon className={styles.qrIcon} />
            </Button>
          </>
        ) : (
          <CopyToClipboard
            text={recordFieldsToDisplay['coseSignature']}
            onCopy={onItemClick}
          >
            <Button
              className={styles.button}
              size="large"
              variant="contained"
            >
              Copy signature and verify
            </Button>
          </CopyToClipboard>
        )}
      </Grid>
      <Grid
        item
        container
        width="100%"
        direction="column"
        justifyContent="center"
        alignItems="start"
        spacing={0}
      >
        {Object.entries(recordFieldsToDisplay).map(([key, value]: [FieldsToDisplayArrayKeys, string]) => (
          <ReceiptItem
            key={key}
            {...{ name: key, value, onItemClick }}
          />
        ))}
      </Grid>
    </Grid>
  );
};

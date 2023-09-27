import React from 'react';
import Dialog from '@mui/material/Dialog';
import { QRCode } from 'common/components/QRCode/QRCode';

type SuccessProps = {
  opened: boolean;
};

export const Success = ({ opened }: SuccessProps) => {
  return (
    <Dialog
      disableEscapeKeyDown
      aria-labelledby="dialog-title"
      aria-describedby="dialog-description"
      open={opened}
      maxWidth="xl" // To set width more then 600px
      // TODO: use classes instead
      sx={{ '& .MuiBackdrop-root': { bgcolor: '#F5F9FF' } }}
      PaperProps={{
        sx: {
          width: '450px',
          height: '607px',
          borderRadius: '16px',
          bgcolor: '#F5F9FF',
          boxShadow: '2px 5px 50px 0px rgba(57, 72, 108, 0.20)',
        },
      }}
    >
      Success
      <QRCode data="https://cardanoscan.io/transaction/8c0fbfbde8c59a55793c91a67a0fa78fb7fd66a334aa56f31094b7f8821bcc2a" />
    </Dialog>
  );
};

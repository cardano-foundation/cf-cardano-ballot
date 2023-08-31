import React from 'react';
import Dialog from '@mui/material/Dialog';
import DialogContent from '@mui/material/DialogContent';
import DialogTitle from '@mui/material/DialogTitle';
import { IconButton, Typography } from '@mui/material';
import CloseIcon from '@mui/icons-material/Close';
import Paper from '@mui/material/Paper';
import './Modal.scss';

const StyledPaper = (props: any) => {
  return (
    <Paper
      {...props}
      style={{ borderRadius: '16px' }}
    />
  );
};

type ModalProps = {
  name: string;
  id: string;
  isOpen: boolean;
  title: string;
  width?: string;
  disableBackdropClick?: boolean;
  onClose: () => void;
  children: React.ReactNode;
};

const Modal = (props: ModalProps) => {
  const { name, id, isOpen, title, width, disableBackdropClick, onClose } = props;

  return (
    <Dialog
      open={isOpen}
      onClose={(event, reason) => {
        if (disableBackdropClick && reason === 'backdropClick') return;
        onClose();
      }}
      aria-labelledby={name}
      maxWidth="sm"
      PaperComponent={StyledPaper}
    >
      <DialogTitle
        id={id}
        style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}
      >
        <Typography className="modal-title">{title}</Typography>
        <IconButton
          className="closeButton"
          edge="end"
          color="inherit"
          onClick={onClose}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent style={{ width: width || '400px' }}>{props.children}</DialogContent>
    </Dialog>
  );
};

export default Modal;

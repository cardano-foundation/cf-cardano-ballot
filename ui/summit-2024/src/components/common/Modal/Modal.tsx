import React from "react";
import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import { IconButton, Typography } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import ArrowBackIosNewOutlinedIcon from "@mui/icons-material/ArrowBackIosNewOutlined";
import Paper from "@mui/material/Paper";

const StyledPaper = (props: any) => {
  return <Paper {...props} style={{ borderRadius: "16px" }} />;
};

type ModalProps = {
  name?: string;
  id: string;
  isOpen: boolean;
  backButton?: boolean;
  title: string;
  leftTitle?: boolean;
  width?: string;
  disableBackdropClick?: boolean;
  onClose?: () => void;
  onBack?: () => void | undefined;
  children: React.ReactNode;
};

const Modal = (props: ModalProps) => {
  const {
    name,
    id,
    isOpen,
    title,
    width,
    disableBackdropClick,
    onClose,
    onBack,
  } = props;

  return (
    <Dialog
      open={isOpen}
      onClose={(event, reason) => {
        if (disableBackdropClick && reason === "backdropClick") return;
        if (onClose) {
          onClose();
        }
      }}
      aria-labelledby={name}
      maxWidth="sm"
      PaperComponent={StyledPaper}
    >
      <DialogTitle
        id={id}
        sx={{
          display: "flex",
          justifyContent: "center",
          alignItems: "center",
          backgroundColor: "background.default",
          position: "relative",
        }}
      >
        {props.backButton ? (
          <IconButton
            sx={{
              position: "absolute",
              left: 12,
              display: "inline-flex",
              padding: "12px",
              alignItems: "flex-start",
              borderRadius: "12px",
              backgroundColor: "background.neutralDark",
              cursor: "pointer",
              "&:hover": {
                backgroundColor: "text.neutralLightest",
                color: "background.neutralDark",
              },
            }}
            onClick={onBack}
          >
            <ArrowBackIosNewOutlinedIcon />
          </IconButton>
        ) : null}
        <Typography
          variant="h6"
          component="div"
          sx={{
            flex: 1,
            textAlign: props.leftTitle ? "left" :"center",
            fontFamily: "Dosis",
            weight: 700,
            fontSize: "28px",
            lineHeight: "32px",
          }}
        >
          {title}
        </Typography>

        <IconButton
          sx={{
            position: "absolute",
            right: 20,
            display: "inline-flex",
            padding: "12px",
            alignItems: "flex-start",
            borderRadius: "12px",
            backgroundColor: "background.neutralDark",
            cursor: "pointer",
            "&:hover": {
              backgroundColor: "text.neutralLightest",
              color: "background.neutralDark",
            },
          }}
          edge="end"
          color="inherit"
          onClick={onClose}
        >
          <CloseIcon />
        </IconButton>
      </DialogTitle>
      <DialogContent
        sx={{ width: width || "400px", backgroundColor: "background.default" }}
      >
        {props.children}
      </DialogContent>
    </Dialog>
  );
};

export default Modal;

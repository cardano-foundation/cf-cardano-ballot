import React from "react";
import Dialog from "@mui/material/Dialog";
import DialogContent from "@mui/material/DialogContent";
import DialogTitle from "@mui/material/DialogTitle";
import { IconButton, SxProps, Theme, Typography } from "@mui/material";
import CloseIcon from "@mui/icons-material/Close";
import ArrowBackIosNewOutlinedIcon from "@mui/icons-material/ArrowBackIosNewOutlined";
import theme from "../../../common/styles/theme";

type ModalProps = {
  name?: string;
  id: string;
  isOpen: boolean;
  backButton?: boolean;
  title: string;
  leftTitle?: boolean;
  closeIcon?: boolean;
  position?: "normal" | "bottom";
  width?: string;
  backdrop?: boolean;
  disableBackdropClick?: boolean;
  onClose?: () => void;
  onBack?: () => void | undefined;
  children: React.ReactNode;
  sx?: SxProps<Theme>;
};

const Modal = (props: ModalProps) => {
  const {
    name,
    id,
    isOpen,
    title,
    width,
    position,
    closeIcon,
    disableBackdropClick,
    onClose,
    onBack,
    sx,
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
      back
      aria-labelledby={name}
      maxWidth="sm"
      sx={{
        "& .MuiPaper-root": {
          borderRadius: "16px",
          position: position === "bottom" ? "absolute" : "",
          bottom: position === "bottom" ? "24px" : "",
        },
        ...(sx && sx),
      }}
    >
      <DialogTitle
        id={id}
        sx={{
          display: "flex",
          justifyContent: props.leftTitle ? "left" : "center",
          alignItems: "center",
          backgroundColor: theme.palette.background.default,
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
              backgroundColor: theme.palette.background.neutralDark,
              cursor: "pointer",
              "&:hover": {
                backgroundColor: theme.palette.text.neutralLightest,
                color: theme.palette.background.neutralDark,
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
            maxWidth: "320px",
            textAlign: props.leftTitle ? "left" : "center",
            fontFamily: "Dosis",
            weight: 700,
            fontSize: "28px",
            lineHeight: "32px",
          }}
        >
          {title}
        </Typography>

        {closeIcon === false ? null : (
          <>
            <IconButton
              sx={{
                position: "absolute",
                right: 20,
                display: "inline-flex",
                padding: "12px",
                alignItems: "flex-start",
                borderRadius: "12px",
                backgroundColor: theme.palette.background.neutralDark,
                cursor: "pointer",
                "&:hover": {
                  backgroundColor: theme.palette.text.neutralLightest,
                  color: theme.palette.background.neutralDark,
                },
              }}
              edge="end"
              color="inherit"
              onClick={onClose}
            >
              <CloseIcon />
            </IconButton>
          </>
        )}
      </DialogTitle>
      <DialogContent
        sx={{
          width: width || "400px",
          backgroundColor: theme.palette.background.default,
        }}
      >
        {props.children}
      </DialogContent>
    </Dialog>
  );
};

export default Modal;

import { forwardRef } from "react";
import { Button, Link, Typography } from "@mui/material";

import { ModalContents, ModalHeader, ModalWrapper } from "@atoms";
import { ICONS, IMAGES } from "@consts";
import { useModal } from "@context";
import { openInNewTab } from "@utils";
import { useScreenDimension } from "@hooks";

export interface StatusModalState {
  buttonText?: string;
  cancelText?: string;
  feedbackText?: string;
  status: "warning" | "info" | "success";
  isInfo?: boolean;
  link?: string;
  linkText?: string;
  message: React.ReactNode;
  onSubmit?: () => void;
  onCancel?: () => void;
  onFeedback?: () => void;
  title: string;
  dataTestId: string;
}

export const StatusModal = forwardRef<HTMLDivElement>((_, ref) => {
  const { state, closeModal } = useModal<StatusModalState>();
  const { isMobile } = useScreenDimension();

  const onClickFeedback = () => {
    closeModal();
  };

  return (
    <ModalWrapper
      dataTestId={state ? state.dataTestId : "status-modal"}
      ref={ref}
    >
      <img
        alt="Status icon"
        src={
          state?.status === "warning"
            ? IMAGES.warningImage
            : state?.status === "success"
            ? IMAGES.successImage
            : ICONS.timerIcon
        }
        style={{ height: "84px", margin: "0 auto", width: "84px" }}
      />
      <ModalHeader sx={{ marginTop: "34px", px: isMobile ? 0 : 3 }}>
        {state?.title}
      </ModalHeader>
      <ModalContents>
        <Typography
          textAlign="center"
          sx={{
            fontSize: "16px",
            fontWeight: "400",
            overflow: "hidden",
            display: "-webkit-box",
            WebkitBoxOrient: "vertical",
            WebkitLineClamp: 8,
            wordBreak: "break-word",
            whiteSpace: "pre-line",
          }}
        >
          {state?.message}{" "}
          {state?.link && (
            <Link
              onClick={() => openInNewTab(state?.link || "")}
              target="_blank"
              sx={[{ "&:hover": { cursor: "pointer" } }]}
            >
              {state?.linkText || 'this link'}
            </Link>
          )}
        </Typography>
      </ModalContents>
      <Button
        data-testid="confirm-modal-button"
        onClick={state?.onSubmit ? state?.onSubmit : closeModal}
        sx={{
          borderRadius: 50,
          margin: "0 auto",
          padding: "10px 26px",
          textTransform: "none",
          marginTop: "38px",
          width: "100%",
        }}
        variant="contained"
      >
        {state?.buttonText || 'Confirm'}
      </Button>
      {state?.cancelText && (
        <Button
          data-testid="cancel-modal-button"
          onClick={state?.onCancel ? state?.onCancel : closeModal}
          sx={{
            borderRadius: 50,
            margin: "0 auto",
            padding: "10px 26px",
            textTransform: "none",
            marginTop: "24px",
            width: "100%",
          }}
          variant="outlined"
        >
          {state?.cancelText}
        </Button>
      )}
      {state?.feedbackText && (
        <Button
          data-testid="feedback-button"
          onClick={state?.onFeedback ? state?.onFeedback : onClickFeedback}
          sx={{
            borderRadius: 50,
            margin: "0 auto",
            padding: "10px 26px",
            textTransform: "none",
            marginTop: "24px",
            width: "100%",
          }}
          variant="outlined"
        >
          {state?.feedbackText}
        </Button>
      )}
    </ModalWrapper>
  );
});

import {forwardRef} from "react";
import {useModal} from "@context";
import {useScreenDimension} from "@hooks";
import { ModalContents, ModalHeader, ModalWrapper } from "@atoms";

export interface ConfirmModalState {
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

export const ConfirmModal = forwardRef<HTMLDivElement>((_, ref) => {
  const { state, closeModal } = useModal<ConfirmModalState>();
  const { isMobile } = useScreenDimension();

  return (
    <ModalWrapper
      dataTestId={state ? state.dataTestId : "status-modal"}
      ref={ref}
    >
      <ModalHeader sx={{ marginTop: "34px", px: isMobile ? 0 : 3 }}>
        {state?.title}
      </ModalHeader>
    </ModalWrapper>
}

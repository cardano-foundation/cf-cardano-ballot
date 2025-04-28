import { SxProps, styled } from "@mui/material/styles";

import { ICONS } from "@consts";
import { useModal } from "@context";
import { callAll } from "@utils";
import { forwardRef } from "react";

interface Props {
  variant?: "modal" | "popup";
  onClose?: () => void;
  hideCloseButton?: boolean;
  children: React.ReactNode;
  dataTestId?: string;
  sx?: SxProps;
}

export const BaseWrapper = styled("div")<Pick<Props, "variant">>`
  box-shadow: 1px 2px 11px 0px #00123d5e;
  max-height: 90vh;
  position: fixed;
  top: 50%;
  left: 50%;
  display: flex;
  flex-direction: column;
  background: #fbfbff;
  border-radius: 24px;
  transform: translate(-50%, -50%);

  ${({ variant }) => {
    if (variant === "modal") {
      return `
        width: 80vw;
        max-width: 510px;
        padding: 52px 24px 34px 24px;
        max-height: 80vh;
        overflow: auto;
      `;
    }
    if (variant === "popup") {
      return `
        width: 320px;
        height: 320px;
      `;
    }
  }}
`;

export const CloseButton = styled("img")`
  cursor: pointer;
  position: absolute;
  top: 24px;
  right: 24px;
`;

export const ModalWrapper = forwardRef<HTMLDivElement, Props>(
  (
    {
      children,
      onClose,
      variant = "modal",
      hideCloseButton = false,
      dataTestId = "modal",
      sx,
    },
    ref,
  ) => {
    const { closeModal } = useModal();

    return (
      <BaseWrapper variant={variant} data-testid={dataTestId} sx={sx} ref={ref}>
        {variant !== "popup" && !hideCloseButton && (
          <CloseButton
            data-testid="close-modal-button"
            alt="close"
            onClick={callAll(closeModal, onClose)}
            src={ICONS.closeIcon}
          />
        )}
        {children}
      </BaseWrapper>
    );
  },
);

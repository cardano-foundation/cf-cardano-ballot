import { DialogContent } from "@mui/material";
import MuiModal from "@mui/material/Modal";
import type { ComponentProps } from "react";

export type MuiModalChildren = ComponentProps<typeof MuiModal>["children"];

interface Props {
  open: boolean;
  children: MuiModalChildren;
  handleClose?: () => void;
}

export const Modal = ({ open, children, handleClose }: Props) => (
  <MuiModal open={open} onClose={handleClose} disableAutoFocus>
    <DialogContent>{children}</DialogContent>
  </MuiModal>
);

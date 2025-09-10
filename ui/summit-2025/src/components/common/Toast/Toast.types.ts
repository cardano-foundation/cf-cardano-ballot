import { ReactElement } from "react";

enum ToastType {
  Verified = "verified",
  Error = "error",
  Common = "common",
}

type ToastProps = {
  message: string;
  isOpen: boolean;
  type?: ToastType;
  onClose: () => void;
};

type ToastStylesProps = {
  backgroundColor?: string;
  color?: string;
  fontWeight?: string;
  fontSize?: string;
  icon?: ReactElement;
};

export type { ToastProps, ToastStylesProps };
export { ToastType };

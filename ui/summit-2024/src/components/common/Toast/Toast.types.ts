import { ReactElement } from "react";

type ToastType = "common" | "verified" | "warn" | "error";

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

export { ToastType, ToastProps, ToastStylesProps };

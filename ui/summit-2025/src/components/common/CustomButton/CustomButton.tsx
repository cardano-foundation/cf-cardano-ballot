import React from "react";
import { Button, ButtonProps, useTheme, SxProps, Theme } from "@mui/material";

interface CustomButtonProps extends ButtonProps {
  colorVariant: "primary" | "secondary";
  gradient?: boolean;
  startIcon?: React.ReactNode;
}

const CustomButton: React.FC<CustomButtonProps> = ({
  colorVariant,
  gradient,
  startIcon,
  sx,
  disabled,
  children,
  ...props
}) => {
  const theme = useTheme();

  const getPrimaryStyles = (): SxProps<Theme> => ({
    background: gradient
      ? "linear-gradient(258deg, #EE9766 0%, #40407D 187.58%, #0C7BC5 249.97%)"
      : theme.palette.secondary.main,
    color: theme.palette.background.default,
    "&:hover": {
      backgroundColor: '#FF9277'
    },
  });

  const getSecondaryStyles = (): SxProps<Theme> => ({
    background: "transparent",
    border: `1px solid ${theme.palette.text.primary}`,
    color: theme.palette.text.primary,
    "&:hover": {
      backgroundColor: theme.palette.text.primary,
      color: theme.palette.background.default,
    },
  });

  const disabledStyles: SxProps<Theme> = {
    background: theme.palette.background.disabled,
    color: theme.palette.action.disabled,
    border: `1px solid ${theme.palette.action.disabledBackground}`,
    "&:hover": {
      background: theme.palette.action.disabledBackground,
    },
  };

  const defaultStyles: SxProps<Theme> = {
    textTransform: "none",
    fontSize: "16px",
    fontWeight: 600,
    lineHeight: "24px",
    borderRadius: "8px",
    padding: { xs: "8px 16px", sm: "12px 16px" },
    ...(disabled
      ? disabledStyles
      : colorVariant === "primary"
        ? getPrimaryStyles()
        : getSecondaryStyles()),
  };

  const combinedSx = [defaultStyles, ...(Array.isArray(sx) ? sx : [sx])];

  return (
    <Button
      sx={combinedSx}
      startIcon={startIcon}
      disabled={disabled}
      {...props}
    >
      {children}
    </Button>
  );
};

export { CustomButton };

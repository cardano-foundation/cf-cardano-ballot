import React from "react";
import { Button, ButtonProps, useTheme, SxProps, Theme } from "@mui/material";

interface CustomButtonProps extends ButtonProps {
  colorVariant: "primary" | "secondary";
  startIcon?: React.ReactNode;
}

const CustomButton: React.FC<CustomButtonProps> = ({
  colorVariant,
  startIcon,
  sx,
  disabled,
  children,
  ...props
}) => {
  const theme = useTheme();

  const getPrimaryStyles = (): SxProps<Theme> => ({
    background:
      "linear-gradient(258deg, #EE9766 0%, #40407D 187.58%, #0C7BC5 249.97%)",
    color: theme.palette.background.default,
    "&:hover": {
      color: theme.palette.text.neutralLightest,
      background:
        "linear-gradient(258deg, #EE9766 0%, #40407D 87.58%, #0C7BC5 249.97%)",
      borderColor: theme.palette.text.neutralLightest,
    },
  });

  const getSecondaryStyles = (): SxProps<Theme> => ({
    background: "transparent",
    border: `1px solid ${theme.palette.secondary.main}`,
    color: theme.palette.secondary.main,
    "&:hover": {
      color: theme.palette.text.neutralLightest,
      borderColor: theme.palette.text.neutralLightest,
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
    height: "56px",
    fontSize: "16px",
    fontWeight: 500,
    lineHeight: "24px",
    borderRadius: "12px",
    padding: { xs: "8px 16px", sm: "16px 24px" },
    ...(disabled
      ? disabledStyles
      : colorVariant === "primary"
      ? getPrimaryStyles()
      : getSecondaryStyles()),
  };

  return (
    <Button
      sx={[defaultStyles, sx]}
      startIcon={startIcon}
      disabled={disabled}
      {...props}
    >
      {children}
    </Button>
  );
};

export { CustomButton };

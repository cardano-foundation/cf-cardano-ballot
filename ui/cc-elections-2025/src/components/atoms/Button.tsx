import { CircularProgress, Button as MUIButton } from '@mui/material';
import { ButtonProps } from './types.ts';

export const Button = ({
  size = "large",
  variant = "contained",
  sx,
  isLoading,
  ...props
}: ButtonProps) => {
  const height = {
    extraLarge: 48,
    large: 45,
    medium: 36,
    small: 32,
  }[size];

  return (
    <MUIButton
      sx={{
        fontSize: size === "extraLarge" ? 16 : 14,
        height,
        whiteSpace: "nowrap",
        ...sx,
      }}
      variant={variant}
      {...props}
      disabled={isLoading || props?.disabled}
    >
      {isLoading && (
        <CircularProgress size={26} sx={{ position: "absolute" }} />
      )}
      {props.children}
    </MUIButton>
  );
};

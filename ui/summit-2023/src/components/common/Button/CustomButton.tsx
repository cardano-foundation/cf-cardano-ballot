import { Button } from '@mui/material';
import React from 'react';

type CustomButtonProps = {
  label: string;
  styles?: { [key: string]: string };
  disabled?: boolean;
  fullWidth?: boolean;
  onClick: () => void;
};
const CustomButton = (props: CustomButtonProps) => {
  const { onClick, disabled, styles, label, fullWidth } = props;
  return (
    <Button
      disabled={disabled || false}
      onClick={() => onClick()}
      fullWidth={fullWidth || false}
      sx={{
        ...styles,
        display: 'flex',
        padding: '16px 24px',
        justifyContent: 'center',
        alignItems: 'center',
        gap: '10px',
        borderRadius: '8px',
        fontSize: '16px',
        fontStyle: 'normal',
        fontWeight: '600',
        lineHeight: 'normal',
        textTransform: 'none',
        '&:hover': {
          background: styles?.background,
          boxShadow: 'none',
        },
      }}
    >
      {label}
    </Button>
  );
};

export { CustomButton };

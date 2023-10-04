import { Button } from '@mui/material';
import React from 'react';

type CustomButtonProps = {
  label: string;
  styles?: { [key: string]: string };
  disabled?: boolean;
  fullWidth?: boolean;
  onClick?: () => void;
};
const CustomButton = (props: CustomButtonProps) => {
  const { onClick, disabled, styles, label, fullWidth } = props;
  return (
    <Button
      disabled={disabled || false}
      onClick={onClick}
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
        textDecoration: 'none',
        textTransform: 'none',
        transition: 'transform 0.3s ease',
        '&:hover': {
          background: styles?.background,
          transition:
            'background-color 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms,box-shadow 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms,border-color 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms,color 250ms cubic-bezier(0.4, 0, 0.2, 1) 0ms',
          boxShadow:
            '0px 2px 4px -1px rgba(0,0,0,0.2), 0px 4px 5px 0px rgba(0,0,0,0.14), 0px 1px 10px 0px rgba(0,0,0,0.12)',
        },
      }}
    >
      {label}
    </Button>
  );
};

export { CustomButton };

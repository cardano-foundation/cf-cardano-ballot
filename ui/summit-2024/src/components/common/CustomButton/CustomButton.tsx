import React from 'react';
import { Button, ButtonProps, useTheme, SxProps, Theme } from '@mui/material';
import { SvgIconProps } from '@mui/material/SvgIcon';

interface CustomButtonProps extends ButtonProps {
    colorVariant: 'primary' | 'secondary';
    startIcon?: React.ReactElement<SvgIconProps>;
}

const CustomButton: React.FC<CustomButtonProps> = ({ colorVariant, startIcon, sx, children, ...props }) => {
    const theme = useTheme();

    const defaultStyles: SxProps<Theme> = {
        textTransform: "none",
        height: '56px',
        fontSize: '16px',
        fontWeight: 500,
        lineHeight: '24px',
        borderRadius: '12px',
        padding: { xs: "8px 16px", sm: "16px 24px" },
        ...(colorVariant === 'primary' ? {
            background: 'linear-gradient(70deg, #0C7BC5 -105.24%, #40407D -53.72%, #EE9766 -0.86%, #EE9766 103.82%)',
            color: theme.palette.background.default,
        } : {
            background: 'transparent',
            border: `1px solid ${theme.palette.secondary.main}`,
            color: theme.palette.secondary.main,
            "&:hover": {
                backgroundColor: theme.palette.secondary.dark,
                color: theme.palette.background.default
            },
        })
    };

    return (
        <Button
            variant="contained"
            sx={[defaultStyles, sx]}
            startIcon={startIcon}
            {...props}
        >
            {children}
        </Button>
    );
};

export {CustomButton};

import { TextField } from '@mui/material';
import React from 'react';
import theme from "../../../common/styles/theme";

type CustomInputProps = {
    value: string;
    styles?: { [key: string]: string };
    disabled?: boolean;
    fullWidth?: boolean;
    validate?: (value: string) => boolean;
    onChange: (value: string) => void;
};

const CustomInput = (props: CustomInputProps) => {
    const { disabled, styles, fullWidth, value, onChange, validate } = props;

    const handleChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        const newValue = event.target.value;
        if (validate) {
            onChange(newValue);
        } else {
            onChange(newValue);
        }
    };

    const isValid = value === "" || !validate ? true : validate && validate(value);
    return (
        <TextField
            value={value}
            onChange={handleChange}
            disabled={disabled || false}
            fullWidth={fullWidth || false}
            variant="outlined"
            InputProps={{
                style: {
                    color: theme.palette.text.neutralLightest,
                    fontSize: "16px",
                    fontStyle: "normal",
                    fontWeight: 500,
                    lineHeight: "24px"
                },
            }}
            sx={{
                ...styles,
                '& .MuiOutlinedInput-root': {
                    backgroundColor: theme.palette.background.neutralDark,
                    borderRadius: "12px",
                    height: "56px",
                    '& fieldset': {
                        borderColor: isValid ? '#737380' : '#FF878C',
                    },
                    '&:hover fieldset': {
                        borderColor: isValid ? '#737380' : '#FF878C',
                    },
                    '&.Mui-focused fieldset': {
                        borderColor: isValid ? '#EE9766' : '#FF878C',
                    },
                }
            }}
        />
    );
};

export { CustomInput };

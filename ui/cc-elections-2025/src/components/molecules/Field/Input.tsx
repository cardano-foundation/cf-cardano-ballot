import { forwardRef, useCallback, useImperativeHandle, useRef } from "react";
import { Box, InputLabel } from "@mui/material";

import {
  FormErrorMessage,
  FormHelpfulText,
  Input as InputBase,
} from "@atoms";
import { testIdFromLabel } from "@utils";

import { InputFieldProps } from "./types";

export const Input = forwardRef<HTMLInputElement, InputFieldProps>(
  (
    {
      id,
      errorDataTestId,
      errorMessage,
      errorStyles,
      helpfulTextDataTestId,
      helpfulText,
      helpfulTextStyle,
      label,
      layoutStyles,
      onBlur,
      onFocus,
      ...rest
    },
    ref,
  ) => {
    const inputRef = useRef<HTMLInputElement>(null);

    const handleFocus = useCallback((e: React.FocusEvent<HTMLInputElement>) => {
      onFocus?.(e);
      inputRef.current?.focus();
    }, []);

    const handleBlur = useCallback((e: React.FocusEvent<HTMLInputElement>) => {
      onBlur?.(e);
      inputRef.current?.blur();
    }, []);

    useImperativeHandle(
      ref,
      () =>
        ({
          focus: handleFocus,
          blur: handleBlur,
          ...inputRef.current,
        } as unknown as HTMLInputElement),
      [handleBlur, handleFocus],
    );

    return (
      <Box sx={{ width: "100%", ...layoutStyles }}>
        {label && (
          <InputLabel
            sx={{ mb: '4px' }}
            htmlFor={id}
          >
            {label}
          </InputLabel>
        )}
        <InputBase
          id={id}
          dataTestId={
            rest.dataTestId ?? `${label && `${testIdFromLabel(label)}-`}input`
          }
          errorMessage={errorMessage}
          {...rest}
          ref={inputRef}
        />
        <FormHelpfulText
          dataTestId={helpfulTextDataTestId}
          helpfulText={helpfulText}
          helpfulTextStyle={helpfulTextStyle}
        />
        <FormErrorMessage
          dataTestId={errorDataTestId}
          errorMessage={errorMessage}
          errorStyles={errorStyles}
        />
      </Box>
    );
  },
);

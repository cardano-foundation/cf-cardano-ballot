import {
  forwardRef,
  useCallback,
  useId,
  useImperativeHandle,
  useRef,
} from "react";
import { InputBase } from "@mui/material";

import { InputProps } from "./types";

export const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ errorMessage, dataTestId, onBlur, onFocus, sx, ...rest }, ref) => {
    const id = useId();
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
      <InputBase
        id={id}
        inputProps={{ "data-testid": dataTestId }}
        inputRef={inputRef}
        sx={{
          padding: "8px 16px",
          width: "100%",
          "& input.Mui-disabled": {
            WebkitTextFillColor: "#4C495B",
          },
          "&.Mui-disabled": {
            backgroundColor: "#F5F5F8",
            borderColor: "#9792B5",
          },
          ...sx,
        }}
        {...rest}
      />
    );
  },
);

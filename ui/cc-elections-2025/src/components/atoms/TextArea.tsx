import { forwardRef, useCallback, useImperativeHandle, useRef } from "react";
import { TextareaAutosize, styled } from "@mui/material";

import { useScreenDimension } from "../../hooks";

import { TextAreaProps } from "./types";

const TextAreaBase = styled(TextareaAutosize)(
  () => `
  font-family: "Poppins";
  font-weight: 400;
    ::placeholder {
      font-family: "Poppins";
      font-weight: 400;
      color: #a6a6a6;
    }
    `,
);

export const TextArea = forwardRef<HTMLTextAreaElement, TextAreaProps>(
  (
    {
      errorMessage,
      maxLength,
      onBlur,
      onFocus,
      isModifiedLayout,
      ...props
    },
    ref,
  ) => {
    const { isMobile } = useScreenDimension();
    const textAraeRef = useRef<HTMLTextAreaElement>(null);

    const handleFocus = useCallback(
      (e: React.FocusEvent<HTMLTextAreaElement>) => {
        onFocus?.(e);
        textAraeRef.current?.focus();
      },
      [],
    );

    const handleBlur = useCallback(
      (e: React.FocusEvent<HTMLTextAreaElement>) => {
        onBlur?.(e);
        textAraeRef.current?.blur();
      },
      [],
    );

    useImperativeHandle(
      ref,
      () =>
        ({
          focus: handleFocus,
          blur: handleBlur,
          ...textAraeRef.current,
        } as unknown as HTMLTextAreaElement),
      [handleBlur, handleFocus],
    );

    const getTexAreaHeight = () => {
      if (isModifiedLayout && isMobile) return "312px";
      if (isModifiedLayout) return "208px";
      if (isMobile) return "104px";
      return "128px";
    };

    return (
      <TextAreaBase
        style={{
          border: `1px solid ${errorMessage ? "red" : "rgba(191, 200, 217, 0.38)"}`,
          backgroundColor: errorMessage ? "#FAEAEB" : "white",
          borderRadius: "4px",
          height: getTexAreaHeight(),
          outline: "none",
          padding: "12px 14px",
          resize: "none",
        }}
        maxLength={maxLength}
        ref={textAraeRef}
        sx={{
          fontSize: isModifiedLayout ? "12px" : "auto",
          "&::placeholder": {
            fontSize: isModifiedLayout ? "12px" : "16px",
          },
        }}
        {...props}
      />
    );
  },
);

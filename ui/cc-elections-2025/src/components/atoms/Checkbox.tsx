import { useId } from "react";
import { Checkbox as MUICheckbox } from "@mui/material";

import { CheckboxProps } from "./types";

export const Checkbox = ({
  dataTestId,
  errorMessage,
  sx,
  ...props
}: CheckboxProps) => {
  const id = useId();

  return (
    <MUICheckbox
      id={id}
      inputProps={
        {
          "data-testid": dataTestId,
        } as React.InputHTMLAttributes<HTMLInputElement>
      }
      sx={{
        "& .MuiSvgIcon-root": { fontSize: 24 },
        color: errorMessage ? "red" : "#D9DEE8",
        ...sx,
      }}
      {...props}
    />
  );
};

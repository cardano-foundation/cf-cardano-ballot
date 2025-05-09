import {
  useId,
  useRef,
} from "react";
import SelectBase from '@mui/material/Select';

import { SelectProps} from "./types";
import { SelectIcon } from "@atoms";

export const Select = ({ errorMessage, dataTestId, sx, ...rest }: SelectProps) => {
  const id = useId();
  const inputRef = useRef<HTMLInputElement>(null);

  return (
    <SelectBase
      id={id}
      inputProps={{ "data-testid": dataTestId }}
      inputRef={inputRef}
      IconComponent={
        SelectIcon
      }
      sx={{
        padding: "8px 0",
        width: "100%",
        "& input.Mui-disabled": {
          WebkitTextFillColor: "#4C495B",
        },
        "&.Mui-disabled": {
          backgroundColor: "#F5F5F8",
          borderColor: "#9792B5",
        },
        "&.Mui-focused .MuiOutlinedInput-notchedOutline": {
          borderWidth: 0,
        },
        ...sx,
      }}
      {...rest}
    />
  );
};

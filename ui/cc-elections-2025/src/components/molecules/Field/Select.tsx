import { Box, InputLabel, Tooltip } from "@mui/material";

import {
  FormErrorMessage,
  FormHelpfulText,
  Select as SelectBase,
} from "@atoms";
import { useRef} from "react";
import { SelectFieldProps } from "./types";
import { testIdFromLabel } from "@utils";
import { ICONS } from "@consts";

export const Select = ({
  id,
  errorDataTestId,
  errorMessage,
  errorStyles,
  helpfulTextDataTestId,
  helpfulText,
  helpfulTextStyle,
  label,
  layoutStyles,
  tooltipText,
  ...rest
}: SelectFieldProps) => {
  const inputRef = useRef<HTMLInputElement>(null);

  return (
    <Box sx={{ width: "100%", ...layoutStyles }}>
      {label && (
        <InputLabel
          sx={{ mb: '4px', display: 'flex', alignItems: 'center', gap: '6px' }}
          htmlFor={id}
        >
          {label}
          {tooltipText && (
            <Tooltip title={tooltipText}>
              <img src={ICONS.infoIcon} alt="info" />
            </Tooltip>
          )}
        </InputLabel>
      )}
      <SelectBase
        id={id}
        dataTestId={
          rest.dataTestId ?? `${label && `${testIdFromLabel(label)}-`}input`
        }
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
  )
};

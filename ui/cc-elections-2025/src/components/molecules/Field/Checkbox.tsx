import { Box, InputLabel } from "@mui/material";

import {
  Checkbox as CheckboxBase,
  FormErrorMessage,
  FormHelpfulText,
} from "../../atoms";

import { CheckboxFieldProps } from "./types";

export const Checkbox = ({
  errorMessage,
  errorStyles,
  helpfulText,
  helpfulTextStyle,
  label,
  layoutStyles,
  onChange,
  value,
  ...rest
}: CheckboxFieldProps) => {
  return (
    <Box sx={{ width: "100%", ...layoutStyles }}>
      <Box
        sx={{
          alignItems: "center",
          cursor: "pointer",
          display: "grid",
          gridTemplateColumns: "auto 1fr",
          gridTemplateAreas: '"checkbox label" ". helpfulText"',
          width: "fit-content",
        }}
      >
        <CheckboxBase
          {...{ onChange, value }}
          errorMessage={errorMessage}
          {...rest}
        />
        {label && (
          <InputLabel sx={{ fontSize: '16px', fontWeight: 400, letterSpacing: '0.5px'}}>
            {label}
          </InputLabel>
        )}
        <FormHelpfulText
          helpfulText={helpfulText}
          helpfulTextStyle={helpfulTextStyle}
          sx={{ gridArea: "helpfulText" }}
        />
      </Box>
      <FormErrorMessage errorMessage={errorMessage} errorStyles={errorStyles} />
    </Box>
  );
};

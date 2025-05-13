import {
  Box,
  FormControlLabel,
  Checkbox as MuiCheckbox,
} from "@mui/material";

import {
  FormErrorMessage,
  FormHelpfulText,
} from "@atoms";

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
        <FormControlLabel
          sx={{ marginRight: '4px' }}
          control={
            <MuiCheckbox id={rest.id} {...{ onChange, value }} {...rest} />
          }
          label={label}
        />
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

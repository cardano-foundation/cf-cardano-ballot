import { Typography } from "@mui/material";

import { FormHelpfulTextProps } from "./types";

export const FormHelpfulText = ({
  dataTestId,
  helpfulText,
  helpfulTextStyle,
  sx,
}: FormHelpfulTextProps) =>
  helpfulText && (
    <Typography
      color="#212a3d"
      component="p"
      data-testid={
        dataTestId ?? `${helpfulText.replace(/\s+/g, "-").toLowerCase()}-error`
      }
      sx={{ mt: 0.5, ml: 2, ...sx }}
      variant="caption"
      {...helpfulTextStyle}
    >
      {helpfulText}
    </Typography>
  );

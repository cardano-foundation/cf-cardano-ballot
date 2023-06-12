import * as React from "react";
import { useTheme } from "@mui/material/styles";
import { OptionProps } from "./OptionCard.types";
import { Grid, Typography } from "@mui/material";
import ToggleButton from "@mui/material/ToggleButton";
import "./OptionCard.scss";

import ToggleButtonGroup from "@mui/material/ToggleButtonGroup";

export default function OptionCard({ items }: OptionProps) {
  const theme = useTheme();
  const [alignment, setAlignment] = React.useState("web");
  const [selected, setSelected] = React.useState(false);

  const handleChange = (
    event: React.MouseEvent<HTMLElement>,
    newAlignment: string
  ) => {
    setAlignment(newAlignment);
  };

  return (
    <Grid
      container
      direction="row"
      justifyContent={"center"}
    >
      {items.map((option, index) => (
        <Grid
          item
          xs={12}
          sm={3}
          sx={{ m: theme.spacing(2, 4, 2, 2) }}
          key={index}
        >
          <ToggleButtonGroup
            color="primary"
            value={alignment}
            exclusive
            onChange={handleChange}
            aria-label="Platform"
            className={selected ? "option-card-selected" : "option-card-group"}
          >
            <ToggleButton
              value={option.label}
              className="option-card"
            >
              <Grid
                container
                direction="column"
                justifyContent="center"
                alignItems="center"
              >
                <Grid item>
                  <Typography component="div">{option.icon}</Typography>
                  <Typography
                    component="div"
                    variant="h5"
                  >
                    {option.label}
                  </Typography>
                </Grid>
              </Grid>
            </ToggleButton>
          </ToggleButtonGroup>
        </Grid>
      ))}
    </Grid>
  );
}

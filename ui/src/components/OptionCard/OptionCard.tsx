import * as React from "react";
import { useTheme } from "@mui/material/styles";
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";
import { CardActionArea } from "@mui/material";
import "./OptionCard.scss";
import { OptionProps } from "./OptionCard.types";
import { Grid, Typography } from "@mui/material";

export default function OptionCard({ items }: OptionProps) {
  const theme = useTheme();

  return (
    <Grid
      container
      direction="row"
    >
        {items.map((option, index) => (

      <Grid
      item
      xs={12}
      sm={3}
      sx={{ m: theme.spacing(2, 0, 2, 2) }}

      key={index}
    >
          <Card
            className="option-card"
          >
            <CardActionArea sx={{height: 138}}>
              <CardContent>
                {option.icon}
                <Typography
                  component="div"
                  variant="h5"
                >
                  {option.label}
                </Typography>
              </CardContent>
            </CardActionArea>
          </Card>

      </Grid>
        ))}
    </Grid>
  );
}

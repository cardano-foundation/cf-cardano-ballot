import React, { MouseEvent } from "react";
import cn from "classnames";
import {
  Grid,
  Typography,
  ToggleButtonGroup,
  ToggleButton,
} from "@mui/material";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import styles from "../ChoseExplorerSection/ChoceExplorerSection.module.scss";
import { EXPLORERS } from "./utils";

type ChoseExplorerSectionProps = {
  setExplorer: (explorer: string) => void;
  explorer: string;
};

export const ChoseExplorerSection = ({
  setExplorer,
  explorer,
}: ChoseExplorerSectionProps) => (
  <Grid container direction="column">
    <ToggleButtonGroup
      disabled={false}
      sx={{
        flexDirection: "column",
        gap: "16px",
      }}
      value={explorer}
      exclusive
      onChange={(_event: MouseEvent<HTMLElement>, _active: string | null) => {
        console.log(_active);
        setExplorer(_active);
      }}
      aria-label="verification-app"
    >
      {EXPLORERS?.map((option) => (
        <ToggleButton
          sx={{
            height: "55px",
            borderRadius: "8px",
            padding: "16px 24px",
          }}
          value={option.url}
          className={cn(styles.optionCard, {
            [styles.selected]: explorer === option.url,
          })}
          key={option.label}
          data-testid="option-card"
        >
          <Grid container display={"row"}>
            <Grid item container xs>
              <Grid item>{option.icon}</Grid>
              <Typography
                sx={{
                  color: "#39486C",
                  fontsize: 16,
                  fontFamily: "Roboto",
                  fontWeight: "600",
                  wordWrap: "break-word",
                  textTransform: "none",
                  "&:hover": { color: "#1D439B" },
                }}
                component="div"
              >
                {option.label}
              </Typography>
            </Grid>

            <Grid
              justifyContent="center"
              alignItems="center"
              display="flex"
              item
            >
              {explorer === option.url ? (
                <CheckCircleIcon
                  style={{
                    color: "#1D439B",
                    fontSize: "28px",
                    margin: "-2px",
                  }}
                />
              ) : (
                <div
                  style={{
                    boxSizing: "border-box",
                    width: "23px",
                    minWidth: "23px",
                    height: "23px",
                    minHeight: "23px",
                    border: "1px solid #CCCCCC",
                    borderRadius: "50%",
                  }}
                />
              )}
            </Grid>
          </Grid>
        </ToggleButton>
      ))}
    </ToggleButtonGroup>
  </Grid>
);

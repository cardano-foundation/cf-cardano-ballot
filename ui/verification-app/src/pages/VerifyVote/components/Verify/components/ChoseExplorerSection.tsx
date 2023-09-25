import React, { MouseEvent } from "react";
import cn from "classnames";
import {
  Grid,
  Typography,
  ToggleButtonGroup,
  ToggleButton,
} from "@mui/material";
import { styled } from "@mui/material/styles";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import styles from "../../../VerifyVote.module.scss";

const Img = styled("img")({
  height: "23px",
  width: "23px",
  objectFit: "cover",
  margin: "auto",
  display: "block",
  maxWidth: "100%",
  maxHeight: "100%",
  marginRight: "10px",
});

export const explorers = [
  {
    name: "Explorer",
    label: "Explorer",
    icon: <Img src="/static/ie.png" />,
  },
  {
    name: "Cardanoscan",
    label: "Cardanoscan",
    icon: <Img src="/static/cardanoscan.png" />,
  },
  {
    name: "Cexplorer",
    label: "Cexplorer",
    icon: <Img src="/static/cexplorer.png" />,
  },
];

type ChoseExplorerSectionProps = {
  setExplorer: (explorer: string) => void;
  explorer: string;
};

// TODO: move to separate file
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
      value={"value1"}
      exclusive
      onChange={(_event: MouseEvent<HTMLElement>, _active: string | null) => {
        setExplorer(_active);
      }}
      aria-label="verification-app"
    >
      {explorers?.map((option) => (
        <ToggleButton
          sx={{
            height: "55px",
            borderRadius: "8px",
            padding: "16px 24px",
          }}
          value={option.name}
          className={cn(styles.optionCard, {
            [styles.selected]: explorer === option.name,
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
              {explorer === option.name ? (
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

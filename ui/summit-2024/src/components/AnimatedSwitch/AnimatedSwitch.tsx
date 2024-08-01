import React, { useState } from "react";
import { Box, Typography, styled } from "@mui/material";
import theme from "../../common/styles/theme";

interface AnimatedSwitchProps {
  defaultValue: string;
  onClickOption: (option: string) => void;
}

const SwitchContainer = styled(Box)(({ theme }) => ({
  display: "flex",
  alignItems: "center",
  borderRadius: "24px",
  overflow: "hidden",
  cursor: "pointer",
  userSelect: "none",
  position: "relative",
  width: "388px",
  height: "48px",
  background: theme.palette.background.neutralDark,
}));

const Option = styled(Box)<{ selected: boolean }>(() => ({
  flex: 1,
  padding: "12px 24px",
  textAlign: "center",
  zIndex: 1,
  position: "relative",
  transition: "color 0.3s ease-in-out",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
}));

const AnimatedRectangle = styled(Box)<{ selected: boolean }>(
  ({ selected }) => ({
    position: "absolute",
    top: 0,
    left: 0,
    width: "50%",
    height: "100%",
    backgroundColor: theme.palette.secondary.main,
    borderRadius: "24px",
    transition: "transform 0.3s ease-in-out",
    transform: selected ? "translateX(100%)" : "translateX(0)",
  }),
);

const AnimatedSwitch: React.FC<AnimatedSwitchProps> = ({
  defaultValue,
  onClickOption,
}) => {
  const [selected, setSelected] = useState<string>(defaultValue);

  const handleClick = (option: string) => {
    setSelected(option);
    onClickOption(option);
  };

  return (
    <SwitchContainer>
      <AnimatedRectangle selected={selected === "Overall Votes"} />
      <Option
        selected={selected === "Winners"}
        onClick={() => handleClick("Winners")}
      >
        <Typography
          sx={{
            fontSize: "16px",
            fontStyle: "normal",
            fontWeight: 500,
            lineHeight: "24px",
          }}
          color={
            selected === "Winners"
              ? theme.palette.background.default
              : "inherit"
          }
        >
          Winners
        </Typography>
      </Option>
      <Option
        selected={selected === "Overall Votes"}
        onClick={() => handleClick("Overall Votes")}
      >
        <Typography
          sx={{
            fontSize: "16px",
            fontStyle: "normal",
            fontWeight: 500,
            lineHeight: "24px",
          }}
          color={
            selected === "Overall Votes"
              ? theme.palette.background.default
              : "inherit"
          }
        >
          Overall Votes
        </Typography>
      </Option>
    </SwitchContainer>
  );
};

export default AnimatedSwitch;

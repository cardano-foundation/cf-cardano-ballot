import React, { useState } from "react";
import { Box, Typography, styled } from "@mui/material";

interface AnimatedSwitchProps {
  defaultValue: string;
  optionA: string;
  optionB: string;
  onClickOption: (option: string) => void;
}

const SwitchContainer = styled(Box)(({ theme }) => ({
  display: "flex",
  alignItems: "center",
  borderRadius: "8px",
  overflow: "hidden",
  cursor: "pointer",
  userSelect: "none",
  position: "relative",
  width: "100%",
  maxWidth: "400px",
  height: "48px",
  background: theme.palette.background.neutralDark,
  "@media (max-width: 600px)": {
    height: "40px",
    maxWidth: "300px",
  },
  "@media (max-width: 400px)": {
    height: "32px",
    maxWidth: "250px",
  },
}));

const Option = styled(Box)<{ selected: boolean }>(({ theme, selected }) => ({
  flex: 1,
  padding: "12px 24px",
  textAlign: "center",
  zIndex: 1,
  position: "relative",
  transition: "color 0.3s ease-in-out",
  display: "flex",
  alignItems: "center",
  justifyContent: "center",
  color: selected ? theme.palette.background.default : "inherit",
  "@media (max-width: 600px)": {
    padding: "8px 16px",
    fontSize: "14px",
  },
  "@media (max-width: 400px)": {
    padding: "6px 12px",
    fontSize: "12px",
  },
}));

const AnimatedRectangle = styled(Box)<{ selected: boolean }>(
  ({ theme, selected }) => ({
    position: "absolute",
    top: 0,
    left: 0,
    width: "50%",
    height: "100%",
    backgroundColor: theme.palette.secondary.main,
    borderRadius: "8px",
    transition: "transform 0.3s ease-in-out",
    transform: selected ? "translateX(100%)" : "translateX(0)",
  }),
);

const AnimatedSwitch: React.FC<AnimatedSwitchProps> = ({
  defaultValue,
  optionA,
  optionB,
  onClickOption,
}) => {
  const [selected, setSelected] = useState<string>(defaultValue);

  const handleClick = (option: string) => {
    setSelected(option);
    onClickOption(option);
  };

  return (
    <SwitchContainer>
      <AnimatedRectangle selected={selected === optionB} />
      <Option
        selected={selected === optionA}
        onClick={() => handleClick(optionA)}
      >
        <Typography
          sx={{
            fontSize: "16px",
            fontStyle: "normal",
            fontWeight: 600,
            lineHeight: "20px",
            "@media (max-width: 600px)": {
              fontSize: "14px",
              lineHeight: "20px",
            },
            "@media (max-width: 400px)": {
              fontSize: "12px",
              lineHeight: "18px",
            },
          }}
        >
          {optionA}
        </Typography>
      </Option>
      <Option
        selected={selected === optionB}
        onClick={() => handleClick(optionB)}
      >
        <Typography
          sx={{
            fontSize: "16px",
            fontStyle: "normal",
            fontWeight: 600,
            lineHeight: "20px",
            "@media (max-width: 600px)": {
              fontSize: "14px",
              lineHeight: "20px",
            },
            "@media (max-width: 400px)": {
              fontSize: "12px",
              lineHeight: "18px",
            },
          }}
        >
          {optionB}
        </Typography>
      </Option>
    </SwitchContainer>
  );
};

export default AnimatedSwitch;

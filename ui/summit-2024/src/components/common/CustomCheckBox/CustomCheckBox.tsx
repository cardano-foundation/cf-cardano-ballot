import * as React from "react";
import { Box, Checkbox } from "@mui/material";

const CustomCheckBox = ({ isChecked, setIsChecked }) => {
  const handleCheckboxChange = (event) => {
    setIsChecked(event.target.checked);
  };

  const checkedIcon = (
    <Box
      sx={{
        width: 20,
        height: 20,
        display: "flex",
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "var(--orange, #EE9766)",
        borderRadius: "4px",
      }}
    >
      <svg
        xmlns="http://www.w3.org/2000/svg"
        width="20"
        height="20"
        viewBox="0 0 20 20"
        fill="none"
      >
        <path
          d="M7.875 15.167L2.917 10.208 4.125 9.021 7.875 12.771 15.854 4.792 17.063 5.979 7.875 15.167Z"
          fill="#121212"
        />
      </svg>
    </Box>
  );

  const icon = (
    <Box
      sx={{
        width: 20,
        height: 20,
        backgroundColor: "transparent",
        borderRadius: "4px",
        border: "2px solid white",
      }}
    />
  );

  return (
    <>
      <Checkbox
        icon={icon}
        checkedIcon={checkedIcon}
        checked={isChecked}
        onChange={handleCheckboxChange}
      />
    </>
  );
};

export { CustomCheckBox };

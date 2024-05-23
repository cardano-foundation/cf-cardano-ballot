import React from "react";
import { Tab, Tabs, Box } from "@mui/material";

const TabsSegment = ({ setCurrentTab, currentTab, tabs }) => {
  const handleChange = (newValue) => {
    setCurrentTab(newValue);
  };

  return (
    <Box
      sx={{ display: "flex", flexDirection: "column", alignItems: "center" }}
    >
      <Tabs
        value={currentTab}
        onChange={(event, newValue) => handleChange(newValue)}
        aria-label="mnemonic length segment"
        sx={{
          borderRadius: 24,
          backgroundColor: "var(--neutralDark, #272727)",
          width: "fit-content",
          margin: "auto",
        }}
        TabIndicatorProps={{ style: { display: "none" } }}
      >
        {tabs.map((tab, index) => (
          <Tab
            key={index}
            label={tab}
            value={index}
            sx={{
              width: 200,
              height: 48,
              flexShrink: 0,
              borderRadius: 24,
              fontSize: 16,
              fontWeight: 500,
              lineHeight: "24px",
              textTransform: "none",
              backgroundColor:
                currentTab === index
                  ? "var(--orange, #EE9766)"
                  : "var(--neutralDark, #272727)",
              color:
                currentTab === index
                  ? "var(--neutralDarkest, #121212)"
                  : "var(--neutralLight, #D2D2D9)",
              "&:hover": {
                backgroundColor:
                  currentTab === index
                    ? "var(--orange, #EE9766)"
                    : "var(--neutralDark, #272727)",
              },
            }}
          />
        ))}
      </Tabs>
    </Box>
  );
};

export { TabsSegment };

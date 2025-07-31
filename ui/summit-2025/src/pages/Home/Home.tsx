import React from "react";
import { Hero } from "./components/Hero";
import { Box } from "@mui/material";
import { PageBase } from "../BasePage";

const Home: React.FC = () => {
  return (
    <>
      <PageBase title="Home">
        <>
          <Box component="div">
            <Hero />
          </Box>
        </>
      </PageBase>
    </>
  );
};

export { Home };

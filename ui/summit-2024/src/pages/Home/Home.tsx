import React from "react";
import { useMediaQuery } from "@mui/material";
import Ellipses from "../../assets/ellipse.svg";
import theme from "../../common/styles/theme";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";
import { ExploreSection } from "./components/ExploreSection";
import { Hero } from "./components/Hero";

const Home: React.FC = () => {
  const isMobile = useMediaQuery(theme.breakpoints.down("sm"));
  const isPortrait = useIsPortrait();

  return (
    <>
      <Hero />
      {!isMobile ? <ExploreSection /> : null}
      <img
        src={Ellipses}
        style={{
          position: "fixed",
          right: "0",
          top: isMobile ? "70%" : "50%",
          transform: "translateY(-50%)",
          zIndex: "-1",
          width: isMobile ? "70%" : "100%",
          height: isMobile ? "auto" : "auto",
        }}
      />
    </>
  );
};

export { Home };

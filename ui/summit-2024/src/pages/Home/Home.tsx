import React from "react";
import Ellipses from "../../assets/ellipse.svg";
import { useIsPortrait } from "../../common/hooks/useIsPortrait";
import { ExploreSection } from "./components/ExploreSection";
import { Hero } from "./components/Hero";
import { TicketsSection } from "./components/TicketsSection";

const Home: React.FC = () => {
  const isMobile = useIsPortrait();

  return (
    <>
      <Hero />
      {!isMobile ? <ExploreSection /> : null}
      {!isMobile ? <TicketsSection /> : null}
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

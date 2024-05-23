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
        <ExploreSection />
      {!isMobile ? <TicketsSection /> : null}
      <img
        src={Ellipses}
        style={{
          position: "fixed",
          right: "0",
          top: "70%",
          transform: "translateY(-50%)",
          zIndex: "-1",
          width: "70%",
          height: isMobile ? "auto" : "auto",
        }}
      />
    </>
  );
};

export { Home };

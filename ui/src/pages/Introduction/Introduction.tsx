import "./Introduction.scss";
import IntroSlides from "../../components/IntroSlides/IntroSlides";
import { SlideItem } from "../../components/IntroSlides/IntroSlides.types";

const Introduction = () => {
  const items: SlideItem[] = [
    {
      "image": "/static/cardano-summit-hero.jpeg",
      "title": "What is CIP-1694 voting?",
      "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magnaaliqua. Sit amet justo donec enim diam vulputate."
    },
    {
      "image": "/static/cardano-summit-hero.jpeg",
      "title": "What is CIP-1694 voting?",
      "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magnaaliqua. Sit amet justo donec enim diam vulputate."
    },
    {
      "image": "/static/cardano-summit-hero.jpeg",
      "title": "What is CIP-1694 voting?",
      "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magnaaliqua. Sit amet justo donec enim diam vulputate."
    },
    {
      "image": "/static/cardano-summit-hero.jpeg",
      "title": "What is CIP-1694 voting?",
      "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magnaaliqua. Sit amet justo donec enim diam vulputate."
    },
    {
      "image": "/static/cardano-summit-hero.jpeg",
      "title": "What is CIP-1694 voting?",
      "description": "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magnaaliqua. Sit amet justo donec enim diam vulputate."
    },
  ];

  return <IntroSlides items={items} />;
};

export default Introduction;

import { useState, useEffect } from "react";

function useIsPortrait() {
  const [isPortrait, setIsPortrait] = useState(
    window.innerHeight > window.innerWidth,
  );

  useEffect(() => {
    const handleResize = () => {
      setIsPortrait(window.innerHeight > window.innerWidth);
      console.log(window.innerHeight > window.innerWidth);
    };

    window.addEventListener("resize", handleResize);

    handleResize();

    return () => {
      window.removeEventListener("resize", handleResize);
    };
  }, []);

  return isPortrait;
}

export { useIsPortrait };

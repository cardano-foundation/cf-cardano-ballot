import { ReactNode, useEffect } from "react";

const AppWrapper = (props: { children: ReactNode }) => {
  console.log("AppWrapper");
  useEffect(() => {
    initApp();
  }, []);

  const initApp = async () => {};

  return <>{props.children}</>;
};

export { AppWrapper };

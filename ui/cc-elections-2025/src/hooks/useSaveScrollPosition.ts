import { useEffect } from "react";

export const useSaveScrollPosition = (
  isLoading: boolean,
  isFetching: boolean,
) => {
  const saveScrollPosition = () => {
    sessionStorage.setItem("scrollPosition", window.scrollY.toString());
  };

  useEffect(() => {
    if (!isLoading && !isFetching) {
      const savedPosition = sessionStorage.getItem("scrollPosition");

      if (savedPosition !== null) {
        window.scrollTo(0, parseInt(savedPosition, 10));
        sessionStorage.removeItem("scrollPosition");
      }
    }
  }, [isLoading, isFetching]);

  return saveScrollPosition;
};

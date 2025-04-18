import { useEffect } from "react";

const windowHeightFetchThreshold = 0.85;

export const useFetchNextPageDetector = (
  fetchNextPage: () => void,
  isLoading: boolean,
  hasNextPage?: boolean,
) => {
  useEffect(() => {
    const onScroll = () => {
      const { scrollTop } = document.documentElement;
      const windowHeight = window.innerHeight;
      const fullHeight = document.documentElement.offsetHeight;

      if (
        scrollTop + windowHeight > fullHeight * windowHeightFetchThreshold &&
        hasNextPage &&
        !isLoading
      ) {
        fetchNextPage();
      }
    };

    window.addEventListener("scroll", onScroll);

    return () => {
      window.removeEventListener("scroll", onScroll);
    };
  }, [fetchNextPage, isLoading, hasNextPage]);
};

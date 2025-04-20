import { useQuery } from "react-query";

import { getIndividuals } from "@services";

export const useGetIndividuals = () => {

  const { data, isLoading } = useQuery(
    [],
    getIndividuals,
    {
      refetchOnWindowFocus: true,
      keepPreviousData: true,
    },
  );

  return {
    isIndividualsLoading: isLoading,
    individuals: data,
  };
}

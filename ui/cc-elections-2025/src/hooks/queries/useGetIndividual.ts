import { useQuery } from "react-query";

import { getIndividual } from "@services";

export const useGetIndividual = (id: number) => {

  const { data, isLoading } = useQuery(
    ["useGetProposalKey", id],
    () => getIndividual(id),
    {
      refetchOnWindowFocus: true,
      keepPreviousData: true,
    },
  );

  return {
    isIndividualLoading: isLoading,
    individual: data,
  };
}

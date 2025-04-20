import { useQuery } from "react-query";

import { getAllCandidates } from "@services";

export const useGetAllCandidates = () => {

  const { data, isLoading } = useQuery(
    [],
    getAllCandidates,
    {
      refetchOnWindowFocus: true,
      keepPreviousData: true,
    },
  );

  return {
    isAllCandidatesLoading: isLoading,
    allCandidates: data,
  };
}

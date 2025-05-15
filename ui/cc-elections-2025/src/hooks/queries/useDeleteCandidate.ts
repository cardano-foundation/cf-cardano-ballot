import { useMutation, useQueryClient } from 'react-query';

import { deleteIndividual, deleteCompanies, deleteConsortia } from "@services";

export const useDeleteCandidate = (candidateType: "individual" | "company" | "consortium") => {
  const queryClient = useQueryClient();
  const mutationFn = candidateType === "individual" ? deleteIndividual : candidateType === "company" ? deleteCompanies : deleteConsortia;
  return useMutation(mutationFn, {
    onSuccess: () => {
      queryClient.invalidateQueries('allCandidates');
    },
    onError: () => {
      console.log('error');
    }
  });
}

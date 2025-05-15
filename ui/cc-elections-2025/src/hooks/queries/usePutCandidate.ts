import { useMutation, useQueryClient } from 'react-query';

import { putIndividual, putConsortia, putCompanies } from "@services";

export const usePutCandidate = (candidateType: "individual" | "company" | "consortium") => {
  const queryClient = useQueryClient();
  const mutationFn = candidateType === "individual" ? putIndividual : candidateType === "company" ? putCompanies : putConsortia;
  return useMutation(mutationFn, {
    onSuccess: () => {
      queryClient.invalidateQueries('allCandidates');
    },
    onError: () => {
      console.log('error');
    }
  });
}

import { useMutation } from 'react-query';

import { postIndividual, postCompanies, postConsortia } from "@services";

export const usePostCandidate = (candidateType: "individual" | "company" | "consortium") => {
  const mutationFn = candidateType === "individual" ? postIndividual : candidateType === "company" ? postCompanies : postConsortia;
  const { mutate, isLoading } = useMutation(mutationFn, {
    onSuccess: () => {
      console.log('success');
    },
    onError: () => {
      console.log('error');
    }
  });

  return {
    mutate,
    isLoading,
  };
}

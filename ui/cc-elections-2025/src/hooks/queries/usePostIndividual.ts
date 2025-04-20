import { useMutation } from 'react-query';

import { postIndividual } from "@services";

export const usePostIndividual = () => {
  const { mutate, isLoading } = useMutation(postIndividual, {
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

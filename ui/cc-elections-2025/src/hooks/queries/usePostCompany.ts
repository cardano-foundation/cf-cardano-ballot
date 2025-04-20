import { useMutation } from 'react-query';

import { postCompanies } from "@services";

export const usePostCompany = () => {
  const { mutate, isLoading } = useMutation(postCompanies, {
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

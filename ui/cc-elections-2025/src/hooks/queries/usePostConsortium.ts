import { useMutation } from 'react-query';

import { postConsortia } from "@services";

export const usePostConsortium = () => {
  const { mutate, isLoading } = useMutation(postConsortia, {
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

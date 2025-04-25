import { useContext } from 'react';
import { RegisterFormContext } from '@context';
import type { FormContextType, RegisterFormData } from '../types/formData';

export const useRegisterFormContext = () => {
  return useContext(RegisterFormContext) as FormContextType<RegisterFormData>;
}

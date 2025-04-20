import { useContext } from 'react';
import { FormContext } from '@context';
import type { FormContextType, IndividualFormData } from '../types/formData';

export const useFormContext = () => {
  return useContext(FormContext) as FormContextType<IndividualFormData>;
}

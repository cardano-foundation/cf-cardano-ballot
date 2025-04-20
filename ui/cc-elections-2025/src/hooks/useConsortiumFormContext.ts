import { useContext } from 'react';
import { ConsortiumFormContext } from '@context';
import {FormContextType, ConsortumFormData} from '../types/formData';

export const useConsortiumFormContext = () => {
  return useContext(ConsortiumFormContext) as FormContextType<ConsortumFormData>;
}

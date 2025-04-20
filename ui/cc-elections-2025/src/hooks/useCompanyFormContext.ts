import { useContext } from 'react';
import { CompanyFormContext } from '@context';
import type { FormContextType, CompanyFormData } from '../types/formData';

export const useCompanyFormContext = () => {
  return useContext(CompanyFormContext) as FormContextType<CompanyFormData>;
}

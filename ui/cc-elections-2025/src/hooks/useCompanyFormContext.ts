import { useContext } from 'react';
import FormContext from '../context/CompanyFormContext';
import type { FormContextType, CompanyFormData } from '../types/formData';

const useFormContext = () => {
  return useContext(FormContext) as FormContextType<CompanyFormData>;
}

export default useFormContext;

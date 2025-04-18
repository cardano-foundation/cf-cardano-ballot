import { useContext } from 'react';
import FormContext from '../context/FormContext';
import type { FormContextType, IndividualFormData } from '../types/formData';

const useFormContext = () => {
  return useContext(FormContext) as FormContextType<IndividualFormData>;
}

export default useFormContext;

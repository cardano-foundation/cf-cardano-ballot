import { Candidate } from '@models';
import { API } from '../API';

export const getCompanies = async () => {
  const response = await API.get<Candidate[]>('/api/companies');

  return response.data;
}

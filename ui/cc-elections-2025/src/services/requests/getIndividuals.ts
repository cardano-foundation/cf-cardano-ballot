import { Candidate } from '@models';
import { API } from '../API';

export const getIndividuals = async () => {
  const response = await API.get<Candidate[]>('/api/individuals');

  return response.data;
}

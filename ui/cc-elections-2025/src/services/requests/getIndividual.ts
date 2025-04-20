import { Candidate } from '@models';
import { API } from '../API';

export const getIndividual = async (id: number) => {
  const response = await API.get<Candidate>(`/api/individuals/${id}`);

  return response.data;
}

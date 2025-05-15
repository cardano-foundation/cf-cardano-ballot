import { CandidateBody } from '@models';
import { API } from '../API';

export const putIndividual = async ({ id, individual }: { id: number, individual: CandidateBody }) => {
  const response = await API.put(`/api/individuals/${id}`, individual);

  return response.data;
}

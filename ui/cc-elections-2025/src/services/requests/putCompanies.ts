import { CandidateBody } from '@models';
import { API } from '../API';

export const putCompanies = async ({ id, individual }: { id: number, individual: CandidateBody }) => {
  const response = await API.put(`/api/companies/${id}`, individual);

  return response.data;
}

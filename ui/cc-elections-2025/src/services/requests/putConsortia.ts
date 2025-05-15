import { CandidateBody } from '@models';
import { API } from '../API';

export const putConsortia = async ({ id, individual }: { id: number, individual: CandidateBody }) => {
  const response = await API.put(`/api/consortia/${id}`, individual);

  return response.data;
}

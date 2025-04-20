import { CandidateBody } from '@models';
import { API } from '../API';

export const postConsortia = async (individual: CandidateBody) => {
  const response = await API.post('/api/consortia', individual);

  return response.data;
}

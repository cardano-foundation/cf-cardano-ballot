import { CandidateBody } from '@models';
import { API } from '../API';

export const postIndividual = async (individual: CandidateBody) => {
  const response = await API.post('/api/individuals', individual);

  return response.data;
}

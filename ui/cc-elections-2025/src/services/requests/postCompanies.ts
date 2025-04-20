import { CandidateBody } from '@models';
import { API } from '../API';

export const postCompanies = async (individual: CandidateBody) => {
  const response = await API.post('/api/companies', individual);

  return response.data;
}

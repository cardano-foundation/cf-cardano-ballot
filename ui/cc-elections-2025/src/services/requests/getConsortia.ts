import { Candidate } from '@models';
import { API } from '../API';

export const getConsortia = async () => {
  const response = await API.get<Candidate[]>('/api/consortia');

  return response.data;
}

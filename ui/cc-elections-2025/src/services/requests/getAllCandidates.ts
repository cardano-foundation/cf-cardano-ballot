import { Candidate } from '@models';
import { API } from '../API';

export const getAllCandidates = async () => {
  const endpoints = [
    '/api/individuals',
    '/api/companies',
    '/api/consortia'
  ];

  const response = await Promise.all(
    endpoints.map((endpoint) => API.get<Candidate[]>(endpoint)));

  return response[0].data.concat(response[1].data).concat(response[2].data);
}

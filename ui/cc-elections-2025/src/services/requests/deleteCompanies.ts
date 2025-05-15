import { API } from '../API';

export const deleteCompanies = async (id: number) => {
  return await API.delete(`/api/companies/${id}`);
}

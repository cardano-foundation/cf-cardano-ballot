import { API } from '../API';

export const deleteIndividual = async (id: number) => {
  return await API.delete(`/api/individuals/${id}`);
}

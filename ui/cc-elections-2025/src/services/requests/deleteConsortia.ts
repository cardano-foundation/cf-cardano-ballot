import { API } from '../API';

export const deleteConsortia = async (id: number) => {
  return await API.delete(`/api/consortia/${id}`);
}

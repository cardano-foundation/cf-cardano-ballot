import axios from 'axios';
import { NavigateFunction } from 'react-router-dom';

const TIMEOUT_IN_SECONDS = 30 * 1000; // 1000 ms is 1 s then its 30 s
const BASE_URL = import.meta.env.VITE_BASE_URL;

export const API = axios.create({
  baseURL: BASE_URL,
  timeout: TIMEOUT_IN_SECONDS,
});

export const SetupInterceptors = (navigate: NavigateFunction) =>
  API.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error?.response?.status === 500) {
        navigate('/error', { state: { errorCode: 500 } });
      }

      return Promise.reject(error);
    },
  );

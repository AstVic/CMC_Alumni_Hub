import axios, { AxiosError } from 'axios';
import { API_BASE_URL } from '../config';
import { tokenStorage } from '../auth/tokenStorage';

/**
 * Shared axios instance. Attaches the bearer token and, on 401, clears the
 * session so the app falls back to the login screen.
 */
export const http = axios.create({
  baseURL: API_BASE_URL,
  headers: { 'Content-Type': 'application/json' },
});

http.interceptors.request.use((config) => {
  const token = tokenStorage.getAccess();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

http.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    if (error.response?.status === 401) {
      tokenStorage.clear();
      // Avoid redirect loops on the login page itself.
      if (!window.location.pathname.startsWith('/login')) {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  },
);

/** Extracts a human-readable message from an API error. */
export function apiErrorMessage(error: unknown, fallback = 'Что-то пошло не так'): string {
  if (axios.isAxiosError(error)) {
    const data = error.response?.data as
      | { message?: string; fieldErrors?: { field: string; message: string }[] }
      | undefined;
    if (data?.fieldErrors?.length) {
      return data.fieldErrors.map((f) => f.message).join('. ');
    }
    if (data?.message) {
      return data.message;
    }
  }
  return fallback;
}

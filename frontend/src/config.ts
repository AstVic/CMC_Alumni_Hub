/**
 * Runtime configuration derived from Vite env vars.
 * The API base URL differs per mode (.env.development vs .env.production)
 * and must never be hardcoded.
 */
export const API_BASE_URL: string =
  import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api';

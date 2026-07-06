import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: true,
    port: 5173,
    proxy: {
      // Photos are referenced as /uploads/... relative to the frontend origin;
      // proxy them to the backend during `npm run dev`.
      '/uploads': 'http://localhost:8080',
    },
  },
  preview: {
    host: true,
    port: 5173,
  },
});

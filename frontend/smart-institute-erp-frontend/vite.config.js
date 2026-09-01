import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';

// Path aliases mirror the src/ folder structure.
// Using import.meta.dirname (Node 20.11+/Vite native config loader)
// instead of __dirname, which is a CommonJS global unavailable in ESM.
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@app': path.resolve(import.meta.dirname, './src/app'),
      '@assets': path.resolve(import.meta.dirname, './src/assets'),
      '@components': path.resolve(import.meta.dirname, './src/components'),
      '@features': path.resolve(import.meta.dirname, './src/features'),
      '@hooks': path.resolve(import.meta.dirname, './src/hooks'),
      '@services': path.resolve(import.meta.dirname, './src/services'),
      '@schemas': path.resolve(import.meta.dirname, './src/schemas'),
      '@store': path.resolve(import.meta.dirname, './src/store'),
      '@routes': path.resolve(import.meta.dirname, './src/routes'),
      '@theme': path.resolve(import.meta.dirname, './src/theme'),
      '@utils': path.resolve(import.meta.dirname, './src/utils'),
      '@constants': path.resolve(import.meta.dirname, './src/constants'),
    },
  },
  server: {
    port: 5173,
  },
});
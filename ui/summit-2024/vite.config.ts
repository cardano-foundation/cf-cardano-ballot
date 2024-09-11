import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import path from 'path';

export default defineConfig({
  plugins: [react()],
  base: './',
  server: {
    port: 3000
  },
  resolve: {
    alias: {
      '@assets': path.resolve(__dirname, './src/assets'),
    },
  },
  build: {
    rollupOptions: {
      output: {
        assetFileNames: 'assets/[name]-[hash][extname]',
      }
    }
  }
})

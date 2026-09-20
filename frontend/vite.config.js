import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/books': 'http://localhost:8080',
      '/request': 'http://localhost:8080',
      '/librarian': 'http://localhost:8080',
      '/admin': 'http://localhost:8080',
      '/register': 'http://localhost:8080',
      '/login': 'http://localhost:8080',
      '/logout': 'http://localhost:8080',
      '/user': 'http://localhost:8080',
      '/my-books': 'http://localhost:8080',
      '/my-requests': 'http://localhost:8080',
    }
  },
  build: {
    outDir: '../src/main/resources/static',
    emptyOutDir: true,
  }
})

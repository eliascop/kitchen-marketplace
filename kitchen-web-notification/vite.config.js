import { defineConfig, loadEnv} from 'vite';
import react from '@vitejs/plugin-react';
import tsconfigPaths from 'vite-tsconfig-paths';

export default defineConfig({
  base: '/web-notification/',
  plugins: [
    react(),
    tsconfigPaths(),
  ],
});
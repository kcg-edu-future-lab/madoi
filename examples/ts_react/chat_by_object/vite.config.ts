import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'
import { vitePluginTypescriptTransform } from 'vite-plugin-typescript-transform';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    vitePluginTypescriptTransform({
      enforce: "pre",
      filter: {
        files: {
          include: ["**/src/*.ts"]
        }
      }
    }),
    react(),
  ],
  server: {
    open: true,
    port: 3000,
  },
  optimizeDeps: {
    esbuildOptions: {
      tsconfigRaw: {
        compilerOptions: {
          experimentalDecorators: true,
        }
      }
    }
  },
})

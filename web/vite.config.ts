import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
    plugins: [react()],
    resolve: {
        alias: {
            "@styles": '/src/styles'
        }
    },
    server: {
        proxy: {
            "/api": {
                target: "http://localhost:6204",
                changeOrigin: true,
                secure: false,
                rewrite: (path) => path.replace(/^\/api/, "")
            }
        }
    }
})

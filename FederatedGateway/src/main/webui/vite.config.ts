import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import federation from '@originjs/vite-plugin-federation'

import pkg from './package.json';
import tailwindcss from "@tailwindcss/vite";

const { dependencies } = pkg;

// https://vite.dev/config/
export default defineConfig({
    optimizeDeps: {
        exclude: ['virtual:__federation__']
    },
    plugins: [
        react(),
        tailwindcss(),
        federation({
            name: 'app',
            remotes: {
                remoteApp: 'http://localhost:5002/assets/remoteEntry.js',
            },
            shared: {
                ...dependencies,
                react: {
                    version: dependencies['react'],
                },
                'react-dom': {
                    version: dependencies['react-dom'],
                },
            }
        })
    ],
    build: {
        modulePreload: false,
        target: 'esnext',
        minify: false,
        cssCodeSplit: false,
        rollupOptions: {
            external: ['virtual:__federation__'],
        },
    }
})
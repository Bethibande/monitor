import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import federation from '@originjs/vite-plugin-federation'
import tailwindcss from "@tailwindcss/vite";

// https://vite.dev/config/
export default defineConfig({
    optimizeDeps: {
        exclude: ['virtual:__federation__']
    },
    plugins: [
        react(),
        tailwindcss(),
        federation({
            name: "remote_app",
            filename: "remoteEntry.js",
            exposes: {
                './extension-config': './src/extension-config'
            },
            shared: [
                'react',
                'react-dom',
                "@monitor/shared-library",
                "react-router",
                "react/jsx-runtime",
                "react-bootstrap-icons",
                "i18next"
            ]
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
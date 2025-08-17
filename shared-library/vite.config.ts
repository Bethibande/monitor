import {defineConfig} from 'vite';
import react from '@vitejs/plugin-react';
import tailwindcss from '@tailwindcss/vite';
import * as path from "node:path";

export default defineConfig({
    plugins: [
        react(),
        tailwindcss(),
    ],
    resolve: {
        alias: {
            "@": path.resolve(__dirname, "./src"),
        },
    },
    build: {
        lib: {
            entry: 'src/index.ts',
            name: '@monitor/shared-library',
            fileName: 'shared-library',
            formats: ['es'],
        },
        rollupOptions: {
            external: [
                'react',
                'react-dom',
                'react-router',
                'virtual:__federation__',
                'react/jsx-runtime',
            ],
            output: {
                globals: {
                    react: 'React',
                    'react-dom': 'ReactDOM',
                },
            },
        },
    },
});
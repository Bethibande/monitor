import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import {ExtensionProvider} from '@monitor/shared-library'
import i18next from "i18next";

i18next.init({
    lng: 'en',
    resources: {
        en: {
            translation: {
                "test": "ABC"
            }
        }
    }
})

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ExtensionProvider>
        <App />
    </ExtensionProvider>
  </StrictMode>,
)

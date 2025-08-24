import {StrictMode} from 'react'
import {createRoot} from 'react-dom/client'
import './index.css'
import App from './App.tsx'
import {ApiClientBuilder, ClusterContextProvider, ExtensionProvider} from '@monitor/shared-library'
import i18next from "i18next";

i18next.init({
    lng: 'en',
    resources: {
        en: {
            translation: {
                "nav.context.search": "Search",
                "nav.context.more": "+ {{num, number}} more",
            }
        }
    }
})

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <ClusterContextProvider>
            <ExtensionProvider>
                <App/>
            </ExtensionProvider>
        </ClusterContextProvider>
    </StrictMode>,
)


export const apiClient = new ApiClientBuilder()
    .withBaseUrl("/api/v1")
    .build();
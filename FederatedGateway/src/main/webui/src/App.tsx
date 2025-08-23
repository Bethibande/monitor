import './App.css'
import {useEffect} from "react";
import {type Translations, useExtensions} from '@monitor/shared-library';
import {fetchAvailableModulePaths} from "./utils/extensions.ts";
import {createBrowserRouter, type RouteObject} from "react-router";
import {RouterProvider} from "react-router/dom";
import AppLayout from "./layouts/AppLayout.tsx";
import ErrorLayout from "./layouts/ErrorLayout.tsx";
import "@monitor/shared-library/dist/shared-library.css"
import i18next from "i18next";

function App() {
    const {extensions, loadExtension} = useExtensions();
    useEffect(() => {
        fetchAvailableModulePaths().then(urls => {
            urls.forEach(url => loadExtension(url).then(extension => {
                console.log("Loaded extension: ", extension)

                if (extension.translations) {
                    const translations: Translations = extension.translations
                    const extensionBundleNamespace = extension.name
                    for (let language in translations) {
                        if (i18next.hasResourceBundle(language, extensionBundleNamespace)) {
                            continue
                        }

                        const bundle = translations[language]

                        i18next.addResourceBundle(language, extensionBundleNamespace, bundle)
                        console.log("Added language bundle", extensionBundleNamespace, language)
                    }
                }
            }))
        })
    }, []);

    const routes: RouteObject[] = []
    extensions.forEach(ext => {
        ext.routes && routes.push(...ext.routes)
    })

    const router = createBrowserRouter([{
        path: "/",
        ErrorBoundary: ErrorLayout,
        Component: AppLayout,
        children: routes
    }]);

    return (
        <RouterProvider router={router}>

        </RouterProvider>
    )
}

export default App

import './App.css'
import {useEffect} from "react";
import {useExtensions} from '@monitor/shared-library';
import {fetchAvailableModulePaths} from "./utils/extensions.ts";
import {createBrowserRouter, type RouteObject} from "react-router";
import {RouterProvider} from "react-router/dom";
import AppLayout from "./AppLayout.tsx";

function App() {
    const {extensions, loadExtension} = useExtensions();
    useEffect(() => {
        fetchAvailableModulePaths().then(urls => {
            urls.forEach(url => loadExtension(url).then(ext => console.log("Loaded extension: ", ext)))
        })
    }, []);

    const routes: RouteObject[] = []
    extensions.forEach(ext => { ext.routes && routes.push(...ext.routes) })

    const router = createBrowserRouter([{
        path: "/",
        Component: AppLayout,
        children: routes
    }]);

    return (
        <RouterProvider router={router}>

        </RouterProvider>
    )
}

export default App

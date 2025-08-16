import reactLogo from './assets/react.svg'
import viteLogo from '/vite.svg'
import './App.css'
import {useEffect} from "react";
import {useExtensions} from '@monitor/shared-library';
import {fetchAvailableModulePaths} from "./utils/extensions.ts";

function App() {
    const {extensions, loadExtension} = useExtensions();
    useEffect(() => {
        fetchAvailableModulePaths().then(urls => {
            urls.forEach(url => loadExtension(url).then(ext => console.log("Loaded extension: ", ext)))
        })
    }, []);

    console.log("Extensions: ", extensions)

    return (
        <>
            <div>
                <a href="https://vite.dev" target="_blank">
                    <img src={viteLogo} className="logo" alt="Vite logo"/>
                </a>
                <a href="https://react.dev" target="_blank">
                    <img src={reactLogo} className="logo react" alt="React logo"/>
                </a>
            </div>
            <h1>Vite + React</h1>
            <div className="card">
                <p>
                    Edit <code>src/App.tsx</code> and save to test HMR
                </p>
            </div>
            <p className="read-the-docs">
                Click on the Vite and React logos to learn more
            </p>
        </>
    )
}

export default App

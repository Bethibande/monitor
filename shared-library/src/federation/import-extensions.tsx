import {
    __federation_method_getRemote as getRemote,
    __federation_method_setRemote as setRemote,
    __federation_method_unwrapDefault as unwrapModule,
} from "virtual:__federation__";
import {createContext, type FC, type ReactNode, useCallback, useContext, useState} from "react";
import type {ExtensionConfig} from "../index";

type ExtensionContextType = {
    extensions: ExtensionConfig[];
    loadExtension: (url: string) => Promise<ExtensionConfig>;
}

const ExtensionContext = createContext<ExtensionContextType>({
    extensions: [],
    loadExtension: () => Promise.resolve({} as ExtensionConfig)
});

interface ExtensionProviderProps {
    children: ReactNode;
}

type LoadedExtension = {
    extension: ExtensionConfig,
    url: string,
}

export const ExtensionProvider: FC<ExtensionProviderProps> = ({children}) => {
    const [extensions, setExtensions] = useState<ExtensionConfig[]>([]);
    const [loadedExtensions, setLoadedExtensions] = useState<LoadedExtension[]>([]);

    const loadExtension = useCallback(async (url: string) => {
        try {
            const loaded = loadedExtensions.find(ex => ex.url === url)
            if (loaded) {
                return Promise.reject(loaded.extension)
            }

            const extension = await __loadExtension(url)
            const loadedExtension: LoadedExtension = {
                extension: extension,
                url: url
            }

            setLoadedExtensions([...loadedExtensions, loadedExtension])
            setExtensions([...extensions, extension])

            return extension;
        } catch (error) {
            console.error("Failed to load extension from URL:", url, error);
            throw error;
        }
    }, []);

    return (
        <ExtensionContext.Provider value={{extensions, loadExtension}}>
            {children}
        </ExtensionContext.Provider>
    )
}

function __loadExtension(url: string): Promise<ExtensionConfig> {
    setRemote('dynamic', {url: () => Promise.resolve(url), format: 'esm', from: 'vite'});
    return getRemote('dynamic', './extension-config')
        .then(moduleWrapped => unwrapModule(moduleWrapped))
        .then(module => {
            return module as ExtensionConfig;
        });
}

export const useExtensions = () => useContext(ExtensionContext);

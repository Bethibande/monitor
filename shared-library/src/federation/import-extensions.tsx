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

export const ExtensionProvider: FC<ExtensionProviderProps> = ({ children }) => {
    const [extensions, setExtensions] = useState<ExtensionConfig[]>([]);

    const loadExtension = useCallback(async (url: string) => {
        try {
            let extension = await __loadExtension(url)

            setExtensions((prev) => {
                const existing = prev.find((e) => e.name === extension.name);
                if (existing) {
                    extension = existing;
                    return prev;
                }
                return [...prev || [], extension]
            });

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

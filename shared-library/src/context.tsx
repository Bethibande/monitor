import {createContext, ReactNode, useContext, useState} from "react";

export enum ContextScope {
    CLUSTER = "CLUSTER",
    NAMESPACE = "NAMESPACE",
    NODE = "NODE"
}

export type ClusterContext = {
    scope: ContextScope,
    id: string,
    parent?: ClusterContext
}

export const DefaultClusterContext: ClusterContext = {
    scope: ContextScope.CLUSTER,
    id: "host"
}

type ContextType = {
    context: ClusterContext,
    setContext: (context: ClusterContext) => void
}

export const ClusterContext = createContext<ContextType | undefined>(undefined);

export const ClusterContextProvider = ({children}: { children: ReactNode }) => {
    const [context, setContextState] = useState<ClusterContext>(() => {
        try {
            const stored = window.sessionStorage.getItem('clusterContext');
            return stored ? JSON.parse(stored) : DefaultClusterContext;
        } catch {
            return DefaultClusterContext;
        }
    });

    const setContext = (newContext: ClusterContext) => {
        setContextState(newContext);
        try {
            window.sessionStorage.setItem('clusterContext', JSON.stringify(newContext));
        } catch {
        }
    };

    return (
        <ClusterContext.Provider value={{context, setContext}}>
            {children}
        </ClusterContext.Provider>
    );
};

export const useClusterContext = () => {
    const context = useContext(ClusterContext);
    if (!context) {
        throw new Error("useClusterContext must be used within a ClusterContextProvider");
    }
    return context;
};
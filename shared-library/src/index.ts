import type {ReactNode} from "react";
import type {NavItem} from "./navigation";

export interface ConfiguredRoute {
    path: string;
    view: ReactNode;
}

export interface ExtensionConfig {
    name: string;
    routes?: ConfiguredRoute[];
    navItems?: NavItem[];
}

export * from './federation/import-extensions';
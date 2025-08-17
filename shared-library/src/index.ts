import type {NavItem} from "./navigation";
import type {RouteObject} from 'react-router';

export interface ExtensionConfig {
    name: string;
    routes?: RouteObject[];
    navItems?: NavItem[];
}

export * from './federation/import-extensions';
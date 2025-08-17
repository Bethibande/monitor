import type {NavItem} from "./navigation";
import type {RouteObject} from 'react-router';
import './index.css';

export interface ExtensionConfig {
    name: string;
    routes?: RouteObject[];
    navItems?: NavItem[];
}

export * from './navigation';
export * from './federation/import-extensions';
export * from './api/api';

export * from './lib/utils';
export * from './components/ui/button';
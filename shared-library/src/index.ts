import type {NavItem} from "./navigation";
import type {RouteObject} from 'react-router';
import './index.css';

export interface ExtensionConfig {
    name: string;
    routes?: RouteObject[];
    navItems?: NavItems;
    translations?: Translations
}

export interface NavItems {
    /**
     * The primary item group is always fixed to the top of the nav-bar and should only contain the most important items.
     */
    primary?: NavItem[],
    /**
     * The secondary item group contains all other items and is scrollable
     */
    secondary?: NavItem[],
}

export interface Translations {
    [language: string]: Record<string, string>;
}

export * from './context';
export * from './navigation';
export * from './federation/import-extensions';

export * from './api/api';
export * from './lib/utils';
export * from './components/ui/button';
export * from './components/ui/input';
export * from './components/ui/popover';

import {ReactNode} from "react";

export interface NavItem {
    name: string | (() => string);
    url?: string;
    children?: NavItem[];
    icon?: ReactNode,
    suffixIcon?: ReactNode,
}
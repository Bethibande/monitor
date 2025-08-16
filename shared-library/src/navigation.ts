import type {ReactNode} from "react";

export interface NavItem {
    name: string;
    url?: string;
    children?: NavItem[];
    icon?: ReactNode,
    suffixIcon?: ReactNode,
}
import type {ExtensionConfig} from '@monitor/shared-library';
import {lazy} from "react";
import i18next from "i18next";
import {primary, secondary} from "./nav-config.tsx";
import './extension.css';

const name = "console";

export function translate(key: string): string {
    return i18next.t(key, {ns: name})
}

const config: ExtensionConfig = {
    name: name,
    routes: [{
        path: "/overview",
        Component: lazy(() => import("./views/overview/Overview"))
    }],
    navItems: {
        primary: primary,
        secondary: secondary
    },
    translations: {
        "en": {
            "nav.overview": "Overview",
            "nav.nodes": "Nodes",
            "nav.namespaces": "Namespaces",
            "nav.workloads": "Workloads",
            "nav.workloads.pods": "Pods",
            "nav.workloads.deployments": "Deployments",
            "nav.workloads.statefulSets": "StatefulSets",
            "nav.workloads.daemonSets": "DaemonSets",
            "nav.workloads.jobs": "Jobs",
            "nav.workloads.cronJobs": "CronJobs",
            "nav.networking": "Networking",
            "nav.networking.services": "Services",
            "nav.networking.ingresses": "Ingresses",
            "nav.networking.networkPolicies": "NetworkPolicies",
            "nav.storage": "Storage",
            "nav.storage.persistentVolumeClaims": "PersistentVolumeClaims",
            "nav.storage.configMaps": "ConfigMaps",
            "nav.storage.secrets": "Secrets",
            "nav.storage.storageClasses": "StorageClasses",
            "nav.security": "Security",
            "nav.security.users": "Users",
            "nav.security.roles": "Roles",
            "nav.security.roleBindings": "RoleBindings",
            "nav.security.serviceAccounts": "ServiceAccounts",
            "nav.security.clusterRoles": "ClusterRoles",
            "nav.security.clusterRoleBindings": "ClusterRoleBindings",
            "nav.CRDs": "CRDs",
            "nav.settings": "Settings",
        }
    }
}

export default config;

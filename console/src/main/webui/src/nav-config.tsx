import type {NavItem} from "@monitor/shared-library";
import {Activity, Diagram2, Folder, Gear, Grid, HddStack, Shield, Tag, Wifi} from "react-bootstrap-icons";
import {translate} from "./extension-config.tsx";

export const primary: NavItem[] = [
    {
        name: () => translate("nav.overview"),
        icon: <Activity/>,
        url: "/overview"
    },
    {
        name: () => translate("nav.nodes"),
        icon: <Diagram2/>,
        url: "/resources/nodes"
    },
    {
        name: () => translate("nav.namespaces"),
        icon: <Tag/>,
        url: "/resources/namespaces"
    }
]
export const secondary: NavItem[] = [
    {
        name: () => translate("nav.workloads"),
        icon: <Grid/>,
        children: [
            {name: () => translate("nav.workloads.pods")},
            {name: () => translate("nav.workloads.deployments")},
            {name: () => translate("nav.workloads.statefulSets")},
            {name: () => translate("nav.workloads.daemonSets")},
            {name: () => translate("nav.workloads.jobs")},
            {name: () => translate("nav.workloads.cronJobs")},
        ]
    },
    {
        name: () => translate("nav.networking"),
        icon: <Wifi/>,
        children: [
            {name: () => translate("nav.networking.services")},
            {name: () => translate("nav.networking.ingresses")},
            {name: () => translate("nav.networking.networkPolicies")},
        ]
    },
    {
        name: () => translate("nav.storage"),
        icon: <HddStack/>,
        children: [
            {name: () => translate("nav.storage.persistentVolumeClaims")},
            {name: () => translate("nav.storage.configMaps")},
            {name: () => translate("nav.storage.secrets")},
            {name: () => translate("nav.storage.storageClasses")},
        ]
    },
    {
        name: () => translate("nav.security"),
        icon: <Shield/>,
        children: [
            {name: () => translate("nav.security.users")},
            {name: () => translate("nav.security.roles")},
            {name: () => translate("nav.security.roleBindings")},
            {name: () => translate("nav.security.serviceAccounts")},
            {name: () => translate("nav.security.clusterRoles")},
            {name: () => translate("nav.security.clusterRoleBindings")},
        ]
    },
    {
        name: () => translate("nav.CRDs"),
        icon: <Folder/>
    },
    {
        name: () => translate("nav.settings"),
        icon: <Gear/>,
        url: "/administration"
    }
]
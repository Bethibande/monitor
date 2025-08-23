import {cn, type NavItem, useExtensions} from "@monitor/shared-library";
import {Activity, ChevronDown, Diagram2, Tag} from "react-bootstrap-icons";
import {NavLink} from "react-router";
import {type ReactNode, useState} from "react";
import i18next from "i18next";

type NavText = string | (() => string);

function renderText(name: NavText): string {
    if (typeof name === "string") {
        return name;
    } else {
        return name();
    }
}

function BasicItemWithText(props: {
    icon?: ReactNode,
    text: NavText,
    suffixIcon?: ReactNode,
    focusable?: boolean,
    ariaExpanded?: boolean,
    onClick?: () => void
}) {
    return (
        <BasicItem focusable={props.focusable} onClick={props.onClick} ariaExpanded={props.ariaExpanded}>
            <span className={"flex flex-row gap-2 items-center font-semibold text-white"}>
                {props.icon}
                {renderText(props.text)}

                {props.suffixIcon && <div className={"pr-0 ml-auto"}>
                    {props.suffixIcon}
                </div>}
            </span>
        </BasicItem>
    )
}

function BasicItem(props: {
    children: ReactNode,
    focusable?: boolean,
    onClick?: () => void,
    ariaExpanded?: boolean,
}) {
    return (
        <div
            aria-expanded={props.ariaExpanded}
            onClick={props.onClick}
            onKeyDown={e => {
                if (!props.onClick) {
                    return;
                }
                if (e.key === "Enter" || e.key === " ") {
                    e.preventDefault();
                    props.onClick();
                }
            }}
            className={"cursor-pointer group-hover:underline p-2 px-4 rounded-md group-[.active]:bg-white/20 group-[.active]:outline hover:bg-white/20 hover:outline outline-white/40 nav-item"}
            tabIndex={props.focusable ? 0 : -1}>
            {props.children}
        </div>
    )
}

function GroupedItem(props: { icon?: ReactNode, text: NavText, children: NavItem[] }) {
    const [open, setOpen] = useState(false)

    return (
        <div>
            <BasicItemWithText text={props.text}
                               icon={props.icon}
                               focusable={true}
                               ariaExpanded={open}
                               onClick={() => setOpen(!open)}
                               suffixIcon={<ChevronDown
                                   className={cn("transition-transform", open ? "rotate-180" : "rotate-0")}/>}/>
            <div className={cn("overflow-hidden", open ? "h-full p-1" : "h-0", "pl-6 pr-1")}>
                {props.children.map(item => renderItem(item, open))}
            </div>
        </div>
    )
}

function renderItem(item: NavItem, focusable?: boolean) {
    let component;
    if (item.children) {
        component = <GroupedItem key={item.url || renderText(item.name)}
                                 text={item.name}
                                 icon={item.icon}
                                 children={item.children}/>
    } else {
        component = <BasicItemWithText key={item.url || renderText(item.name)}
                                       text={item.name}
                                       icon={item.icon}
                                       focusable={!item.url && focusable}
                                       suffixIcon={item.suffixIcon}/>
    }

    if (item.url) {
        return (<NavLink key={item.url || renderText(item.name)}
                         tabIndex={focusable ? 0 : -1}
                         className={({isActive}) => cn("group nav-link rounded-md", isActive && "active")}
                         to={item.url}>
            {component}
        </NavLink>)
    }
    return component;
}

function itemsToTree(items: NavItem[]) {
    return items.map(item => renderItem(item, true))
}

export default function SideNav() {
    const primary: NavItem[] = [
        {
            name: i18next.t("test"),
            icon: <Activity/>,
            url: "/overview"
        },
        {
            name: "Nodes",
            icon: <Diagram2/>,
            url: "/resources/nodes"
        },
        {
            name: "Namespaces",
            icon: <Tag/>,
            url: "/resources/namespaces"
        }
    ]
    /*const secondary: NavItem[] = [
        {
            name: "Workloads",
            icon: <Grid/>,
            children: [
                {name: "Pods"},
                {name: "Deployments"},
                {name: "StatefulSets"},
                {name: "DaemonSets"},
                {name: "Jobs"},
                {name: "CronJobs"},
            ]
        },
        {
            name: "Networking",
            icon: <Wifi/>,
            children: [
                {name: "Services"},
                {name: "Ingresses"},
                {name: "NetworkPolicies"},
            ]
        },
        {
            name: "Storage",
            icon: <HddStack/>,
            children: [
                {name: "PersistentVolumeClaims"},
                {name: "ConfigMaps"},
                {name: "Secrets"},
                {name: "StorageClasses"},
            ]
        },
        {
            name: "Security",
            icon: <Shield/>,
            children: [
                {name: "Users"},
                {name: "Roles"},
                {name: "RoleBindings"},
                {name: "ServiceAccounts"},
                {name: "ClusterRoles"},
                {name: "ClusterRoleBindings"},
            ]
        },
        {
            name: "CRDs",
            icon: <Folder/>
        },
        {
            name: "Settings",
            icon: <Gear/>,
            url: "/administration"
        }
    ]*/

    //const {context, setContext} = useClusterContext()

    const {extensions} = useExtensions();
    const secondary: NavItem[] = extensions.flatMap(ext => ext.navItems || []);

    return (
        <div className={"h-full rounded-r-xl bg-blue-500 flex flex-col items-center gap-2 text-white side-nav"}>
            {/*<div
                className={"flex flex-row gap-2 w-full items-center p-4 scroll-stable overflow-y-auto min-h-fit px-4"}>
                <ContextSelect context={context || DefaultClusterContext} setContext={setContext}/>
            </div>*/}
            <div className={"p-2 px-4 flex flex-col gap-1 w-full scroll-stable min-h-fit overflow-y-auto"}>
                {itemsToTree(primary)}
            </div>
            <div className={"flex-grow overflow-y-auto flex flex-col gap-1 p-2 px-4 w-full scroll-stable"}>
                {itemsToTree(secondary)}
            </div>
            <div className={"flex flex-row gap-2 items-center"}>
                <p>Footer</p>
            </div>
        </div>
    )
}
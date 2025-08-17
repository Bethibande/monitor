import type {ExtensionConfig} from '@monitor/shared-library';
import {lazy} from "react";
import {Gear} from "react-bootstrap-icons";

const config: ExtensionConfig = {
    name: "counter-button-extension",
    routes: [{
        path: "/my-view",
        Component: lazy(() => import("./views/MyView"))
    }],
    navItems: [
        {
            icon: (<Gear/>),
            name: "My extension",
            children: [{
                url: "/my-view",
                name: "My view"
            }]
        }
    ]
}

export default config;

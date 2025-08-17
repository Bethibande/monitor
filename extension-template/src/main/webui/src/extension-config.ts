import type {ExtensionConfig} from '@monitor/shared-library';
import {lazy} from "react";

const config: ExtensionConfig = {
    name: "counter-button-extension",
    routes: [{
        path: "/my-view",
        Component: lazy(() => import("./views/MyView"))
    }]
}

export default config;
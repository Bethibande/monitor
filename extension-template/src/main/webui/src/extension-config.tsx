import type {ExtensionConfig} from '@monitor/shared-library';
import {lazy} from "react";
import {Gear} from "react-bootstrap-icons";
import i18next from "i18next";

const name = "extension-template";

export function translate(key: string): string {
    return i18next.t(key, {ns: name})
}

const config: ExtensionConfig = {
    name: name,
    routes: [{
        path: "/my-view",
        Component: lazy(() => import("./views/MyView"))
    }],
    navItems: [
        {
            icon: (<Gear/>),
            name: () => i18next.t("navbar.extension", {ns: name}),
            children: [{
                url: "/my-view",
                name: () => i18next.t("navbar.extension.view", {ns: name})
            }]
        }
    ],
    translations: {
        "en": {
            "navbar.extension": "My extension",
            "navbar.extension.view": "My view test",
            "view.title": "My extension view.",
            "view.helloWorld": "Hello World!",
            "view.loadedExtensions": "Loaded extensions:",
        }
    }
}

export default config;

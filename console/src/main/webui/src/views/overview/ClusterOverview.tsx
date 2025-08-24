import {translate} from "../../extension-config.tsx";
import {Columns} from "react-bootstrap-icons";

export const translations = {
    "en": {
        "view.overview.cluster.title": "Cluster Overview"
    }
}

export function ClusterOverview() {
    return (
        <div className={"w-full h-full flex flex-col gap-2"}>
            <h2>{translate("view.overview.cluster.title")}</h2>
            <div className={"flex flex-row gap-5 w-full h-full"}>
                <div className={"w-full h-full flex items-center justify-center"}>
                    <div className={"self-center justify-self-center"}>
                        <h3 className={"flex text-xl font-bold items-center gap-2"}><Columns/> No Widgets found.</h3>
                        <p>Install some extensions to fill this empty space</p>
                    </div>
                </div>
                <div className={"w-96 h-full flex flex-col gap-3"}>
                    <div className={"bg-slate-100 rounded-sm p-3 grid grid-cols-2"}>
                        <div className={"flex flex-row gap-2"}>
                            <p>5</p>
                            <p>Nodes</p>
                        </div>
                        <div className={"flex flex-row gap-2"}>
                            <p>110</p>
                            <p>Pods</p>
                        </div>
                        <div className={"flex flex-row gap-2"}>
                            <p>16</p>
                            <p>Namespaces</p>
                        </div>
                    </div>
                    <div className={"flex flex-col gap-3"}>
                        <div className={"bg-slate-100 rounded-sm p-3"}>
                            <div className={"w-full flex flex-row justify-between"}>
                                <p>Node-3</p>
                                <span className={"bg-red-500 text-white rounded-sm px-1 font-semibold"}>Offline</span>
                            </div>
                            <p>123.63.126.73 / fde0:0cdf:da35::23</p>
                        </div>
                        <div className={"bg-slate-100 rounded-sm p-3"}>
                            <div className={"w-full flex flex-row justify-between"}>
                                <p>Node-2</p>
                                <span
                                    className={"bg-yellow-500 text-white rounded-sm px-1 font-semibold"}>Unknown</span>
                            </div>
                            <p>123.63.126.73 / fde0:0cdf:da35::23</p>
                        </div>
                        <div className={"bg-slate-100 rounded-sm p-3"}>
                            <div className={"w-full flex flex-row justify-between"}>
                                <p>Master-1</p>
                                <span className={"bg-green-500 text-white rounded-sm px-1 font-semibold"}>Online</span>
                            </div>
                            <p>123.63.126.73 / fde0:0cdf:da35::23</p>
                        </div>
                        <div className={"bg-slate-100 rounded-sm p-3"}>
                            <div className={"w-full flex flex-row justify-between"}>
                                <p>Node-1</p>
                                <span className={"bg-green-500 text-white rounded-sm px-1 font-semibold"}>Online</span>
                            </div>
                            <p>123.63.126.73 / fde0:0cdf:da35::23</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    )
}
import {ContextScope, useClusterContext} from "@monitor/shared-library";
import ClusterOverview from "./ClusterOverview.tsx";
import NodeOverview from "./NodeOverview.tsx";
import NamespaceOverview from "./NamespaceOverview.tsx";

export default function Overview() {
    const {context} = useClusterContext()

    if (context.scope === ContextScope.CLUSTER) {
        return (<ClusterOverview/>)
    } else if (context.scope === ContextScope.NODE) {
        return (<NodeOverview/>)
    } else {
        return (<NamespaceOverview/>)
    }
}
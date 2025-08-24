import {
    type ClusterContext,
    ContextScope,
    Input,
    Popover,
    PopoverContent,
    PopoverTrigger,
    useClusterContext
} from "@monitor/shared-library";
import {type ReactNode, useEffect, useState} from "react";
import {apiClient} from "../main.tsx";
import {ChevronExpand} from "react-bootstrap-icons";
import {capitalize} from "../utils/strings.ts";
import i18next from "i18next";

function ContextChip(props: {ctx: ClusterContext, onSelect: (ctx: ClusterContext) => void}) {
    const {ctx, onSelect} = props;

    return (
        <div className={"p-2 rounded-sm hover:bg-gray-200 transition-colors cursor-pointer flex flex-col"}
             onClick={() => onSelect(ctx)}>
            <div className={"flex flex-row justify-between items-center"}>
                <p className={"m-0"}>{ctx.id}</p>
                <span className={"text-xs rounded-sm p-1 bg-slate-100"}>{ctx.scope}</span>
            </div>
        </div>
    )
}

function renderFirstN<T>(items: T[], maxNum: number, render: (value: T) => ReactNode) {
    return (
        <div className={"flex flex-col gap-1"}>
            {items.slice(0, maxNum).map((item) => (render(item)))}
            {items.length > maxNum && (<p>{i18next.t("nav.context.more", {num: items.length - maxNum})}</p>)}
        </div>
    )
}

function filterScopeAndSearch(items: ClusterContext[], scope: ContextScope, search: string): ClusterContext[] {
    return items.filter((item) => item.scope === scope)
        .filter(item => item.id.includes(search));
}

export default function ContextSelect() {
    const {context, setContext} = useClusterContext();
    const [contexts, setContexts] = useState<ClusterContext[]>([])

    const [open, setOpen] = useState(false);
    const [search, setSearch] = useState("");

    useEffect(() => {
        apiClient.fetch<ClusterContext[]>("/context").then(json => setContexts(json))
    })

    const clusters = filterScopeAndSearch(contexts, ContextScope.CLUSTER, search)
    const nodes = filterScopeAndSearch(contexts, ContextScope.NODE, search)
    const namespaces = filterScopeAndSearch(contexts, ContextScope.NAMESPACE, search)

    function onSelect(ctx: ClusterContext) {
        setOpen(false)
        setContext(ctx)
    }

    return (
        <Popover open={open} onOpenChange={setOpen}>
            <PopoverTrigger className={"w-full min-w-56"} asChild>
                <div
                    className={"bg-white/20 outline outline-white/40 w-full rounded-lg px-4 py-3 text-sm flex items-center justify-between select-none cursor-pointer"}
                    tabIndex={0}
                    onKeyDown={e => (e.key === "Enter" || e.key === " ") && setOpen(!open)}>
                    <div className={"flex flex-col"}>
                        <span className={"font-semibold"}>{context.id}</span>
                        <span className={"text-xs"}>{capitalize(context.scope.toString())}</span>
                    </div>
                    <div>
                        <ChevronExpand/>
                    </div>
                </div>
            </PopoverTrigger>
            <PopoverContent align={"start"}>
                <div className={"flex flex-col gap-2"}>
                    <div>
                        <Input placeholder={i18next.t("nav.context.search")} onChange={e => setSearch(e.target.value)}/>
                    </div>
                    <div className={"flex flex-col gap-2"}>
                        {clusters.length > 0 && (
                            <div>
                                <p className={"font-semibold"}>Clusters</p>
                                {renderFirstN(clusters, 5, (ctx) => <ContextChip ctx={ctx} onSelect={onSelect} key={ctx.scope + ctx.id}/>)}
                            </div>
                        )}
                        {nodes.length > 0 && (
                            <div>
                                <p className={"font-semibold"}>Nodes</p>
                                {renderFirstN(nodes, 5, (ctx) => <ContextChip ctx={ctx} onSelect={onSelect} key={ctx.scope + ctx.id}/>)}
                            </div>
                        )}
                        {namespaces.length > 0 && (
                            <div>
                                <p className={"font-semibold"}>Namespaces</p>
                                {renderFirstN(namespaces, 5, (ctx) => <ContextChip ctx={ctx} onSelect={onSelect} key={ctx.scope + ctx.id}/>)}
                            </div>
                        )}
                    </div>
                </div>
            </PopoverContent>
        </Popover>
    )
}
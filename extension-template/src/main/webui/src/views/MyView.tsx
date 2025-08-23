import {useExtensions} from "@monitor/shared-library";
import {translate} from "../extension-config.tsx";

export default function MyView() {
    const {extensions} = useExtensions();

    return <div>
        <h2>{translate("view.title")}</h2>
        <p>{translate("view.helloWorld")}</p>

        <h3>{translate("view.loadedExtensions")}</h3>
        <div>
            {extensions.map(ext => <div key={ext.name}>&bull;&nbsp;{ext.name}</div>)}
        </div>
    </div>
}
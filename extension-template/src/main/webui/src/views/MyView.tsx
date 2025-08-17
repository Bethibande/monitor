import {useExtensions} from "@monitor/shared-library";

export default function MyView() {
    const {extensions} = useExtensions();

    return <div>
        <h2>My extension view.</h2>
        <p>Hello World!</p>

        <h3>Loaded extensions:</h3>
        <div>
            {extensions.map(ext => <div key={ext.name}>{ext.name}</div>)}
        </div>
    </div>
}
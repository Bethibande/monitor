import {Outlet} from "react-router";
import SideNav from "../navigation/SideNav.tsx";


export default function AppLayout() {
    return (
        <div className={"flex flex-row w-full h-full"}>
            <div>
                <SideNav/>
            </div>
            <div className={"p-4"}>
                <Outlet/>
            </div>
        </div>
    )
}
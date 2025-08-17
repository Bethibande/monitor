import {NavLink, Outlet} from "react-router";

export default function AppLayout() {
    return (
        <div>
            <p>Some layout</p>
            <NavLink to={"/my-view"}>Link</NavLink>
            <Outlet/>
        </div>
    )
}
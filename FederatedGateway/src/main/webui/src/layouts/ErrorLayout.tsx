import {NavLink, useNavigate, useRouteError} from "react-router";
import {Button, useExtensions} from "@monitor/shared-library";
import {useEffect} from "react";
import {HouseFill} from "react-bootstrap-icons";

export default function ErrorLayout() {
    const error: any = useRouteError()

    const {extensions} = useExtensions();
    const navigate = useNavigate();

    useEffect(() => {
        if (extensions.length > 0) {
            navigate(window.location.pathname, {replace: true})
        }
    }, [extensions]);

    return (
        <div className={"w-full h-full flex flex-col items-center justify-center"}>
            <h1>Error {error.status}</h1>
            <p>{error.statusText}</p>
            <div className={"mt-4"}>
                <NavLink to={"/"}>
                    <Button variant={"link"}><HouseFill/>&nbsp;Go to home</Button>
                </NavLink>
            </div>
        </div>
    )
}
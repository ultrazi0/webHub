import { useParams } from "react-router-dom";
import ImageRow from "./ControlPanelComponents/ImageRow";
import ControlRow from "./ControlPanelComponents/ControlRow";
import { onChangeCallbacks, XBOXLayout } from "../hooks/useGamepad";
import Gamepad from "../components/Gamepad";

export default function ControlPanel() {
    const { robotId } = useParams();

    const callbacks: onChangeCallbacks<typeof XBOXLayout> = {
        A: pressed => console.log("A pressed: " + pressed),
        "-RightStickY": value => console.log("Right stick Y: " + value),
    };

    return (
        <>
            <h1>
                <span style={{ display: "flex", justifyContent: "center", marginTop: "1rem" }}>
                    Control Panel
                </span>
            </h1>
            <div className="container text-center">
                <ImageRow robotId={robotId} />
                <ControlRow robotId={robotId} />
            </div>
            <Gamepad layout={XBOXLayout} callbacks={callbacks} />
        </>
    );
}

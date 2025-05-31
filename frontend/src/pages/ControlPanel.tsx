import { useParams } from "react-router-dom";
import ImageRow from "./ControlPanelComponents/ImageRow";
import ControlRow from "./ControlPanelComponents/ControlRow";

export default function ControlPanel() {
    const { robotId } = useParams();

    return (
        <>
            <h1>
                <span style={{ display: "flex", justifyContent: "center", marginTop: "1rem" }}>
                    Control Panel
                </span>
            </h1>
            <div className="container text-center">
                <ImageRow robotId={robotId ?? null} />
                <ControlRow robotId={robotId ?? null} />
            </div>
        </>
    );
}

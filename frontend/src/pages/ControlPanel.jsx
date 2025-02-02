import { useParams } from "react-router-dom";
import ControlRow from "./ControlPanelComponents/ControlRow";
import ImageRow from "./ControlPanelComponents/ImageRow";

export default function ControlPanel() {
    const { robotId } = useParams()

    return (
        <>
            <h1>
                Control Panel
            </h1>
            <div className="container text-center">
                <ImageRow robotId={robotId} />
                <ControlRow robotId={robotId} />
            </div>
        </>
    )
}

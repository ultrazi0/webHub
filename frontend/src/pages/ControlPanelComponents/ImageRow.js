import { useEffect, useState } from "react";
import CameraStream from "./CameraStream";
import useWebSocket from "react-use-websocket";

export default function ImageRow( {robotId} ) {
    const [image, setImage] = useState("");
    const [lastImage, setLastImage] = useState("");

    const WS_URL = "ws://localhost:8080/api/image/client/" + robotId;

    // Establishing WebSocket connection
    const { lastJsonMessage } = useWebSocket(WS_URL, {
        shouldReconnect: (closeEvent) => false,
        onError: (event) => console.error("Image-WebSocket error observed:", event),
        onOpen: () => console.log("Image-WebSocket connection opened"),
        onClose: (event) => console.log("Image-WebSocket connection closed:", event)
    });

    // If there is a json message, then change image variable
    useEffect(() => {
        if (lastJsonMessage) {
            if (Object.keys(lastJsonMessage).length) {
                if (lastJsonMessage.messageType === "image") {
                    setImage("data:image/jpg;base64, " + lastJsonMessage.image);
                } else if (lastJsonMessage.messageType === "lastImage") {
                    setLastImage("data:image/jpg;base64," + lastJsonMessage.image);
                } else if (lastJsonMessage.messageType === "regularMessage") {
                    console.log("Message from /api/image/topic: " + lastJsonMessage.message);
                }
            }
        }
    }, [lastJsonMessage])

    return (
        <div className="row g-2 my-2">
            <div className="col">
                <CameraStream image={image} altText={"Stream from the robot's camera"} />
            </div>
            <div className="col">
                <CameraStream image={lastImage} altText={"Still with a QR-code"} />
            </div>
        </div>
    );
}

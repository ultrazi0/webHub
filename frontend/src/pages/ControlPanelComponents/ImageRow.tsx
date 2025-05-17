import { useEffect, useState } from "react";
import useWebSocket from "react-use-websocket";
import { Col, Row } from "react-bootstrap";
import CameraStream from "./CameraStream";

// TODO: combine with ControlResponse and align with the BE
type ImageResponse = {
    messageType: "image" | "lastImage" | "regularMessage",
    image?: string,
    message?: string,
    feedback?: string,
}

export default function ImageRow({ robotId }: { robotId: string }) {
    const [image, setImage] = useState<string>("");
    const [lastImage, setLastImage] = useState<string>("");

    const WS_URL = "ws://localhost:8080/api/image/client/" + robotId;

    // Establishing WebSocket connection
    const { lastJsonMessage } = useWebSocket<ImageResponse>(WS_URL, {
        shouldReconnect: () => false,
        onError: (event) => console.error("Image-WebSocket error observed:", event),
        onOpen: () => console.log("Image-WebSocket connection opened"),
        onClose: (event) => console.log("Image-WebSocket connection closed:", event),
    });

    // If there is a JSON message, then change the image variable
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
    }, [ lastJsonMessage ]);

    return (
        <Row className="g-2 my-2">
            <Col>
                <CameraStream image={image} altText="Stream from the robot's camera" />
            </Col>
            <Col>
                <CameraStream image={lastImage} altText="Still with a QR-code" />
            </Col>
        </Row>
    );
}
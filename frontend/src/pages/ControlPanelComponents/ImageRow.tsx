import { useEffect, useState } from "react";
import useWebSocket from "react-use-websocket";
import { Col, Row } from "react-bootstrap";
import CameraStream from "./CameraStream";
import { AimImageMessage, ImageMessage, MessageType, RegularMessage } from "./index";

export default function ImageRow({ robotId }: { robotId: string | null }) {
    const [image, setImage] = useState<string>("");
    const [lastImage, setLastImage] = useState<string>("");

    const WS_URL = robotId ? "ws://localhost:8080/api/image/client/" + robotId : null;

    // Establishing WebSocket connection
    const { lastJsonMessage } = useWebSocket<ImageMessage | AimImageMessage | RegularMessage>(WS_URL, {
        shouldReconnect: () => false,
        onError: (event) => console.error("Image-WebSocket error observed:", event),
        onOpen: () => console.log("Image-WebSocket connection opened"),
        onClose: (event) => console.log("Image-WebSocket connection closed:", event),
    });

    // If there is a JSON message, then change the image variable
    useEffect(() => {
        if (lastJsonMessage) {
            if (Object.keys(lastJsonMessage).length) {
                if (lastJsonMessage.messageType === MessageType.Image) {
                    setImage("data:image/jpg;base64, " + lastJsonMessage.image);
                } else if (lastJsonMessage.messageType === MessageType.AimImage) {
                    setLastImage("data:image/jpg;base64," + lastJsonMessage.image);
                } else if (lastJsonMessage.messageType === MessageType.RegularMessage) {
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
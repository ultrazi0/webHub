import { useEffect, useRef } from "react";
import useWebSocket from "react-use-websocket";
import handleKeyPress from "./handleKeyPress";
import { Col, Row } from "react-bootstrap";
import Controls from "./Controls";

type CommandResponse = {
    messageType: "regularMessage" | "feedback",
    feedback?: string,
    message?: string,
}

export default function ControlRow({ robotId }: { robotId: string }) {
    const feedbackArea = useRef(null); // ref that controls textarea for feedback

    const WS_URL = "ws://localhost:8080/api/command/client/" + robotId;
    const { sendJsonMessage, lastJsonMessage } = useWebSocket<CommandResponse>(WS_URL, {
        shouldReconnect: () => false,
        onError: (event) => console.error("Command-WebSocket error observed:", event),
        onOpen: () => console.log("Command-WebSocket connection opened"),
        onClose: (event) => console.log("Command-WebSocket connection closed:", event),
    });

    // Handle updates to the textarea
    useEffect(() => {
        if (lastJsonMessage) {
            if (Object.keys(lastJsonMessage).length) {
                if ((lastJsonMessage.messageType === "feedback") || (lastJsonMessage.messageType === "regularMessage")) {
                    if (feedbackArea.current) {
                        // Add a message depending on which field is present in the message
                        feedbackArea.current.value += lastJsonMessage.feedback ? lastJsonMessage.feedback + "\n" : "Message: \"" + lastJsonMessage.message + "\"\n";
                        feedbackArea.current.scrollTop = feedbackArea.current.scrollHeight; // Scroll down
                    } else {
                        console.log("Feedback: " + lastJsonMessage.feedback);
                        console.log("Message: " + lastJsonMessage.message);
                    }
                }
            }
        }
    }, [ lastJsonMessage ]);

    useEffect(() => {
        const onKeyPress = (event: KeyboardEvent) => handleKeyPress(event, sendJsonMessage);

        window.addEventListener("keydown", onKeyPress);
        window.addEventListener("keyup", onKeyPress);

        return () => {
            window.removeEventListener("keydown", onKeyPress);
            window.removeEventListener("keyup", onKeyPress);
        };
    }, [ sendJsonMessage ]);

    return (
        <Row className="g-2 my-2">
            <Col>
                <textarea className="form-control" ref={feedbackArea} readOnly={true} rows={10} />
            </Col>
            <Col>
                <Controls sendCommand={sendJsonMessage} />
            </Col>
        </Row>
    );
}
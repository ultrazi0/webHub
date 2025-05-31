import { useEffect, useMemo, useRef } from "react";
import useWebSocket from "react-use-websocket";
import handleKeyPress from "./handleKeyPress";
import { Col, Row } from "react-bootstrap";
import Controls from "./Controls";
import { getCommands } from "./handleGamepadEvents";
import { onChangeCallbacks, XBOXLayout } from "../../hooks/useGamepad";
import Gamepad from "../../components/Gamepad";
import { FeedbackMessage, MessageType, RegularMessage } from "./index";

export default function ControlRow({ robotId }: { robotId: string }) {
    const feedbackArea = useRef(null); // ref that controls textarea for feedback

    const WS_URL = "ws://localhost:8080/api/command/client/" + robotId;
    const { sendJsonMessage, lastJsonMessage } = useWebSocket<RegularMessage | FeedbackMessage>(WS_URL, {
        shouldReconnect: () => false,
        onError: (event) => console.error("Command-WebSocket error observed:", event),
        onOpen: () => console.log("Command-WebSocket connection opened"),
        onClose: (event) => console.log("Command-WebSocket connection closed:", event),
    });

    const gamepadOnChangeCallbacks = useMemo((): onChangeCallbacks<typeof XBOXLayout> => {
        const gamepadCommands = getCommands(sendJsonMessage);

       return {
           B: pressed => { if (pressed) gamepadCommands.stop(); },
           LB: pressed => { if (pressed) gamepadCommands.aim(); },
           X: pressed => { if (pressed) gamepadCommands.shoot(); },
           onAxesChange: axes => {
               const moveSpeed = axes.LeftStickX ?? 0;
               const moveTurn = -(axes["-LeftStickY"] ?? 0);
               const turretSpeed = axes.RightStickX ?? 0;
               const turretTurn = -(axes["-RightStickY"] ?? 0);

               const shouldMove = moveSpeed !== 0 || moveTurn !== 0;
               const shouldMoveTurret = turretSpeed !== 0 || turretTurn !== 0;
               if (shouldMove && shouldMoveTurret) {
                   gamepadCommands.moveAndTurret([ moveSpeed, moveTurn ], [ turretSpeed, turretTurn ]);
               } else if (shouldMove) {
                   gamepadCommands.move(moveSpeed, moveTurn);
               } else if (shouldMoveTurret) {
                   gamepadCommands.turret(turretSpeed, turretTurn);
               } else {
                   gamepadCommands.stop();
               }
           },
        };
    }, [ sendJsonMessage ]);

    // Handle updates to the textarea
    useEffect(() => {
        if (lastJsonMessage) {
            if (Object.keys(lastJsonMessage).length) {
                let message: string | null = null;
                if (lastJsonMessage.messageType === MessageType.Feedback) {
                    message = lastJsonMessage.feedback + "\n";
                } else if (lastJsonMessage.messageType === MessageType.RegularMessage) {
                    message = "Message: \"" + lastJsonMessage.message + "\"\n";
                }

                if (message !== null && feedbackArea.current) {
                    feedbackArea.current.value += message;
                    feedbackArea.current.scrollTop = feedbackArea.current.scrollHeight; // Scroll down
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
        <>
            <Row className="g-2 my-2">
                <Col>
                    <textarea className="form-control" ref={feedbackArea} readOnly={true} rows={10} />
                </Col>
                <Col>
                    <Controls sendCommand={sendJsonMessage} />
                </Col>
            </Row>
            <Gamepad layout={XBOXLayout} callbacks={gamepadOnChangeCallbacks} />
        </>
    );
}
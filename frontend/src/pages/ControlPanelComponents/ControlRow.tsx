import { useEffect, useMemo, useRef } from "react";
import useWebSocket from "react-use-websocket";
import handleKeyPress from "./handleKeyPress";
import { Col, Row } from "react-bootstrap";
import Controls from "./Controls";
import { getCommands } from "./handleGamepadEvents";
import { onChangeCallbacks, XBOXLayout } from "../../hooks/useGamepad";
import Gamepad from "../../components/Gamepad";
import { FeedbackMessage, MessageType, RegularMessage } from "./index";

export default function ControlRow({ robotId }: { robotId: string | null }) {
    const feedbackArea = useRef<HTMLTextAreaElement | null>(null); // ref that controls textarea for feedback

    const WS_URL = robotId ? `ws://${window.location.host}/api/command/client/${robotId}` : null;

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
           DPadLeft: pressed => { if (pressed) gamepadCommands.turret(0, -1); },
           DPadRight: pressed => { if (pressed) gamepadCommands.turret(0, 1); },
           DPadUp: pressed => { if (pressed) gamepadCommands.turret(1, 0); },
           DPadDown: pressed => { if (pressed) gamepadCommands.turret(-1, 0); },
           onAxesChange: axes => {
               const moveSpeed = -(axes["-LeftStickY"] ?? 0);
               const moveTurn = axes.LeftStickX ?? 0;
               const turretSpeed = -(axes["-RightStickY"] ?? 0);
               const turretTurn = axes.RightStickX ?? 0;

               if (moveSpeed !== 0 || moveTurn !== 0 || turretSpeed !== 0 || turretTurn !== 0) {
                   gamepadCommands.moveAndTurret([ moveSpeed, moveTurn ], [ turretSpeed, turretTurn ]);
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
                    <Controls robotId={robotId} sendCommand={sendJsonMessage} />
                </Col>
            </Row>
            <Row className="g-2 my-2">
                <Col />
                <Col>
                    <Gamepad layout={XBOXLayout} callbacks={gamepadOnChangeCallbacks} />
                </Col>
            </Row>
        </>
    );
}
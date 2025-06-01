import { Col, FormSelect, Row } from "react-bootstrap";
import useGamepad, { onChangeCallbacks, Layout } from "../hooks/useGamepad";
import { Circle } from "lucide-react";

export default function Gamepad<T extends Layout>({ layout, callbacks }: { layout: T, callbacks: onChangeCallbacks<T>}) {

    const [ gamepad ] = useGamepad<T>(layout, callbacks);

    return (
        <>
            <Row>
                <Col className="col-sm-4 align-self-center">
                    {gamepad?.connected ? (
                        <span><Circle style={{ verticalAlign: "-.125em" }} color="#A59595" fill="#00A500" size={16} /> Connected</span>
                    ) : (
                        <span><Circle style={{ verticalAlign: "-.125em" }} color="#A59595" fill="#A41313" size={16} /> Disconnected</span>
                    )}
                </Col>
                <Col className="col-sm-8">
                    <FormSelect disabled={!gamepad?.connected} aria-label="Mapping select"><option>XBOX</option></FormSelect>
                </Col>
            </Row>
        </>
    );
}
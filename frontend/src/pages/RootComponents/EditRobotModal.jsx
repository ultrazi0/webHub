import { Button, FormControl, FormGroup, FormLabel, FormText, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle } from "react-bootstrap";
import { useEffect, useState } from "react";
import CsrfHiddenInput from "../../components/CsrfHiddenInput";

export default function EditRobotModal({ fetcher, robotId, setRobotId, csrfToken }) {
    const [robot, setRobot] = useState(null);

    const handleCloseModal = () => {
        setRobotId(null);
        setRobot(null);
        fetcher.reset();
    };

    useEffect(() => {
        let ignore = false;
        if (robotId != null) {
            fetch("/api/robots/" + robotId)
            .then(response => {
                if (response.ok) {
                    return response.json();
                }
                throw new Error("No robot with this ID or bad request");
            })
            .then(json => {
                if (!ignore) {
                    setRobot(json);
                }
            })
            .catch(error => {
                console.log(error);
            });
        } else {
            if (robot != null) {
                setRobot(null);
            }
        }

        return () => {
            ignore = true;
        };
    }, [robot, robotId]);

    return (
        <Modal show={robot != null} onHide={handleCloseModal} backdrop="static" keyboard={true}>
            <ModalHeader closeButton>
                <ModalTitle>Edit robot</ModalTitle>
            </ModalHeader>
            <fetcher.Form method="put" action={"/edit/" + robotId}>
                <ModalBody>
                    <FormGroup className="mb-3" controlId="formName">
                        <FormLabel>Robot name</FormLabel>
                        <FormControl type="text" placeholder="Enter robot name" name="name" defaultValue={robot ? robot.name : ""} />
                        {fetcher.data === false && <FormText className="text-danger-emphasis">This name is already taken</FormText>}
                        {csrfToken && <CsrfHiddenInput csrfToken={csrfToken} />}
                    </FormGroup>
                </ModalBody>
                <ModalFooter>
                    <Button variant="secondary" onClick={handleCloseModal}>Close</Button>
                    <Button type="submit" variant="primary">Save</Button>
                </ModalFooter>
            </fetcher.Form>
        </Modal>
    );
} 
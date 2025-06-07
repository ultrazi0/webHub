import {
    FormGroup,
    Modal,
    ModalBody,
    ModalHeader,
    ModalTitle,
    Form,
    FormLabel,
    FormControl, ModalFooter, Button,
} from "react-bootstrap";
import { Robot } from "../../types";

type ShareRobotModalProps = {
    showModal: boolean,
    robot: Robot | null,
}

export default function ShareRobotModal({ showModal, robot }: ShareRobotModalProps) {

    const handleCloseModal = () => {
        console.log(12);
    };

    return (
        <Modal show={showModal} onHide={handleCloseModal} backdrop="static" keyboard>
            <ModalHeader closeButton>
                <ModalTitle>Share {robot?.name} with</ModalTitle>
            </ModalHeader>
            <Form>
                <ModalBody>
                    <FormGroup controlId="shareRobotFormUsername">
                        <FormLabel>Username</FormLabel>
                        <FormControl
                            type="text"
                            name="username"
                            placeholder="Enter the userename of the user you want to share this robot with"
                        />
                    </FormGroup>
                </ModalBody>
                <ModalFooter>
                    <Button variant="secondary" onClick={handleCloseModal}>Close</Button>
                    <Button variant="primary">Share</Button>
                </ModalFooter>
            </Form>
        </Modal>
    );
}
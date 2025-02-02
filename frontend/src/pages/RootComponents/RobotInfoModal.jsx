import { Button, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle } from "react-bootstrap";

export default function RobotInfoModal({ robot, setRobot }) {

    const handleCloseModal = () => setRobot(null);

    return (
        <Modal show={robot} onHide={handleCloseModal}>
            <ModalHeader closeButton>
                <ModalTitle>Robot Info</ModalTitle>
            </ModalHeader>
            <ModalBody>
                <p>
                    <span className="text-info">
                        In order to get access to your robot, it has to provide its <b>ID</b> and its <b>password</b>.
                        This means that you should copy the data from here and paste it into the designated fields.
                    </span>
                    <br />
                    <br />
                    <b>Name:</b> <span>{robot?.name}</span>
                    <br />
                    <b>ID:</b> <span>{robot?.id}</span>
                    <br />
                    <b>Password:</b> <span>{robot?.password}</span>
                    <br />
                    <b>Owned by:</b> <span>{robot?.ownerName}</span>
                    <br />
                    <b>Robot was created at:</b> <span>{new Date(robot?.createdAt).toLocaleString()}</span>
                    <br />
                    <i>This robot is currently {robot?.online ? <span className="text-success">online</span> : <span className="text-danger">offline</span>}</i>
                </p>
            </ModalBody>
            <ModalFooter>
                <Button variant="secondary" onClick={handleCloseModal}>Close</Button>
            </ModalFooter>
        </Modal>
    );
}
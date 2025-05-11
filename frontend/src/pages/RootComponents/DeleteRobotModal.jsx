import { Button, FormGroup, FormText, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle } from "react-bootstrap";
import CsrfHiddenInput from "../../components/CsrfHiddenInput";

export default function DeleteRobotModal({ fetcher, robotId, setRobotId, csrfToken }) {

    const handleCloseModal = () => {
        setRobotId(null);
        fetcher.reset();
    };

    return (
        <Modal show={robotId} onHide={handleCloseModal}>
            <ModalHeader closeButton>
                <ModalTitle>Delete robot</ModalTitle>
            </ModalHeader>
            <fetcher.Form method="delete" action={"/delete/" + robotId}>
                <ModalBody>
                    <FormGroup className="mb-3" controlId="formText">
                        <FormText>Are you sure you want to delete this robot?</FormText>
                        {csrfToken && <CsrfHiddenInput csrfToken={csrfToken} />}
                    </FormGroup>
                </ModalBody>
                <ModalFooter>
                    <Button variant="secondary" onClick={handleCloseModal}>Close</Button>
                    <Button type="submit" variant="danger">Delete</Button>
                </ModalFooter>
            </fetcher.Form>
        </Modal>
    );
}
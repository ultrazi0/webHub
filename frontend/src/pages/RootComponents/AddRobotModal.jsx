import { Modal, ModalHeader, ModalTitle, ModalBody, FormGroup, FormLabel, FormControl, ModalFooter, Button, FormText } from "react-bootstrap";
import CsrfHiddenInput from "../../components/CsrfHiddenInput";

export default function AddRobotModal({ fetcher, showModal, setShowModal, csrfToken }) {

    const handleCloseModal = () => {
        setShowModal(false);
        fetcher.reset();
    };

    return (
        <Modal show={showModal} onHide={handleCloseModal} backdrop="static" keyboard={true}>
            <ModalHeader closeButton>
                <ModalTitle>Add robot</ModalTitle>
            </ModalHeader>
            <fetcher.Form method="post">
                <ModalBody>
                    <FormGroup className="mb-3" controlId="formName">
                        <FormLabel>Robot name</FormLabel>
                        <FormControl type="text" placeholder="Enter robot name" name="name" />
                        {fetcher.data === false && <FormText className="text-danger-emphasis">This name is already taken</FormText>}
                        {csrfToken && <CsrfHiddenInput csrfToken={csrfToken} />}
                    </FormGroup>
                </ModalBody>
                <ModalFooter>
                    <Button variant="secondary" onClick={handleCloseModal}>Close</Button>
                    <Button type="submit" variant="primary">Add</Button>
                </ModalFooter>
            </fetcher.Form>
        </Modal>
    );
}
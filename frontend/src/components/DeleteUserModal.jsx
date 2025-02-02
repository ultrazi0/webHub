import { Button, FormGroup, FormText, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle } from "react-bootstrap";


export async function deleteUserAction() {
    const headers = new Headers();

    const csrfToken = await fetch("/api/csrf")
    .then(response => {
        if (response.ok) {
            return response.json();
        }
        throw new Error(response.statusText);
    }).catch(error => console.log(error));

    headers.append(csrfToken.headerName, csrfToken.token);

    const success = await fetch("/api/user", {
        method: "DELETE",
        headers: headers 
    }).then(response => {
        if (response.ok) {
            return true;
        }
        throw new Error(response.statusText);
    }).catch(error => {
        console.error(error);
        return false;
    });

    return success;
}

export default function DeleteUserModal({ fetcher, showModal, setShowModal }) {
    
    const handleCloseModal = () => {
        setShowModal(false);
        fetcher.reset();
    };

    return (
        <Modal show={showModal} onHide={handleCloseModal}>
            <ModalHeader closeButton>
                <ModalTitle>Delete user</ModalTitle>
            </ModalHeader>
            <fetcher.Form method="delete" action={"delete"}>
                <ModalBody>
                    <FormGroup className="mb-3" controlId="formText">
                        <FormText>Are you sure you want to delete your user account?</FormText>
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
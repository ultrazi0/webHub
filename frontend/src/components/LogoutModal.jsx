import { useEffect, useState } from "react";
import { Button, FormGroup, FormText, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle } from "react-bootstrap";
import CsrfHiddenInput from "./CsrfHiddenInput";

export default function LogoutModal({ fetcher, showModal, setShowModal }) {
    const [csrfToken, setCsrfToken] = useState(null);
    
    const handleCloseModal = () => {
        setShowModal(false);
        fetcher.reset();
    };

    useEffect(() => {
        if (showModal) {
            fetch("/api/csrf").then(response => {
                if (response.ok) {
                    return response.json();
                }
                throw new Error(response.statusText);
            }).then(json => setCsrfToken(json))
            .catch(error => console.log(error));
        }
    }, [showModal]);

    return (
        <Modal show={showModal} onHide={handleCloseModal} backdrop="static" keyboard={true}>
            <ModalHeader closeButton>
                <ModalTitle>Log out</ModalTitle>
            </ModalHeader>
            <fetcher.Form method="post" action="/logout">
                <ModalBody>
                    <FormGroup className="mb-3" controlId="formLogout">
                        <FormText>Are you sure you want to log out?</FormText>
                        {csrfToken && <CsrfHiddenInput csrfToken={csrfToken} />}
                    </FormGroup>
                </ModalBody>
                <ModalFooter>
                    <Button variant="secondary" onClick={handleCloseModal}>Close</Button>
                    <Button type="submit" variant="primary">Log out</Button>
                </ModalFooter>
            </fetcher.Form>
        </Modal>
    );
}
import { Button, FormGroup, FormText, Modal, ModalBody, ModalFooter, ModalHeader, ModalTitle } from "react-bootstrap";
import CsrfHiddenInput from "../../components/CsrfHiddenInput";
import { FetcherWithReset } from "../../hooks/useFetcherWithReset";
import { CsrfResponse } from "../../types";
import FormButton from "../../components/FormButton";

type DeleteRobotModalProps = {
    fetcher: FetcherWithReset<boolean>,
    robotId: number | null,
    setRobotId: (robotId: number | null) => void,
    csrfToken: CsrfResponse,
};

export default function DeleteRobotModal({ fetcher, robotId, setRobotId, csrfToken }: DeleteRobotModalProps) {

    const handleCloseModal = () => {
        setRobotId(null);
        fetcher.reset();
    };

    return (
        <Modal show={!!robotId} onHide={handleCloseModal}>
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
                    <FormButton isLoading={fetcher.state !== "idle"} variant="danger">Delete</FormButton>
                </ModalFooter>
            </fetcher.Form>
        </Modal>
    );
}
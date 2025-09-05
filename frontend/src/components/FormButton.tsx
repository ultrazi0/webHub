import { Button, Spinner } from "react-bootstrap";

export default function FormButton({ isLoading, children, ...props }: {
    isLoading?: boolean
} & Parameters<typeof Button>[0]) {
    return (
        <Button
            type="submit"
            variant="primary"
            {...props}
            disabled={isLoading || props.disabled}
        >
            {isLoading ? (
                <Spinner as="span" animation="border" size="sm" role="status" aria-hidden>
                    <span className="visually-hidden">Loading...</span>
                </Spinner>
            ) : children}
        </Button>
    );
}

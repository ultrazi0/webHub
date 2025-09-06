import { Trash2 } from "lucide-react";

import "../css/Button.scss";

export default function DeleteButton({ onClick }: { onClick: () => void }) {
    return (
        <span className="rexus-button delete-button" onClick={onClick}>
            <Trash2 size={18} />
        </span>
    );
}
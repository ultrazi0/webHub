import img from "../../images/no-camera-stream.png";

import { Image } from "react-bootstrap";

export default function CameraStream({ image, altText }: { image: string, altText: string} ) {
    return <Image src={image || img} alt={altText} rounded fluid />;
}
export default function CameraStream( {image, altText} ) {
    return <img src={image || null} alt={altText} className="img-fluid" />;
}

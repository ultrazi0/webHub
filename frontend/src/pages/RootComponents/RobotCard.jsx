import { Button, Card, CardBody, CardFooter, CardImg, CardText, CardTitle, CloseButton } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";

import "../../css/RobotCard.css";

export default function RobotCard({ user, robot, setRobotInfo, setEditRobotId, setDeleteRobotId }) {
    const navigate = useNavigate();
    const createdAt = new Date(robot.createdAt);

    return (
        <div onClick={() => navigate("control-panel/" + robot.id)} role="button">
            <Card style={{ width: "18rem" }} className="robotCard">
                    <Button variant="link" className="robotCard-infoButton p-3" onClick={event => {
                        event.stopPropagation();
                        setRobotInfo(robot);
                    }}>
                        <i className="bi bi-info-circle"></i>
                    </Button>
                    <CloseButton className="robotCard-closeButton p-3" onClick={(event) => {
                        event.stopPropagation();
                        setDeleteRobotId(robot.id);
                    }}/>
                <CardImg variant="tip" src={"http://robohash.org/" + robot.name + ".png?size=280x160"} />
                <CardBody>
                    <CardTitle>{robot.name}</CardTitle>
                    <CardText>
                        Created at: {createdAt.toString()}
                        {user.id !== robot.ownerId && robot.ownerName && <><br /><i>Owned by:</i> <Link to={"user/" + robot.ownerId} className="link-info link-opacity-50-hover"><b>{robot.ownerName}</b></Link></>}
                    </CardText>
                </CardBody>
                <CardFooter className="d-flex justify-content-between align-items-center">
                    {robot.online ? <span className="text-success">Online</span> : <span className="text-danger">Offline</span>}
                    
                    {user.id === robot.ownerId && <Button onClick={(event) => {
                        event.stopPropagation();
                        console.log("I have been Clicked");
                        setEditRobotId(robot.id);
                    }}>Edit</Button>}
                </CardFooter>
            </Card>
        </div>
    );
}
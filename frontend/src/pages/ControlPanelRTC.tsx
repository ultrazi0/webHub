import img from "../images/no-camera-stream.png";

import { useRef, useState } from "react";
import useWebSocket from "react-use-websocket";
import { useParams } from "react-router-dom";
import { Alert, Button, Col, Container, Row } from "react-bootstrap";
import { TriangleAlert } from "lucide-react";
import Commands from "./ControlPanelComponents/Controls";
import FormButton from "../components/FormButton";

enum SignalType {
	Offer = "offer",
	Answer = "answer",
	IceCandidate = "iceCandidate",
	Disconnect = "disconnect",
	Text = "text",
	Error = "error",
}

type SignalMessage = {
	type: SignalType.Offer | SignalType.Answer,
	payload: RTCSessionDescriptionInit,
	robotId: string,
} | {
	type: SignalType.IceCandidate,
	payload: RTCIceCandidateInit,
	robotId: string,
} | {
	type: SignalType.Disconnect,
	payload: {
		timestamp: number,
	},
	robotId: string,
} | {
	type: SignalType.Text | SignalType.Error,
	payload: string,
};

const configuration: RTCConfiguration = {
	iceServers: [
		{ urls: "stun:stun.l.google.com:19302" },
	],
};

function ControlPanelRTC() {
	const { robotId } = useParams();

	const WS_URL = `ws://${window.location.host}/api/signal`;

	const peerConnectionRef = useRef<RTCPeerConnection>(null);
	const remoteVideoRef = useRef<HTMLVideoElement>(null);

	const [ connected, setConnected ] = useState(false);
	const [ connecting, setConnecting ] = useState(false);

	const getPeerConnection = () => {
		const peerConnection = peerConnectionRef.current;
		if (peerConnection) {
			return peerConnection;
		}

		const newPeerConnection = new RTCPeerConnection(configuration);
		// Listen for local ICE candidates on the local RTCPeerConnection
		newPeerConnection.onicecandidate = (event) => {
			if (!robotId) {
				console.error("No robotId provided");
				return;
			}

			if (event.candidate) {
				sendJsonMessage<SignalMessage>({
					type: SignalType.IceCandidate,
					payload: event.candidate,
					robotId: robotId,
				});
			}
		};

		// Listen for connectionstatechange on the local RTCPeerConnection
		newPeerConnection.onconnectionstatechange = (event) => {
			switch (newPeerConnection.connectionState) {
				case "connected": {
					console.log("Connected to peer.", event);
					setConnected(true);
					setConnecting(false);
					break;
				}
				case "disconnected":
				case "closed":
				case "failed": {
					handleDisconnect();
					break;
				}
			}
		};

		newPeerConnection.ontrack = (event: RTCTrackEvent) => {
			const [ remoteStream ] = event.streams;
			if (remoteVideoRef.current) {
				remoteVideoRef.current.srcObject = remoteStream;
			}
		};

		peerConnectionRef.current = newPeerConnection;
		return newPeerConnection;
	};

	function handleDisconnect() {
		const peerConnection = peerConnectionRef.current;
		if (peerConnection) {
			// Stop all tracks
			peerConnection.getSenders().forEach(sender => {
				if (sender.track) {
					sender.track.stop();
				}
			});

			peerConnection.getReceivers().forEach(receiver => {
				if (receiver.track) {
					receiver.track.stop();
				}
			});

			// Remove event listeners
			peerConnection.onconnectionstatechange = null;
			peerConnection.ondatachannel = null;
			peerConnection.onicecandidate = null;
			peerConnection.onicecandidateerror = null;
			peerConnection.oniceconnectionstatechange = null;
			peerConnection.onicegatheringstatechange = null;
			peerConnection.onnegotiationneeded = null;
			peerConnection.onsignalingstatechange = null;
			peerConnection.ontrack = null;

			// Close the peer connection
			peerConnection.close();
		}

		// Reset the peer connection reference
		peerConnectionRef.current = null;
		if (remoteVideoRef.current) {
			remoteVideoRef.current.srcObject = null;
		}
		setConnected(false);
	}

	const { sendJsonMessage } = useWebSocket<SignalMessage>(WS_URL, {
		shouldReconnect: () => false,
		onOpen: () => console.log("Signal-WebSocket connection opened"),
		onError: (event) => console.error("Signal-WebSocket error observed:", event),
		onClose: (event) => console.log("Signal-WebSocket connection closed:", event),
		onMessage: async (event) => {
			console.log("Received message:", event);
			const peerConnection = getPeerConnection();

			const data: SignalMessage = JSON.parse(event.data);
			switch (data.type) {
				case SignalType.Offer: {
					// Don't think this should even be handled, it shouldn't be possible
					await peerConnection.setRemoteDescription(new RTCSessionDescription(data.payload));
					const answer = await peerConnection.createAnswer();
					await peerConnection.setLocalDescription(answer);
					if (!robotId) {
						console.error("No robotId provided");
						return;
					}
					sendJsonMessage<SignalMessage>({
						type: SignalType.Answer,
						payload: answer,
						robotId: robotId,
					});
					break;
				}
				case SignalType.Answer: {
					const remoteDesc = new RTCSessionDescription(data.payload);
					await peerConnection.setRemoteDescription(remoteDesc);
					break;
				}
				case SignalType.IceCandidate: {
					try {
						await peerConnection.addIceCandidate(data.payload);
					} catch (e) {
						console.error("Error adding received ice candidate", e);
					}
					break;
				}
				case SignalType.Disconnect: {
					handleDisconnect();
					break;
				}
				case SignalType.Error: {
					console.error("Error received:", data.payload);
					setConnecting(false);
					break;
				}
			}
		},
	});

	async function connect() {
		if (!robotId) {
			console.error("No robotId provided");
			return;
		}
		setConnecting(true);

		const peerConnection = getPeerConnection();
		const offer = await peerConnection.createOffer({
			offerToReceiveAudio: true,
			offerToReceiveVideo: true,
		});
		await peerConnection.setLocalDescription(offer);

		sendJsonMessage<SignalMessage>({
			type: SignalType.Offer,
			payload: offer,
			robotId: robotId,
		});
	}

	return (
		<Container className="mt-3">
			{!connected && (
				<Row>
					<Col>
						<Alert variant="warning" className="d-flex align-items-center justify-content-between">
							<span style={{ display: "flex", gap: 8 }}>
								<TriangleAlert size={22} />
								<span>Robot is not connected</span>
							</span>
							<FormButton
								variant="primary"
								onClick={connect}
								isLoading={connecting}
							>
								Connect
							</FormButton>
						</Alert>
					</Col>
				</Row>
			)}
			<Row className="row-gap-3">
				<Col xs={12} lg={9} className="d-flex align-items-start justify-content-center">
					<video
						ref={remoteVideoRef}
						autoPlay
						poster={img}
						style={{ width: "100%" }}
					/>
				</Col>
				<Col className="d-flex flex-column gap-3" xs={12} lg={3}>
					<textarea className="form-control" readOnly={true} rows={10} placeholder="No messages yet" />
					<Commands robotId={robotId ?? null} sendCommand={sendJsonMessage} />
					<Button
						variant="danger"
						onClick={() => {
							sendJsonMessage<SignalMessage>({
								type: SignalType.Disconnect,
								payload: {
									timestamp: new Date().valueOf(),
								},
								robotId: robotId!,
							});
							handleDisconnect();
						}}
						disabled={!connected}
					>
						Disconnect
					</Button>
				</Col>
			</Row>
		</Container>
	);
}

export default ControlPanelRTC;

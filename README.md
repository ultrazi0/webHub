# WORK IN PROGRESS!

## *Project Name:* **WebHub**

*This repository is a part of the 3-repository project*

This repository is the backend part of the project *WebHub*, whose idea is to allow multiple users control their robots seamlessly and simultaneously.

The Spring Boot server maintains websocket connections to both the human-client and the robot-client and allows the user to send commands/directives to the robot and receive feedback as well as the stream from the robot's camera.

---------------------
### The most recent change:
Added security support, so that multiple users are able to connect simultaneously and manage their own robots.
For the robot to connect, it now needs to provide the correct robot ID with a correct UUID which serves as a password

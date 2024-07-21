
function connectToWebsocket(uri_path) {
    ws = new WebSocket("ws://" + host + uri_path);

    ws.addEventListener("close", (event) => {
        console.log(uri_path+">>>", "Disconnected")
    })

    return ws;
}

function replaceImage(image_base64) {
    var image_src = "data:image/jpg;base64, " + image_base64;

    $("#image-field").attr("src", image_src);
}

host = window.location.host;

wsImages = connectToWebsocket("/image/topic"); // Listen to images

wsImages.addEventListener("message", (event) => {
    var message = event.data;

    if (message.charAt(0) == "{") {
        var json_image = JSON.parse(message);
        replaceImage(json_image.image);
    } else {
        console.log("Received: ", event.data);
    }

});


// ############################# //


wsCommands = connectToWebsocket("/command/app/send");  // Send commands

wsCommands.addEventListener("close", (event) => {
    console.log(wsCommands.uri_path+">>>", "Disconnected");
    clearInterval(interval);
});

function sendJson() {

    var type = $("#selectCommandType").val();

    var commandValues = {};

    $("#commandValues").find(".commandValue").each(function() {
        commandValues[$(this).attr("name").toLowerCase()] = Number($(this).val());
    });

    var message = {
        "command": type,
        "values": commandValues
    };

    wsCommands.send(JSON.stringify(message));

}


function onSelectCommandTypeChange() {
    var selectId = /* [['selectCommandType']] */ "selectCommandType";
    var valuesTable = $("#commandValues");

    $.post("getCommandValues", {"commandType": $("#"+selectId).val()}). done(function(fragment) {
        if (valuesTable.attr("hidden")) {
            valuesTable.removeAttr("hidden");
        };

        valuesTable.html(fragment);
        if (!Boolean(fragment)) {
            valuesTable.attr("hidden", "hidden");
        };
    });
}


// ############################# //

var moveValues = {
    "speed": 0,
    "turn": 0
}

var turretValues = {
    "tilt": 0,
    "turn": 0
}

$(document).on("keydown", function(event) {
    var key = event.which;

    if (key == 65) {
        // 'A' is pressed
        
        if (moveValues.turn >= 0) {
            moveValues.turn -= 0.5;
        }

    } else if (key == 68) {
        // 'D' is pressed

        if (moveValues.turn <= 0) {
            moveValues.turn += 0.5;
        }

    } else if (key == 87) {
        // 'W' is pressed
        
        if (moveValues.speed <= 0) {
            moveValues.speed += 0.5;
        }

    } else if (key == 83) {
        // 'S' is pressed
        
        if (moveValues.speed >= 0) {
            moveValues.speed -= 0.5;
        }

    } else if (key == 37) {
        // 'Left Arrow' is pressed
        
        if (turretValues.turn >= 0) {
            turretValues.turn -= 0.5;
        }
    } else if (key == 39) {
        // 'Right Arrow' is pressed
        
        if (turretValues.turn <= 0) {
            turretValues.turn += 0.5;
        }
    } else if (key == 38) {
        // 'Up Arrow' is pressed
        
        if (turretValues.tilt <= 0) {
            turretValues.tilt += 0.5;
        }
    } else if (key == 40) {
        // 'Down Arrow' is pressed
        
        if (turretValues.tilt >= 0) {
            turretValues.tilt -= 0.5;
        }
    };
});

$(document).on("keyup", function(event) {
    var key = event.which;

    if (key == 32) {
        // Space is pressed
    } else if (key == 27) {
        // ESC pressed
        sendStopCommand();
    } else if ((key == 65) || (key == 68) || (key == 87) || (key == 83)) {
        // 'A' or 'D' or 'W' or 'S' is released
        
        switch (key) {
            case 65:
                // 'A'
                moveValues.turn += 0.5;
                break;
            case 68:
                // 'D'
                moveValues.turn -= 0.5;
                break;
            case 87:
                // 'W'
                moveValues.speed -= 0.5;
                break;
            case 83:
                // 'S'
                moveValues.speed += 0.5;
                break;
        };

    } else if ((key == 37) || (key == 38) || (key == 39) || (key == 40)) {
        // 'Left' or 'Up' or 'Right' or 'Down' is released
        
        switch (key) {
            case 37:
                // 'Left'
                turretValues.turn += 0.5;
                break;
            case 39:
                // 'Right'
                turretValues.turn -= 0.5;
                break;
            case 38:
                // 'Up'
                turretValues.tilt -= 0.5;
                break;
            case 40:
                // 'Down'
                turretValues.tilt += 0.5;
                break;
        };

    };
});

function sendMoveCommand() {
    var moveType = /*[[${moveCommandType}]]*/ "hello";
    var turretType = /*[[${turretCommandType}]]*/ "hello";

    var message = [
        {
            "command": moveType,
            "values": moveValues
        },
        {
            "command": turretType,
            "values": turretValues
        }
    ];

    wsCommands.send(JSON.stringify(message));
}

function sendStopCommand() {
    var type = /* [[${stopCommandType}]] */ "STOP";

    var message = {
        "command": type,
        "values": {}
    };

    moveValues.speed = 0;
    moveValues.turn = 0;
    turretValues.tilt = 0;
    turretValues.turn = 0;

    wsCommands.send(JSON.stringify(message));
}

interval = setInterval(sendMoveCommand, 50);

// ############################# //

wsFeedback = connectToWebsocket("/feedback/topic");

wsFeedback.addEventListener("message", (event) => {
    var message = event.data;

    $("#feedbackTextArea").append(message + "\n");
    $("#feedbackTextArea").scrollTop($("#feedbackTextArea").get(0).scrollHeight);
});

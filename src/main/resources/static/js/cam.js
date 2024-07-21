host = window.location.host;

ws = new WebSocket("ws://" + host + "/image/topic");

ws.addEventListener("message", (event) => {
    var message = event.data;

    if (message.charAt(0) == "{") {
        var json_image = JSON.parse(message);
        replaceImage(json_image.image);
    } else {
        console.log("Received: ", event.data);
    }

});

function replaceImage(image_base64) {
    var image_src = "data:image/jpg;base64, " + image_base64;

    $("#image-field").attr("src", image_src);
}

function con(uri_path) {
    ws = new WebSocket("ws://" + host + uri_path);
    ws.addEventListener("message", (event) => {
        console.log("Received: ", event.data);
    });
    ws.addEventListener("close", (event) => {
        console.log("Disconnected")
    })

    return ws;
}
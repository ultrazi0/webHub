package com.nemo.webHub.Sock.Image;

import com.fasterxml.jackson.core.*;
import jakarta.annotation.Nullable;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public record JsonImage(Mat image) {

    private static final Map<Integer, JsonImage> lastImageMap = new HashMap<>();

    @Nullable
    public static JsonImage createFromJson(String json) throws IOException {
        JsonFactory jsonFactory = new JsonFactory();
        JsonParser jsonParser = jsonFactory.createParser(json);

        String image = null;

        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jsonParser.getCurrentName();

            if ("messageType".equals(fieldName)) {
                jsonParser.nextToken();
                if (!jsonParser.getText().equals("image")) {
                    // If messageType says message is not an image, no need to parse further
                    return null;
                }
            }

            if ("image".equals(fieldName)) {
                jsonParser.nextToken();
                image = jsonParser.getText();
            }
        }
        jsonParser.close();

        if (image == null) {
            return null;
        }

        return new JsonImage(decode(image));

    }

    public String jsonify(String messageType) throws IOException {
        // See https://www.baeldung.com/jackson-streaming-api

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(stream, JsonEncoding.UTF8);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("messageType", messageType);
        jsonGenerator.writeStringField("image", encode(image));
        jsonGenerator.writeEndObject();

        jsonGenerator.close();

        return stream.toString(StandardCharsets.UTF_8);

    }

    private static Mat decode(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);

        Mat mat = new MatOfByte(decodedBytes);
        return Imgcodecs.imdecode(mat, Imgcodecs.IMREAD_COLOR);
    }

    private static String encode(Mat image) {  // TODO: Think about whether it should be static or not
        MatOfByte encodedBytes = new MatOfByte();

        Imgcodecs.imencode(".JPG", image, encodedBytes);

        return Base64.getEncoder().encodeToString(encodedBytes.toArray());
    }

    @Nullable
    public static JsonImage getLastImage(int robotId) {
        System.out.println("Last image map: " + lastImageMap);
        return lastImageMap.get(robotId);
    }

    public static void setLastImage(int robotId, JsonImage lastImage) {
        lastImageMap.put(robotId, lastImage);
    }

    public static void removeFromLastImageMap(int robotId) {
        lastImageMap.remove(robotId);
    }

    @Override
    public String toString() {
        return "JsonImage{" +
                "image='" + image + '\'' +
                '}';
    }
}

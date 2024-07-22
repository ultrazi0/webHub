package com.nemo.webHub.Sock.Image;

import com.fasterxml.jackson.core.*;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Null;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public record JsonImage(Mat image) {

    private static JsonImage lastImage = null;

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

    public String jsonify() throws IOException {
        // See https://www.baeldung.com/jackson-streaming-api

        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        JsonFactory jsonFactory = new JsonFactory();
        JsonGenerator jsonGenerator = jsonFactory.createGenerator(stream, JsonEncoding.UTF8);

        jsonGenerator.writeStartObject();
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

    public static JsonImage getLastImage() {
        return lastImage;
    }

    public static void setLastImage(JsonImage lastImage) {
        JsonImage.lastImage = lastImage;
    }

    @Override
    public String toString() {
        return "JsonImage{" +
                "image='" + image + '\'' +
                '}';
    }
}

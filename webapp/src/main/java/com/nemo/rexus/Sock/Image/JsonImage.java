package com.nemo.rexus.Sock.Image;

import com.nemo.rexus.Sock.Messages.JsonMessage;
import com.nemo.rexus.Sock.Messages.MessageType;
import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.core.ObjectReadContext;
import tools.jackson.core.json.JsonFactory;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class JsonImage implements JsonMessage {

    private static final Map<Integer, JsonImage> lastImageMap = new HashMap<>();

    private final Mat image;
    private boolean aimImage = false;

    @Nullable
    public static JsonImage createFromJson(String json) {
        JsonFactory jsonFactory = new JsonFactory();

        try (JsonParser jsonParser = jsonFactory.createParser(ObjectReadContext.empty(), json)) {

            String image = null;

            while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
                String fieldName = jsonParser.currentName();

                if (JsonMessage.getMessageTypeFieldName().equals(fieldName)) {
                    jsonParser.nextToken();
                    if (!MessageType.IMAGE.toString().equals(jsonParser.getString())) {
                        // If messageType says a message is not an image, no need to parse further
                        return null;
                    }
                }

                if ("image".equals(fieldName)) {
                    jsonParser.nextToken();
                    image = jsonParser.getString();
                }
            }

            if (image == null) {
                return null;
            }

            return new JsonImage(decode(image));
        }
    }

    public JsonImage asAimImage() {
        this.aimImage = true;
        return this;
    }

    @Override
    public MessageType getMessageType() {
        return aimImage ? MessageType.AIM_IMAGE : MessageType.IMAGE;
    }

    @Override
    public void addImplementationSpecificFields(JsonGenerator jsonGenerator) {
        jsonGenerator.writeStringProperty("image", encode(image));
    }

    private static Mat decode(String encodedString) {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedString);

        Mat mat = new MatOfByte(decodedBytes);
        return Imgcodecs.imdecode(mat, Imgcodecs.IMREAD_COLOR);
    }

    private static String encode(Mat image) {
        MatOfByte encodedBytes = new MatOfByte();

        Imgcodecs.imencode(".JPG", image, encodedBytes);

        return Base64.getEncoder().encodeToString(encodedBytes.toArray());
    }

    @Nullable
    public static JsonImage getLastImage(int robotId) {
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
            ", lastImage=" + aimImage +
            '}';
    }
}

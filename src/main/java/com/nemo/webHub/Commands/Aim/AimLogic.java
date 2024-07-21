package com.nemo.webHub.Commands.Aim;

import com.nemo.webHub.Commands.Aim.Math.Matrix;
import com.nemo.webHub.Commands.Aim.Math.Point3D;
import com.nemo.webHub.Commands.Aim.Math.PointInImage;
import com.nemo.webHub.Commands.Aim.Math.PointM;
import com.nemo.webHub.Commands.CommandType;
import com.nemo.webHub.Commands.JsonCommand;
import com.nemo.webHub.Config;
import com.nemo.webHub.Sock.Image.JsonImage;
import jakarta.annotation.Nullable;
import org.opencv.core.Mat;

import java.util.HashMap;
import java.util.List;

import static com.nemo.webHub.Commands.Aim.DetectQRCode.detect;

public class AimLogic {

    @Nullable
    public static double[] aim(JsonImage jsonImage, Config config) {
        Mat image = jsonImage.image();

        // Get image resolution
        int imageWidth = image.width();
        int imageHeight = image.height();

        // Get parameters from the config
        double realSideLength = config.getRealSideLength();
        double focus = config.getFocus();
        double sensorWidth = config.getSensorWidth();
        double sensorHeight = config.getSensorHeight();

        Matrix K = config.getK();
        Matrix T = config.getT();

        // Detect QR-code
        List<PointInImage> points = detect(image, config.getQrCodeDetector());

        if (points == null) {
            return null;
        }

        // Translate coordinates in pixels to coordinates in meters
        List<PointInImage> vertices = PointInImage.translatePointsToCommonCoordinateSystem(
                points, imageWidth, imageHeight, sensorWidth, sensorHeight);

        // Find the image of point M (centre of the original QR-code), i.e. centre of the QR-code in the image
        PointInImage imageM = PointInImage.findCenter(vertices);

        // Calculate the coordinates of the point M
        Point3D M = PointM.findCoordinatesOfPointM(imageM, vertices, realSideLength, focus, K, T);

        return M.getAngelsDegrees();
    }

    public static JsonCommand createCommand(double[] angles) {

        HashMap<String, Double> values = new HashMap<>();
        values.put(CommandType.TURRET.getKeys()[0].toLowerCase(), angles[1]); // Tilt - angleY
        values.put(CommandType.TURRET.getKeys()[1].toLowerCase(), angles[0]); // Turn - angleX

        return new JsonCommand(CommandType.TURRET, values);

    }
}

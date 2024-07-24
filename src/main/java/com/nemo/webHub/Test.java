package com.nemo.webHub;

import com.nemo.webHub.Sock.Image.JsonImage;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.QRCodeDetector;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.nemo.webHub.Commands.Aim.AimLogic.aim;

public class Test {

    public static void main(String[] args) throws IOException {
/*        OpenCV.loadLocally();

        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        Mat image0 = Imgcodecs.imread("code0.jpg");;

        QRCodeDetector qrCodeDetector = new QRCodeDetector();

        Mat res = new Mat();

        qrCodeDetector.detect(image0, res);

        MatOfPoint2f pointMat = new MatOfPoint2f(res);

        Point[] vertices = pointMat.toArray();
        for (Point vertex : vertices) {
            System.out.println("(" + vertex.x + ", " + vertex.y + ")");
        }*/

        URL url = new URL("https://www.youtube.com/watch?v=dQw4w9WgXcQ&q=rickroll");
        System.out.println(getQueryParametersMap(url.getQuery()));
    }

    public static Map<String, String> getQueryParametersMap(String query) {
        Map<String, String> map = new HashMap<>();

        for (String param : query.split("&")) {
            String[] splitParam = param.split("=");

            map.put(splitParam[0], splitParam[1]);
        }

        return map;
    }
}

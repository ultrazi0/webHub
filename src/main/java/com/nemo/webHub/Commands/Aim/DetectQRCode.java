package com.nemo.webHub.Commands.Aim;

import com.nemo.webHub.Commands.Aim.Math.PointInImage;
import jakarta.annotation.Nullable;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.objdetect.QRCodeDetector;

import java.util.LinkedList;
import java.util.List;

public class DetectQRCode {

    @Nullable
    public static List<PointInImage> detect(Mat img, QRCodeDetector qrCodeDetector) {

        Mat points = new Mat();

        if (!qrCodeDetector.detect(img, points)) {
            System.out.println("DetectQRCode>>> Unable to find QR-code");

            return null;
        }

        List<PointInImage> vertices = new LinkedList<>();
        for (Point vertex : new MatOfPoint2f(points).toArray()) {
            vertices.add(new PointInImage(vertex.x, vertex.y));
        }

        return vertices;
    }
}

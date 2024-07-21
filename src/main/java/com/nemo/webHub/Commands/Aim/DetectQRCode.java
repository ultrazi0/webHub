package com.nemo.webHub.Commands.Aim;

import com.nemo.webHub.Commands.Aim.Math.Matrix;
import com.nemo.webHub.Commands.Aim.Math.Point3D;
import com.nemo.webHub.Commands.Aim.Math.PointInImage;
import com.nemo.webHub.Commands.Aim.Math.PointM;
import com.nemo.webHub.Config;
import jakarta.annotation.Nullable;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
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

    public static void main(String[] args) {
        OpenCV.loadLocally();

        QRCodeDetector qrCodeDetector = new QRCodeDetector();

        Mat image0 = Imgcodecs.imread("code0.jpg");

        int imageWidth = image0.width();
        int imageHeight = image0.height();

        Config config = new Config();

        double realSideLength = config.getRealSideLength();
        double focus = config.getFocus();
        double sensorWidth = config.getSensorWidth();
        double sensorHeight = config.getSensorHeight();

        Matrix K = config.getK();
        Matrix T = config.getT();


        List<PointInImage> points = detect(image0, qrCodeDetector);

        List<PointInImage> vertices = PointInImage.translatePointsToCommonCoordinateSystem(
                points, imageWidth, imageHeight, sensorWidth, sensorHeight);

        PointInImage imageM = PointInImage.findCenter(vertices);

        Point3D M = PointM.findCoordinatesOfPointM(imageM, vertices, realSideLength, focus, K, T);

        System.out.println(M);
    }
}

package com.nemo.webHub.Command.Aim;

import com.nemo.webHub.Commands.Aim.Math.Matrix;
import com.nemo.webHub.Commands.Aim.Math.Point3D;
import com.nemo.webHub.Commands.Aim.Math.PointInImage;
import com.nemo.webHub.Commands.Aim.Math.PointM;
import nu.pattern.OpenCV;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.QRCodeDetector;
import org.springframework.util.Assert;

import java.util.LinkedList;
import java.util.List;

import static com.nemo.webHub.Commands.Aim.DetectQRCode.detect;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class DetectQRCodeTests {
    @BeforeAll
    static void initCV() {
        OpenCV.loadLocally();
    }

    @Test
    void givenImageWithQRCode_thenQRCodeFound() {
        QRCodeDetector qrCodeDetector = new QRCodeDetector();

        Mat image = Imgcodecs.imread("../code0.jpg");

        List<PointInImage> points = detect(image, qrCodeDetector);
        Assert.notNull(points, "QR-code not found");
    }

    @Test
    void givenImageWithoutQRCode_thenQRCodeNotFound() {
        QRCodeDetector qrCodeDetector = new QRCodeDetector();

        Mat image = Imgcodecs.imread("../image0.png");

        List<PointInImage> points = detect(image, qrCodeDetector);
        Assert.isNull(points, "QR-code found in an image without one");
    }

    @Test
    void givenQRCode_thenFindMiddle() {
        QRCodeDetector qrCodeDetector = new QRCodeDetector();

        Mat image0 = Imgcodecs.imread("../code0.jpg");

        int imageWidth = image0.width();
        int imageHeight = image0.height();

        double realSideLength = 0.34d;
        double focus = 0.00304d;
        double sensorWidth = 3.68 * Math.pow(10, -3);
        double sensorHeight = 2.76 * Math.pow(10, -3);

        Matrix K = new Matrix(new double[][] {
                {focus, 0, 0},
                {0, focus, 0},
                {0, 0, 1}
        });

        Matrix T = new Matrix(new double[][] {
                {0},
                {0},
                {0}
        });

        List<PointInImage> points = detect(image0, qrCodeDetector);
        Assumptions.assumeTrue(points != null);

        List<PointInImage> expectedPoints = new LinkedList<>();
        expectedPoints.add(new PointInImage(284.0d, 102.0d));
        expectedPoints.add(new PointInImage(437.4560546875d, 94.97913360595703d));
        expectedPoints.add(new PointInImage(440.8043518066406d, 249.0d));
        expectedPoints.add(new PointInImage(283.0d, 249.0d));
        assertEquals(expectedPoints, points);

        List<PointInImage> vertices = PointInImage.translatePointsToCommonCoordinateSystem(
                points, imageWidth, imageHeight, sensorWidth, sensorHeight);
        List<PointInImage> expectedVertices = new LinkedList<>();
        expectedVertices.add(new PointInImage(-2.07E-4d, 7.935E-4d));
        expectedVertices.add(new PointInImage(6.75372314453125E-4d, 8.33869981765747E-4d));
        expectedVertices.add(new PointInImage(6.946250228881836E-4d, -5.175E-5d));
        expectedVertices.add(new PointInImage(-2.1275E-4d, -5.175E-5d));

        assertEquals(expectedVertices, vertices);

        PointInImage imageM = PointInImage.findCenter(vertices);
        assertEquals(new PointInImage(2.3756183433532713E-4d, 3.809674954414368E-4d), imageM);

        Point3D M = PointM.findCoordinatesOfPointM(imageM, vertices, realSideLength, focus, K, T);
        assertEquals(new Point3D(0.09174541495910442d, 0.14712809847169853d, 1.1740356453132597d), M);
    }
}

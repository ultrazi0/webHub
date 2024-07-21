package com.nemo.webHub;

import com.nemo.webHub.Sock.Image.JsonImage;
import nu.pattern.OpenCV;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.objdetect.QRCodeDetector;

import java.io.IOException;
import java.util.Arrays;

import static com.nemo.webHub.Commands.Aim.AimLogic.aim;

public class Test {

    public static void main(String[] args) throws IOException {
        OpenCV.loadLocally();

        System.out.println("Working Directory = " + System.getProperty("user.dir"));

        Mat image0 = Imgcodecs.imread("code0.jpg");;

        QRCodeDetector qrCodeDetector = new QRCodeDetector();

        Mat res = new Mat();

        qrCodeDetector.detect(image0, res);

        MatOfPoint2f pointMat = new MatOfPoint2f(res);

        Point[] vertices = pointMat.toArray();
        for (Point vertex : vertices) {
            System.out.println("(" + vertex.x + ", " + vertex.y + ")");
        }
    }
}

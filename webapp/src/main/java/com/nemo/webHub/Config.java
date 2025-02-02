package com.nemo.webHub;

import com.nemo.webHub.Commands.Aim.Math.Matrix;
import org.opencv.objdetect.QRCodeDetector;
import org.springframework.stereotype.Component;

@Component
public class Config {
    private double focus = 0.00304d;
    private double realSideLength = 0.34d;
    private double sensorWidth = 3.68 * Math.pow(10, -3);
    private double sensorHeight = 2.76 * Math.pow(10, -3);

    private Matrix K = new Matrix(new double[][] {
            {focus, 0, 0},
            {0, focus, 0},
            {0, 0, 1}
    });

    private Matrix T = new Matrix(new double[][] {
            {0},
            {0},
            {0}
    });

    private final QRCodeDetector qrCodeDetector = new QRCodeDetector();

    public QRCodeDetector getQrCodeDetector() {
        return qrCodeDetector;
    }

    public double getFocus() {
        return focus;
    }

    public void setFocus(double focus) {
        this.focus = focus;
    }

    public double getRealSideLength() {
        return realSideLength;
    }

    public void setRealSideLength(double realSideLength) {
        this.realSideLength = realSideLength;
    }

    public double getSensorWidth() {
        return sensorWidth;
    }

    public void setSensorWidth(double sensorWidth) {
        this.sensorWidth = sensorWidth;
    }

    public double getSensorHeight() {
        return sensorHeight;
    }

    public void setSensorHeight(double sensorHeight) {
        this.sensorHeight = sensorHeight;
    }

    public Matrix getK() {
        return this.K;
    }

    public void setK(Matrix k) {
        this.K = k;
    }

    public Matrix getT() {
        return this.T;
    }

    public void setT(Matrix t) {
        this.T = t;
    }
}

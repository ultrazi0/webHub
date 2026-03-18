package com.nemo.rexus;

import com.nemo.rexus.Commands.Aim.Math.Matrix;
import lombok.Getter;
import lombok.Setter;
import org.opencv.objdetect.QRCodeDetector;
import org.springframework.stereotype.Component;

@Getter
@Setter
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

}

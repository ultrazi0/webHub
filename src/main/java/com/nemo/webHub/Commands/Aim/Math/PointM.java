package com.nemo.webHub.Commands.Aim.Math;

import com.nemo.webHub.Config;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.List;

public class PointM {

    @Autowired
    private static Config config;

    public static Point3D findCoordinatesOfPointM(PointInImage imageM, List<PointInImage> vertices,
                                                  double realSideLength, double focus, Matrix K, Matrix T) {
        // TODO: maybe make a separate class with data?

        Matrix matrixImageM = new Matrix(new double[][] {{imageM.x()}, {imageM.y()}, {1}});

        double s = Depth.findDepthS(vertices, realSideLength, focus);

        matrixImageM = matrixImageM.multiplyByNumber(s);

        Matrix matrixM = Matrix.multiply(K.inverse(), matrixImageM);

        matrixM = Matrix.addMatrixToMatrix(matrixM, T);

        System.out.println("PointM>>> Calculated M: " + matrixM.convertToPoint());

        return matrixM.convertToPoint();
    }

    public static Point3D findCoordinatesOfPointM(PointInImage imageM, List<PointInImage> vertices) {
        return findCoordinatesOfPointM(imageM, vertices,
                config.getRealSideLength(), config.getFocus(), config.getK(), config.getT());
    }

    public static void main(String[] args) {
        PointInImage imageM = new PointInImage(-0.08557, -0.83426);

        List<PointInImage> points = new LinkedList<>();

        points.add(new PointInImage(1.6845, 4.66755));
        points.add(new PointInImage(2.17995, 4.4321));
        points.add(new PointInImage(2.14365, 3.66549));
        points.add(new PointInImage(1.5, 3.75));


        double realSideLength = 4d;
        double focus = 2d;

        Matrix K = new Matrix(new double[][] {
                {2, 0, 0},
                {0, 2, 0},
                {0, 0, 1}
        });

        Matrix T = new Matrix(new double[][] {
                {2},
                {5},
                {-4}
        });

        Point3D M = findCoordinatesOfPointM(imageM, points, realSideLength, focus, K, T);

        System.out.println(M);
    }
}

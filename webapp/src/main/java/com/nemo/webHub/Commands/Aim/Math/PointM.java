package com.nemo.webHub.Commands.Aim.Math;

import java.util.List;

public class PointM {

    public static Point3D findCoordinatesOfPointM(PointInImage imageM, List<PointInImage> vertices,
                                                  double realSideLength, double focus, Matrix K, Matrix T) {

        Matrix matrixImageM = new Matrix(new double[][] {{imageM.x()}, {imageM.y()}, {1}});

        double s = Depth.findDepthS(vertices, realSideLength, focus);

        matrixImageM = matrixImageM.multiplyByNumber(s);

        Matrix matrixM = Matrix.multiply(K.inverse(), matrixImageM);

        matrixM = Matrix.addMatrixToMatrix(matrixM, T);

        return matrixM.convertToPoint();
    }
}

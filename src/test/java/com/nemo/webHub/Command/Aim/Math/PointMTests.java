package com.nemo.webHub.Command.Aim.Math;

import com.nemo.webHub.Commands.Aim.Math.Matrix;
import com.nemo.webHub.Commands.Aim.Math.Point3D;
import com.nemo.webHub.Commands.Aim.Math.PointInImage;
import org.junit.jupiter.api.Test;

import java.util.LinkedList;
import java.util.List;

import static com.nemo.webHub.Commands.Aim.Math.PointM.findCoordinatesOfPointM;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PointMTests {

    @Test
    void testFindCoordinatesOfPointM() {
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
        assertEquals(new Point3D(1.5312614750002835d, 0.4300595785174304d, 6.955674301734639d), M);
    }
}

package com.nemo.webHub.Commands.Aim.Math;

import java.util.List;

public class Depth {
    private static double averageDiagonalInImage(List<PointInImage> vertices) {
        // Finds the average length of a diagonal in the image

        if (vertices.size() != 4) {
            throw new IllegalArgumentException("Provided list has other than 4 elements, hence it is no square");
        }

        double diagonal1 = PointInImage.distance(vertices.get(0), vertices.get(2));
        double diagonal2 = PointInImage.distance(vertices.get(1), vertices.get(3));

        return (diagonal1 + diagonal2) / 2;

    }

    private static double relationImageDiagonalToReal(double averageDiagonalLengthImage, double realSideLength) {
        // Finds the relation between the average diagonal length in the image (d') and the length of a real diagonal

        return averageDiagonalLengthImage / (realSideLength * Math.sqrt(2));
    }

    public static double findDepthS(List<PointInImage> vertices, double realSideLength, double focus) {
        // Finds the depth of the image (s), when s = M_z (middle of the square) - Q_z (middle of the image)

        double averageDiagonalLengthImage = averageDiagonalInImage(vertices);

        double relationImageOriginal = relationImageDiagonalToReal(averageDiagonalLengthImage,realSideLength);

        return focus / relationImageOriginal;
    }
}

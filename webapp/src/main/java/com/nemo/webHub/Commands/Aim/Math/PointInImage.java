package com.nemo.webHub.Commands.Aim.Math;

import java.util.LinkedList;
import java.util.List;

public record PointInImage(double x, double y) {

    public static double distance(PointInImage point1, PointInImage point2) {
        return Math.sqrt(Math.pow(point1.x()-point2.x(), 2) + Math.pow(point1.y()-point2.y(), 2));
    }

    public static List<PointInImage> translatePointsToCommonCoordinateSystem(
            List<PointInImage> pointsInImage, int imageWidth, int imageHeight, double sensorWidth, double sensorHeight) {

        List<PointInImage> translatedPoints = new LinkedList<>();
        for (PointInImage pointInImage : pointsInImage) {
            PointInImage translatedPoint = new PointInImage(
                    sensorWidth / imageWidth * (pointInImage.x() - (double) imageWidth / 2),
                    sensorHeight / imageHeight * ((double) imageHeight / 2 - pointInImage.y())
            );

            translatedPoints.add(translatedPoint);
        }

        return translatedPoints;
    }

    public static PointInImage findCenter(List<PointInImage> points) {
        double x = 0;
        double y = 0;

        for (PointInImage point : points) {
            x += point.x();
            y += point.y();
        }

        return new PointInImage(x/points.size(), y/points.size());
    }
}

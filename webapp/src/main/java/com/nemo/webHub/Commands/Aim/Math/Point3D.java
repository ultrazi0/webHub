package com.nemo.webHub.Commands.Aim.Math;

public record Point3D(double x, double y, double z) {

    public double[] getAngelsDegrees() {
        double angleX = Math.toDegrees(Math.atan(this.x() / this.z()));
        double angleY = Math.toDegrees(Math.atan(this.y() / this.z()));

        return new double[] {angleX, angleY};
    }
}

package com.nemo.webHub.Commands.Aim.Math;

import java.util.Arrays;

public class Matrix {
    final int rows, columns;
    private double[][] matrix;

    public Matrix(int n, int m) {
        // Creates matrix with n rows and m columns

        this.rows = n;
        this.columns = m;
        this.matrix = new double[n][m];
    }

    public Matrix(double[][] matrix) {
        // Creates matrix from the given double array

        this.matrix = matrix;
        this.rows = matrix.length;
        this.columns = matrix[0].length;
    }

    public void setMatrix(double[][] matrix) {
        if (this.rows != matrix.length || this.columns != matrix[0].length) {
            throw new IllegalArgumentException("Cannot set this matrix: matrix dimensions do not align");
        }

        this.matrix = matrix;
    }

    public double[][] getMatrix() {
        return this.matrix;
    }

    public static Matrix identityMatrix(int n) {
        Matrix mat = new Matrix(n, n);

        for (int i = 0; i < n; i++) {
            mat.matrix[i][i] = 1;
        }

        return mat;
    }

    public static Matrix multiply(Matrix matrix1, Matrix matrix2) {
        if (matrix1.columns != matrix2.rows) {
            throw new IllegalArgumentException("Cannot multiply matrices: matrix1.columns != matrix2.rows");
        }

        Matrix result = new Matrix(matrix1.rows, matrix2.columns);

        for (int row = 0; row < matrix1.rows; row++) {
            for (int column = 0; column < matrix2.columns; column++) {
                result.matrix[row][column] = multiplyMatrixCell(matrix1, matrix2, row, column);
            }
        }

        return result;
    }

    private static double multiplyMatrixCell(Matrix matrix1, Matrix matrix2, int row, int column) {
        double result = 0;

        for (int i = 0; i < matrix1.columns; i++) {
            result += matrix1.matrix[row][i] * matrix2.matrix[i][column];
        }

        return result;
    }

    public Matrix multiplyByNumber(double a) {
        Matrix result = new Matrix(this.rows, this.columns);

        for (int row = 0; row < this.rows; row++) {
            for (int column = 0; column < this.columns; column++) {
                result.matrix[row][column] = a * this.matrix[row][column];
            }
        }

        return result;
    }

    public static Matrix addMatrixToMatrix(Matrix matrix1, Matrix matrix2) {
        if (matrix1.rows != matrix2.rows || matrix1.columns != matrix2.columns) {
            throw new IllegalArgumentException("Matrix dimensions do not match");
        }

        Matrix result = new Matrix(matrix1.rows, matrix1.columns);

        for (int row = 0; row < matrix1.rows; row++) {
            for (int column = 0; column < matrix1.columns; column++) {
                result.matrix[row][column] = matrix1.matrix[row][column] + matrix2.matrix[row][column];
            }
        }

        return result;
    }

    public Matrix inverse() {
        assert this.rows == this.columns;

        return new Matrix(MatrixInverseLU.inverse(this.matrix));
    }

    public Point3D convertToPoint() {
        if (this.rows != 3 || this.columns != 1) {
            throw new UnsupportedOperationException("Cannot convert this matrix to a point - check the dimensions");
        }

        return new Point3D(this.matrix[0][0], this.matrix[1][0], this.matrix[2][0]);
    }

    @Deprecated
    public void printMatrix() {
        for (double[] row : this.matrix) {
            System.out.print('[');
            for (double val : row) {
                System.out.printf("%10.4f ", val);
            }
            System.out.println(']');
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Matrix) {
            return Arrays.deepEquals(this.matrix, ((Matrix) obj).getMatrix());
        }
        return false;
    }
}

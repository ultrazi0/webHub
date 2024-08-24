package com.nemo.webHub.Commands.Aim.Math;

import java.util.Arrays;

class MatrixInverseLU {

    /**
     * Function to perform LU decomposition
     */
    static double[][][] luDecomposition(double[][] matrix) {
        int n = matrix.length;
        double[][] L = new double[n][n];
        double[][] U = new double[n][n];

        for (int i = 0; i < n; i++) {
            // Upper Triangular
            for (int k = i; k < n; k++) {
                double sum = 0;
                for (int j = 0; j < i; j++) {
                    sum += (L[i][j] * U[j][k]);
                }
                U[i][k] = matrix[i][k] - sum;
            }
            // Lower Triangular
            for (int k = i; k < n; k++) {
                if (i == k) {
                    L[i][i] = 1; // Diagonal as 1
                } else {
                    double sum = 0;
                    for (int j = 0; j < i; j++) {
                        sum += (L[k][j] * U[j][i]);
                    }
                    L[k][i] = (matrix[k][i] - sum) / U[i][i];
                }
            }
        }
        return new double[][][] { L, U };
    }

    /**
     * Function to solve lower triangular matrix
     */
    static double[] forwardSubstitution(double[][] L, double[] b) {
        int n = L.length;
        double[] y = new double[n];

        for (int i = 0; i < n; i++) {
            y[i] = b[i];
            for (int j = 0; j < i; j++) {
                y[i] -= L[i][j] * y[j];
            }
            y[i] /= L[i][i];
        }

        return y;
    }

    /**
     * Function to solve upper triangular matrix
     */
    static double[] backSubstitution(double[][] U, double[] y) {
        int n = U.length;
        double[] x = new double[n];

        for (int i = n - 1; i >= 0; i--) {
            x[i] = y[i];
            for (int j = i + 1; j < n; j++) {
                x[i] -= U[i][j] * x[j];
            }
            x[i] /= U[i][i];
        }

        return x;
    }

    /**
     * Function to find the inverse using LU decomposition
     */
    static double[][] inverse(double[][] matrix) {
        int n = matrix.length;
        double[][][] lu = luDecomposition(matrix);
        double[][] L = lu[0];
        double[][] U = lu[1];

        double[][] inverse = new double[n][n];
        double[] e = new double[n];

        for (int i = 0; i < n; i++) {
            Arrays.fill(e, 0);
            e[i] = 1;
            double[] y = forwardSubstitution(L, e);
            double[] x = backSubstitution(U, y);
            for (int j = 0; j < n; j++) {
                inverse[j][i] = x[j];
            }
        }

        return inverse;
    }
}

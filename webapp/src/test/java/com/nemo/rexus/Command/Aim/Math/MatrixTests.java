package com.nemo.rexus.Command.Aim.Math;

import com.nemo.rexus.Commands.Aim.Math.Matrix;
import org.junit.jupiter.api.Test;

import static com.nemo.rexus.Commands.Aim.Math.Matrix.multiply;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MatrixTests {

    @Test
    void multiplyTwoMatrices() {
        Matrix m = new Matrix(3, 3);
        m.setMatrix(new double[][] {
                {800d, 0d, 320d},
                {-3d, 1d, 240d},
                {0d, 1d, 1d}
        });

        Matrix n = new Matrix(new double[][] {
                {1d},
                {2d},
                {-5d},
        });

        Matrix r = multiply(m, n);
        assertEquals(new Matrix(new double[][]{
                {-800.0d},
                {-1201.0d},
                {-3.0d}
        }), r);
    }

    @Test
    void inverseMatrix() {
        Matrix matrix = new Matrix(new double[][] {
                {5d, 6d, 2.5d, 100d, 15d},
                {36d, 0d, -1000d, 98d, 72d},
                {55.5d, 6.25d, 67d, 3d, 67d},
                {1234d, -12d, -13.5d, 9d, 5d},
                {4d, 8d, 6d, 33d, 1d}
        });

        Matrix inverse = matrix.inverse();

        assertEquals(new Matrix(new double[][]{
                {-6.114241289626634E-4d, 8.174827834335485E-7d, 5.164583968343961E-5d, 8.052326355034686E-4d, 0.0016260687377245065d},
                {-0.05356972614500407d, 0.0013723420684477892d, 0.008217223996830619d, -7.037435233936419E-4d, 0.15770197307613867d},
                {0.0014708941734358134d, -9.577220719659975E-4d, 7.25316765073455E-4d, -5.203434940982196E-6d, -0.0016776295052019711d},
                {0.012688288272019692d, -1.8402854698509327E-4d, -0.0025394158829498874d, 9.213738549751961E-5d, -0.0073940914672138884d},
                {0.003464627658618604d, 8.372680035880278E-4d, 0.013504448213526947d, -6.002954666369999E-4d, -0.014049264110737392d}
        }), inverse);
    }

    @Test
    void inverseSingularMatrix() {
        Matrix matrix = new Matrix(new double[][] {
                {1d},
                {2d},
                {3d}
        });

        assertThrows(AssertionError.class, matrix::inverse);

    }
}

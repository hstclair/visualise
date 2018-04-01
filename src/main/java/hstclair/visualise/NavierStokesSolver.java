package hstclair.visualise;

public class NavierStokesSolver {

    /** size of the two-dimensional array to be simulated */
    final static int N = 80;

    /** pre-computed inverse of size */
    final static double N_INVERSE = 1 / N;

    /** size of the one-dimensional array required to hold the two-dimensional array to be simulated */
    final static int SIZE = (N + 2) * (N + 2);


    double[] u = new double[SIZE];
    double[] v = new double[SIZE];
    double[] u_prev = new double[SIZE];
    double[] v_prev = new double[SIZE];
    double[] dense = new double[SIZE];
    double[] dense_prev = new double[SIZE];

    public double getDx(int x, int y) {
        return u[INDEX(x + 1, y + 1)];
    }

    public double getDy(int x, int y) {
        return v[INDEX(x + 1, y + 1)];
    }

    public void applyForce(int cellX, int cellY, double vx, double vy) {
        double dx = u[INDEX(cellX, cellY)];
        double dy = v[INDEX(cellX, cellY)];

        u[INDEX(cellX, cellY)] = (vx != 0) ? vx : dx;
        v[INDEX(cellX, cellY)] = (vy != 0) ? vy : dy;

    }

    /**
     * compute changes during a single tick
     * @param dt the duration
     * @param visc the viscosity of the fluid
     * @param diff the differential between delta velocity and delta density?
     */
    void tick(double dt, double visc, double diff) {
        vel_step(u, v, u_prev, v_prev, visc, dt);
        dens_step(dense, dense_prev, u, v, diff, dt);
    }

    /**
     * All parameters and return values are in normalized form 0.0 - 1.0
     *
     * @param x
     *          original horizontal position
     * @param y
     *          original vertical position
     * @return warped position, inverse to the motion direction
     */
    double[] getInverseWarpPosition(double x, double y, double scale) {
        double[] result = new double[2];

        int cellX = (int) Math.floor(x * N);
        int cellY = (int) Math.floor(y * N);

        double cellU = (x * N - (cellX)) * N_INVERSE;
        double cellV = (y * N - (cellY)) * N_INVERSE;

        cellX += 1;
        cellY += 2;

        result[0] = (cellU > 0.5) ? lerp(u[INDEX(cellX, cellY)], u[INDEX(cellX + 1,
                cellY)], cellU - 0.5) : lerp(u[INDEX(cellX - 1, cellY)], u[INDEX(cellX,
                cellY)], 0.5 - cellU);
        result[1] = (cellV > 0.5) ? lerp(v[INDEX(cellX, cellY)], v[INDEX(cellX,
                cellY + 1)], cellU) : lerp(v[INDEX(cellX, cellY)], v[INDEX(cellX,
                cellY - 1)], 0.5 - cellV);

        result[0] *= -scale;
        result[1] *= -scale;

        result[0] += x;
        result[1] += y;

        return result;
    }

    /**
     * compute linear interpolation of l between x0 and x1
     * @param x0
     * @param x1
     * @param l value between 0 and 1 (inclusive)
     * @return
     */
    final double lerp(double x0, double x1, double l) {
        return (1 - l) * x0 + l * x1;
    }

    /**
     * compute index into one dimensional array for accessing a
     * @param i
     * @param j
     * @return
     */
    final int INDEX(int i, int j) {
        return i + (N + 2) * j;
    }

    /**
     * swap two buffers??
     * @param x0
     * @param x
     */
    final void SWAP(double[] x0, double[] x) {
        double[] tmp = new double[SIZE];
        System.arraycopy(x0, 0, tmp, 0, SIZE);
        System.arraycopy(x, 0, x0, 0, SIZE);
        System.arraycopy(tmp, 0, x, 0, SIZE);
    }

    /**
     * add sources to a buffer?
     * @param x the buffer to which the sources will be added
     * @param s the sources
     * @param dt elapsed time
     */
    void add_source(double[] x, double[] s, double dt) {
        int i;
        for (i = 0; i < SIZE; i++)
            x[i] += dt * s[i];
    }

    /**
     * compute the diffusion of the fluid/gas
     * @param b the type of boundary value to apply
     * @param x the buffer representing the density?
     * @param x0 the buffer representing the pressure ?
     * @param diff the differential between delta pressure and delta density???
     * @param dt the elapsed time
     */
    void diffuse(int b, double[] x, double[] x0, double diff, double dt) {
        int i, j, k;
        double a = dt * diff * N * N;
        for (k = 0; k < 20; k++) {
            for (i = 1; i <= N; i++) {
                for (j = 1; j <= N; j++) {
                    int cell = INDEX(i, j);
                    int rightNeighbor = INDEX(i + 1, j);
                    int leftNeighbor = INDEX(i - 1, j);
                    int upperNeighbor = INDEX(i, j - 1);
                    int lowerNeighbor = INDEX(i, j + 1);

                    x[cell] = (x0[cell] + a * (x[leftNeighbor] + x[rightNeighbor] + x[upperNeighbor] + x[lowerNeighbor])) / (1 + 4 * a);
                }
            }
            set_bnd(b, x);
        }
    }

    /**
     * compute the movement (advection) within our fluid / gas
     * @param b the type of boundary value to apply
     * @param d
     * @param d0
     * @param velocityX
     * @param velocityY
     * @param dt elapsed time
     */
    void advect(int b, double[] d, double[] d0, double[] velocityX, double[] velocityY, double dt) {
        int i, j, i0, j0, i1, j1;
        double dx, dy, s0, t0, s1, t1, dt0;
        dt0 = dt * N;
        for (i = 1; i <= N; i++) {
            for (j = 1; j <= N; j++) {
                int cell = INDEX(i, j);

                dx = i - dt0 * velocityX[cell];
                dy = j - dt0 * velocityY[cell];

                if (dx < 0.5)
                    dx = 0.5;

                if (dx > N + 0.5)
                    dx = N + 0.5;

                i0 = (int) dx;
                i1 = i0 + 1;

                if (dy < 0.5)
                    dy = 0.5;

                if (dy > N + 0.5)
                    dy = N + 0.5;

                j0 = (int) dy;
                j1 = j0 + 1;

                s1 = dx - i0;
                s0 = 1 - s1;
                t1 = dy - j0;
                t0 = 1 - t1;

                int rightNeighbor = INDEX(i + 1, j);
                int leftNeighbor = INDEX(i - 1, j);
                int upperNeighbor = INDEX(i, j - 1);
                int lowerNeighbor = INDEX(i, j + 1);

                d[cell] = s0 * (t0 * d0[INDEX(i0, j0)] + t1 * d0[INDEX(i0, j1)])
                        + s1 * (t0 * d0[INDEX(i1, j0)] + t1 * d0[INDEX(i1, j1)]);
            }
        }
        set_bnd(b, d);
    }

    /**
     * set the boundary values
     * @param b the type of boundary value to apply:
     *          0  indicates set all boundary values equal to neighboring edge values
     *          1  indicates set left and right boundary equal to negative of neighboring left and right edge
     *             but set top and bottom boundary equal to neighboring top and bottom edge (not-negated)
     *          2  indicates set top and bottom boundary equal to negative of neighboring top and bottom edge
     *             but set left and right boundary equal to neighboring left and right edge (not-negated)
     * @param x the buffer to which the boundary is to be applied
     */
    void set_bnd(int b, double[] x) {
        int i;
        for (i = 1; i <= N; i++) {

            int pLeftBoundary = INDEX(0, i);
            int pLeftEdge = INDEX(1, i);
            int pTopBoundary = INDEX(i, 0);
            int pTopEdge = INDEX(i, 1);

            int pRightBoundary = INDEX(N+1, i);
            int pRightEdge = INDEX(N, i);
            int pBottomEdge = INDEX(i, N);
            int pBottomBoundary = INDEX(i, N+1);

            if (b == 1) {
                x[pLeftBoundary] = -x[pLeftEdge];
                x[pRightBoundary] = -x[pRightEdge];
            } else {
                x[pLeftBoundary] = x[pLeftEdge];
                x[pRightBoundary] = x[pRightEdge];
            }

            if (b == 2) {
                x[pTopBoundary] = -x[pTopEdge];
                x[pBottomBoundary] = -x[pBottomEdge];
            } else {
                x[pTopBoundary] = x[pTopEdge];
                x[pBottomBoundary] = x[pBottomEdge];
            }
        }

        // set boundary corners equal to mean of their boundary neighbors
        x[INDEX(0, 0)] = 0.5 * (x[INDEX(1, 0)] + x[INDEX(0, 1)]);
        x[INDEX(0, N + 1)] = 0.5 * (x[INDEX(1, N + 1)] + x[INDEX(0, N)]);
        x[INDEX(N + 1, 0)] = 0.5 * (x[INDEX(N, 0)] + x[INDEX(N + 1, 1)]);
        x[INDEX(N + 1, N + 1)] = 0.5 * (x[INDEX(N, N + 1)] + x[INDEX(N + 1, N)]);
    }

    /**
     * Compute the density for a single iteration
     * @param x the buffer to receive the results?
     * @param x0 the buffer containing previous results???
     * @param u velocity X??
     * @param v velocity Y??
     * @param diff the differential?
     * @param dt the elapsed time
     */
    void dens_step(double[] x, double[] x0, double[] u, double[] v, double diff,
                   double dt) {
        add_source(x, x0, dt);
        SWAP(x0, x);
        diffuse(0, x, x0, diff, dt);
        SWAP(x0, x);
        advect(0, x, x0, u, v, dt);
    }

    /**
     * Compute the velocity for a single iteration
     * @param u the velocity X to receive the result
     * @param v the velocity Y to receive the result
     * @param u0 the current velocity X
     * @param v0 the current velocity Y
     * @param visc the viscosity of our fluid / gas
     * @param dt the elapsed time
     */
    void vel_step(double[] u, double[] v, double[] u0, double[] v0, double visc,
                  double dt) {
        add_source(u, u0, dt);
        add_source(v, v0, dt);
        SWAP(u0, u);
        diffuse(1, u, u0, visc, dt);
        SWAP(v0, v);
        diffuse(2, v, v0, visc, dt);
        project(u, v, u0, v0);
        SWAP(u0, u);
        SWAP(v0, v);
        advect(1, u, u0, u0, v0, dt);
        advect(2, v, v0, u0, v0, dt);
        project(u, v, u0, v0);
    }

    /**
     *
     * @param u velocity X
     * @param v velocity Y
     * @param p previous velocity X?
     * @param div previous velocity Y?
     */
    void project(double[] u, double[] v, double[] p, double[] div) {
        int i, j, k;
        double h = 1.0 / N;

        for (i = 1; i <= N; i++) {
            for (j = 1; j <= N; j++) {

                int cell = INDEX(i, j);
                int rightNeighbor = INDEX(i + 1, j);
                int leftNeighbor = INDEX(i - 1, j);
                int upperNeighbor = INDEX(i, j - 1);
                int lowerNeighbor = INDEX(i, j + 1);

                div[cell] = -0.5 * h * (u[rightNeighbor] - u[leftNeighbor] + v[lowerNeighbor] - v[upperNeighbor]);
                p[cell] = 0;
            }
        }

        set_bnd(0, div);
        set_bnd(0, p);

        for (k = 0; k < 20; k++) {
            for (i = 1; i <= N; i++) {
                for (j = 1; j <= N; j++) {
                    int cell = INDEX(i, j);
                    int rightNeighbor = INDEX(i + 1, j);
                    int leftNeighbor = INDEX(i - 1, j);
                    int upperNeighbor = INDEX(i, j - 1);
                    int lowerNeighbor = INDEX(i, j + 1);

                    p[cell] = (div[cell] + p[leftNeighbor] + p[rightNeighbor] + p[upperNeighbor] + p[lowerNeighbor]) / 4;
                }
            }
            set_bnd(0, p);
        }


        for (i = 1; i <= N; i++) {
            for (j = 1; j <= N; j++) {
                int cell = INDEX(i, j);
                int rightNeighbor = INDEX(i + 1, j);
                int leftNeighbor = INDEX(i - 1, j);
                int upperNeighbor = INDEX(i, j - 1);
                int lowerNeighbor = INDEX(i, j + 1);

                u[cell] -= 0.5 * (p[rightNeighbor] - p[leftNeighbor]) / h;
                v[cell] -= 0.5 * (p[lowerNeighbor] - p[upperNeighbor]) / h;
            }
        }

        set_bnd(1, u);
        set_bnd(2, v);
    }

}

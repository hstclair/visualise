package hstclair.visualise;


/**
 * Implementation of the fluid solver model described by Jos Stam in Real-Time Fluid Dynamics for Games
 * {@see <a href="http://www.dgp.toronto.edu/people/stam/reality/Research/pdf/GDC03.pdf">this</a>}
 *
 * User: hstclair
 * Date: 12/18/12 8:36 PM
 */
public class MyFluidSolver {
    static final int side = 128;
    static final int size = (side + 2) * (side + 2);

    public double dt;   // the time between snapshots

    public double[] u = new double [size];
    public double[] v = new double [size];
    public double[] u_prev = new double [size];
    public double[] v_prev = new double [size];
    public double[] density = new double [size];
    public double[] density_prev = new double [size];

    void setup(int gridSize, double interval) {

        // do nothing for now...
    }

    void reset() {
        // do nothing for now...
    }

    /**
     * index calculation
     *
     * @param i column
     * @param j row
     * @return
     */
    int IX(int i, int j) {
        return i + (side + 2) * j;
    }

    /**
     * add sources to the provided fluid simulation array
     *
     * @param x the fluid simulation array to which sources will be added
     * @param s the array of 'sources'
     * @param dt the time step value (the time elapsed since the last update)
     */
    void add_source(int side, double[] x, double[] s, double dt) {
        for (int index = 0; index < x.length; index++)
            x[index] += dt * s[index];
    }

    void applyForce(int x, int y, double dx, double dy) {
        this.u[IX(x, y)] += dx;
        this.v[IX(x, y)] += dy;
    }

    void applyDensity(int x, int y, double density) {
        this.density[IX(x, y)] += density;
    }

    void diffuse(int side, int b, double[] x, double[] x0, double diff, double dt) {
        double a = dt * diff * side * side;


        for (int k=0; k < 20; k++) {
            for (int i = 1; i <= side; i++) {
                for (int j = 1; j<= side; j++) {
                    int row = side + 2;
                    int index = i + row * j;

                    x[index] = (x0[index] + a * (x[index-1] + x[index+1] + x[index - row] + x[index + row])) / (1 + 4 * a);
                }
            }

            set_bnd(side, b, x);
        }
    }

    void advect(int side, int b, double[] d, double[] d0, double[] u, double[] v, double dt) {
        double dt0;

        dt0 = dt * side;

        for (int i = 1; i <= side; i++) {
            for (int j = 1; j<= side; j++) {
                double x = i - dt0 * u[IX(i, j)];
                double y = j-dt0*v[IX(i,j)];

                if (x < 0.5) x = 0.5;
                if (x > side+0.5) x= side + 0.5;
                int i0=(int) x;
                int i1 = i0 + 1;
                if (y < 0.5) y = 0.5;
                if (y > side+0.5) y = side + 0.5;
                int j0 = (int) y;
                int j1 = j0 + 1;

                double s1 = x-i0;
                double s0 = 1-s1;
                double t1 = y - j0;
                double t0 = 1 - t1;

                d[IX(i,j)] = s0*(t0*d0[IX(i0, j0)] + t1*d0[IX(i0, j1)]) +
                        s1*(t0*d0[IX(i1, j0)] + t1*d0[IX(i1, j1)]);
            }
        }

        set_bnd(side, b, d);
    }

    public void tick(double dt, double diffusion, double viscosity) {
        vel_step(side, u, v, u_prev, v_prev, viscosity, dt);
        dens_step(side, u, v, u_prev, v_prev, diffusion, dt);
    }

    void dens_step(int side, double[] x, double[] x0, double[] u, double[] v, double diff, double dt) {
        add_source(side, x, x0, dt);

        diffuse(side, 0, x0, x, diff, dt);

        advect(side, 0, x, x0, u, v, dt);
    }

    void vel_step(int side, double[] u, double[] v, double[] u0, double[] v0, double visc, double dt) {
        add_source(side, u, u0, dt);
        add_source(side, v, v0, dt);
        diffuse(side, 1, u0, u, visc, dt);
        diffuse(side, 2, v0, v, visc, dt);
        project(side, u0, v0, u, v);
        advect(side, 1, u, u0, u0, v0, dt);
        advect(side, 2, v, v0, u0, v0, dt);
        project(side, u, v, u0, v0);
    }

    void project(int side, double[] u, double[] v, double[] p, double[] div) {
        double h = 1.0 / side;

        for (int i = 1; i <= side; i++) {
            for (int j = 1; j <= side; j++) {
                div[IX(i, j)] = -0.5 * h * (u[IX(i + 1, j)] - u[IX(i - 1, j)] + v[IX(i, j + 1)] - v[IX(i, j - 1)]);
                p[IX(i, j)] = 0;
            }
        }

        set_bnd(side, 0, div);

        set_bnd(side, 0, p);

        for (int k = 0; k < 20; k++) {
            for ( int i = 1; i <= side; i++) {
                for ( int j = 1; j <= side; j++) {
                    p[IX(i,j)] = (div[IX(i, j)] + p[IX(i - 1,j)] + p[IX(i + 1, j)] + p[IX(i, j - 1)] + p[IX(i, j + 1)]) / 4;
                }
            }

            set_bnd(side, 1, u);
            set_bnd(side, 2, v);
        }
    }

    void set_bnd(int side, int b, double[] x) {
        for (int i = 1; i <= side; i++) {
            x[IX(0,  i)] = b == 1 ? -x[IX(1,i)] : x[IX(1,i)];
            x[IX(side + 1, i)] = b == 1 ? -x[IX(side, i)] : x[IX(side, i)];
            x[IX(i, 0)] = b == 2 ? -x[IX(i, 1)] : x[IX(i,1)];
            x[IX(i, side + 1)] = b == 2 ? -x[IX(i, side)] : x[IX(i, side)];
        }

        x[IX(0, 0)] = 0.5 * (x[IX(1, 0)] + x[IX(0, 1)]);
        x[IX(0, side + 1)] = 0.5 * (x[IX(1, side + 1)] + x[IX(0, side)]);
        x[IX(side + 1, 0)] = 0.5 * (x[IX(side, 0)] + x[IX(side + 1, 1)]);
        x[IX(side + 1, side + 1)] = 0.5 * (x[IX(side, side + 1)] + x[IX(side + 1, side)]);
    }
}

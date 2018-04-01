package hstclair.visualise;

import java.util.Random;

/**
 * Jos Stam style fluid solver with vorticity confinement
 * and buoyancy force.
 *
 * @author Alexander McKenzie
 * @version 1.0
 **/

public class FluidSolver
{
    Random random = new Random(System.currentTimeMillis());

    int row;
    int edgeLength, size;
    int nSquared;

    //    float visc = 0;
//    float visc = 0.000085f;
//    float visc = 0.00015f;
//    double visc = .00008;   // non-jupiter
    double visc = .000002;   // jupiter
    double diff = 0.0d;

    double[] tmp;

    double[] density, densityOld;
    double[] u, uOld;
    double[] v, vOld;
    double[] curl;

    Advector advector;

    double meanDensity;
    double maxDensity;
    double minDensity;

    int repeats = 20;


    public FluidSolver(int edgeLength, double dt, double viscosity, double diffusion) {
        setup(edgeLength);
        visc = viscosity;
        diff = diffusion;
    }

    /**
     * Set the grid size.
     **/
    public void setup(int n)
    {
        this.edgeLength = n;
        this.nSquared = n*n;
        this.row = n+2;
        size = (n + 2) * (n + 2);

        advector = new AdvectorNew(this, edgeLength, size);

        reset();
    }


    /**
     * Reset the datastructures.
     * We use 1d arrays for speed.
     **/

    public void reset()
    {
        density = new double[size];
        densityOld = new double[size];
        u    = new double[size];
        uOld = new double[size];
        v    = new double[size];
        vOld = new double[size];
        curl = new double[size];

        for (int i = 0; i < size; i++)
        {
            u[i] = uOld[i] = v[i] = vOld[i] = 0.0f;
            density[i] = densityOld[i] = curl[i] = 0.0f;
        }
    }


    /**
     * Calculate the buoyancy force as part of the velocity solver.
     * Fbuoy = -a*d*Y + b*(T-Tamb)*Y where Y = (0,1). The constants
     * a and b are positive with appropriate (physically meaningful)
     * units. T is the temperature at the current cell, Tamb is the
     * average temperature of the fluid grid. The density d provides
     * a mass that counteracts the buoyancy force.
     *
     * In this simplified implementation, we say that the tempterature
     * is synonymous with density (since smoke is *hot*) and because
     * there are no other heat sources we can just use the density
     * field instead of a new, seperate temperature field.
     *
     * @param Fbuoy Array to store buoyancy force for each cell.
     **/
    public void buoyancy(double [] Fbuoy)
    {
        double ambientTemperature = 0;
        double a = 0.000625f;
        double b = 0.025f;

        // sum all temperatures
        for (int rowIndex = row; rowIndex <= size - row; rowIndex += row) {
            for (int col = 1; col <= edgeLength; col++) {
                ambientTemperature += density[rowIndex + col];
            }
        }

        // get average temperature
        ambientTemperature /= nSquared;

        double q = b*ambientTemperature;
        double p = a-b;

        // for each cell compute buoyancy force
        for (int rowIndex = row; rowIndex <= size - row; rowIndex += row) {
            for (int col = 1; col < edgeLength; col++) {
                int index = rowIndex + col;
//                Fbuoy[index] = a * d[index] -b * (d[index] - ambientTemperature);

                // fbuoy = a*x - b*(x-amb)
                // fbuoy = a*x -b*x + b*amb
                // fbuoy = (a-b)*x + b*amb

                // p = a-b
                // q = b*amb

                // fbuoy = p*x + q

                Fbuoy[index] = p* density[index] + q;
            }
        }
    }


    /**
     * Calculate the curl at position (i, j) in the fluid grid.
     * Physically this represents the vortex strength at the
     * cell. Computed as follows: w = (del x U) where U is the
     * velocity vector at (i, j).
     *
     * @param index the index of the cell we're working with
     **/

    public double curl(int index /* int i, int j */)
    {

        double uDiff = u[index + row] - u[index - row];
        double vDiff = v[index + 1] - v[index - 1];

        return (uDiff-vDiff) * .5;
    }


    /**
     * Calculate the vorticity confinement force for each cell
     * in the fluid grid. At a point (i,j), Fvc = N x w where
     * w is the curl at (i,j) and N = del |w| / |del |w||.
     * N is the vector pointing to the vortex center, hence we
     * add force perpendicular to N.
     *
     * @param Fvc_x The array to store the x component of the
     *        vorticity confinement force for each cell.
     * @param Fvc_y The array to store the y component of the
     *        vorticity confinement force for each cell.
     **/

    public void vorticityConfinement(double[] Fvc_x, double[] Fvc_y)
    {
        double dw_dx, dw_dy;
        double length;
        double v;

        int rowIndex = 0;
        // Calculate magnitude of curl(u,v) for each cell. (|w|)
        for (int i = 1; i <= edgeLength; i++) {
            rowIndex += row;
            for (int col = 1; col <= edgeLength; col++) {
                int index = rowIndex + col;

                curl[index] = Math.abs(curl(index));
            }
        }

        int i = 0, j = 0;

        for (rowIndex = row << 1; rowIndex < size - row; rowIndex += row) {
            i++;
            for (int col = 2; col < edgeLength; col++) {
                j++;

                int index = rowIndex + col;
                // Find derivative of the magnitude (n = del |w|)
                dw_dx = (curl[index + 1] - curl[index-1]) * 0.5f;
                dw_dy = (curl[index + row] - curl[index-row]) * 0.5f;

                // Calculate vector length. (|n|)
                // Add small factor to prevent divide by zeros.
                length = (float) Math.sqrt(dw_dx * dw_dx + dw_dy * dw_dy) + 0.000001f;

                // N = ( n/|n| )
                dw_dx /= length;
                dw_dy /= length;

                v = curl(rowIndex + col);

                // N x w
                Fvc_x[index] = dw_dy * -v;
                Fvc_y[index] = dw_dx *  v;
            }
        }
    }

    public void applyDensity(int x, int y, double density) {
        this.density[I(x, y)] += density;
    }

    public void applyForce(int x, int y, double dx, double dy) {
        this.u[I(x, y)] += dx;
        this.v[I(x, y)] += dy;
    }

    public void tick(double dt) {
        velocitySolver(dt);
        densitySolver(dt);
    }


    /**
     * The basic velocity solving routine as described by Stam.
     **/

    public void velocitySolver(double dt)
    {
        // add velocity that was input by mouse
        addSource(u, uOld, dt);
        addSource(v, vOld, dt);

        // add in vorticity confinement force
        vorticityConfinement(uOld, vOld);
        addSource(u, uOld, dt);
        addSource(v, vOld, dt);

        // add in buoyancy force
        buoyancy(vOld);
        addSource(v, vOld, dt);

        // swapping arrays for economical mem use
        // and calculating diffusion in velocity.
        swapU();
        diffuse(0, u, uOld, visc, dt);

        swapV();
        diffuse(0, v, vOld, visc, dt);

        // we create an incompressible field
        // for more effective advection.
        project(u, v, uOld, vOld);

        swapU(); swapV();

        // self advect velocities
        advect(1, u, uOld, uOld, vOld, dt);
        advect(2, v, vOld, uOld, vOld, dt);

        // make an incompressible field
        project(u, v, uOld, vOld);

        // clear all input velocities for next frame
        for (int i = 0; i < size; i++){ uOld[i] = 0; vOld[i] = 0; }
    }


    /**
     * The basic density solving routine.
     **/

    public void densitySolver(double dt)
    {
        // add density inputted by mouse
        addSource(density, densityOld, dt);
        swapD();

        diffuse(0, density, densityOld, diff, dt);
        swapD();

        advect(0, density, densityOld, u, v, dt);

        // clear input density array for next frame
        for (int i = 0; i < size; i++) densityOld[i] = 0;

        meanDensity = 0;
        maxDensity = 0;

        for (int i = 0; i < size; i++) {
            double densityValue = density[i];
            maxDensity = Math.max(maxDensity, densityValue);
            minDensity = Math.min(minDensity, densityValue);
            meanDensity += densityValue;
        }

        meanDensity /= size;

    }

    private void addSource(double[] x, double[] x0, double dt)
    {
        for (int i = 0; i < size; i++)
        {
            x[i] += dt * x0[i];
        }
    }


    /**
     * Calculate the input array after advection. We start with an
     * input array from the previous timestep and an and output array.
     * For all grid cells we need to calculate for the next timestep,
     * we trace the cell's center position backwards through the
     * velocity field. Then we interpolate from the grid of the previous
     * timestep and assign this value to the current grid cell.
     *
     * @param b Flag specifying how to handle boundries.
     * @param d Array to store the advected field.
     * @param d0 The array to advect.
     * @param du The x component of the velocity field.
     * @param dv The y component of the velocity field.
     **/

    private void advect(int b, double[] d, double[] d0, double[] du, double[] dv, double dt)
    {
        advector.advect(b, d, d0, du, dv, dt);

        setBoundry(b, d);
    }



    /**
     * Recalculate the input array with diffusion effects.
     * Here we consider a stable method of diffusion by
     * finding the densities, which when diffused backward
     * in time yield the same densities we started with.
     * This is achieved through use of a linear solver to
     * solve the sparse matrix built from this linear system.
     *
     * @param b Flag to specify how boundries should be handled.
     * @param c The array to store the results of the diffusion
     * computation.
     * @param c0 The input array on which we should compute
     * diffusion.
     * @param diff The factor of diffusion.
     **/

    private void diffuse(int b, double[] c, double[] c0, double diff, double dt)
    {
        double a = dt * diff * nSquared;
        linearSolver(b, c, c0, a, 1 + 4 * a);
    }


    /**
     * Use project() to make the velocity a mass conserving,
     * incompressible field. Achieved through a Hodge
     * decomposition. First we calculate the divergence field
     * of our velocity using the mean finite differnce approach,
     * and apply the linear solver to compute the Poisson
     * equation and obtain a "height" field. Now we subtract
     * the gradient of this field to obtain our mass conserving
     * velocity field.
     *
     * @param x The array in which the x component of our final
     * velocity field is stored.
     * @param y The array in which the y component of our final
     * velocity field is stored.
     * @param p A temporary array we can use in the computation.
     * @param div Another temporary array we use to hold the
     * velocity divergence field.
     *
     **/

    void project(double[] x, double[] y, double[] p, double[] div)
    {
        int halfN = (edgeLength >> 1);
        double negOneOverTwoN = -0.5/ edgeLength;

        for (int i = 1; i <= edgeLength; i++)
        {
            for (int j = 1; j <= edgeLength; j++)
            {
                int index = I(i,j);

                div[index] = (x[index+1] - x[index-1]
                        + y[index+row] - y[index-row])
                        * negOneOverTwoN;
                p[index] = 0;
            }
        }

        setBoundry(0, div);
        setBoundry(0, p);

        linearSolver(0, p, div, 1, 4);

        for (int i = 1; i <= edgeLength; i++)
        {
            for (int j = 1; j <= edgeLength; j++)
            {
                int index = I(i,j);

                x[index] -= halfN * (p[index+1] - p[index-1]);
                y[index] -= halfN * (p[index+row] - p[index-row]);
            }
        }

        setBoundry(1, x);
        setBoundry(2, y);
    }


    /**
     * Iterative linear system solver using the Gauss-sidel
     * relaxation technique. Room for much improvement here...
     *
     **/

    void linearSolver(int b, double[] x, double[] x0, double a, double c)
    {
        int maxCol = edgeLength + 1;
        int maxIndex = size - row;

        for (int k = 0; k < repeats; k++) {
            for (int col = 1; col < maxCol; col++) {
                for (int index = row + col; index < maxIndex; index += row) {
                    x[index] = (a * ( x[index-1] + x[index+1] + x[index- row] + x[index+ row]) + x0[index]) / c;
                }
            }
            setBoundry(b, x);
        }
    }


    // specifies simple boundry conditions for wrapping around a sphere east to west.
    void setBoundry(int b, double[] x)
    {
        for (int i = 0; i <= edgeLength + 1; i++)
        {
            x[I(  i, 0  )] = x[I(i, edgeLength)];
            x[I( i, edgeLength +1)] = + x[I(i,1)];
        }
    }

    // util array swapping methods
    public void swapU(){ tmp = u; u = uOld; uOld = tmp; }
    public void swapV(){ tmp = v; v = vOld; vOld = tmp; }
    public void swapD(){ tmp = density; density = densityOld; densityOld = tmp; }

    public int INDEX(int x, int y) {
        return I(x, y);
    }

    // util method for indexing 1d arrays
    int I(int i, int j){ return i + (edgeLength + 2) * j; }
}

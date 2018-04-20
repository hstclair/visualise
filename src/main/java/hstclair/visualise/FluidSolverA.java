package hstclair.visualise;

import hstclair.visualise.grid.DoubleGrid;

import java.util.Random;

/**
 * Jos Stam style fluid solver with vorticity confinement
 * and buoyancy force.
 *
 * @author Alexander McKenzie
 * @version 1.0
 **/

public class FluidSolverA
{
    Random random = new Random(System.currentTimeMillis());

//    int row;
//    int edgeLength, size;
//    int nSquared;

    //    float visc = 0;
//    float visc = 0.000085f;
//    float visc = 0.00015f;
//    double visc = .00008;   // non-jupiter
//    double visc = .000002;   // jupiter
//    double diff = 0.0d;


//    DoubleGrid tmp;
//    DoubleGrid density;
//    DoubleGrid densityOld;
//    DoubleGrid u;
//    DoubleGrid uOld;
//    DoubleGrid v;
//    DoubleGrid vOld;
//    DoubleGrid curl;


//    double[] tmp;
//
//    double[] density, densityOld;
//    double[] u, uOld;
//    double[] v, vOld;
//    double[] curl;

    Advector advector;

//    double meanDensity;
//    double maxDensity;
//    double minDensity;
//
//    int repeats = 20;


    public FluidSolverA() {
//        setup(edgeLength);
//        visc = viscosity;
//        diff = diffusion;
    }

    /**
     * Set the grid size.
     **/
    public void setup(int n)
    {
//        this.edgeLength = n;
//        this.nSquared = n*n;
//        this.row = n+2;
//        size = (n + 2) * (n + 2);
//
//        advector = new AdvectorNewA(this, edgeLength, size);
//
//        reset();
    }


    /**
     * Reset the datastructures.
     * We use 1d arrays for speed.
     **/

    public void reset()
    {
//        density = new DoubleGrid(edgeLength);
//        densityOld = new DoubleGrid(edgeLength);
//        u    = new DoubleGrid(edgeLength);
//        uOld = new DoubleGrid(edgeLength);
//        v    = new DoubleGrid(edgeLength);
//        vOld = new DoubleGrid(edgeLength);
//        curl = new DoubleGrid(edgeLength);
//
//        for (int i = 0; i < size; i++)
//        {
//            u.grid[i] = uOld.grid[i] = v.grid[i] = vOld.grid[i] = 0.0f;
//            density.grid[i] = densityOld.grid[i] = curl.grid[i] = 0.0f;
//        }
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
    public void buoyancy(DoubleGrid Fbuoy, DoubleGrid density)
    {
        double ambientTemperature = 0;
        double a = 0.000625f;
        double b = 0.025f;

        int row = Fbuoy.rowLength;
        int size = Fbuoy.size;
        int edgeLength = Fbuoy.edgeLength;
        int nSquared = edgeLength*edgeLength;

        // sum all temperatures
        for (int rowIndex = row; rowIndex <= size - row; rowIndex += row) {
            for (int col = 1; col <= edgeLength; col++) {
                ambientTemperature += density.grid[rowIndex + col];
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

                Fbuoy.grid[index] = p* density.grid[index] + q;
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

    public double curl(int index, DoubleGrid u, DoubleGrid v)
    {

        int row = u.rowLength;

        double uDiff = u.grid[index + row] - u.grid[index - row];
        double vDiff = v.grid[index + 1] - v.grid[index - 1];

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

    public void vorticityConfinement(DoubleGrid Fvc_x, DoubleGrid Fvc_y, DoubleGrid curl, DoubleGrid u, DoubleGrid v)
    {
        double dw_dx, dw_dy;
        double length;
        double dblV;

        int row = Fvc_x.rowLength;
        int edgeLength = Fvc_x.edgeLength;
        int size = Fvc_x.size;

        int rowIndex = 0;
        // Calculate magnitude of curl(u,v) for each cell. (|w|)
        for (int i = 1; i <= edgeLength; i++) {
            rowIndex += row;
            for (int col = 1; col <= edgeLength; col++) {
                int index = rowIndex + col;

                curl.grid[index] = Math.abs(curl(index, u, v));
            }
        }

        int i = 0, j = 0;

        for (rowIndex = row << 1; rowIndex < size - row; rowIndex += row) {
            i++;
            for (int col = 2; col < edgeLength; col++) {
                j++;

                int index = rowIndex + col;
                // Find derivative of the magnitude (n = del |w|)
                dw_dx = (curl.grid[index + 1] - curl.grid[index-1]) * 0.5f;
                dw_dy = (curl.grid[index + row] - curl.grid[index-row]) * 0.5f;

                // Calculate vector length. (|n|)
                // Add small factor to prevent divide by zeros.
                length = (float) Math.sqrt(dw_dx * dw_dx + dw_dy * dw_dy) + 0.000001f;

                // N = ( n/|n| )
                dw_dx /= length;
                dw_dy /= length;

                dblV = curl(rowIndex + col, u, v);

                // N x w
                Fvc_x.grid[index] = dw_dy * -dblV;
                Fvc_y.grid[index] = dw_dx *  dblV;
            }
        }
    }

    public void applyDensity(int x, int y, DoubleGrid densityGrid, double density) {
        densityGrid.grid[I(x, y, densityGrid.edgeLength)] += density;
    }

    public void applyForce(int x, int y, double dx, double dy, DoubleGrid u, DoubleGrid v) {
        u.grid[I(x, y, u.edgeLength)] += dx;
        v.grid[I(x, y, v.edgeLength)] += dy;
    }

    public void tick(double dt, double viscosity, double diffusion, int repeats, DoubleGrid u, DoubleGrid v, DoubleGrid uOld, DoubleGrid vOld, DoubleGrid curl, DoubleGrid density, DoubleGrid densityOld) {
        velocitySolver(dt, viscosity, repeats, u, v, uOld, vOld, curl, density);
        densitySolver(dt, diffusion, density, densityOld, u, v, repeats);
    }


    /**
     * The basic velocity solving routine as described by Stam.
     **/

    public void velocitySolver(double dt, double viscosity, int repeats, DoubleGrid u, DoubleGrid v, DoubleGrid uOld, DoubleGrid vOld, DoubleGrid curl, DoubleGrid density)
    {
        // add velocity that was input by mouse
        addSource(u, uOld, dt);
        addSource(v, vOld, dt);

        // add in vorticity confinement force
        vorticityConfinement(uOld, vOld, curl, u, v);
        addSource(u, uOld, dt);
        addSource(v, vOld, dt);

        // add in buoyancy force
        buoyancy(vOld, density);
        addSource(v, vOld, dt);

        // swapping arrays for economical mem use
        // and calculating diffusion in velocity.
        swap(u, uOld);
        diffuse(0, u, uOld, viscosity, dt, repeats);

        swap(v, vOld);
        diffuse(0, v, vOld, viscosity, dt, repeats);

        // we create an incompressible field
        // for more effective advection.
        project(u, v, uOld, vOld, repeats);

        swap(u, uOld); swap(v, vOld);

        // self advect velocities
        advect(1, u, uOld, uOld, vOld, dt);
        advect(2, v, vOld, uOld, vOld, dt);

        // make an incompressible field
        project(u, v, uOld, vOld, repeats);

        int size = uOld.size;

        // clear all input velocities for next frame
        for (int i = 0; i < size; i++){ uOld.grid[i] = 0; vOld.grid[i] = 0; }
    }


    /**
     * The basic density solving routine.
     **/

    public void densitySolver(double dt, double diffusion, DoubleGrid density, DoubleGrid densityOld, DoubleGrid u, DoubleGrid v, int repeats)
    {
        // add density inputted by mouse
        addSource(density, densityOld, dt);
        swap(density, densityOld);

        diffuse(0, density, densityOld, diffusion, dt, repeats);
        swap(density, densityOld);

        advect(0, density, densityOld, u, v, dt);

        int size = densityOld.size;

        // clear input density array for next frame
        for (int i = 0; i < size; i++) densityOld.grid[i] = 0;

//        meanDensity = 0;
//        maxDensity = 0;
//
//        for (int i = 0; i < size; i++) {
//            double densityValue = density.grid[i];
//            maxDensity = Math.max(maxDensity, densityValue);
//            minDensity = Math.min(minDensity, densityValue);
//            meanDensity += densityValue;
//        }
//
//        meanDensity /= size;

    }

    public void addSource(DoubleGrid x, DoubleGrid x0, double dt)
    {
        int size = x.size;

        for (int i = 0; i < size; i++)
        {
            x.grid[i] += dt * x0.grid[i];
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

    public void advect(int b, DoubleGrid d, DoubleGrid d0, DoubleGrid du, DoubleGrid dv, double dt)
    {
        if (advector == null || advector.getEdgeLength() != d.edgeLength)
            advector = new AdvectorNewA(d.edgeLength, d.size);

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

    public void diffuse(int b, DoubleGrid c, DoubleGrid c0, double diff, double dt, int repeats)
    {
        double a = dt * diff * c.area;
        linearSolver(b, c, c0, a, 1 + 4 * a, repeats);
    }


    public void divergence(DoubleGrid x, DoubleGrid y, DoubleGrid p, DoubleGrid div) {

        int edgeLength = x.edgeLength;
        int rowLength = x.rowLength;

        double negOneOverTwoN = -0.5/ edgeLength;

        for (int i = 1; i <= edgeLength; i++)
        {
            for (int j = 1; j <= edgeLength; j++)
            {
                int index = I(i,j, edgeLength);

                div.grid[index] = (x.grid[index+1] - x.grid[index-1]
                        + y.grid[index+rowLength] - y.grid[index-rowLength])
                        * negOneOverTwoN;
                p.grid[index] = 0;
            }
        }

        setBoundry(0, div);
        setBoundry(0, p);
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

    public void project(DoubleGrid x, DoubleGrid y, DoubleGrid p, DoubleGrid div, int repeats)
    {
        int edgeLength = x.edgeLength;
        int rowLength = x.rowLength;

        int halfN = (edgeLength >> 1);
//        double negOneOverTwoN = -0.5/ edgeLength;

        divergence(x, y, p, div);

        linearSolver(0, p, div, 1, 4, repeats);

        compressionCounterForce(x, y, p);

//        for (int i = 1; i <= edgeLength; i++)
//        {
//            for (int j = 1; j <= edgeLength; j++)
//            {
//                int index = I(i,j,edgeLength);
//
//                x.grid[index] -= halfN * (p.grid[index+1] - p.grid[index-1]);
//                y.grid[index] -= halfN * (p.grid[index+rowLength] - p.grid[index-rowLength]);
//            }
//        }
//
//        setBoundry(1, x);
//        setBoundry(2, y);
    }

    public void compressionCounterForce(DoubleGrid x, DoubleGrid y, DoubleGrid p) {

        int edgeLength = x.edgeLength;
        int rowLength = x.rowLength;
        int halfN = edgeLength >> 1;

        for (int i = 1; i <= edgeLength; i++)
        {
            for (int j = 1; j <= edgeLength; j++)
            {
                int index = I(i,j,edgeLength);

                x.grid[index] -= halfN * (p.grid[index+1] - p.grid[index-1]);
                y.grid[index] -= halfN * (p.grid[index+rowLength] - p.grid[index-rowLength]);
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

    public void linearSolver(int b, DoubleGrid x, DoubleGrid x0, double a, double c, int repeats)
    {
        int maxCol = x.edgeLength + 1;
        int maxIndex = x.size - x.rowLength;

        int rowLength = x.rowLength;

        for (int k = 0; k < repeats; k++) {
            for (int col = 1; col < maxCol; col++) {
                for (int index = rowLength + col; index < maxIndex; index += rowLength) {
                    x.grid[index] = (a * ( x.grid[index-1] + x.grid[index+1] + x.grid[index- rowLength] + x.grid[index+ rowLength]) + x0.grid[index]) / c;
                }
            }
            setBoundry(b, x);
        }
    }


    // specifies simple boundry conditions for wrapping around a sphere east to west.
    public void setBoundry(int b, DoubleGrid x)
    {
        int edgeLength = x.edgeLength;

        for (int i = 0; i <= edgeLength + 1; i++)
        {
            x.grid[I(  i, 0  , edgeLength)] = x.grid[I(i, edgeLength, edgeLength)];
            x.grid[I( i, edgeLength +1, edgeLength)] = + x.grid[I(i,1, edgeLength)];
        }
    }

    public void swap(DoubleGrid oldGrid, DoubleGrid newGrid) {
        double[] tmp = oldGrid.grid;
        oldGrid.grid = newGrid.grid;
        newGrid.grid = tmp;
    }

//    // util array swapping methods
//    public void swapU(){ tmp = u; u = uOld; uOld = tmp; }
//    public void swapV(){ tmp = v; v = vOld; vOld = tmp; }
//    public void swapD(){ tmp = density; density = densityOld; densityOld = tmp; }

//    public int INDEX(int x, int y) {
//        return I(x, y);
//    }

    // util method for indexing 1d arrays
    int I(int i, int j, int edgeLength){ return i + (edgeLength + 2) * j; }
}

package hstclair.visualise;

import hstclair.visualise.component.*;
import hstclair.visualise.grid.DoubleGrid;

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

    FluidSolverA fluidSolverA = new FluidSolverA();

    VorticitySolver vorticityConfinementSolver;
    LinearSolver linearSolver;
    Projector projector;
    Boundary boundary;
    Advector advector;
    BuoyancySolver buoyancySolver;


    int row;
    int edgeLength, size;
    int nSquared;

    double visc = .000002;   // jupiter
    double diffusion = 0.0d;

    DoubleGrid tmp;
    DoubleGrid density;
    DoubleGrid densityOld;
    DoubleGrid u;
    DoubleGrid uOld;
    DoubleGrid v;
    DoubleGrid vOld;
    DoubleGrid curl;

    double meanDensity;
    double maxDensity;
    double minDensity;

    int repeats = 20;

//    boolean useOldDensitySolver = false;
//    boolean useOldAdvection = false;
//    boolean useOldVelocitySolver = false;
//    boolean useOldDiffusor = false;
//    boolean useOldProjection = false;
//    boolean useOldVorticity = false;
//    boolean useOldBuoyancy = false;


    public FluidSolver(int edgeLength, double dt, double viscosity, double diffusion) {

        setup(edgeLength);

        visc = viscosity;
        this.diffusion = diffusion;
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

        density = new DoubleGrid(edgeLength);
        densityOld = new DoubleGrid(edgeLength);
        u = new DoubleGrid(edgeLength);
        uOld = new DoubleGrid(edgeLength);
        v = new DoubleGrid(edgeLength);
        vOld = new DoubleGrid(edgeLength);
        curl = new DoubleGrid(edgeLength);

        advector = new AdvectorNew();

//        boundary = new CyclicalYBoundary(u);

        boundary = new OldBoundary();

        buoyancySolver = new AlexanderMcKinzieBuoyancySolverImpl(edgeLength, row);
        vorticityConfinementSolver = new AlexanderMcKinzieVorticityConfinementSolver(edgeLength, 1);
        linearSolver = new GaussSeidelLinearSolver(repeats, boundary);
        projector = new AlexanderMcKinzieProjector(linearSolver, boundary);
    }


    public void applyDensity(int x, int y, double density) {
        this.density.add(x, y, density);
    }

    public void applyForce(int x, int y, double dx, double dy) {
        this.u.add(x, y, dx);
        this.v.add(x, y, dy);
    }

    public void tick(double dt) {
        velocitySolver(dt);
        densitySolver(dt);
        computeDensityStatistics();
        clearArrays();
    }

    void clearArrays() {
//        uOld.clear();
//        vOld.clear();
//        densityOld.clear();
    }

    void computeDensityStatistics() {
        meanDensity = 0;
        maxDensity = 0;

        for (int i = 0; i < size; i++) {
            double densityValue = density.grid[i];
            maxDensity = Math.max(maxDensity, densityValue);
            minDensity = Math.min(minDensity, densityValue);
            meanDensity += densityValue;
        }

        meanDensity /= size;
    }


    /**
     * The basic velocity solving routine as described by Stam.
     **/

    void velocitySolver(double dt) {

//        if (useOldVelocitySolver) {
//            fluidSolverA.velocitySolver(dt, visc, repeats, u, v, uOld, vOld, curl, density);
//
//            return;
//        }

        // add velocity that was input by mouse
        u.add(uOld, dt);
        v.add(vOld, dt);

        // add in vorticity confinement force

//        if (useOldVorticity)
//            fluidSolverA.vorticityConfinement(uOld, vOld, curl, u, v);
//        else
            vorticityConfinementSolver.solve(uOld, vOld, u, v);

        u.add(uOld, dt);
        v.add(vOld, dt);

        // add in buoyancy force
//        if (useOldBuoyancy)
//            fluidSolverA.buoyancy(vOld, density);
//        else
            buoyancySolver.buoyancy(vOld, density);
        v.add(vOld, dt);

        // swapping arrays for economical mem use
        // and calculating diffusion in velocity.
        swapU();
        diffuse(0, u, uOld, visc, dt);

        swapV();
        diffuse(0, v, vOld, visc, dt);

        // we create an incompressible field
        // for more effective advection.

//        if (useOldProjection)
//            fluidSolverA.project(u, v, uOld, vOld, repeats);
//        else
            projector.project(u, v, uOld, vOld);

        swapU(); swapV();

        // self advect velocities
        advect(1, u, uOld, uOld, vOld, dt);
        advect(2, v, vOld, uOld, vOld, dt);

        // make an incompressible field
//        if (useOldProjection)
//            fluidSolverA.project(u, v, uOld, vOld, repeats);
//        else
            projector.project(u, v, uOld, vOld);

        uOld.clear();
        vOld.clear();
    }


    /**
     * The basic density solving routine.
     **/

    void densitySolver(double dt)
    {
//
//        if (useOldDensitySolver) {
//            fluidSolverA.densitySolver(dt, diffusion, density, densityOld, u, v, repeats);
//            return;
//        }


        // add density inputted by mouse
        density.add(densityOld, dt);
        swapD();

        diffuse(0, density, densityOld, diffusion, dt);
        swapD();

        advect(0, density, densityOld, u, v, dt);

        densityOld.clear();
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

    private void advect(int b, DoubleGrid d, DoubleGrid d0, DoubleGrid du, DoubleGrid dv, double dt) {

//        if (useOldAdvection) {
//
//            fluidSolverA.advect(b, d, d0, du, dv, dt);
//            return;
//        }

        advector.advect(b, d, d0, du, dv, dt);

        boundary.apply(d);
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

    private void diffuse(int b, DoubleGrid c, DoubleGrid c0, double diff, double dt) {

//        if (useOldDiffusor) {
//            fluidSolverA.diffuse(b, c, c0, diff, dt, repeats);
//        }

        double a = dt * diff * nSquared;
        linearSolver.solve(b, c, c0, a, 1 + 4 * a);
    }

    // util array swapping methods
    public void swapU(){ tmp = u; u = uOld; uOld = tmp; }
    public void swapV(){ tmp = v; v = vOld; vOld = tmp; }
    public void swapD(){ tmp = density; density = densityOld; densityOld = tmp; }
}

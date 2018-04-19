package hstclair.visualise.component;

import hstclair.visualise.BuoyancySolver;
import hstclair.visualise.grid.Accumulator;
import hstclair.visualise.grid.DoubleGrid;

public class AlexanderMcKinzieBuoyancySolverImpl implements BuoyancySolver {


    int edgeLength;
    int rowLength;
    int area;


    public AlexanderMcKinzieBuoyancySolverImpl(int edgeLength, int rowLength) {
        this.edgeLength = edgeLength;
        this.rowLength = rowLength;
        this.area = edgeLength * edgeLength;
    }

    // Force of buoyancy in Newtons is calculated based on:
    // pf = fluidDensity = kg / m^3
    // Vf = displacedVolume = m^3
    // g = gravitationalForce = 9.8m/s^s

    // Fb = pf * Vf * g

    // I suspect that this is based on the Boussinesq approximation
    // see:  https://en.wikipedia.org/wiki/Boussinesq_approximation_(buoyancy)


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
     * @param buoyancyForce Array to store buoyancy force for each cell.
     **/
    public void buoyancy(DoubleGrid buoyancyForce, DoubleGrid density)
    {
        double a = 0.000625f;
        double b = 0.025f;

        Accumulator accumulator = new Accumulator();

        density.eachOuterColRow(accumulator::accumulate);

        double ambientTemperature = accumulator.getMean();

        BuoyancyForceFieldGenerator fieldGenerator = new BuoyancyForceFieldGenerator(density, ambientTemperature, a, b);

        buoyancyForce.eachOuterColRow(fieldGenerator::generate);


//        for (int row = 0; row < density.edgeLength; row++) {
//            for (int col = 0; col < density.edgeLength; col++)
//                ambientTemperature += density.get(col, row);
//        }

        // sum all temperatures
//        for (int rowIndex = density.rowLength; rowIndex <= buoyancyForce.size - density.rowLength; rowIndex += density.rowLength) {
//            for (int col = 1; col <= edgeLength; col++) {
//                ambientTemperature += density.grid[rowIndex + col];
//            }
//        }

        // get average temperature
//        ambientTemperature /= area;

//        double q = b*ambientTemperature;
//        double p = a-b;

        // Fb = (a - b) * d + b * ambientTemperature

//        for (int row = 0; row < buoyancyForce.edgeLength; row++) {
//            for (int col = 0; col < buoyancyForce.edgeLength; col++)
//                buoyancyForce.set(col, row, p*density.get(col, row) + q);
//        }

        // for each cell compute buoyancy force
//        for (int rowIndex = buoyancyForce.rowLength; rowIndex <= area - buoyancyForce.rowLength; rowIndex += buoyancyForce.rowLength) {
//            for (int col = 1; col < edgeLength; col++) {
//                int index = rowIndex + col;
//
//                buoyancyForce.grid[index] = p* density.grid[index] + q;
//            }
//        }
    }
}

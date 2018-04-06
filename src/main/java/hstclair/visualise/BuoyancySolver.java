package hstclair.visualise;

import hstclair.visualise.grid.DoubleGrid;

public interface BuoyancySolver {

    void buoyancy(DoubleGrid buoyancyForce, DoubleGrid density);
}

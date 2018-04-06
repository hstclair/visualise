package hstclair.visualise.component;

import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.grid.Indexor;

public class BuoyancyForceFieldGenerator {

    DoubleGrid density;
    double q;
    double p;

    public BuoyancyForceFieldGenerator(DoubleGrid density, double ambientTemperature, double a, double b) {
        this.density = density;
        q = b*ambientTemperature;
        p = a - b;
    }

    public void generate(Indexor indexor) {
        indexor.set(p * indexor.get(density) + q);
    }
}

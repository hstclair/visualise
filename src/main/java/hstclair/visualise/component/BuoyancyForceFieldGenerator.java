package hstclair.visualise.component;

import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.grid.Indexor;

public class BuoyancyForceFieldGenerator {

    DoubleGrid density;
    double buoyancyConst;
    double buoyancyScale;

    public BuoyancyForceFieldGenerator(DoubleGrid density, double ambientTemperature, double masslikeConstant, double thermalExpansionCoefficientlikeConstant) {
        this.density = density;
        buoyancyConst = thermalExpansionCoefficientlikeConstant*ambientTemperature;
        buoyancyScale = masslikeConstant - thermalExpansionCoefficientlikeConstant;
    }

    public void generate(Indexor indexor) {
        indexor.set(buoyancyScale * indexor.get(density) + buoyancyConst);
    }
}

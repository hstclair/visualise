package hstclair.visualise.component;

import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.grid.Indexor;

public class CompressionCounterForceFieldGenerator {

    int halfN;

    DoubleGrid uField;
    DoubleGrid vField;

    public CompressionCounterForceFieldGenerator(DoubleGrid uField, DoubleGrid vField) {

        this.uField = uField;
        this.vField = vField;

        halfN = uField.edgeLength >> 1;
    }

    void generate(Indexor indexor) {

        indexor.subtract(uField, halfN * indexor.lateralGradient());
        indexor.subtract(vField, halfN * indexor.verticalGradient());
    }

}

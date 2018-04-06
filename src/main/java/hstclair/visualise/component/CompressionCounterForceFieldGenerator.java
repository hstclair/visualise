package hstclair.visualise.component;

import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.grid.Indexor;

public class CompressionCounterForceFieldGenerator {

    int halfN;
    double negOneOverTwoN;

    DoubleGrid uField;
    DoubleGrid vField;

    public CompressionCounterForceFieldGenerator(DoubleGrid uField, DoubleGrid vField) {

        this.uField = uField;
        this.vField = vField;

        halfN = uField.edgeLength >> 1;
        negOneOverTwoN = -0.5 / uField.edgeLength;
    }

    void generate(Indexor indexor) {

        indexor.add(uField, -halfN * indexor.lateralGradient());
        indexor.add(vField, -halfN * indexor.verticalGradient());
    }

}

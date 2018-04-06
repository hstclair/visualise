package hstclair.visualise.component;

import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.grid.Indexor;

public class DivergenceFieldGenerator {

    DoubleGrid uField;
    DoubleGrid vField;

    int halfN;
    double negOneOverTwoN;

    public DivergenceFieldGenerator(DoubleGrid uField, DoubleGrid vField) {

        this.uField = uField;
        this.vField = vField;

        halfN = uField.edgeLength >> 1;
        negOneOverTwoN = -0.5 / uField.edgeLength;
    }

    void generate(Indexor indexor) {

        indexor.set((indexor.lateralGradient(uField) + indexor.verticalGradient(vField)) * negOneOverTwoN);
    }
}

package hstclair.visualise.component;

import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.grid.Indexor;

public class DivergenceFieldGenerator implements FieldGenerator {

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

    public void generate(Indexor indexor) {

        indexor.setValue((indexor.lateralGradient(uField) + indexor.verticalGradient(vField)) * negOneOverTwoN);
    }
}

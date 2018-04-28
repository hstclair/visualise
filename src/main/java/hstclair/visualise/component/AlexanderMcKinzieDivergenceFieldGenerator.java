package hstclair.visualise.component;

import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.grid.Indexor;

public class AlexanderMcKinzieDivergenceFieldGenerator implements FieldGenerator {

    DoubleGrid uField;
    DoubleGrid vField;

    int halfN;
    double negOneOverTwoN;

    public AlexanderMcKinzieDivergenceFieldGenerator(DoubleGrid uField, DoubleGrid vField) {

        this.uField = uField;
        this.vField = vField;

        halfN = uField.edgeLength >> 1;
        negOneOverTwoN = -0.5 / uField.edgeLength;
    }

    public void generate(Indexor indexor) {

        int index = indexor.index;
        int rowLength = uField.rowLength;

        double orgLateralGradient = uField.grid[index+1] - uField.grid[index-1];
        double orgVerticalGradient = vField.grid[index+rowLength] - vField.grid[index-rowLength];

        double orgResult = (orgLateralGradient
                + orgVerticalGradient)
                * negOneOverTwoN;

        indexor.setValue(orgResult);
    }

}

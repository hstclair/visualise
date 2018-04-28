package hstclair.visualise.component;

import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.grid.Indexor;

public class AlexanderMcKinzieCurlFieldGenerator implements FieldGenerator {

    DoubleGrid uField;
    DoubleGrid vField;

    public AlexanderMcKinzieCurlFieldGenerator(DoubleGrid uField, DoubleGrid vField) {

        this.uField = uField;
        this.vField = vField;
    }

    @Override
    public void generate(Indexor indexor) {

        int index = indexor.index;

        int row = uField.rowLength;

        double uDiff = uField.grid[index + row] - uField.grid[index - row];
        double vDiff = vField.grid[index + 1] - vField.grid[index - 1];

        indexor.setValue(Math.abs((uDiff-vDiff) * .5));
    }
}

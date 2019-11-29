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

        Indexor uFieldIndexor = (Indexor) indexor.getAccessor(uField);
        Indexor vFieldIndexor = (Indexor) indexor.getAccessor(vField);

        uFieldIndexor.subtractValue(halfN * indexor.lateralGradient());
        vFieldIndexor.subtractValue(halfN * indexor.verticalGradient());
    }

}

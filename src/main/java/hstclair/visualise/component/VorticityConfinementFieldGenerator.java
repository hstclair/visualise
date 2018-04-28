package hstclair.visualise.component;

import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.grid.Indexor;

public class VorticityConfinementFieldGenerator implements FieldGenerator {

    DoubleGrid w;
    DoubleGrid fvcX;
    DoubleGrid fvcY;
    DoubleGrid uField;
    DoubleGrid vField;

    public VorticityConfinementFieldGenerator(DoubleGrid w, DoubleGrid fvcX, DoubleGrid fvcY, DoubleGrid uField, DoubleGrid vField) {
        this.w = w;
        this.fvcX = fvcX;
        this.fvcY = fvcY;
        this.uField = uField;
        this.vField = vField;
    }

    public void generate(Indexor indexor) {

        if (indexor.x < 1 || indexor.x >= w.edgeLength || indexor.y < 1 || indexor.y >= w.edgeLength)
            return;

        // Find derivative of the magnitude (n = del |w|)
        double dw_dx = indexor.lateralGradient(w);
        double dw_dy = indexor.verticalGradient(w);

        if (dw_dx == 0 && dw_dy == 0) {
            indexor.setValue(fvcX, 0);
            indexor.setValue(fvcY, 0);
            return;
        }

        // Calculate vector length. (|n|)
        // Add small factor to prevent divide by zeros.
        double length = Math.sqrt(dw_dx * dw_dx + dw_dy * dw_dy);

        // N = ( n/|n| )
        double v = (indexor.verticalGradient(uField) - indexor.lateralGradient(vField)) * .5 / length;

        // N x w
        indexor.setValue(fvcX, dw_dy * -v);
        indexor.setValue(fvcY, dw_dx * v);
    }
}

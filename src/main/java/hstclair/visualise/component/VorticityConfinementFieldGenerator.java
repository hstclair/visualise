package hstclair.visualise.component;

import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.grid.Indexor;

public class VorticityConfinementFieldGenerator {

    DoubleGrid w;
    DoubleGrid fvcX;
    DoubleGrid fvcY;

    public VorticityConfinementFieldGenerator(DoubleGrid w, DoubleGrid fvcX, DoubleGrid fvcY) {
        this.w = w;
        this.fvcX = fvcX;
        this.fvcY = fvcY;
    }

    public void generate(Indexor indexor) {

        // Find derivative of the magnitude (n = del |w|)
        double dw_dx = indexor.lateralGradient(w) * 0.5f;
        double dw_dy = indexor.verticalGradient(w) * 0.5f;

        if (dw_dx == 0 && dw_dy == 0) {
            indexor.set(fvcX, 0);
            indexor.set(fvcY, 0);
            return;
        }

        // Calculate vector length. (|n|)
        // Add small factor to prevent divide by zeros.
        double length = Math.sqrt(dw_dx * dw_dx + dw_dy * dw_dy);

        // N = ( n/|n| )
//        dw_dx /= length;
//        dw_dy /= length;

        double v = indexor.get(w) / length;

        // N x w
        indexor.set(fvcX, dw_dy * -v);
        indexor.set(fvcY, dw_dx * v);
    }
}

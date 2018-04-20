package hstclair.visualise.component;

import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.grid.Indexor;

public class GaussSeidelFieldGenerator implements FieldGenerator {

    double a;
    double c;

    DoubleGrid input;

    public GaussSeidelFieldGenerator(DoubleGrid input, double a, double c) {
        this.input = input;
        this.a = a;
        this.c = c;
    }

    public void generate(Indexor indexor) {

        indexor.set((a * indexor.neighborSum() + indexor.get(input)) / c);
    }

}

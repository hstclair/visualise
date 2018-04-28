package hstclair.visualise.component;

import hstclair.visualise.grid.DoubleGrid;
import hstclair.visualise.grid.Indexor;

public class AlexanderMcKinzieGaussSeidelFieldGenerator implements FieldGenerator {

    double a;
    double c;

    DoubleGrid input;

    public AlexanderMcKinzieGaussSeidelFieldGenerator(DoubleGrid input, double a, double c) {
        this.input = input;
        this.a = a;
        this.c = c;
    }

    public void generate(Indexor indexor) {

        DoubleGrid x = indexor.target;
        int index = indexor.index;
        int rowLength = x.rowLength;

        double neighborSum =  x.grid[index-1] + x.grid[index+1] + x.grid[index- rowLength] + x.grid[index+ rowLength];


        double result = (a * neighborSum + input.grid[index]) / c;

        indexor.setValue(result);
    }

}

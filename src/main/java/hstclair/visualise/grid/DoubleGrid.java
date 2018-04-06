package hstclair.visualise.grid;

import java.util.Arrays;
import java.util.function.Consumer;

public class DoubleGrid {

    public final int edgeLength;

    public final int boundaryCell = 1;

    public final int rowLength;

    public final int size;

    public double[] grid;

    int[] rowOffset;

    public DoubleGrid(int edgeLength) {
        this.edgeLength = edgeLength;
        this.rowLength = edgeLength + (boundaryCell << 1);
        this.size = rowLength * rowLength;
        this.grid = new double[size];

        rowOffset = new int[edgeLength << 1];

        for (int i = 0; i < rowOffset.length; i++) {
            int offset = i - edgeLength;

            rowOffset[i] = offset * (edgeLength + 2);
        }
    }

    public void clear() {
        Arrays.setAll(grid, x -> 0);
    }

    public void eachInner( Consumer<Indexor> operation) {

        Indexor index = new Indexor(this, edgeLength, edgeLength, rowOffset, false, true);

        do {
            operation.accept(index);
        } while (index.advance());
    }

    public void each(Consumer<Indexor> operation) {

        Indexor index = new Indexor(this, edgeLength, rowLength, rowOffset, false, true);

        do {
            operation.accept(index);
        } while (index.advance());
    }

    public int index(int x, int y) {

        return (y + 1) * rowLength + x + 1;
    }

    public void set(int x, int y, double value) {
        grid[index(x, y)] = value;
    }

    public void add(int x, int y, double value) {
        grid[index(x, y)] += value;
    }

    public double get(int x, int y) {
        return grid[index(x, y)];
    }

    public void add(DoubleGrid addend, double scale) {
        Arrays.setAll(this.grid, x -> this.grid[x] + addend.grid[x] * scale );
    }
}

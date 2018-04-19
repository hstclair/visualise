package hstclair.visualise.grid;

import java.util.Arrays;
import java.util.function.Consumer;

public class DoubleGrid {

    public final int edgeLength;

    public final int boundaryCell = 1;

    public final int rowLength;

    public final int size;

    public final int area;

    public double[] grid;

    public int[] rowOffset;

    public DoubleGrid(int edgeLength) {
        this.edgeLength = edgeLength;
        this.rowLength = edgeLength + (boundaryCell << 1);
        this.size = rowLength * rowLength;
        this.grid = new double[size];
        this.area = edgeLength * edgeLength;

        rowOffset = new int[rowLength];
        int offset = 0;

        for (int i = 0; i < rowOffset.length; i++) {

            rowOffset[i] = offset;

            offset += rowLength;
        }
    }

    public void clear() {
        Arrays.fill(grid, 0);
    }

    public void eachInnerColRow(Consumer<Indexor> operation) {

        Indexor indexor = new Indexor(this, edgeLength, rowOffset, TraversalRange.innerTraversal(edgeLength), new ColumnRowTraversalStrategy());

        while (! indexor.rangeDepleted()) {

            operation.accept(indexor);

            indexor.advance();
        }
    }

    public void eachInnerRowCol( Consumer<Indexor> operation) {

        Indexor indexor = new Indexor(this, edgeLength, rowOffset, TraversalRange.innerTraversal(edgeLength), new RowColumnTraversalStrategy());

        while (! indexor.rangeDepleted()) {

            operation.accept(indexor);

            indexor.advance();
        }
    }

    public void eachOuterColRow(Consumer<Indexor> operation) {

        Indexor indexor = new Indexor(this, edgeLength, rowOffset, TraversalRange.fullTraversal(edgeLength), new ColumnRowTraversalStrategy());

        while (! indexor.rangeDepleted()) {

            operation.accept(indexor);

            indexor.advance();
        }
    }

    public void eachOuterRowCol(Consumer<Indexor> operation) {

        Indexor indexor = new Indexor(this, edgeLength, rowOffset, TraversalRange.fullTraversal(edgeLength), new RowColumnTraversalStrategy());

        while (! indexor.rangeDepleted()) {

            operation.accept(indexor);

            indexor.advance();
        }
    }

    public int index(int x, int y) {

        return rowOffset[y + 1] + x + 1;
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

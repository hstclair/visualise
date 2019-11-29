package hstclair.visualise.grid;

import java.util.Arrays;
import java.util.function.Consumer;

public class DoubleGrid implements Grid<Double> {

    public final int edgeLength;

    public final int boundaryCell = 1;

    public final int rowLength;

    public final int size;

    public final int area;

    public Double[] grid;

    public int[] rowOffset;

    public DoubleGrid(int edgeLength) {
        this.edgeLength = edgeLength;
        this.rowLength = edgeLength + (boundaryCell << 1);
        this.size = rowLength * rowLength;
        this.area = edgeLength * edgeLength;
        this.grid = new Double[size];

        rowOffset = new int[rowLength];
        int offset = 0;

        for (int i = 0; i < rowOffset.length; i++) {

            rowOffset[i] = offset;

            offset += rowLength;
        }

        clear();
    }

    public void clear() {
        Arrays.fill(grid, 0d);
    }

    public void each(Consumer<Indexor> operation, TraversalRange traversalRange, TraversalStrategyFactory traversalStrategyFactory) {

        Indexor indexor = new Indexor(this, edgeLength, rowOffset, traversalRange, traversalStrategyFactory);

        while (! indexor.rangeDepleted()) {

            operation.accept(indexor);

            indexor.advance();
        }
    }

    public void eachInnerColRow(Consumer<Indexor> operation) {

        Indexor indexor = new Indexor(this, edgeLength, rowOffset, TraversalRange.innerTraversal(edgeLength), new ColumnRowTraversalStrategyFactory());

        while (! indexor.rangeDepleted()) {

            operation.accept(indexor);

            indexor.advance();
        }
    }

    public void eachInnerRowCol( Consumer<Indexor> operation) {

        Indexor indexor = new Indexor(this, edgeLength, rowOffset, TraversalRange.innerTraversal(edgeLength), new RowColumnTraversalStrategyFactory());

        while (! indexor.rangeDepleted()) {

            operation.accept(indexor);

            indexor.advance();
        }
    }

    public void eachOuterColRow(Consumer<Indexor> operation) {

        Indexor indexor = new Indexor(this, edgeLength, rowOffset, TraversalRange.fullTraversal(edgeLength), new ColumnRowTraversalStrategyFactory());

        while (! indexor.rangeDepleted()) {

            operation.accept(indexor);

            indexor.advance();
        }
    }

    public void eachOuterRowCol(Consumer<Indexor> operation) {

        Indexor indexor = new Indexor(this, edgeLength, rowOffset, TraversalRange.fullTraversal(edgeLength), new RowColumnTraversalStrategyFactory());

        while (! indexor.rangeDepleted()) {

            operation.accept(indexor);

            indexor.advance();
        }
    }

    public int index(int x, int y) {

        return rowOffset[y + 1] + x + 1;
    }

    @Override
    public int getOffsetX() {
        return 0;
    }

    @Override
    public int getOffsetY() {
        return 0;
    }

    @Override
    public int getMaxX() {
        return 0;
    }

    @Override
    public int getMaxY() {
        return 0;
    }

    public boolean sameSize(int width, int height) {
        return width == rowLength && height == rowLength;
    }

    @Override
    public Double[] getArray() {
        return grid;
    }

    @Override
    public Double set(int x, int y, Double value) {
        return grid[index(x, y)] = value;
    }

    @Override
    public Double add(int x, int y, Double addend) {
        return (grid[index(x, y)] += addend);
    }

    @Override
    public Double get(int x, int y) {
        return grid[index(x, y)];
    }

    public <V extends Number> V[] validateAndGetArray(Grid<V> other) {

        if (! other.sameSize(rowLength, rowLength))
            throw new IllegalArgumentException("Grids must be the same size");

        V[] otherArray = other.getArray();

        if (otherArray.length != grid.length)
            throw new IllegalArgumentException("Arrays must be the same length");

        return otherArray;
    }

    @Override
    public <V extends Number> DoubleGrid sum(Grid<V> addend) {

        V[] addendArray = validateAndGetArray(addend);

        Arrays.setAll(this.grid, x -> this.grid[x] + addendArray[x].doubleValue() );

        return this;
    }

    @Override
    public <V extends Number> DoubleGrid sum(Grid<V> addend, Double scale) {

        V[] addendArray = validateAndGetArray(addend);

        Arrays.setAll(this.grid, x -> this.grid[x] + addendArray[x].doubleValue() * scale );

        return this;
    }

    @Override
    public Double minus(int x, int y, Double subtrahend) {
        return grid[index(x, y)] -= subtrahend;
    }

    @Override
    public <V extends Number> DoubleGrid difference(Grid<V> subtrahend) {

        V[] subtrahendArray = validateAndGetArray(subtrahend);

        Arrays.setAll(this.grid, x -> this.grid[x] + subtrahendArray[x].doubleValue() );

        return this;
    }

    @Override
    public <V extends Number> DoubleGrid difference(Grid<V> subtrahend, Double scale) {

        V[] subtrahendArray = validateAndGetArray(subtrahend);

        Arrays.setAll(this.grid, x -> this.grid[x] + subtrahendArray[x].doubleValue() * scale );

        return this;
    }
}

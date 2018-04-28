package hstclair.visualise.grid;

public class Indexor {

    public DoubleGrid target;

    public int x;

    public int y;

    int edgeLength;

    int rowLength;

    public int index;

    double leftBoundary;
    double rightBoundary;
    double upperBoundary;
    double lowerBoundary;

    int[] rowOffset;

    TraversalRange traversalRange;

    TraversalStrategy traversalStrategy;

    Interpolator interpolatorAlt;
    Interpolator interpolator;


    public Indexor(DoubleGrid target, int edgeLength, int[] rowOffset, TraversalRange traversalRange, TraversalStrategy traversalStrategy) {

        this.target = target;
        this.edgeLength = edgeLength;
        this.rowLength = edgeLength + 2;
        this.traversalRange = traversalRange;
        this.traversalStrategy = traversalStrategy;

        this.upperBoundary = 0;
        this.leftBoundary = .5;
        this.lowerBoundary = edgeLength + 1;
        this.rightBoundary = edgeLength + .5;

        this.rowOffset = rowOffset;

        interpolatorAlt = new InterpolatorAlt(this);
        interpolator = new InterpolatorNew(this);
        traversalStrategy.init(this);
    }

    public void setIndex(int index, int x, int y) {
        this.index = index;
        this.x = x;
        this.y = y;
    }

    boolean rangeDepleted() {
        return traversalStrategy.done(this);
    }

    void advance() {

        if (traversalStrategy.done(this))
            return;

        traversalStrategy.advance(this);
    }

    public double getValue() {
        return target.grid[index];
    }

    public double getValue(DoubleGrid source) {
        return source.grid[index];
    }

    public void setValue(double value) {
        target.grid[index] = value;
    }

    public void setValue(DoubleGrid target, double value) {
        target.grid[index] = value;
    }

    public void add(double value) {
        target.grid[index] += value;
    }

    public void add(DoubleGrid target, double value) {
        target.grid[index] += value;
    }

    public void subtract(double value) {
        target.grid[index] -= value;
    }

    public void subtract(DoubleGrid target, double value) {
        target.grid[index] -= value;
    }

    public double above() {
        return target.grid[index - rowLength];
    }

    public double above(DoubleGrid source) {
        return source.grid[index - rowLength];
    }

    public double below() {
        return target.grid[index + rowLength];
    }

    public double below(DoubleGrid source) {
        return source.grid[index + rowLength];
    }

    public double left() {
        return target.grid[index - 1];
    }

    public double left(DoubleGrid source) {
        return source.grid[index - 1];
    }

    public double right() {
        return target.grid[index + 1];
    }

    public double right(DoubleGrid source) {
        return source.grid[index + 1];
    }

    public double lateralGradient() {
        return target.grid[index + 1] - target.grid[index - 1];
    }
    
    public double lateralGradient(DoubleGrid source) {
        return source.grid[index + 1] - source.grid[index - 1];
    }

    public double verticalGradient() {
        return target.grid[index + rowLength] - target.grid[index - rowLength];
    }

    public double verticalGradient(DoubleGrid source) {
        return source.grid[index + rowLength] - source.grid[index - rowLength];
    }

    public double neighborSum() {
        return target.grid[index + 1] + target.grid[index - 1] + target.grid[index - rowLength] + target.grid[index + rowLength];
    }

    public double neighborSum(DoubleGrid source) {
        return source.grid[index + 1] + source.grid[index - 1] + source.grid[index + rowLength] + source.grid[index - rowLength];
    }

    /**
     * get the index of the cell identified by the provided coordinates
     * @param x
     * @param y
     * @return
     */
    public int indexOf(int x, int y) {
        return rowOffset[y + 1] + x + 1;
    }

    public double getInterpolatedAlt(DoubleGrid source, double xOffset, double yOffset) {

        return interpolatorAlt.getInterpolated(source, xOffset, yOffset);
    }

    public double getInterpolated(DoubleGrid source, double xOffset, double yOffset) {

        return interpolator.getInterpolated(source, xOffset, yOffset);
    }

    public double getInterpolated(double xOffset, double yOffset) {
        return getInterpolated(target, xOffset, yOffset);
    }
}

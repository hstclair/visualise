package hstclair.visualise.grid;

public class Indexor implements GridCursor, GridAccessor<Double> {


    // TODO refactor to replace DoubleGrid with Grid<Double>
    // TODO refactor to replace Indexor with GridAccessor
    // TODO refactor to wrap GridAccessor in a BoundaryCondition-enforcing GridAccessor
    // TODO refactor each computation step into a GridComputation (?)  => intent here is to drive toward a model that focuses on computation and is more agnostic to the type of traversal to be performed


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

    TraversalStrategyFactory traversalStrategyFactory;

    TraversalStrategy traversalStrategy;

    Interpolator interpolatorAlt;
    Interpolator interpolator;


    public Indexor(DoubleGrid target, int edgeLength, int[] rowOffset, TraversalRange traversalRange, TraversalStrategyFactory traversalStrategyFactory) {

        this.target = target;
        this.edgeLength = edgeLength;
        this.rowLength = edgeLength + 2;
        this.traversalRange = traversalRange;

        this.upperBoundary = 0;
        this.leftBoundary = .5;
        this.lowerBoundary = edgeLength + 1;
        this.rightBoundary = edgeLength + .5;

        this.rowOffset = rowOffset;

        interpolatorAlt = new InterpolatorAlt(this);
        interpolator = new InterpolatorNew(this);

        this.traversalStrategyFactory = traversalStrategyFactory;

        this.traversalStrategy = traversalStrategyFactory.create(this);
    }

    public Indexor setIndex(int index, int x, int y) {
        this.index = index;
        this.x = x;
        this.y = y;

        return this;
    }

    public boolean rangeDepleted() {
        return traversalStrategy.done(this);
    }

    public void advance() {

        if (traversalStrategy.done(this))
            return;

        traversalStrategy.advance(this);
    }

    public void move(Orientation orientation) {

        switch (orientation) {
            case Up:

                y--;
                index -= rowLength;

                break;

            case Down:

                y++;
                index += rowLength;

                break;

            case Left:

                x--;
                index--;

                break;

            case Right:

                x++;
                index++;

                break;

            default:

                throw new IllegalArgumentException("Unknown orientation");
        }
    }

    @Override
    public void incrementX() {
        x++;
        index++;
    }

    @Override
    public void incrementY() {
        y++;
        index += rowLength;
    }

    @Override
    public void decrementX() {
        x--;
        index--;
    }

    @Override
    public void decrementY() {
        y--;
        index -= rowLength;
    }

    @Override
    public int getCurrentX() {
        return x;
    }

    @Override
    public int getCurrentY() {
        return y;
    }

    @Override
    public <T extends Number> GridAccessor<T> getAccessor(Grid<T> grid) {

        if (! (grid instanceof DoubleGrid))
            throw new IllegalArgumentException("Indexor only supports DoubleGrid");

        DoubleGrid that = (DoubleGrid) grid;

        if (! that.sameSize(this.target.rowLength, this.target.rowLength))
            throw new IllegalArgumentException("Grids must be the same size");

        Indexor indexor = new Indexor(that, edgeLength, rowOffset, traversalRange, traversalStrategyFactory)
                .setIndex(index, x, y);

        return (GridAccessor<T>) indexor;
    }

    public void moveTo(CoordReference coordReference) {

        switch (coordReference) {

            case MaxXMinY:

                x = traversalRange.maxX;
                y = traversalRange.minY;

                break;

            case MinXMaxY:

                x = traversalRange.minX;
                y = traversalRange.maxY;

                break;

            case MinXY:

                x = traversalRange.minX;
                y = traversalRange.minY;

                break;

            case MaxXY:

                x = traversalRange.maxX;
                y = traversalRange.maxY;

                break;

            case MinY:

                y = traversalRange.minY;

                break;

            case MinX:

                x = traversalRange.minX;

                break;

            case MaxY:

                y = traversalRange.maxY;

                break;

            case MaxX:

                x = traversalRange.maxX;

                break;

            case CenterY:

                y = traversalRange.centerY;

                break;

            case CenterX:

                x = traversalRange.centerX;

                break;

            case CenterXY:

                x = traversalRange.centerX;
                y = traversalRange.centerY;

                break;

            default:
                throw new IllegalArgumentException("Unknown coordinate reference");
        }

        index = indexOf(x, y);
    }

    public boolean at(CoordReference coordReference) {

        switch (coordReference) {

            case MinXMaxY:

                return x == traversalRange.minX && y == traversalRange.maxY;

            case MaxXMinY:

                return x == traversalRange.maxX && y == traversalRange.minY;

            case MaxX:

                return x == traversalRange.maxX;

            case MaxY:

                return y == traversalRange.maxY;

            case MinX:

                return x == traversalRange.minX;

            case MinY:

                return y == traversalRange.minY;

            case MaxXY:

                return x == traversalRange.maxX && y == traversalRange.maxY;

            case MinXY:

                return x == traversalRange.minX && y == traversalRange.minY;

            case CenterXY:

                return x == traversalRange.centerX && y == traversalRange.centerY;

            case CenterX:

                return x == traversalRange.centerX;

            case CenterY:

                return y == traversalRange.centerY;

            case EqualXY:

                return x == y;

            default:
                throw new IllegalArgumentException("Unknown coordinate reference");
        }
    }

    @Override
    public Double getValue() {
        return target.grid[index];
    }

    @Override
    public <V extends Number> Double getValue(Grid<V> source) {

        return source.getArray()[index].doubleValue();
    }

    @Override
    public Double getValue(int xOffset, int yOffset) {
        return null;
    }

    @Override
    public Double setValue(double value) {
        return(target.grid[index] = value);
    }

    @Override
    public Double addValue(double value) {
        return(target.grid[index] += value);
    }

    @Override
    public Double subtractValue(double value) {
        return(target.grid[index] -= value);
    }

//    public double above() {
//        return target.grid[index - rowLength];
//    }
//
//    public double above(DoubleGrid source) {
//        return source.grid[index - rowLength];
//    }
//
//    public double below() {
//        return target.grid[index + rowLength];
//    }
//
//    public double below(DoubleGrid source) {
//        return source.grid[index + rowLength];
//    }
//
//    public double left() {
//        return target.grid[index - 1];
//    }
//
//    public double left(DoubleGrid source) {
//        return source.grid[index - 1];
//    }
//
//    public double right() {
//        return target.grid[index + 1];
//    }
//
//    public double right(DoubleGrid source) {
//        return source.grid[index + 1];
//    }

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

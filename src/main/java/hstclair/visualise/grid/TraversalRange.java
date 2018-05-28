package hstclair.visualise.grid;

public class TraversalRange {

    int minX;
    int minY;
    int maxX;
    int maxY;
    int centerX;
    int centerY;

    private TraversalRange(int minX, int minY, int maxX, int maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.centerX = ((maxX - minX) >> 1) + minX;
        this.centerY = ((maxY - minY) >> 1) + minY;
    }

    public static TraversalRange customTraversal(int minX, int minY, int maxX, int maxY) {
        return new TraversalRange(minX, minY, maxX, maxY);
    }

    public static TraversalRange innerTraversal(int edgeLength) {
        return new TraversalRange(0, 0, edgeLength -1, edgeLength - 1);
    }

    public static TraversalRange fullTraversal(int edgeLength) {
        return new TraversalRange( -1, -1, edgeLength, edgeLength);
    }
}

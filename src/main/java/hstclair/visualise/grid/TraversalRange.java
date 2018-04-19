package hstclair.visualise.grid;

public class TraversalRange {

    int minX;
    int minY;
    int maxX;
    int maxY;

    private TraversalRange(int minX, int minY, int maxX, int maxY) {
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
    }

    public static TraversalRange customTraversal(int minX, int minY, int maxX, int maxY) {
        return new TraversalRange(minX, minY, maxX, maxY);
    }

    public static TraversalRange innerTraversal(int edgeLength) {
        return new TraversalRange(0, 0, edgeLength, edgeLength);
    }

    public static TraversalRange fullTraversal(int edgeLength) {
        return new TraversalRange( -1, -1, edgeLength + 1, edgeLength + 1);
    }
}

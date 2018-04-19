package hstclair.visualise.grid;

public class ColumnRowTraversalStrategy implements TraversalStrategy {

    int xMin;
    int yMin;
    int xMax;
    int yMax;

    @Override
    public void init(Indexor indexor) {

        TraversalRange traversalRange = indexor.traversalRange;

        this.xMin = traversalRange.minX;
        this.xMax = traversalRange.maxX;
        this.yMin = traversalRange.minY;
        this.yMax = traversalRange.maxY;

        indexor.x = xMin;
        indexor.y = yMin;

        indexor.index = indexor.indexOf(indexor.x, indexor.y);
    }

    @Override
    public boolean done(Indexor indexor) {

        return indexor.x + 1 >= xMax && indexor.y + 1 >= yMax;
    }

    @Override
    public void advance(Indexor indexor) {
        indexor.index++;
        indexor.x++;

        if (indexor.x >= xMax) {
            indexor.y++;
            indexor.x = xMin;
            indexor.index = indexor.indexOf(indexor.x, indexor.y);
        }
    }
}

package hstclair.visualise.grid;

public class ColumnRowTraversalStrategyFactory implements TraversalStrategyFactory {

    @Override
    public TraversalStrategy create(Indexor indexor) {

        TraversalRange traversalRange = indexor.traversalRange;

        return new ColumnRowTraversalStrategy(traversalRange.minX, traversalRange.minY, traversalRange.maxX, traversalRange.maxY)
                .initIndexor(indexor);
    }

    public static class ColumnRowTraversalStrategy implements TraversalStrategy {

        int xMin;
        int yMin;
        int xMax;
        int yMax;

        public ColumnRowTraversalStrategy(int xMin, int yMin, int xMax, int yMax) {
            this.xMin = xMin;
            this.yMin = yMin;
            this.xMax = xMax;
            this.yMax = yMax;
        }

        @Override
        public TraversalStrategy initIndexor(Indexor indexor) {
            indexor.x = xMin;
            indexor.y = yMin;

            indexor.index = indexor.indexOf(indexor.x, indexor.y);

            return this;
        }

        @Override
        public boolean done(Indexor indexor) {

            return indexor.x >= xMax && indexor.y >= yMax;
        }

        @Override
        public void advance(Indexor indexor) {
            indexor.index++;
            indexor.x++;

            if (indexor.x > xMax) {
                indexor.y++;
                indexor.x = xMin;
                indexor.index = indexor.indexOf(indexor.x, indexor.y);
            }
        }
    }
}

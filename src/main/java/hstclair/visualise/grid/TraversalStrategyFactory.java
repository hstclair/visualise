package hstclair.visualise.grid;

public interface TraversalStrategyFactory {

    /**
     * construct a new instance of this TraversalStrategy
     * @param indexor the indexor that will be traversed
     */
    TraversalStrategy create(Indexor indexor);
}

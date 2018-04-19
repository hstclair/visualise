package hstclair.visualise.grid;

public interface TraversalStrategy {


    /**
     * prepare the indexor for traversal
     * @param indexor the indexor that will be traversed
     */
    void init(Indexor indexor);

    /**
     * returns true if the indexor has reached the end of its range
     * @param indexor the indexor that this object is traversing
     * @return
     */
    boolean done(Indexor indexor);

    /**
     * advances this indexor to the next cell in the sequence
     * @param indexor the indexor that this object is traversing
     */
    void advance(Indexor indexor);
}

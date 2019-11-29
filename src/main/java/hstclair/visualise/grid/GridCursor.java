package hstclair.visualise.grid;

public interface GridCursor {

    void incrementX();

    void incrementY();

    void decrementX();

    void decrementY();

    /** get the current X coordinate value */
    int getCurrentX();

    /** get the current Y coordinate value */
    int getCurrentY();

    <T extends Number> GridAccessor<T> getAccessor(Grid<T> grid);
}

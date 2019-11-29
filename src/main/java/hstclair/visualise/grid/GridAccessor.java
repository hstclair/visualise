package hstclair.visualise.grid;

public interface GridAccessor<T extends Number> {

    /** get the current X coordinate value */
    int getCurrentX();

    /** get the current Y coordinate value */
    int getCurrentY();

    /** get the value of the current cell */
    T getValue();

    /** get the value of the current cell from the given source grid */
    <V extends Number> T getValue(Grid<V> source);

    /**
     * Get the value of the cell at the given offset from the current cell
     * (i.e. the value contained by the cell at currentX + xOffset, currentY + yOffset)
     * @param xOffset the offset from the current X coordinate
     * @param yOffset the offset from the current Y coordinate
     * @return the value of the selected cell
     */
    T getValue(int xOffset, int yOffset);

    /**
     * set the current cell to the provided value
     * @param value the value to store in the cell identified by currentX, currentY
     * @return the new value
     */
    T setValue(double value);

    /**
     * store the sum of the provided value and the current cell into the current cell
     * @param value the value to add to the cell identified by currentX, currentY
     * @return the new value
     */
    T addValue(double value);

    /**
     * store the difference of the current cell minus the provided value into the current cell
     * @param value the value to subtract from the cell identified by currentX, currentY
     * @return the new value
     */
    T subtractValue(double value);
}

package hstclair.visualise.grid;

public interface Grid<T extends Number> {

    int getOffsetX();

    int getOffsetY();

    int getMaxX();

    int getMaxY();

    boolean sameSize(int width, int height);

    T set(int x, int y, T value);

    T get(int x, int y);

    T add(int x, int y, T addend);

    T minus(int x, int y, T subtrahend);

    <V extends Number> Grid<T> sum(Grid<V> addend);

    <V extends Number> Grid<T> sum(Grid<V> addend, Double scale);

    <V extends Number> Grid<T> difference(Grid<V> subtrahend);

    <V extends Number> Grid<T> difference(Grid<V> subtrahend, Double scale);

    T[] getArray();
}

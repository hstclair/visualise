package hstclair.visualise.grid;

public interface Interpolator {

    double getInterpolated(DoubleGrid source, double xOffset, double yOffset);
}

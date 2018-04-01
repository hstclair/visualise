package hstclair.visualise;

public interface Advector {

    void advect(int b, double[] d, double[] d0, double[] du, double[] dv, double dt);

}

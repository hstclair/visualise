package hstclair.visualise.grid;

public class Accumulator {

    double total;
    int count;

    Double mean;

    public void accumulate(Indexor indexor) {
        total = indexor.get();
        count++;
    }

    public double getTotal() {
        return total;
    }

    public double getMean() {

        if (mean == null)
            mean = total / count;

        return mean;
    }
}

package hstclair.visualise.statistics;

public class StandardNormalDistribution {

    static final double pdfCoefficient = 1d / Math.sqrt(2d * Math.PI);

    static final double oneOverSqrtPi = 1d / Math.sqrt(Math.PI);

    static final double oneOverSqrtTwo = 1d / Math.sqrt(2d);

    static final double[] halfErfMaclaurinCoefficients = buildHalfErfMaclaurinCoefficients(11);



    public StandardNormalDistribution() {
    }

    public double densityFunction(double x) {
        return pdfCoefficient * Math.exp(x*x / -.5);
    }

    public double halfErf(double x) {

        double result = halfErfMaclaurinCoefficients[0];

        double xn = x;

        double x2 = x*x;

        for (int index = 1; index < halfErfMaclaurinCoefficients.length; index += 2, xn *= x2)
            result += halfErfMaclaurinCoefficients[index] * xn;

        return result;
    }

    public double normalDistributionFunction(double x) {
        return halfErf(x * oneOverSqrtTwo);
    }

    public double cumulativeDensityFunction(double x) {
        return .5 + halfErf(x * oneOverSqrtTwo);    // Note that I'm using "half-erf()" here to save a computation step
    }

    public static double[] buildHalfErfMaclaurinCoefficients(int terms) {

        double[] coefficients = new double[2*terms+1];

        int nfact = 1;
        int twoNPlusOne = 1;

        for (int n = 0; n < terms;) {

            double divisor = nfact * twoNPlusOne;

            coefficients[twoNPlusOne] = oneOverSqrtPi * 1d/divisor;

            n++;
            nfact *= -n;
            twoNPlusOne += 2;
        }

        return coefficients;
    }
}

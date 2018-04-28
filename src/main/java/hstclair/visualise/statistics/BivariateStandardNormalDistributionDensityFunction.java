package hstclair.visualise.statistics;

/**
 * model of Bivariate Normal Distribution Density Function
 * with sigmaX = sigmaY = 1 and cor(X, Y) = 0 --> rho = 0
 * and mean at (0, 0)
 */
public class BivariateStandardNormalDistributionDensityFunction {

    double coefficient;
    double exponentMultiplier;

    public BivariateStandardNormalDistributionDensityFunction() {

        coefficient = 1d/(2d * Math.PI);
        exponentMultiplier = -.5;
    }

    public double density(double x, double y) {
        double z = x*x + y*y;

        return coefficient * Math.exp(z * exponentMultiplier);
    }
}

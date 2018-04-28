package hstclair.visualise.statistics;

public class BivariateNormalDistributionDensityFunction {

    double muX;
    double muY;
    double sigmaX;
    double sigmaY;
    double rho;

    double coefficient;
    double exponentDivisor;
    double sigma2X;
    double sigma2Y;
    double sigmaXsigmaYOver2Rho;


    public BivariateNormalDistributionDensityFunction(double muX, double muY, double sigmaX, double sigmaY, double rho) {

        this.muX = muX;
        this.muY = muY;
        this.sigmaX = sigmaX;
        this.sigmaY = sigmaY;
        this.rho = rho;

        coefficient = 1/(2 * Math.PI * sigmaX * sigmaY * Math.sqrt(1 - rho*rho));
        exponentDivisor = -2 * (1 - rho*rho);

        sigma2X = sigmaX * sigmaX;
        sigma2Y = sigmaY * sigmaY;
        sigmaXsigmaYOver2Rho = sigmaX * sigmaY / (2 * rho);     // a little sleight of hand in the interest of conserving a repeated operation
    }

    public double density(double x, double y) {
        double deltaX = (x - muX);
        double deltaY = (y - muY);

        double z = deltaX*deltaX / sigma2X - deltaX*deltaY / sigmaXsigmaYOver2Rho + deltaY/sigma2Y;

        return coefficient * Math.exp(z / exponentDivisor);
    }
}

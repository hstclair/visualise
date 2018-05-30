package hstclair.visualise.statistics;

public class StandardNormalDistribution {

    static final int Iterations = 20;

    static final int ContinuedFractionIterationCount = Iterations;

    static final int MaclaurinCdfCoefficientCount = Iterations;

    static final int MaclaurinHalfErfCoefficientCount = Iterations;

    static final int AsymptoticCdfCoefficientCount = Iterations;

    static final double tau = 2*Math.PI;

    static final double pdfCoefficient = 1d / Math.sqrt(2d * Math.PI);

    static final double oneOverSqrtPi = 1d / Math.sqrt(Math.PI);

    static final double oneOverSqrtTwo = 1d / Math.sqrt(2d);

    static final double sqrtPiOverTwo = Math.sqrt(Math.PI) / 2;

    static final double sqrtHalfPi = Math.sqrt(Math.PI / 2);

    static final double oneOverSqrtTwoPi = 1d / Math.sqrt(2 * Math.PI);

    static final double[] halfErfMaclaurinCoefficients = buildHalfErfMaclaurinCoefficients(MaclaurinHalfErfCoefficientCount);

    static final double[] cdfMaclaurinCoefficients = buildMaclaurinCdfCoefficients(MaclaurinCdfCoefficientCount);

    static final double[] asymptoticSeriesCdfCoefficients = adjustAsymptoticCdfCoefficients(buildAsymptoticInnerCdfCoefficients(AsymptoticCdfCoefficientCount));


    // implementation based on formulas at http://mathworld.wolfram.com/NormalDistributionFunction.html
    // and at http://demonstrations.wolfram.com/NormalDistributionWithContinuedFractions/


    public StandardNormalDistribution() {
    }

    public double densityFunction(double x) {
        return pdfCoefficient * Math.exp(x*x / -2);
    }


    public double cumulativeDensityFunction(double z) {

        double absZ = Math.abs(z);

        if (absZ < 1d)
            return cumulativeDensityFunctionMaclaurin(z);

        if (z < -8d) return 0d;
        if (z > 8d) return  1d;

        double sum = 0d;
        double term = z;
        double zSquared = z * z;

        for (int index = 3; sum + term != sum; index+=2) {
            sum += term;
            term *= zSquared / index;
        }

        return 0.5 + sum * densityFunction(z);
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

    double nthFraction(int n, double x, double nPlusOneFraction) {

//        if (n == 0)
//            return 0.5 * Math.exp(-x*x) / nPlusOneFraction;

        return n / ((1 + (n & 1)) * x + nPlusOneFraction);
    }

    public double cumulativeDensityFunctionContinuedFraction(double x) {

        double fraction = 0;

        for (int n = ContinuedFractionIterationCount; n > 0; n--)
            fraction = nthFraction(n, x, fraction);

        return sqrtPiOverTwo - .5 * Math.exp(-x*x) / (x + fraction);
    }

    final static double pi=Math.PI;

    public static double shenton(final double x,final int steps) {
        double res=0;
        for (int i = steps; i > 0; i--) {
            res = Math.pow(-1,i)*i *x*x/ ((2*i+1) + res);
        }
        return 1 - 1/(Math.sqrt(2*pi))* (Math.sqrt(pi/2) - Math.exp(-0.5 * Math.pow(x,2))*x/(1 +res));
    }

    public static double shentonAlt(final double x,final int steps) {
        double res=0;

        double xSquared = x * x;

        double sign[] = { 1d, -1d};

        for (int i = steps; i > 0; i--)
            res = sign[i & 1] * i * xSquared / (((i << 1 ) + 1) + res);

        return 1 - oneOverSqrtTwoPi * (sqrtHalfPi - Math.exp(-0.5 * xSquared)*x/(1 +res));
    }

    public static double contracted(final double x,final int steps) {

        double xSquared = x*x;
        double xFourth = x*x*x*x;
        double res=0;
        for (int i = steps-1; i > -1; i--) {
            res = ((2*(27 + 57*i + 38*i*i + 8*i*i*i))*xFourth/(5 + 4*i))/((63 + 64*i + 16*i*i)-((7 + 4*i) *xSquared/(5 + 4*i)) + res);
        }
        return 1 - 1/(Math.sqrt(2*pi))* (Math.sqrt(pi/2) - Math.exp(-0.5 * xSquared)*x/(1 -5*xSquared/(15 + 2*xSquared+res)));
    }

    public static double contractedOrg(final double x,final int steps) {
        double res=0;
        for (int i = steps-1; i > -1; i--) {
            res = ((2*(27 + 57*i + 38*i*i + 8*i*i*i))*x*x*x*x/(5 + 4*i))/((63 + 64*i + 16*i*i)-((7 + 4*i) *x*x/(5 + 4*i)) + res);
        }
        return 1 - 1/(Math.sqrt(2*pi))* (Math.sqrt(pi/2) - Math.exp(-0.5 * Math.pow(x,2))*x/(1 -5*x*x/(15 + 2*x*x+res)));
    }




    public static double contractedAlt(final double x,final int steps) {
        double xSquared = x*x;
        double xFourth = xSquared*xSquared;

        double res=0;
        for (int i = steps-1; i > -1; i--) {

            double fourI = i << 2;

            res = ((2*(27 + i * (57 + i * (38 + (i << 3) ))))*xFourth/(5 + fourI))/((63 + i * (64 + (i << 4)) )-((7 + fourI) *xSquared/(5 + fourI)) + res);
        }

        return 1 - oneOverSqrtTwoPi * (sqrtHalfPi - Math.exp(-0.5 * xSquared)*x/(1 -5*xSquared/(15 + 2*xSquared+res)));
    }


    public static double[] adjustAsymptoticCdfCoefficients(double[] coefficients) {
        for (int index = 1; index < coefficients.length; index++) {
            coefficients[index] *= oneOverSqrtTwoPi;
        }

        return coefficients;
    }

    public static double[] buildAsymptoticInnerCdfCoefficients(int terms) {

        double[] coefficients = new double[2*terms + 1];

        double coefficient = 1;

        coefficients[1] = 1d;

        // might be a better way to express this
        // coefficients are :
        // [ 1, -1, 3, -3*5, 3*5*7, -3*5*7*9, ... ]

        for (int n = 1, index = 3; n < terms; n++, index += 2) {

            coefficient *= -(index - 2);
            coefficients[index] = coefficient;
        }

        return coefficients;
    }

    public double cumulativeDensityFunctionAsymptoticSeries(double x) {

        double xSquared = x*x;

        double coefficient = Math.exp(-xSquared / 2);

        double term = 1/x;
        double factor = 1/xSquared;

        double expansion = 0;

        for (int index = 1 ; index < asymptoticSeriesCdfCoefficients.length; index+=2) {
            expansion += asymptoticSeriesCdfCoefficients[index] * term;
            term *= factor;
        }

        double result = .5 + coefficient * expansion;

        return result;
    }

    public double cumulativeDensityFunctionMaclaurin(double z) {

        double zSquared = z*z;

        double zTerm = z;

        double result = 0;

        for (int index = 1; index < cdfMaclaurinCoefficients.length; index+=2, zTerm *= zSquared)
            result += zTerm * cdfMaclaurinCoefficients[index];

        return result;
    }

    public static double[] buildMaclaurinCdfCoefficients(int terms) {

        double[] coefficients = new double[2*terms];

        double sqrtTwoPI = Math.sqrt(2*Math.PI);

        double signTwoToNthTimesSqrtTwoPI = sqrtTwoPI;
        double nFactorial = 1;
        double twoNPlus1 = 1;
        double factorialMultiplier = 1;  // handle special case for 0! = 1

        for (int index = 1, n = 0; index < terms; index += 2, n++) {

            coefficients[index] = 1d / (signTwoToNthTimesSqrtTwoPI * nFactorial * twoNPlus1);

            signTwoToNthTimesSqrtTwoPI *= -2;
            nFactorial *= factorialMultiplier;
            factorialMultiplier = n + 1;
            twoNPlus1 += 2;
        }

        return coefficients;
    }


    public double cumulativeDensityFunctionMaclaurinOld(double x) {
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

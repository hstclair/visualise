package hstclair.visualise.statistics;

import org.junit.Test;

import javax.swing.text.SimpleAttributeSet;

import java.util.Random;

import static org.junit.Assert.assertEquals;

public class StandardNormalDistributionTest {


    @Test
    public void testDensityFunction() {

        StandardNormalDistribution distribution = new StandardNormalDistribution();

        double density = distribution.densityFunction(0);

        double x = 0;

        double foo = Math.exp(-x*x/2)/Math.sqrt(2* Math.PI);

        assertEquals(foo, density, 0);
    }

    @Test
    public void testCumulativeDensityFunctionZeroToInfinityAndBeyond() {

        StandardNormalDistribution distribution = new StandardNormalDistribution();

        double cumulativeDensityAtZero = distribution.cumulativeDensityFunction(0d);

        double cumulativeDensity = distribution.cumulativeDensityFunction(Math.sqrt(Double.MAX_VALUE)) - cumulativeDensityAtZero;

        assertEquals(.5d, cumulativeDensity, 0);
    }

    @Test
    public void testCumulativeDensityFunctionMaclaurinAtPointOne() {
        StandardNormalDistribution distribution = new StandardNormalDistribution();

        double cumulativeDensityAtPointOne = distribution.cumulativeDensityFunctionMaclaurin(.1);

        assertEquals(.03983d, cumulativeDensityAtPointOne, 0.00001);

    }

    @Test
    public void testCumulativeDensityFunctionMaclaurinAtPointFive() {
        StandardNormalDistribution distribution = new StandardNormalDistribution();

        double cumulativeDensityAtPointOne = distribution.cumulativeDensityFunctionMaclaurin(.5);

        assertEquals(.19146, cumulativeDensityAtPointOne, 0.0005);

    }

    @Test
    public void testBuildCumulativeDensityFunctionAsymptoticInnerCoefficients() {

        double[] innerCoefficients = StandardNormalDistribution.buildAsymptoticInnerCdfCoefficients(5);

        assertEquals(1d, innerCoefficients[1], 0);
        assertEquals(-1d, innerCoefficients[3], 0);
        assertEquals(3d, innerCoefficients[5], 0);
        assertEquals(-15, innerCoefficients[7], 0);
        assertEquals(105d, innerCoefficients[9], 0);
    }

    @Test
    public void testCumulativeDensityFunctionAsymptoticSeriesAtFour() {

        StandardNormalDistribution distribution = new StandardNormalDistribution();

        double expected = 1;

        double actual = distribution.cumulativeDensityFunctionAsymptoticSeries(4d);

        assertEquals(expected, actual, .00006);
    }

    @Test
    public void testCumulativeDensityFunctionContinuedFractionsAtOne() {

        StandardNormalDistribution distribution = new StandardNormalDistribution();

        double expected = 0.84134;

        double actual = distribution.cumulativeDensityFunctionContinuedFraction(1d);

        assertEquals(expected, actual, .1);

    }

    @Test
    public void testContractedAtZero() {

        double expected = .5;

        double actual = StandardNormalDistribution.contracted(0,20);

        assertEquals(expected, actual, 0);
    }


    @Test
    public void compareResults() {

        StandardNormalDistribution distribution = new StandardNormalDistribution();


        System.out.println("x        : expected -  density, maclaurin, contracted, fraction,  shenton, asymptotic");

        for (double x = 0; x <= 4; x += .01) {
            double maclaurin = distribution.cumulativeDensityFunctionMaclaurin(x) + .5;
            double asymptotic = distribution.cumulativeDensityFunctionAsymptoticSeries(x);
            double continuedFraction = distribution.cumulativeDensityFunctionContinuedFraction(x);
            double contracted = StandardNormalDistribution.contracted(x, 100);
            double density = distribution.densityFunction(x) + .5;
            double shenton = StandardNormalDistribution.shenton(x, 100);

            System.out.printf("%1$f : %2$f - %3$f,  %4$f,   %5$f, %6$f, %7$f, %8$f\n", x, StandardNormalDistribution.nist(x), density, maclaurin, contracted, continuedFraction, shenton, asymptotic);
        }

    }

    @Test
    public void compareContractedvsAlt() {

        StandardNormalDistribution distribution = new StandardNormalDistribution();


        System.out.println("x        : nist     -  contracted, contractedAlt,    delta, delta vs nist");

        for (double x = -4; x <= 4; x += .01) {
            double contracted = StandardNormalDistribution.contracted(x, 100);
            double contractedAlt = StandardNormalDistribution.contractedAlt(x, 100);

            System.out.printf("%1$f : %2$f -   %3$f,       %4$f, %5$f,    %6$f\n", x, StandardNormalDistribution.nist(x), contracted, contractedAlt, contracted - contractedAlt, StandardNormalDistribution.nist(x) - contractedAlt);
        }

    }

    @Test
    public void compareShentonvsAlt() {

        StandardNormalDistribution distribution = new StandardNormalDistribution();

        System.out.println("x        : nist     -   shenton, shenton Alt,    delta, delta vs Nist");

        for (double x = -4; x <= 4; x += .02) {
            double shenton = StandardNormalDistribution.shenton(x, 100);
            double shentonAlt = StandardNormalDistribution.shentonAlt(x, 100);

            System.out.printf("%1$f : %2$f -  %3$f,    %4$f, %5$f,   %6$f\n", x, StandardNormalDistribution.nist(x), shenton, shentonAlt, shenton - shentonAlt, StandardNormalDistribution.nist(x) - shentonAlt);
        }

    }

    @Test
    public void testShentonAltPerformance() {

        long nanos = System.nanoTime();

        double val = 0;

        for (int count = 0; count < 1000000; count++) {

            double x = StandardNormalDistribution.shentonAlt(val, 10);

            val += .000001;
        }

        nanos = System.nanoTime() - nanos;

        System.out.printf("ShentonAlt:    %1$d nanos elapsed\n", nanos);
    }


    @Test
    public void testShentonPerformance() {

        long nanos = System.nanoTime();

        double val = 0;

        for (int count = 0; count < 1000000; count++) {

            double x = StandardNormalDistribution.shenton(val, 10);

            val += .000001;
        }

        nanos = System.nanoTime() - nanos;

        System.out.printf("Shenton:       %1$d nanos elapsed\n", nanos);
    }

    @Test
    public void testContractedAltPerformance() {

        long nanos = System.nanoTime();

        double val = 0;

        for (int count = 0; count < 1000000; count++) {

            double x = StandardNormalDistribution.contractedAlt(val, 10);

            val += .000001;
        }

        nanos = System.nanoTime() - nanos;

        System.out.printf("ContractedAlt: %1$d nanos elapsed\n", nanos);
    }

    @Test
    public void testContractedPerformance() {

        long nanos = System.nanoTime();

        double val = 0;

        for (int count = 0; count < 1000000; count++) {

            double x = StandardNormalDistribution.contracted(val, 10);

            val += .000001;
        }

        nanos = System.nanoTime() - nanos;

        System.out.printf("Contracted:    %1$d nanos elapsed\n", nanos);
    }

    @Test
    public void testBeyondFour() {
        StandardNormalDistribution distribution = new StandardNormalDistribution();


        System.out.println("x        : expected -  density, maclaurin, contracted, contractedAlt, fraction,  shenton, shentonAlt, asymptotic");

        for (double x = 4; x <= 8; x += .01) {
            double maclaurin = distribution.cumulativeDensityFunctionMaclaurin(x) + .5;
            double asymptotic = distribution.cumulativeDensityFunctionAsymptoticSeries(x);
            double continuedFraction = distribution.cumulativeDensityFunctionContinuedFraction(x);
            double contracted = StandardNormalDistribution.contracted(x, 100);
            double contractedAlt = StandardNormalDistribution.contractedAlt(x, 100);
            double density = distribution.densityFunction(x) + .5;
            double shenton = StandardNormalDistribution.shenton(x, 100);
            double shentonAlt = StandardNormalDistribution.shentonAlt(x, 100);

            System.out.printf("%1$f : no nist  - %3$f,  %4$f,   %5$f,      %6$f, %7$f, %8$f,   %9$f, %10$f\n", x, 0d, density, maclaurin, contracted, contractedAlt, continuedFraction, shenton, shentonAlt, asymptotic);
        }

    }

    @Test
    public void testNegativeFourToZero() {
        StandardNormalDistribution distribution = new StandardNormalDistribution();


        System.out.println("x        : expected  -  density, maclaurin, contracted, contractedAlt, fraction,  shenton, shentonAlt, asymptotic");

        for (double x = -4; x <= 0; x += .01) {
            double maclaurin = distribution.cumulativeDensityFunctionMaclaurin(x) + .5;
            double asymptotic = distribution.cumulativeDensityFunctionAsymptoticSeries(x);
            double continuedFraction = distribution.cumulativeDensityFunctionContinuedFraction(x);
            double contracted = StandardNormalDistribution.contracted(x, 100);
            double contractedAlt = StandardNormalDistribution.contractedAlt(x, 100);
            double density = distribution.densityFunction(x) + .5;
            double shenton = StandardNormalDistribution.shenton(x, 100);
            double shentonAlt = StandardNormalDistribution.shentonAlt(x, 100);

            System.out.printf("%1$f : %2$f - %3$f, %4$f,   %5$f,      %6$f, %7$f, %8$f,   %9$f, %10$f\n", x, StandardNormalDistribution.nist(x), density, maclaurin, contracted, contractedAlt, continuedFraction, shenton, shentonAlt, asymptotic);
        }

    }

    @Test
    public void testNegativeFourToFour() {

        StandardNormalDistribution distribution = new StandardNormalDistribution(100);

        System.out.println("x        :   expected -  density      (delta), contracted    (delta),  contractedAlt  (delta),   shenton     (delta),  shentonAlt   (delta)");

        for (int value = -8; value < 8; value++) {

            double start = ((double) value) / 2;
            double end = start + .5d;

            for (double x = start; x < end; x += .01) {
                double nist = StandardNormalDistribution.nist(x);
                double contracted = StandardNormalDistribution.contracted(x, 100);
                double contractedAlt = StandardNormalDistribution.contractedAlt(x, 100);
                double density = distribution.cumulativeDensityFunction(x);
                double shenton = StandardNormalDistribution.shenton(x, 100);
                double shentonAlt = StandardNormalDistribution.shentonAlt(x, 100);

                System.out.printf("%1$ f : %2$ f - %3$ f (%4$ f), %5$ f (%6$ f),   %7$ f (%8$ f), %9$ f (%10$ f), %11$ f (%12$ f)\n", x, nist, density, nist - density, contracted, nist - contracted, contractedAlt, nist - contractedAlt, shenton, nist - shenton, shentonAlt, nist - shentonAlt);
            }
        }
    }

}

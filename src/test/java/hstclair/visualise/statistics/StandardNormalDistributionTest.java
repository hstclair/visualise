package hstclair.visualise.statistics;

import org.junit.Test;

import javax.swing.text.SimpleAttributeSet;

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

        double cumulativeDensity = distribution.cumulativeDensityFunction(Double.MAX_VALUE) - cumulativeDensityAtZero;

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

        assertEquals(.19146, cumulativeDensityAtPointOne, 0.00001);

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

        assertEquals(expected, actual, 0);

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

        double[] x = new double[] {
                0d,
                .2d,
                .4d,
                .6d,
                .8d,
                1d,
                1.2d,
                1.4d,
                1.6d,
                1.8d,
                2d
        };

        double[] nist = new double[] {
                .5d,
                .57926,
                .65542,
                .72575,
                .78814,
                .84134,
                .88493,
                .91924,
                .94520,
                .96407,
                .97725
        };

        System.out.println("x        : expected -  density, maclaurin, contracted, fraction,  shenton, asymptotic");

        for (int index = 0; index < x.length; index ++) {
            double maclaurin = distribution.cumulativeDensityFunctionMaclaurin(x[index]) + .5;
            double asymptotic = distribution.cumulativeDensityFunctionAsymptoticSeries(x[index]);
            double continuedFraction = distribution.cumulativeDensityFunctionContinuedFraction(x[index]);
            double contracted = StandardNormalDistribution.contracted(x[index], 20);
            double density = distribution.densityFunction(x[index]) + .5;
            double shenton = StandardNormalDistribution.shenton(x[index], 20);

            System.out.printf("%1$f : %2$f - %3$f,  %4$f,   %5$f, %6$f, %7$f, %8$f\n", x[index], nist[index], density, maclaurin, contracted, continuedFraction, shenton, asymptotic);
        }

    }



}

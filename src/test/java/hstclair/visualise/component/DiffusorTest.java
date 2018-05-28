package hstclair.visualise.component;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DiffusorTest {

    @Test
    public void testConstructRadiusOne() {

        int center = 0;

        Diffusor diffusor = new Diffusor(1);

        double value = diffusor.grid.get(center, center);

        assertEquals(1, value, 0);
    }

    @Test
    public void testConstructRadiusTwo() {

        int center = 1;

        Diffusor diffusor = new Diffusor(2);

        assertEquals(1, diffusor.grid.get(center, center), 0);
    }
}

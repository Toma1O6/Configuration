package test.toma.configuration;

import dev.toma.configuration.api.type.BooleanType;
import dev.toma.configuration.api.type.DoubleType;
import dev.toma.configuration.api.type.IntType;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestTypes {

    @Test
    public void testBoolean() {
        BooleanType type = new BooleanType("Boolean", true);
        assertEquals("Boolean", type.getId());
        assertTrue(type.get());
    }

    @Test
    public void testInteger() {
        IntType unbounded = new IntType("Int", -15);
        assertEquals("Int", unbounded.getId());
        assertEquals(Integer.MIN_VALUE, (int) unbounded.getMin());
        assertEquals(Integer.MAX_VALUE, (int) unbounded.getMax());
        assertEquals(-15, (int) unbounded.get());
        IntType bounded = new IntType("Int2", -10, 0, 10);
        assertEquals("Int2", bounded.getId());
        assertEquals(0, (int) bounded.get());
        assertEquals(0, (int) bounded.getMin());
        assertEquals(10, (int) bounded.getMax());
        bounded.set(5);
        assertEquals(5, (int) bounded.get());
        bounded.set(11);
        assertEquals(10, (int) bounded.get());
    }

    @Test
    public void testDecimal() {
        DoubleType unbounded = new DoubleType("DoubleU", 12.34);
        assertEquals(12.34, unbounded.get(), 0.0001);
        assertEquals(-Double.MAX_VALUE, unbounded.getMin(), 0.0001);
        assertEquals(Double.MAX_VALUE, unbounded.getMax(), 0.0001);
        DoubleType bounded = new DoubleType("DoubleB", -987.654321, -987.7, -987.5);
        assertEquals(-987.654321, bounded.get(), 0.0001);
        assertEquals(-987.7, bounded.getMin(), 0.0001);
        assertEquals(-987.5, bounded.getMax(), 0.0001);
        bounded.set(0.0);
        assertEquals(-987.5, bounded.get(), 0.00001);
        bounded.set(-987.6);
        assertEquals(-987.6, bounded.get(), 0.00001);
    }
}

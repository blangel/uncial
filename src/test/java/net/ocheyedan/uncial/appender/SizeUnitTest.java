package net.ocheyedan.uncial.appender;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * User: blangel
 * Date: 4/27/12
 * Time: 6:22 PM
 */
public class SizeUnitTest {

    @Test
    public void toBytes() {
        assertEquals(1, SizeUnit.BYTES.toBytes(1));
        assertEquals(1024, SizeUnit.KILOBYTES.toBytes(1));
        assertEquals(1024 * 1024, SizeUnit.MEGABYTES.toBytes(1));
        assertEquals(1024 * 1024 * 1024, SizeUnit.GIGABYTES.toBytes(1));
    }

    @Test
    public void toKilobytes() {
        assertEquals(0, SizeUnit.BYTES.toKilobytes(1));
        assertEquals(1, SizeUnit.KILOBYTES.toKilobytes(1));
        assertEquals(1024, SizeUnit.MEGABYTES.toKilobytes(1));
        assertEquals(1024 * 1024, SizeUnit.GIGABYTES.toKilobytes(1));
    }

    @Test
    public void toMegabytes() {
        assertEquals(0, SizeUnit.BYTES.toMegabytes(1));
        assertEquals(0, SizeUnit.KILOBYTES.toMegabytes(1));
        assertEquals(1, SizeUnit.MEGABYTES.toMegabytes(1));
        assertEquals(1024, SizeUnit.GIGABYTES.toMegabytes(1));
    }

    @Test
    public void toGigabytes() {
        assertEquals(0, SizeUnit.BYTES.toGigabytes(1));
        assertEquals(0, SizeUnit.KILOBYTES.toGigabytes(1));
        assertEquals(0, SizeUnit.MEGABYTES.toGigabytes(1));
        assertEquals(1, SizeUnit.GIGABYTES.toGigabytes(1));
    }

    @Test
    public void convert() {
        assertEquals(1024, SizeUnit.BYTES.convert(1, SizeUnit.KILOBYTES));
        assertEquals(1024 * 1024, SizeUnit.BYTES.convert(1, SizeUnit.MEGABYTES));
        assertEquals(1024 * 1024 * 1024, SizeUnit.BYTES.convert(1, SizeUnit.GIGABYTES));

        assertEquals(0, SizeUnit.KILOBYTES.convert(1, SizeUnit.BYTES));
        assertEquals(1024, SizeUnit.KILOBYTES.convert(1, SizeUnit.MEGABYTES));
        assertEquals(1024 * 1024, SizeUnit.KILOBYTES.convert(1, SizeUnit.GIGABYTES));

        assertEquals(0, SizeUnit.MEGABYTES.convert(1, SizeUnit.BYTES));
        assertEquals(0, SizeUnit.MEGABYTES.convert(1, SizeUnit.KILOBYTES));
        assertEquals(1024, SizeUnit.MEGABYTES.convert(1, SizeUnit.GIGABYTES));

        assertEquals(0, SizeUnit.GIGABYTES.convert(1, SizeUnit.BYTES));
        assertEquals(0, SizeUnit.GIGABYTES.convert(1, SizeUnit.KILOBYTES));
        assertEquals(0, SizeUnit.GIGABYTES.convert(1, SizeUnit.MEGABYTES));
    }

}

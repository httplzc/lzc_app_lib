package com.yioks.lzclib;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        int i=2147483647;
        i++;
        i++;
        System.out.print(Integer.MAX_VALUE);
        System.out.print(i);
        assertEquals(4, 2 + 2);
    }
}
package com.cs;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest {
    @Test
    public void test1() {
        String email = "1397368928@qq.com";
        System.out.println(email.substring(0,email.indexOf("@")));
    }
}

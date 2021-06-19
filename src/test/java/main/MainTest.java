package main;

import org.junit.Test;

import java.io.IOException;

public class MainTest {

    @Test
    public void testMain() throws IOException {
        String file = "jklsrc/main/resources/misc_test_1.txt";
        Main.main("-o", "validate", file);

    }

}

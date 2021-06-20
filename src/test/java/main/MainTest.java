package main;

import org.junit.Test;

import java.io.IOException;

public class MainTest {

    @Test
    public void testMain() throws IOException {
        String file = "src/main/resources/demo_2.txt";
        Main.main("-o", "execute", file);

    }

}

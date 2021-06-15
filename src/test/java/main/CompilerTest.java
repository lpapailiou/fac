package main;

import org.junit.Test;

import java.io.IOException;

public class CompilerTest {

    @Test
    public void testCompiler() throws IOException {
        String file = "src/main/resources/misc_test_0.txt";
        Compiler.main("-o", "execute", file);

    }

}

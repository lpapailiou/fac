package main;

import org.junit.Test;

public class CompilerTest {

    @Test
    public void testCompiler() {
        String file = "src/main/resources/expressions.txt";
        Compiler.main("-o", "execute", file);
    }

}

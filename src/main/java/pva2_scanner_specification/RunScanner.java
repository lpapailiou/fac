package pva2_scanner_specification;

import java.nio.file.Paths;

public class RunScanner {


    public static void main(String[] args) {
        JLang jlang = new JLang(Paths.get("src/main/resources/scanner_test_1.txt").toAbsolutePath().toString());
        jlang.printTokens();
        System.out.println();
        jlang.printValues();
    }

}

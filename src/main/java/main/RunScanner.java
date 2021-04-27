package main;


import scanner.JScanner;

import java.nio.file.Paths;

public class RunScanner {


    public static void main(String[] args) {
        JScanner jlang = new JScanner(Paths.get("src/main/resources/scanner_test_1.txt").toAbsolutePath().toString());
        jlang.printTokens();
        System.out.println();
        jlang.printValues();
    }

}

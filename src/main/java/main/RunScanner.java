package main;


import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import scanner.JScanner;

import java.io.*;
import java.nio.file.Paths;

public class RunScanner {


    public static void main(String[] args) {
        String encodingName = "UTF-8";
        String file = Paths.get("src/main/resources/parser_test_1.txt").toAbsolutePath().toString();
        try (FileInputStream stream = new FileInputStream(file); InputStreamReader reader = new InputStreamReader(stream, encodingName)) {
            JScanner scanner = new JScanner(reader);
            while (!scanner.yyatEOF()) {
                Symbol symbol = scanner.next_token();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

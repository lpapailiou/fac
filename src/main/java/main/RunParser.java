package main;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import parser.JParser;
import scanner.JScanner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class RunParser {

    public static void main(String[] args) {
        String encodingName = "UTF-8";
        String file = Paths.get("src/main/resources/parser_test_1.txt").toAbsolutePath().toString();
        try (FileInputStream stream = new FileInputStream(file); InputStreamReader reader = new InputStreamReader(stream, encodingName)) {
            JScanner scanner = new JScanner(reader);
            JParser parser = new JParser(scanner);

            Symbol reducedResult = null;

            while (!scanner.yyatEOF()) {
                reducedResult = parser.parse();
            }

            System.out.println("***** PARSER RESULT *****\n\n" + reducedResult.value + "\n");

            scanner.printTokens();
            scanner.printValues();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

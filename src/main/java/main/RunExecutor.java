package main;

import interpreter.Executor;
import java_cup.runtime.Symbol;
import parser.JParser;
import parser.parsetree.Program;
import parser.validation.Validator;
import scanner.JScanner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class RunExecutor {

    public static void main(String[] args) {
        String encodingName = "UTF-8";
        String file = Paths.get("src/main/resources/parser_test_1.txt").toAbsolutePath().toString();


        try (FileInputStream stream = new FileInputStream(file); InputStreamReader reader = new InputStreamReader(stream, encodingName)) {
            JParser parser = new JParser(reader, true);

            Symbol reducedResult = null;

            while (!parser.yyatEOF()) {
                reducedResult = parser.parse();
            }

            System.out.println("***** PARSER RESULT *****\n\n" + reducedResult.value + "\n");

            System.out.println("***** EXECUTION RESULT *****\n");

            Executor executor = new Executor();
            executor.visit((Program) reducedResult.value);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

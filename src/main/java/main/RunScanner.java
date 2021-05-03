package main;

import scanner.JScanner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class RunScanner {


    public static void main(String[] args) {
        String encodingName = "UTF-8";
        String file = Paths.get("src/main/resources/scanner_test_1.txt").toAbsolutePath().toString();
        try (FileInputStream stream = new FileInputStream(file); InputStreamReader reader = new InputStreamReader(stream, encodingName)) {
            JScanner scanner = new JScanner(reader);
            while (!scanner.yyatEOF()) {
                scanner.next_token();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

package scanner;

import org.junit.Test;
import parser.JSymbol;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.*;

public class JScannerTest {

    @Test
    public void varTest() {
        try {
            assertEquals(JSymbol.VAR, getToken("a"));
            assertEquals(JSymbol.VAR, getToken("test"));
            assertEquals(JSymbol.VAR, getToken("a111"));
            assertEquals(JSymbol.VAR, getToken("_test_x"));

            assertNotEquals(JSymbol.VAR, getToken("1_test_x"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void strTest() {
        try {
            assertEquals(JSymbol.STR, getToken("''"));
            assertEquals(JSymbol.STR, getToken("'    '"));
            assertEquals(JSymbol.STR, getToken("' 11 this is a string ++'"));
            assertEquals(JSymbol.STR, getToken("'str'"));

            assertThrows(Error.class, () -> getToken("'str with char !'"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void numTest() {
        try {
            assertEquals(JSymbol.NUM, getToken("0"));
            assertEquals(JSymbol.NUM, getToken("0001"));
            assertEquals(JSymbol.NUM, getToken("01.01"));
            assertEquals(JSymbol.NUM, getToken("-500"));

            assertThrows(Error.class, () -> getToken("Infinity"));
            assertThrows(Error.class, () -> getToken("NaN"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void commentTest() {
        try {
            assertEquals(0, getToken("// comment"));
            assertEquals(0, getToken("// comment\n"));
            assertEquals(0, getToken("/* */"));
            assertEquals(0, getToken("/**/"));
            assertEquals(0, getToken("/****** comment ****/"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void whitespaceTest() {
        try {
            assertEquals(0, getToken(" "));
            assertEquals(0, getToken("     "));
            assertEquals(0, getToken("    \n\n     "));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void errTest() {
        assertThrows(Error.class, () -> getToken("ä"));
        assertThrows(Error.class, () -> getToken("AABBCC"));
        assertThrows(Error.class, () -> getToken("^"));
    }

    @Test
    public void tokenTest() {
        try {
            // reserved words
            assertEquals(JSymbol.BOOL, getToken("true"));
            assertEquals(JSymbol.BOOL, getToken("false"));
            assertEquals(JSymbol.RETURN, getToken("return"));
            assertEquals(JSymbol.WHILE, getToken("while"));
            assertEquals(JSymbol.BREAK, getToken("break"));
            assertEquals(JSymbol.IF, getToken("if"));
            assertEquals(JSymbol.ELSE, getToken("else"));
            assertEquals(JSymbol.DEF, getToken("def"));
            assertEquals(JSymbol.PRINT, getToken("print"));

            // operators
            assertEquals(JSymbol.EQ, getToken("=="));
            assertEquals(JSymbol.NEQ, getToken("!="));
            assertEquals(JSymbol.GREQ, getToken(">="));
            assertEquals(JSymbol.LEQ, getToken("<="));
            assertEquals(JSymbol.AND, getToken("&&"));
            assertEquals(JSymbol.OR, getToken("||"));
            assertEquals(JSymbol.PLUSEQ, getToken("+="));
            assertEquals(JSymbol.MINEQ, getToken("-="));
            assertEquals(JSymbol.MULEQ, getToken("*="));
            assertEquals(JSymbol.DIVEQ, getToken("/="));
            assertEquals(JSymbol.MODEQ, getToken("%="));
            assertEquals(JSymbol.EQUAL, getToken("="));
            assertEquals(JSymbol.INC, getToken("++"));
            assertEquals(JSymbol.DEC, getToken("--"));
            assertEquals(JSymbol.LESS, getToken("<"));
            assertEquals(JSymbol.GREATER, getToken(">"));
            assertEquals(JSymbol.PLUS, getToken("+"));
            assertEquals(JSymbol.MINUS, getToken("-"));
            assertEquals(JSymbol.MUL, getToken("*"));
            assertEquals(JSymbol.DIV, getToken("/"));
            assertEquals(JSymbol.MOD, getToken("%"));
            assertEquals(JSymbol.EXCL, getToken("!"));

            // special characters
            assertEquals(JSymbol.BL, getToken("("));
            assertEquals(JSymbol.BR, getToken(")"));
            assertEquals(JSymbol.CBL, getToken("{"));
            assertEquals(JSymbol.CBR, getToken("}"));
            assertEquals(JSymbol.COMMA, getToken(","));
            assertEquals(JSymbol.STOP, getToken(";"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getToken(String input) throws IOException {
        Reader in = new StringReader(input);
        JScanner scanner = new JScanner(in, false);
        return scanner.next_token().sym;
    }
}

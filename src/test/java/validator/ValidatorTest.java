package validator;

import exceptions.GrammarException;
import exceptions.MissingDeclarationException;
import exceptions.OperatorMismatchException;
import exceptions.TypeMismatchException;
import java_cup.runtime.Symbol;
import org.junit.Test;
import parser.JParser;
import parser.parsetree.Program;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

public class ValidatorTest {

    @Test
    public void whileTest() {
        try {
            validate("while (true) { }");
            validate("while (!true) { }");
            validate("while (true && false) { }");
            validate("while ((true && false) || true) { }");
            validate("while (!(true && !false) || true) { }");

            validate("while (true) { while (false) { } }");
            validate("while (true) { if (false) { } else { } }");

            validate("while (true) { break; }");
            validate("while (true) { if (false) { break; } else { break; } }");

            assertThrows(MissingDeclarationException.class, () -> validate("while (true) { x = 1; }"));
            assertThrows(MissingDeclarationException.class, () -> validate("while (true) { number x = 1; } print(x);"));
            assertThrows(GrammarException.class, () -> validate("while (true) { if (false) { break; } else { break; } break; }"));
            assertThrows(GrammarException.class, () -> validate("while (true) { break; break; }"));
            assertThrows(GrammarException.class, () -> validate("while (true) { break; number x = 1; }"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void ifThenTest() {
        try {
            validate("if (true) { }");
            validate("if (true) { } else { }");

            validate("if (!true) { }");
            validate("if (true && false) { }");

            validate("if (true) { number x = 1; }");
            validate("if (true) { number x = 1; number y; y += 2; }");

            assertThrows(GrammarException.class, () -> validate("if (true) { break; }"));
            assertThrows(GrammarException.class, () -> validate("if (true) { } else { break; }"));
            assertThrows(MissingDeclarationException.class, () -> validate("if (true) { x = 1; }"));
            assertThrows(MissingDeclarationException.class, () -> validate("if (true) { number x = 1; } x = 2;"));
            assertThrows(Exception.class, () -> validate("if (true) { def number fun() { return 1; } }"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void functionDefinitionTest() {
        try {
            validate("def string fun() { return 'a'; }");
            validate("def number fun() { return 1; }");
            validate("def boolean fun() { return true; }");

            validate("def number fun(number a) { return 1; } def number fun(number a, string b) { return 1; }");

            assertThrows(TypeMismatchException.class, () -> validate("def string fun() { return 1; }"));
            assertThrows(MissingDeclarationException.class, () -> validate("def number fun(number y) { return x; }"));
            assertThrows(GrammarException.class, () -> validate("def number fun(number y) { return y; } def number fun(string y) { return 1; }"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void functionCallTest() {
        try {
            validate("def number fun() { return 1; } number x = fun();");

            assertThrows(MissingDeclarationException.class, () -> validate("def number fun1() { return 1; } number x = fun();"));
            assertThrows(MissingDeclarationException.class, () -> validate("def number fun(number y) { return 1; } number x = fun();"));
            assertThrows(TypeMismatchException.class, () -> validate("def number fun() { return 1; } string x = fun();"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void printCallTest() {
        try {
            validate("print();");
            validate("print(1);");
            validate("print('a');");
            validate("print(!true);");
            validate("print(1 + 2 * 3);");
            validate("print('a' + 'b');");

            assertThrows(MissingDeclarationException.class, () -> validate("print(x);"));
            assertThrows(MissingDeclarationException.class, () -> validate("print(fun());"));
            assertThrows(TypeMismatchException.class, () -> validate("print(1 + true);"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void conditionalExpressionTest() {
        try {
            validate("boolean x; x = true;");
            validate("boolean x; x = (true && true);");
            validate("boolean x; x = ((true && true) || false);");
            validate("boolean x; x = ((true && true) && (true || false));");

            validate("boolean x = (1 > 2);");
            validate("boolean x = (1 >= 2);");
            validate("boolean x = (1 <= 2);");
            validate("boolean x = (1 < 2);");
            validate("boolean x = (1 == 2);");

            validate("boolean x = !true;");
            validate("boolean x = !(true && !true);");
            validate("boolean x = (!(true && true) || !false);");
            validate("boolean x = !((true && true) && (true || false));");

            validate("boolean x = (1 != 2);");

            assertThrows(TypeMismatchException.class, () -> validate("boolean x = (true != 2);"));
            assertThrows(TypeMismatchException.class, () -> validate("boolean x = (1 != 'a');"));

            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void expressionTest() {
        try {
            validate("number var; number x = -var + 2 * 80 % 12 - 5.5 / 2;");

            assertThrows(MissingDeclarationException.class, () -> validate("string y = x + 1;"));
            assertThrows(TypeMismatchException.class, () -> validate("number x; string y = x + 1;"));
            assertThrows(TypeMismatchException.class, () -> validate("boolean x = true; boolean y = (1 < true);"));
            assertThrows(TypeMismatchException.class, () -> validate("number x = 1; boolean y = (true && x);"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void assignmentTest() {
        try {
            validate("string x; x += 'a1';");
            validate("string x; x += 1;");
            validate("string x; x += true;");
            validate("number x; x++;");
            validate("number x; x = -x;");
            validate("boolean x; x = !x;");

            assertThrows(OperatorMismatchException.class, () -> validate("string x; x++;"));
            assertThrows(OperatorMismatchException.class, () -> validate("string x; x = -x;"));

            assertThrows(TypeMismatchException.class, () -> validate("string x; x = !x;"));
            assertThrows(TypeMismatchException.class, () -> validate("string x; x -= 'a1';"));
            assertThrows(TypeMismatchException.class, () -> validate("string x; x *= 'a1';"));
            assertThrows(TypeMismatchException.class, () -> validate("string x; x /= 'a1';"));
            assertThrows(TypeMismatchException.class, () -> validate("string x; x = 1 * 2;"));
            assertThrows(TypeMismatchException.class, () -> validate("number x; x = 'test';"));
            assertThrows(TypeMismatchException.class, () -> validate("boolean x; x = 'test';"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void variableDeclarationTest() {
        try {
            validate("string x = 'a1';");
            validate("string x = '';");
            validate("string x = '' + 1 + true;");
            validate("number x = 500;");
            validate("number x = -500;");
            validate("boolean x = true;");
            validate("boolean x = false;");

            assertThrows(MissingDeclarationException.class, () -> validate("string x = y;"));
            assertThrows(MissingDeclarationException.class, () -> validate("string x = fun(7);"));

            assertThrows(TypeMismatchException.class, () -> validate("string x = true;"));
            assertThrows(TypeMismatchException.class, () -> validate("string x = 1 + 2;"));

            assertThrows(TypeMismatchException.class, () -> validate("number x = true;"));
            assertThrows(TypeMismatchException.class, () -> validate("number x = 'a';"));

            assertThrows(TypeMismatchException.class, () -> validate("boolean x = 'a';"));
            assertThrows(TypeMismatchException.class, () -> validate("boolean x = 1 + 2;"));

            assertThrows(TypeMismatchException.class, () -> validate("boolean x = true; string y = x;"));
            assertThrows(TypeMismatchException.class, () -> validate("boolean x = true; number y = 1 + x;"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    private void validate(String input) throws Exception {
        Reader in = new StringReader(input);
        JParser parser = new JParser(in, false);
        Symbol root = parser.parse();
        Program program = (Program) root.value;
        Validator validator = new Validator();
        program.accept(validator);
    }
}

package execution;

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

public class InterpreterTest {

    @Test
    public void runtimeTest() {
        try {
            assertThrows(ArithmeticException.class, () -> interpret("number x; x = 1/0;"));
            assertThrows(ArithmeticException.class, () -> interpret("number x = 1/0;"));
            assertThrows(ArithmeticException.class, () -> interpret("print(1/0);"));
            assertThrows(ArithmeticException.class, () -> interpret("while(1/0 > 0) { }"));
            assertThrows(ArithmeticException.class, () -> interpret("if(1/0 > 0) { }"));
            assertThrows(ArithmeticException.class, () -> interpret("if(1/0 > 0) { } else {}"));
            assertThrows(ArithmeticException.class, () -> interpret("def number fun() { return 1/0; } fun();"));

            assertThrows(StackOverflowError.class, () -> interpret("while (true) { }"));
            assertThrows(StackOverflowError.class, () -> interpret("def number fun() { return fun(); } fun();"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void whileTest() {
        try {
            interpret("while (!true) { }");
            interpret("while (true && false) { }");

            interpret("while (!true) { while (false) { } }");
            interpret("while (!true) { if (false) { } else { } }");

            interpret("while (!true) { break; }");
            interpret("while (!true) { if (false) { break; } else { break; } }");

            assertThrows(MissingDeclarationException.class, () -> interpret("while (!true) { x = 1; }"));
            assertThrows(MissingDeclarationException.class, () -> interpret("while (!true) { number x = 1; } print(x);"));
            assertThrows(GrammarException.class, () -> interpret("while (!true) { if (false) { break; } else { break; } break; }"));
            assertThrows(GrammarException.class, () -> interpret("while (!true) { break; break; }"));
            assertThrows(GrammarException.class, () -> interpret("while (!true) { break; number x = 1; }"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void ifThenTest() {
        try {
            interpret("if (true) { }");
            interpret("if (true) { } else { }");

            interpret("if (!true) { }");
            interpret("if (true && false) { }");

            interpret("if (true) { number x = 1; }");
            interpret("if (true) { number x = 1; number y; y += 2; }");

            assertThrows(GrammarException.class, () -> interpret("if (true) { break; }"));
            assertThrows(GrammarException.class, () -> interpret("if (true) { } else { break; }"));
            assertThrows(MissingDeclarationException.class, () -> interpret("if (true) { x = 1; }"));
            assertThrows(MissingDeclarationException.class, () -> interpret("if (true) { number x = 1; } x = 2;"));
            assertThrows(Exception.class, () -> interpret("if (true) { def number fun() { return 1; } }"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void functionDefinitionTest() {
        try {
            interpret("def string fun() { return 'a'; }");
            interpret("def number fun() { return 1; }");
            interpret("def boolean fun() { return true; }");

            interpret("def number fun(number a) { return 1; } def number fun(number a, string b) { return 1; }");

            assertThrows(TypeMismatchException.class, () -> interpret("def string fun() { return 1; }"));
            assertThrows(MissingDeclarationException.class, () -> interpret("def number fun(number y) { return x; }"));
            assertThrows(GrammarException.class, () -> interpret("def number fun(number y) { return y; } def number fun(string y) { return 1; }"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void functionCallTest() {
        try {
            interpret("def number fun() { return 1; } number x = fun();");

            assertThrows(MissingDeclarationException.class, () -> interpret("def number fun1() { return 1; } number x = fun();"));
            assertThrows(MissingDeclarationException.class, () -> interpret("def number fun(number y) { return 1; } number x = fun();"));
            assertThrows(TypeMismatchException.class, () -> interpret("def number fun() { return 1; } string x = fun();"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void printCallTest() {
        try {
            interpret("print();");
            interpret("print(1);");
            interpret("print('a');");
            interpret("print(!true);");
            interpret("print(1 + 2 * 3);");
            interpret("print('a' + 'b');");

            assertThrows(MissingDeclarationException.class, () -> interpret("print(x);"));
            assertThrows(MissingDeclarationException.class, () -> interpret("print(fun());"));
            assertThrows(TypeMismatchException.class, () -> interpret("print(1 + true);"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void conditionalExpressionTest() {
        try {
            interpret("boolean x; x = true;");
            interpret("boolean x; x = (true && true);");
            interpret("boolean x; x = ((true && true) || false);");
            interpret("boolean x; x = ((true && true) && (true || false));");

            interpret("boolean x = (1 > 2);");
            interpret("boolean x = (1 >= 2);");
            interpret("boolean x = (1 <= 2);");
            interpret("boolean x = (1 < 2);");
            interpret("boolean x = (1 == 2);");

            interpret("boolean x = !true;");
            interpret("boolean x = !(true && !true);");
            interpret("boolean x = (!(true && true) || !false);");
            interpret("boolean x = !((true && true) && (true || false));");

            interpret("boolean x = (1 != 2);");

            assertThrows(TypeMismatchException.class, () -> interpret("boolean x = (true != 2);"));
            assertThrows(TypeMismatchException.class, () -> interpret("boolean x = (1 != 'a');"));

            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void expressionTest() {
        try {
            interpret("number var; number x = -var + 2 * 80 % 12 - 5.5 / 2;");

            assertThrows(MissingDeclarationException.class, () -> interpret("string y = x + 1;"));
            assertThrows(TypeMismatchException.class, () -> interpret("number x; string y = x + 1;"));
            assertThrows(TypeMismatchException.class, () -> interpret("boolean x = true; boolean y = (1 < true);"));
            assertThrows(TypeMismatchException.class, () -> interpret("number x = 1; boolean y = (true && x);"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void assignmentTest() {
        try {
            interpret("string x; x += 'a1';");
            interpret("string x; x += 1;");
            interpret("string x; x += true;");
            interpret("number x; x++;");
            interpret("number x; x = -x;");
            interpret("boolean x; x = !x;");

            assertThrows(OperatorMismatchException.class, () -> interpret("string x; x++;"));
            assertThrows(OperatorMismatchException.class, () -> interpret("string x; x = -x;"));

            assertThrows(TypeMismatchException.class, () -> interpret("string x; x = !x;"));
            assertThrows(TypeMismatchException.class, () -> interpret("string x; x -= 'a1';"));
            assertThrows(TypeMismatchException.class, () -> interpret("string x; x *= 'a1';"));
            assertThrows(TypeMismatchException.class, () -> interpret("string x; x /= 'a1';"));
            assertThrows(TypeMismatchException.class, () -> interpret("string x; x = 1 * 2;"));
            assertThrows(TypeMismatchException.class, () -> interpret("number x; x = 'test';"));
            assertThrows(TypeMismatchException.class, () -> interpret("boolean x; x = 'test';"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void variableDeclarationTest() {
        try {
            interpret("string x = 'a1';");
            interpret("string x = '';");
            interpret("string x = '' + 1 + true;");
            interpret("number x = 500;");
            interpret("number x = -500;");
            interpret("boolean x = true;");
            interpret("boolean x = false;");

            assertThrows(MissingDeclarationException.class, () -> interpret("string x = y;"));
            assertThrows(MissingDeclarationException.class, () -> interpret("string x = fun(7);"));

            assertThrows(TypeMismatchException.class, () -> interpret("string x = true;"));
            assertThrows(TypeMismatchException.class, () -> interpret("string x = 1 + 2;"));

            assertThrows(TypeMismatchException.class, () -> interpret("number x = true;"));
            assertThrows(TypeMismatchException.class, () -> interpret("number x = 'a';"));

            assertThrows(TypeMismatchException.class, () -> interpret("boolean x = 'a';"));
            assertThrows(TypeMismatchException.class, () -> interpret("boolean x = 1 + 2;"));

            assertThrows(TypeMismatchException.class, () -> interpret("boolean x = true; string y = x;"));
            assertThrows(TypeMismatchException.class, () -> interpret("boolean x = true; number y = 1 + x;"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    private void interpret(String input) throws Exception {
        Reader in = new StringReader(input);
        JParser parser = new JParser(in, false);
        Symbol root = parser.parse();
        Program program = (Program) root.value;
        Interpreter interpreter = new Interpreter();
        program.accept(interpreter);
    }
}

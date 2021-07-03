package parser;

import org.junit.Test;

import java.io.Reader;
import java.io.StringReader;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;

public class JParserTest {

    @Test
    public void programTest() {
        try {
            parse("");
            assertThrows(NullPointerException.class, () -> parse(null));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void whileTest() {
        try {
            parse("while (true) { }");
            parse("while (!true) { }");
            parse("while (true && false) { }");
            parse("while ((true && false) || true) { }");
            parse("while (!(true && !false) || true) { }");

            parse("while (true) { x = 1; }");
            parse("while (true) { x = 1; y += 2; }");

            parse("while (true) { while (false) { } }");
            parse("while (true) { if (false) { } else { } }");

            parse("while (true) { break; }");
            parse("while (true) { if (false) { break; } else { break; } break; }");

            assertThrows(Exception.class, () -> parse("while (true) { };"));
            assertThrows(Exception.class, () -> parse("while true { }"));
            assertThrows(Exception.class, () -> parse("while (true)"));
            assertThrows(Exception.class, () -> parse("while !(true) { };"));
            assertThrows(Exception.class, () -> parse("while (1 + 2) { }"));
            assertThrows(Exception.class, () -> parse("while (true) { def number fun() { return 1; } }"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void ifThenTest() {
        try {
            parse("if (true) { }");
            parse("if (true) { } else { }");

            parse("if (!true) { }");
            parse("if (true && false) { }");
            parse("if ((true && false) || true) { }");
            parse("if (!(true && !false) || true) { }");

            parse("if (true) { x = 1; }");
            parse("if (true) { x = 1; y += 2; }");
            parse("if (true) { x = 1; } else { } ");
            parse("if (true) { x = 1; y += 2; } else { }");

            parse("if (true) { } else { x = 1; y += 2; }");
            parse("if (true) { } else { x = 1; }");
            parse("if (true) { fun(); } else { x++; }");
            parse("if (true) { if (false) { } } else { if (true) { } else { } }");

            assertThrows(Exception.class, () -> parse("if (true) { };"));
            assertThrows(Exception.class, () -> parse("if true { }"));
            assertThrows(Exception.class, () -> parse("if (true)  "));
            assertThrows(Exception.class, () -> parse("if (true) { } else"));
            assertThrows(Exception.class, () -> parse("(true) { }"));
            assertThrows(Exception.class, () -> parse("if !(true) { };"));
            assertThrows(Exception.class, () -> parse("if (true) { } else if (false) { }"));
            assertThrows(Exception.class, () -> parse("if (true) { def number fun() { return 1; } }"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void functionDefinitionTest() {
        try {
            parse("def string fun() { return x; }");
            parse("def number fun() { return x; }");
            parse("def boolean fun() { return x; }");

            parse("def string fun(number a) { return x; }");
            parse("def string fun(string a) { return x; }");
            parse("def string fun(boolean a) { return x; }");

            parse("def string fun(number a, string b, boolean c, string d) { return x; }");

            parse("def string fun(number a) { x++; return x; }");
            parse("def string fun(number a) { y++; return x; }");
            parse("def string fun(number a) { a = 1; string test = 'test'; return x; }");

            parse("def string fun(number a) { return 1; }");
            parse("def string fun(number a) { return 1 + 1; }");
            parse("def string fun(number a) { return true; }");
            parse("def string fun(number a) { return fun(); }");
            parse("def string fun(number a) { return -x; }");

            assertThrows(Exception.class, () -> parse("def string fun() { return x; };"));
            assertThrows(Exception.class, () -> parse("string fun() { return x; }"));
            assertThrows(Exception.class, () -> parse("def fun() { return x; }"));
            assertThrows(Exception.class, () -> parse("def string fun { return x; }"));
            assertThrows(Exception.class, () -> parse("def string fun() return x;"));
            assertThrows(Exception.class, () -> parse("def string fun() {}"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void functionCallTest() {
        try {
            parse("fun1();");
            parse("funct(x);");
            parse("fun(1);");
            parse("fun('a');");
            parse("fun(true);");
            parse("fun(true && false);");
            parse("fun(!true);");
            parse("fun(1 + 2 * 3);");
            parse("fun('a' + 'b');");
            parse("fun(fun());");
            parse("fun(fun(x) + fun(1));");
            parse("fun(1, 2);");
            parse("fun(7, -var);");
            parse("fun(1 + 2, 'a', 100000, 'test', 4.4);");
            parse("x = fun();");

            assertThrows(Exception.class, () -> parse("fun"));
            assertThrows(Exception.class, () -> parse("fun()"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void printCallTest() {
        try {
            parse("print();");
            parse("print(x);");
            parse("print(1);");
            parse("print('a');");
            parse("print(true);");
            parse("print(!true);");
            parse("print(1 + 2 * 3);");
            parse("print('a' + 'b');");
            parse("print(fun());");
            parse("print(fun(x) + fun(1));");

            assertThrows(Exception.class, () -> parse("print"));
            assertThrows(Exception.class, () -> parse("print;"));
            assertThrows(Exception.class, () -> parse("print()"));
            assertThrows(Exception.class, () -> parse("print(1, 2);"));
            assertThrows(Exception.class, () -> parse("print('a', a, 7);"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void conditionalExpressionTest() {
        try {
            parse("x = true;");
            parse("x = (true && true);");
            parse("x = ((true && true) || false);");
            parse("x = ((true && true) && (true || false));");

            parse("x = (1 > 2);");
            parse("x = (1 >= 2);");
            parse("x = (1 <= 2);");
            parse("x = (1 < 2);");
            parse("x = (1 == 2);");
            parse("x = (1 != 2);");

            parse("x = !true;");
            parse("x = !(true && !true);");
            parse("x = (!(true && true) || !false);");
            parse("x = !((true && true) && (true || false));");

            assertThrows(Exception.class, () -> parse("x = true || false;"));
            assertThrows(Exception.class, () -> parse("x = (true && false) && var;"));
            assertThrows(Exception.class, () -> parse("x = (boolean > 2);"));
            assertThrows(Exception.class, () -> parse("x = (1 + 2);"));
            assertThrows(Exception.class, () -> parse("x = (1 * 2);"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void expressionTest() {
        try {
            parse("x = -var + 2 * 80 % 12 - 5.5 / 2;");

            assertThrows(Exception.class, () -> parse("x = (1 + 2);"));
            assertThrows(Exception.class, () -> parse("x = 1 && 2;"));
            assertThrows(Exception.class, () -> parse("x = 1 == 2;"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void assignmentTest() {
        try {
            parse("x = 'a1';");
            parse("x = '';");
            parse("x = 500;");
            parse("x = -500;");
            parse("x = true;");
            parse("x = false;");

            parse("x = y;");
            parse("x = fun(7);");
            parse("x = fun();");
            parse("x = (true && false);");
            parse("x = ((true && false) || true);");
            parse("x = !(true && var);");
            parse("x = !true;");
            parse("x = !var;");
            parse("x = -var;");
            parse("x = -var + 2;");
            parse("x = -var + 2 * 80 % 12 - 5.5 / 2;");

            parse("x++;");
            parse("x--;");

            parse("x += 'a1';");
            parse("x -= 'a1';");
            parse("x *= 'a1';");
            parse("x /= 'a1';");
            parse("x %= 'a1';");

            assertThrows(Exception.class, () -> parse("x && 'a1';"));
            assertThrows(Exception.class, () -> parse("x || 'a1';"));
            assertThrows(Exception.class, () -> parse("x ( 'a1';"));
            assertThrows(Exception.class, () -> parse("x , 'a1';"));
            assertThrows(Exception.class, () -> parse("x ! 'a1';"));
            assertThrows(Exception.class, () -> parse("x print 'a1';"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    @Test
    public void variableDeclarationTest() {
        try {
            parse("string x = 'a1';");
            parse("string x = '';");
            parse("number x = 500;");
            parse("number x = -500;");
            parse("boolean x = true;");
            parse("boolean x = false;");

            parse("string x;");
            parse("number x;");
            parse("boolean x;");

            parse("string x = y;");
            parse("string x = fun(7);");
            parse("string x = fun();");
            parse("string x = (true && false);");
            parse("string x = ((true && false) || true);");
            parse("string x = !(true && var);");
            parse("string x = !true;");
            parse("string x = !var;");
            parse("string x = -var;");
            parse("string x = -var + 2;");
            parse("string x = -var + 2 * 80 % 12 - 5.5 / 2;");

            parse("number x = y;");
            parse("number x = fun(7);");
            parse("number x = fun();");
            parse("number x = (true && false);");
            parse("number x = ((true && false) || true);");
            parse("number x = !(true && var);");
            parse("number x = !true;");
            parse("number x = !var;");
            parse("number x = -var;");
            parse("number x = -var + 2;");
            parse("number x = -var + 2 * 80 % 12 - 5.5 / 2;");

            parse("boolean x = y;");
            parse("boolean x = fun(7);");
            parse("boolean x = fun();");
            parse("boolean x = (true && false);");
            parse("boolean x = ((true && false) || true);");
            parse("boolean x = !(true && var);");
            parse("boolean x = !true;");
            parse("boolean x = !var;");
            parse("boolean x = -var;");
            parse("boolean x = -var + 2;");
            parse("boolean x = -var + 2 * 80 % 12 - 5.5 / 2;");

            assertThrows(Exception.class, () -> parse("string x = print;"));
            assertThrows(Exception.class, () -> parse("string x = if;"));
            assertThrows(Exception.class, () -> parse("string x = print();"));
            assertThrows(Error.class, () -> parse("string x = 'a1;"));
            assertThrows(Error.class, () -> parse("string x = a1';"));
            assertThrows(Exception.class, () -> parse("string x = 'a1'"));
            assertThrows(Exception.class, () -> parse("string x = ;"));
            assertThrows(Exception.class, () -> parse("string x 'a1';"));
            assertThrows(Exception.class, () -> parse("string x 'a1'"));
            assertThrows(Exception.class, () -> parse("string = 'a1';"));
            assertThrows(Exception.class, () -> parse("string = 'a1'"));
            assertThrows(Exception.class, () -> parse("x = 'a1'"));
            assertThrows(Exception.class, () -> parse("'a1'"));
            assertThrows(Exception.class, () -> parse("string 'a1'"));
            assertThrows(Exception.class, () -> parse("string = "));
            assertThrows(Exception.class, () -> parse("string;"));
            assertThrows(Exception.class, () -> parse("string"));
            assertThrows(Exception.class, () -> parse("x"));
            assertThrows(Exception.class, () -> parse("="));
            assertThrows(Exception.class, () -> parse("'a'"));
            assertThrows(Exception.class, () -> parse(";"));
            assert true;
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }

    private void parse(String input) throws Exception {
        Reader in = new StringReader(input);
        JParser parser = new JParser(in, false);
        parser.parse();
    }
}

package scanner;
import java.util.ArrayList;
import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;import parser.sym;

%%
// ------------------------------------------------------------------------------------------------------------------------------------------------------
// options

%public
%class JScanner
%cup
%implements sym
%unicode
%standalone
%char
%line
%column

// ------------------------------------------------------------------------------------------------------------------------------------------------------
// user code definition
%{
  private ArrayList<Token> tokenList = new ArrayList<>();
  ComplexSymbolFactory symbolFactory;

  public JScanner(java.io.Reader in, ComplexSymbolFactory sf){
    this(in);
 	symbolFactory = sf;
  }

  public JScanner(String filename) {
      System.out.println("start reading file " + filename + "...\n");
      String[] argv = new String[] {filename};
      if (argv.length == 0) {
        System.out.println("Usage : java JScanner [ --encoding <name> ] <inputfile(s)>");
      }
      else {
        int firstFilePos = 0;
        String encodingName = "UTF-8";
        if (argv[0].equals("--encoding")) {
          firstFilePos = 2;
          encodingName = argv[1];
          try {
            // Side-effect: is encodingName valid?
            java.nio.charset.Charset.forName(encodingName);
          } catch (Exception e) {
            System.out.println("Invalid encoding '" + encodingName + "'");
            return;
          }
        }
        for (int i = firstFilePos; i < argv.length; i++) {
          try {
            java.io.FileInputStream stream = new java.io.FileInputStream(argv[i]);
            this.zzReader = new java.io.InputStreamReader(stream, encodingName);
            while ( !this.zzAtEOF ) this.next_token();
          }
          catch (java.io.FileNotFoundException e) {
            System.out.println("File not found : \""+argv[i]+"\"");
          }
          catch (java.io.IOException e) {
            System.out.println("IO error scanning file \""+argv[i]+"\"");
            System.out.println(e);
          }
          catch (Exception e) {
            System.out.println("Unexpected exception:");
            e.printStackTrace();
          }
        }
      }
  }

  public String getNextToken() {
      return null;
  }

  private Symbol collectToken(String token) {
      tokenList.add(new Token(token, yytext()));
      consolePrint(token);
      return null;
  }

  private void consolePrint(String value) {
      System.out.println("token {" + value + "}: found match <" + yytext() + "> at line " + yyline + ", column " + yycolumn + ".");
  }

  public ArrayList<Token> getTokenList() {
      return tokenList;
  }

  public ArrayList<String> getTokens() {
      ArrayList<String> list = new ArrayList<>();
      for (Token token : tokenList) {
          list.add(token.name);
      }
      return list;
  }

  public ArrayList<Object> getValues() {
      ArrayList<Object> list = new ArrayList<>();
        for (Token token : tokenList) {
            list.add(token.value);
        }
        return list;
  }

  public void printTokens() {
      System.out.println("\n***** TOKENS *****\n");
      ArrayList<String> tokens = getTokens();
      for (int i = 0; i < tokens.size(); i++) {
          String token = tokens.get(i);
          if (i < tokens.size()-1) {
              token += ", ";
          }
          System.out.print(token);
      }
  }

  public void printValues() {
        System.out.println("\n***** File content *****\n");
        ArrayList<Object> values = getValues();
        for (Object obj : values) {
            System.out.print(obj);
        }
    }

  static class Token {
      String name;
      Object value;
      Token(String name, Object value) {
          this.name = name;
          this.value = value;
      }
  }
  private Symbol symbol(String name, int sym) {
        return symbolFactory.newSymbol(name, sym, null);
    }

    private Symbol symbol(String name, int sym, Object val) {
        return symbolFactory.newSymbol(name, sym, val);
    }
    private Symbol symbol(String name, int sym, Object val,int buflength) {
        return symbolFactory.newSymbol(name, sym, val);
    }

%}

%eofval{
     return symbolFactory.newSymbol("EOF", sym.EOF, null);
%eofval}

%eof{
    System.out.println("\n\n...end of file reached at line " + yyline + ", column " + yycolumn + ".\n");
%eof}

// ------------------------------------------------------------------------------------------------------------------------------------------------------
// macro definition

// comment
COMMENT = "/*" [^*] ~"*/" | "/*" "*"+ "/" | "//" [^\r\n]* \r|\n|\r\n? | "/**" ( [^*] | \*+ [^/*] )* "*"+ "/"

// reserved words
BOOL = true | false

// character classes for numbers and strings
NUM = -?[0-9]\d*(\.\d+)?                // decimal/int number, positive or negative
VAR = [a-z_]+                           // variables
STR = '[a-z0-9_\,\.\(\)\;\:\/\+\-\*\/ \s\t\f\r\n]*'   // strings

WHITESPACE = [ \t\f\r\n]+               // newline or spaces

ERR = [^]                               // fallback

%%

// ------------------------------------------------------------------------------------------------------------------------------------------------------
// lexical rules

// comments
{COMMENT}                         { /* ignore */ }

// reserved words
string                            { return collectToken("STRTYPE"); }
boolean                           { return collectToken("BOOLTYPE"); }
number                            { return collectToken("NUMTYPE"); }
{BOOL}                            { return collectToken("BOOL"); }
return                            { return collectToken("RETURN"); }
while                             { return collectToken("WHILE"); }
if                                { return collectToken("IF"); }
else                              { return collectToken("IF"); }
def                               { return collectToken("DEF"); }
main                              { return collectToken("MAIN"); }
print                             { return collectToken("PRINT"); }

// stop
;                                 { return collectToken("STOP"); }

// special characters / terminals
==                                { return collectToken("EQ"); }
\!=                               { return collectToken("NEQ"); }
>=                                { return collectToken("GREQ"); }
\<=                               { return collectToken("LEQ"); }
&&                                { return collectToken("AND"); }
\|\|                              { return collectToken("OR"); }
\+=                               { return collectToken("PLUSEQ"); }
-=                                { return collectToken("MINEQ"); }
\*=                               { return collectToken("MULEQ"); }
\/=                               { return collectToken("MULEQ"); }
\(                                { return collectToken("BL"); }
\)                                { return collectToken("BR"); }
\{                                { return collectToken("CBL"); }
\}                                { return collectToken("CBR"); }
,                                 { return collectToken("COMMA"); }
=                                 { return collectToken("EQUAL"); }
\<                                { return collectToken("LESS"); }
>                                 { return collectToken("GREATER"); }
\+                                { return collectToken("PLUS"); }
-                                 { return collectToken("MINUS"); }
\*                                { return collectToken("MUL"); }
\/                                { return collectToken("DIV"); }

// character classes for numbers and strings
{NUM}                             { return collectToken("NUM"); }
{VAR}                             { return collectToken("VAR"); }
{STR}                             { return collectToken("STR"); }

// whitespace
{WHITESPACE}                      { /* ignore */ }

// error fallback
{ERR}                             { throw new Error("Illegal character <"+ yytext()+">"); }
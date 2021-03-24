package pva2_scanner_specification;
import java.util.ArrayList;

%%
// ------------------------------------------------------------------------------------------------------------------------------------------------------
// options

%public
%class JLang

%unicode
%standalone
%char
%line
%column

// ------------------------------------------------------------------------------------------------------------------------------------------------------
// user code definition
%{
  private ArrayList<Token> tokenList = new ArrayList<>();

  public JLang(String filename) {
      System.out.println("start reading file " + filename + "...\n");
      String[] argv = new String[] {filename};
      if (argv.length == 0) {
        System.out.println("Usage : java JLang [ --encoding <name> ] <inputfile(s)>");
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
            while ( !this.zzAtEOF ) this.yylex();
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

  private void collectToken(String token) {
      tokenList.add(new Token(token, yytext()));
      consolePrint(token);
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

%}

%eof{
    System.out.println("\n\n...end of file reached at line " + yyline + ", column " + yycolumn + ".\n");
%eof}

// ------------------------------------------------------------------------------------------------------------------------------------------------------
// macro definition

// comment
COMMENT = "/*" [^*] ~"*/" | "/*" "*"+ "/" | "//" [^\r\n]* \r|\n|\r\n? | "/**" ( [^*] | \*+ [^/*] )* "*"+ "/"

// reserved words
STRTYPE = string
BOOLTYPE = boolean
NUMTYPE = number
BOOL = true | false
RETURN = return
WHILE = while
IF = if
ELSE = else
DEF = def
MAIN = main
PRINT = print

// special characters / terminals
APO = \'
BL = \(
BR = \)
CBL = \{
CBR = }
COMMA = ,
EQUAL = =
EXCL = \!
LESS = \<
GREATER = >
PLUS = \+
MINUS = -
MUL = \*
DIV = \/
ND = &
RR = \|
EQ = ==
NEQ = \!=
GREQ = >=
LEQ = <=
AND = &&
OR = \|\|
PLUSEQ = \+=
MINEQ = -=
MULEQ = \*=
DIVEQ = \/=

STOP = ;[ \t\f\r\n]*                    // stop token

// character classes for numbers and strings
N = -?[0-9]\d*(\.\d+)?                  // decimal/int number, positive or negative
STR = [a-z_]+                           // variables
W = '[a-z0-9_\,\.\(\)\;\:\/\+\-\*\/ \s\t\f\r\n]*'   // strings

NL_SPACE = [ \t\f\r\n]+                 // newline or spaces
SPACE = [ \t\f]+                        // one or more spaces

ERR = [^]                               // fallback

%%

// ------------------------------------------------------------------------------------------------------------------------------------------------------
// lexical rules

// comments
{COMMENT}                         { /* ignore */ }

// reserved words
{STRTYPE}                         { collectToken("STRTYPE"); }
{BOOLTYPE}                        { collectToken("BOOLTYPE"); }
{NUMTYPE}                         { collectToken("NUMTYPE"); }
{BOOL}                            { collectToken("BOOL"); }
{RETURN}                          { collectToken("RETURN"); }
{WHILE}                           { collectToken("WHILE"); }
{IF}                              { collectToken("IF"); }
{ELSE}                            { collectToken("IF"); }
{DEF}                             { collectToken("DEF"); }
{MAIN}                            { collectToken("MAIN"); }
{PRINT}                           { collectToken("PRINT"); }

// stop
{STOP}                            { collectToken("STOP"); }

// special characters / terminals
{EQ}                              { collectToken("EQ"); }
{NEQ}                             { collectToken("NEQ"); }
{GREQ}                            { collectToken("GREQ"); }
{LEQ}                             { collectToken("LEQ"); }
{AND}                             { collectToken("AND"); }
{OR}                              { collectToken("OR"); }
{PLUSEQ}                          { collectToken("PLUSEQ"); }
{MINEQ}                           { collectToken("MINEQ"); }
{MULEQ}                           { collectToken("MULEQ"); }
{DIVEQ}                           { collectToken("MULEQ"); }
{APO}                             { collectToken("APO"); }
{BL}                              { collectToken("BL"); }
{BR}                              { collectToken("BR"); }
{CBL}                             { collectToken("CBL"); }
{CBR}                             { collectToken("CBR"); }
{COMMA}                           { collectToken("COMMA"); }
{EQUAL}                           { collectToken("EQUAL"); }
{EXCL}                            { collectToken("EXCL"); }
{LESS}                            { collectToken("LESS"); }
{GREATER}                         { collectToken("GREATER"); }
{PLUS}                            { collectToken("PLUS"); }
{MINUS}                           { collectToken("MINUS"); }
{MUL}                             { collectToken("MUL"); }
{DIV}                             { collectToken("DIV"); }
{ND}                              { collectToken("ND"); }
{RR}                              { collectToken("RR"); }

// character classes for numbers and strings
{N}                               { collectToken("N"); }
{STR}                             { collectToken("STR"); }
{W}                               { collectToken("W"); }

// whitespace
{SPACE}                           { collectToken("SPACE"); }
{NL_SPACE}                        { collectToken("NL_SPACE"); }

// error fallback
{ERR}                             { throw new Error("Illegal character <"+ yytext()+">"); }
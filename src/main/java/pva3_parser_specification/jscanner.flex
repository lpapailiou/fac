package pva3_parser_specification;
import java.util.ArrayList;

%%
// ------------------------------------------------------------------------------------------------------------------------------------------------------
// options

%public
%class JScanner

%unicode
%standalone
%char
%line
%column

// ------------------------------------------------------------------------------------------------------------------------------------------------------
// user code definition
%{
  private ArrayList<Token> tokenList = new ArrayList<>();

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

  public String getNextToken() {
      return null;
  }

  private int collectToken(String token) {
      tokenList.add(new Token(token, yytext()));
      consolePrint(token);
      return 0;
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
BOOL = true | false

// character classes for numbers and strings
NUM = -?[0-9]\d*(\.\d+)?                // decimal/int number, positive or negative
VAR = [a-z_]+                           // variables
STR = '[a-z0-9_\,\.\(\)\;\:\/\+\-\*\/ \s\t\f\r\n]*'   // strings

NL_SPACE = [ \t\f\r\n]+                 // newline or spaces
SPACE = [ \t\f]+                        // one or more spaces
OPT_SPACE = [ \t\f]*                    // optional space

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
;[ \t\f\r\n]*                     { return collectToken("STOP"); }

// special characters / terminals
{OPT_SPACE}=={OPT_SPACE}          { return collectToken("EQ"); }
{OPT_SPACE}\!={OPT_SPACE}         { return collectToken("NEQ"); }
{OPT_SPACE}>={OPT_SPACE}          { return collectToken("GREQ"); }
{OPT_SPACE}\<={OPT_SPACE}         { return collectToken("LEQ"); }
{OPT_SPACE}&&{OPT_SPACE}          { return collectToken("AND"); }
{OPT_SPACE}\|\|{OPT_SPACE}        { return collectToken("OR"); }
{OPT_SPACE}\+={OPT_SPACE}         { return collectToken("PLUSEQ"); }
{OPT_SPACE}-={OPT_SPACE}          { return collectToken("MINEQ"); }
{OPT_SPACE}\*={OPT_SPACE}         { return collectToken("MULEQ"); }
{OPT_SPACE}\/={OPT_SPACE}         { return collectToken("MULEQ"); }
\(                                { return collectToken("BL"); }
\)                                { return collectToken("BR"); }
\{                                { return collectToken("CBL"); }
\}                                { return collectToken("CBR"); }
,                                 { return collectToken("COMMA"); }
{OPT_SPACE}={OPT_SPACE}           { return collectToken("EQUAL"); }
{OPT_SPACE}\<{OPT_SPACE}          { return collectToken("LESS"); }
{OPT_SPACE}>{OPT_SPACE}           { return collectToken("GREATER"); }
{OPT_SPACE}\+{OPT_SPACE}          { return collectToken("PLUS"); }
{OPT_SPACE}-{OPT_SPACE}           { return collectToken("MINUS"); }
{OPT_SPACE}\*{OPT_SPACE}          { return collectToken("MUL"); }
{OPT_SPACE}\/{OPT_SPACE}          { return collectToken("DIV"); }

// character classes for numbers and strings
{NUM}                             { return collectToken("NUM"); }
{VAR}                             { return collectToken("VAR"); }
{STR}                             { return collectToken("STR"); }

// whitespace
{SPACE}                           { return collectToken("SPACE"); }
{NL_SPACE}                        { return collectToken("NL_SPACE"); }

// error fallback
{ERR}                             { throw new Error("Illegal character <"+ yytext()+">"); }
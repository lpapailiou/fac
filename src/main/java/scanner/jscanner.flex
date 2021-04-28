package scanner;
import java.util.ArrayList;
import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;
import parser.JSymbol;

%%
// ------------------------------------------------------------------------------------------------------------------------------------------------------
// options

%public
%class JScanner
%cup
%implements JSymbol
%unicode
%char
%line
%column

// ------------------------------------------------------------------------------------------------------------------------------------------------------
// user code definition
%{
  private ArrayList<Symbol> symbols = new ArrayList<>();
  private ComplexSymbolFactory symbolFactory;

  private Symbol collectToken(int token) {
      Symbol symbol = symbol(yytext(), token, yytext());
      symbols.add(symbol);
      consolePrint(yytext());
      System.out.println(symbol.toString());
      return symbol;
  }

  private void consolePrint(String value) {
      System.out.println("token {" + value + "}: found match <" + yytext() + "> at line " + yyline + ", column " + yycolumn + ".");
  }

  public void printTokens() {
      System.out.println("\n***** TOKENS *****\n");
      for (int i = 0; i < symbols.size(); i++) {
          String str = symbols.get(0).toString().split(" ")[1];
          if (i < symbols.size()-1) {
              str += ", ";
          }
          System.out.print(str);
      }
      System.out.println();
  }

  public void printValues() {
        System.out.println("\n***** File content *****\n");
        for (int i = 0; i < symbols.size(); i++) {
              String str = symbols.get(0).value.toString();
              if (i < symbols.size()-1) {
                  str += ", ";
              }
              System.out.print(str);
          }
        System.out.println();
    }

    private Symbol symbol(String name, int sym, Object val) {
        Location left = new Location(yyline+1,(int)yycolumn+1,(int)yychar);
        Location right= new Location(yyline+1,(int)(yycolumn+yylength()), (int)(yychar+yylength()));
        return symbolFactory.newSymbol(name, sym, left, right, val);
    }
    private void error(String message) {
      System.out.println("Error at line "+(yyline+1)+", column "+(yycolumn+1)+" : "+message);
    }

%}

%init{
    symbolFactory = new ComplexSymbolFactory();
%init}

%eofval{
     return symbolFactory.newSymbol("EOF", JSymbol.EOF, null);
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
string                            { return collectToken(STRTYPE); }
boolean                           { return collectToken(BOOLTYPE); }
number                            { return collectToken(NUMTYPE); }
{BOOL}                            { return collectToken(BOOL); }
return                            { return collectToken(RETURN); }
while                             { return collectToken(WHILE); }
if                                { return collectToken(IF); }
else                              { return collectToken(ELSE); }
def                               { return collectToken(DEF); }
print                             { return collectToken(PRINT); }

// stop
;                                 { return collectToken(STOP); }

// special characters / terminals
==                                { return collectToken(EQ); }
\!=                               { return collectToken(NEQ); }
>=                                { return collectToken(GREQ); }
\<=                               { return collectToken(LEQ); }
&&                                { return collectToken(AND); }
\|\|                              { return collectToken(OR); }
\+=                               { return collectToken(PLUSEQ); }
-=                                { return collectToken(MINEQ); }
\*=                               { return collectToken(MULEQ); }
\/=                               { return collectToken(DIVEQ); }
\(                                { return collectToken(BL); }
\)                                { return collectToken(BR); }
\{                                { return collectToken(CBL); }
\}                                { return collectToken(CBR); }
,                                 { return collectToken(COMMA); }
=                                 { return collectToken(EQUAL); }
\<                                { return collectToken(LESS); }
>                                 { return collectToken(GREATER); }
\+                                { return collectToken(PLUS); }
-                                 { return collectToken(MINUS); }
\*                                { return collectToken(MUL); }
\/                                { return collectToken(DIV); }

// character classes for numbers and strings
{NUM}                             { return collectToken(NUM); }
{VAR}                             { return collectToken(VAR); }
{STR}                             { return collectToken(STR); }

// whitespace
{WHITESPACE}                      { /* ignore */ }

// error fallback
{ERR}                             { throw new Error("Illegal character <"+ yytext()+">"); }
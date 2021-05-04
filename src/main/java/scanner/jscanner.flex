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
    private ComplexSymbolFactory symbolFactory;
    private boolean verbose = true;

    public JScanner(java.io.Reader in, boolean verbose) {
        this(in);
        this.verbose = verbose;
    }

    private Symbol collectToken(int token, String description) {
      Symbol symbol = symbol(yytext(), token, yytext());
      if (verbose) {
        consolePrint(description);
      }
      return symbol;
    }

    private void consolePrint(String value) {
      System.out.println("scanning token {" + value + "}: found match <" + yytext() + "> at line " + yyline + ", column " + yycolumn + ".");
    }

    private Symbol symbol(String name, int sym, Object val) {
        Location left = new Location(yyline+1,(int)yycolumn+1,(int)yychar);
        Location right= new Location(yyline+1,(int)(yycolumn+yylength()), (int)(yychar+yylength()));
        return symbolFactory.newSymbol(name, sym, left, right, val);
    }
    private void error(String message) {
      System.out.println("Error at line " + (yyline+1) + ", column "+ (yycolumn+1) + " : "+message);
    }

%}

%init{
    symbolFactory = new ComplexSymbolFactory();

%init}

%eofval{
     return symbolFactory.newSymbol("EOF", JSymbol.EOF, null);
%eofval}

%eof{
    if (verbose) {
        System.out.println("\n...end of file reached at line " + yyline + ", column " + yycolumn + ".\n");
    }
%eof}

// ------------------------------------------------------------------------------------------------------------------------------------------------------
// macro definition

// comment
COMMENT = "/*" [^*] ~"*/" | "/*" "*"+ "/" | "//" [^\r\n]* \r|\n|\r\n? | "/**" ( [^*] | \*+ [^/*] )* "*"+ "/"

// reserved words
BOOL = true | false

// character classes for numbers and strings
NUM = -?[0-9]\d*(\.\d+)?                // decimal/int number, positive or negative
VAR = [a-z_]+([0-9])*                   // variables
STR = '[a-z0-9_\,\.\(\)\;\:\/\+\-\*\/ \s\t\f\r\n]*'   // strings

WHITESPACE = [ \t\f\r\n]+               // newline or spaces

ERR = [^]                               // fallback

%%

// ------------------------------------------------------------------------------------------------------------------------------------------------------
// lexical rules

// comments
{COMMENT}                         { /* ignore */ }

// reserved words
string                            { return collectToken(STRTYPE, "STRTYPE"); }
boolean                           { return collectToken(BOOLTYPE, "BOOLTYPE"); }
number                            { return collectToken(NUMTYPE, "NUMTYPE"); }
{BOOL}                            { return collectToken(BOOL, "BOOLTYPE"); }
return                            { return collectToken(RETURN, "RETURN"); }
while                             { return collectToken(WHILE, "WHILE"); }
break                             { return collectToken(BREAK, "BREAK"); }
if                                { return collectToken(IF, "IF"); }
else                              { return collectToken(ELSE, "ELSE"); }
def                               { return collectToken(DEF, "DEF"); }
print                             { return collectToken(PRINT, "PRINT"); }

// stop
;                                 { return collectToken(STOP, "STOP"); }

// special characters / terminals
==                                { return collectToken(EQ, "EQ"); }
\!=                               { return collectToken(NEQ, "NEQ"); }
>=                                { return collectToken(GREQ, "GREQ"); }
\<=                               { return collectToken(LEQ, "LEQ"); }
&&                                { return collectToken(AND, "AND"); }
\|\|                              { return collectToken(OR, "OR"); }
\+=                               { return collectToken(PLUSEQ, "PLUSEQ"); }
-=                                { return collectToken(MINEQ, "MINEQ"); }
\*=                               { return collectToken(MULEQ, "MULEQ"); }
\/=                               { return collectToken(DIVEQ, "DIVEQ"); }
\(                                { return collectToken(BL, "BL"); }
\)                                { return collectToken(BR, "BR"); }
\{                                { return collectToken(CBL, "CBL"); }
\}                                { return collectToken(CBR, "CBR"); }
,                                 { return collectToken(COMMA, "COMMA"); }
=                                 { return collectToken(EQUAL, "EQUAL"); }
\<                                { return collectToken(LESS, "LESS"); }
>                                 { return collectToken(GREATER, "GREATER"); }
\+                                { return collectToken(PLUS, "PLUS"); }
-                                 { return collectToken(MINUS, "MINUS"); }
\*                                { return collectToken(MUL, "MUL"); }
\/                                { return collectToken(DIV, "DIV"); }

// character classes for numbers and strings
{NUM}                             { return collectToken(NUM, "NUM"); }
{VAR}                             { return collectToken(VAR, "VAR"); }
{STR}                             { return collectToken(STR, "STR"); }

// whitespace
{WHITESPACE}                      { /* ignore */ }

// error fallback
{ERR}                             { throw new Error("Illegal character <"+ yytext()+">"); }
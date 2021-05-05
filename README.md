# FAC
This repository connects the two theoretical computer science disciplines ``formal languages`` and ``automatons`` with
the rather technical discipline of ``compiler construction`` (<b>F.A.C.</b>).  
The goal is to specify a new programming language, which is deterministic and executable.

## Table of Contents
1. [Language design](#language-design)  
	1.1 [Scope](#scope)  
	1.2 [Lexical rules](#lexical-rules)  
	1.3 [Syntactical rules](#syntactical-rules)  
	1.4 [Semantic rules](#semantic-rules)  	  
	1.5 [Execution](#execution)    
2. [Repository handling](#repository-handling)  
	2.1 [Clone](#clone)   
	2.2 [Package structure](#package-structure)   
	2.3 [Run](#run)  

## Language design
### Scope
Our new toy language has roughly following scope:  
<ul>
<li>variable declarations</li>
<li>string concatenation</li>
<li>arithmetic expressions</li>
<li>conditional statements</li>
<li>loops</li>
<li>function calls</li>
<li>function definitions</li>
<li>built-in print function</li>
</ul>

It is a type safe programming language with three available data types: strings, numbers and booleans, while
``null`` will never be an acceptable value.  
Classes and other complex concepts are not supported.  
  
The look-and-feel will be Java-like. Here's a small code sample:  

    number one = 1;
    while (one < 5) {
        one += 1;
        print('hello world');
    }

### Lexical rules
Lexical rules are applied when the toy code is scanned by the ``Scanner``. During this process, one or multiple 
characters will be transformed to so called tokens ('words of the code'). There is a limited set of allowed tokens, 
meaning that unknown tokens will result in an error automatically.  
  
The scanner used in this repository was generated with [jflex](https://jflex.de/). It can be found in the
package ``src\main\java\scanner``.  
The token identification is implemented with regular expressions. 

    // sample section of .flex file
    ==                                { return collectToken(EQ, "EQ"); }
    \!=                               { return collectToken(NEQ, "NEQ"); }
    >=                                { return collectToken(GREQ, "GREQ"); }
    \<=                               { return collectToken(LEQ, "LEQ"); }

#### Comments
Java-like comments are allowed. They will be ignored in further processing of the code - except the comment takes the 
end of the fail, then an error will be thrown.  
Pattern: ``"/*" [^*] ~"*/" | "/*" "*"+ "/" | "//" [^\r\n]* \r|\n|\r\n? | "/**" ( [^*] | \*+ [^/*] )* "*"+ "/"``  
#### Whitespace
Whitespace may consist of spaces and newlines. It will be ignored in further processing steps, but
is initially useful to separate tokens from each other.  
Pattern: ``[ \t\f\r\n]+`` 
#### Reserved words
Reserved words are: ``string``, ``number``, ``boolean`` (for data types), ``while``, ``break``,
``if``, ``else``, ``def``, ``return`` and ``print`` (for printing to the console).  
#### Boolean values
Boolean values may be either ``true`` or ``false``.  
#### Numeric values
Numeric values will be interpreted as Double. The length will not be checked.  
Pattern: ``-?[0-9]\d*(\.\d+)?``  
#### String values
Strings may contain lowercase letters, digits and a few special characters. The must be enclosed
in apostrophes.  
Pattern: ``'[a-z0-9_\,\.\(\)\;\:\/\+\-\*\/ \s\t\f\r\n]*'``  
#### Variable identifiers
Variable identifiers may consist of lowercase letters and optional digits at the end.  
Pattern: ``[a-z_]+([0-9])*``. 
#### Operators
Additionally, there is quite a set of unary and binary operators.  
Arithmetic operators: ``+``, ``-``, ``*``, ``/``.  
Conditional operators: ``==``, ``!=``, ``<``, ``>``, ``<=``, ``>=``.  
Assignment operators: ``=``, ``+=``, ``-=``, ``*=``, ``/=``.  
Evaluation operators: ``&&``, ``||``.
#### Special characters
Finally, there are two types of brackets: ``(``, ``)``, ``{``, ``}``, as well as the infamous comma ``,`` 
and semicolon ``;``. 

### Syntactical rules
Syntactical rules are applied by the ``Parser``. During the processing of the code, it will receive token by token
from the scanner. The parser then validates if the sequence of tokens follows the defined grammatical rules (e.g. 
a statement must stop always with a semicolon). If the code is valid, the parser will organize the identified statements
 in a so called 'parse tree', which will be handy for further processing.   
  
The parser used in this repository was generated with [cup](http://www2.cs.tum.edu/projects/cup/). It can be found in the
package ``src\main\java\parser``.  
The syntactical rules are designed with the Backus-Naur-notation, which allows context-free validation only. 

    // sample section of .cup file
    ASSIGN 	        ::= VAR:e1 ASSIGN_OP:op EXPR:e2 STOP            {: RESULT = Statement.assgn(op, e1, e2); :}
                        ;
    
    ASSIGN_OP       ::= EQUAL:op                                    {: RESULT = op; :}
                        | PLUSEQ:op                                 {: RESULT = op; :}
                        | MINEQ:op                                  {: RESULT = op; :}
                        | DIVEQ:op                                  {: RESULT = op; :}
                        | MULEQ:op                                  {: RESULT = op; :}
                        ;

#### Variable declarations
<ul>
<li>Variable declarations must start with a data type (string, number or boolean).</li>
<li>The data type is followed by an identifier an an equal character.</li>
<li>Then, a value is assigned. The value can be:
<ul>
<li>a string, number or boolean value according to lexical definition.</li>
<li>another variable identifier.</li>
<li>an arithmetic or conditional expresseion.</li>
<li>a function call.</li>
<li>omitted. in this case, the equal character is omitted as well and the variable will get its default value ('', 0.0 or false).</li>
</ul></li>
<li>The variable declaration must end with a semicolon.</li>
<li>As the parser is context free, the data types of variable and assigned value cannot be evaluated further at this step.</li>
</ul>

    // examples
    string x1 = 1;                      // valid (no type safety yet)
    number y;                           // valid (variable will get default value)
    numbery7 = (true && false);         // valid (conditional expressions can be assigned)
    boolean 1z7 = (true && false);      // scanner fails, as variable identifier starts with a digit
    string x8 = fun1();                 // valid (function calls can be assigned)
    number y8 = print();                // parser fails, as print is a reserved word

#### Variable assignments
<ul>
<li>Variable assignments share the rules of variable declarations, except:
<ul>
<li>the preceding data type must be omitted.</li>
<li>a value must be assigned.</li>
<li>arithmetic assignment operators are additionally allowed.</li>
</ul></li>
</ul>

    // examples
    x1 = 1;                         // valid (no type safety yet)
    y;                              // parser fails, as a value must be assigned
    y7 = (true && false);           // valid (conditional expressions can be assigned)
    x8 = fun1()                     // parser fails, as semicolon is missing

#### Arithmetic expressions
<ul>
<li>Arithmetic expressions are meant to perform numeric calculations and string concatenation. Therefore, they use specific operators (+, -, *, /).</li>
<li>Arithmetic expressions cannot exist as isolated statement. They need to be part of a declaration or be assigned.</li>
<li>By default, its components can be either 'raw' values, conditional expressions or function calls.</li>
<li>Multiplication and division have precedence over addition and subtraction.</li>
<li>Arithmetic expressions can be nested. They are - after precedence - evaluated from left to right.</li>
<li>Arithmetic expressions must not have brackets.</li>
<li>As arithmetic expressions group all possible values and expressions, they can be potentially assigned anywhere.</li>
<li>Also here, data types are not evaluated any further.</li>
</ul>

    // examples
    1 + 2                               // parser fails as expression cannot be isolated
    x = 1 + 2 - 3;                      // valid, interpreted as ((1 + 2) - 3)
    x = 2 * 4 + 2 / 3;                  // valid, interpreted as ((2 * 4) + (2 / 3))
    x = fun();                          // valid (function calls can be assigned)
    x = 'x' + 'abc';                    // valid (will result in string concatenation)
    x = 123 + true;                     // valid (no type safety in parser)
    x = (1 + 2);                        // parser fails, as brackets are not allowed


#### Conditional expressions
<ul>
<li>Conditional expressions are quite similar to arithmetic expressions, except they must be enclosed in (round) brackets.</li>
<li>Conditional expressions use comparing (<, <=, ==, ...) or evaluating (&&, ||) operators.</li>
</ul>

    // examples
    x = (true || false);                // valid
    x = (1 < 2);                        // valid
    x = true && true;                   // parser fails, as brackets are missing
    x = (true && true && false);        // parser fails, as round brackets must always enclose two components

#### Function calls

#### Print calls

#### Function definitions

#### Conditional statements

#### While loops

#### Nested statements

#### Program


<b>Conditional Statements</b>: Conditional statements must have two operands and must be nested in brackets. It is 
possible to nest a conditional statement in another conditional statement.

<b>If-Then Statements</b>: Curly brackets are mandatory around code blocks. 

<b>While Statements</b>: Curly brackets are mandatory around code blocks. 
### Semantic rules



## Repository handling

### Clone
Clone the repository with following command.

    git clone https://github.com/lpapailiou/fac <target path>

The code is written in ``java 8``.
Once the repository is cloned, ``maven`` may take care about the build, plugins and the dependencies.  

### Package structure
Below, the structure of the package tree is listed for better overview.

    + src
        + main
            + java
                + interpreter               // code execution handling
                + main                      // samples (ready for execution)
                + parser                    // syntactical analysis & semantic logic
                    + exceptions 
                    + parsetree                 // parse tree components (syntax)
                        + interfaces
                    + validation                // semantic validation
                + scanner                   // lexical analysis & token generation
            + resources                     // code sample files
                + lib                       // external dependecies (jflex & cup)

### Run
#### Samples
To execute specific components (scanner, parser, execution), there are prepared samples ready in
the directory ``src\main\java\main``.

#### Scanner generation
To generate a new scanner from the flex file, run following command (java path must be set).

    java -jar src/main/resources/lib/jflex-full-1.8.2.jar src/main/java/scanner/jscanner.flex

#### Parser generation
To generate a new parser from the cup file, run following command (java path must be set).

    java -jar src/main/resources/lib/java-cup-11b.jar -interface -destdir src/main/java/parser/ -symbols JSymbol -parser JParser src/main/java/parser/jparser.cup
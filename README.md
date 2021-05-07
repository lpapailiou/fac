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
In this section, the language design will be documented with its rules and a few examples. 
### Scope
Our new toy language has roughly following scope:  
<ul>
<li>variable declarations</li>
<li>string concatenation</li>
<li>arithmetic expressions</li>
<li>conditional expressions</li>
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
Java-like comments are allowed. They will be ignored in further processing of the code.
Pattern: ``"/*" [^*] ~"*/" | "/*" "*"+ "/" | "//" [^\r\n]* \r|\n|\r\n? | "/**" ( [^*] | \*+ [^/*] )* "*"+ "/"``  
#### Whitespace
Whitespace may consist of spaces and newlines. It will be ignored in further processing steps, but
is initially useful to separate tokens from each other.  
Pattern: ``[ \t\f\r\n]+`` 
#### Reserved words
Reserved words are: ``string``, ``number``, ``boolean`` (for data types), ``while``, ``break``,
``if``, ``else``, ``def``, ``return`` (for statements) and ``print`` (for printing to the console).  
#### Boolean values
Boolean values may be either ``true`` or ``false``.  
#### Numeric values
Numeric values will be interpreted as Double. The length will not be checked.  
Pattern: ``-?[0-9]\d*(\.\d+)?``  
#### String values
Strings may contain lowercase letters, digits and a few special characters. They must be enclosed
in apostrophes.  
Pattern: ``'[a-z0-9_\,\.\(\)\;\:\/\+\-\*\/ \s\t\f\r\n]*'``  
#### Variable identifiers
Variable identifiers may consist of lowercase letters, underscores and optional digits at the end.  
Pattern: ``[a-z_]+([0-9])*``. 
#### Operators
Additionally, there is a set of basic operators.  
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
<li>The data type is followed by an identifier and an equal character.</li>
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
    number y7 = (true && false);        // valid (conditional expressions can be assigned)
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
<li>By default, their components can be either 'raw' values, conditional expressions or function calls.</li>
<li>Multiplication and division have precedence over addition and subtraction.</li>
<li>Arithmetic expressions can be nested. They are - after precedence - evaluated from left to right.</li>
<li>Arithmetic expressions must not have brackets if they are not conditional statements.</li>
<li>As arithmetic expressions (for simplicity are abused to) group all possible values and expressions, they can be potentially assigned anywhere.</li>
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
<ul>
<li>Function calls consist of an identifier, followed by an opening round bracket, parameters, a closing bracket and a semicolon.</li>
<li>Parameters can be zero, one or multiple (comma separated) expressions.</li>
<li>Without the semicolon, a function call can be used as value within an expression, as it is expected to always have a return value.</li>
</ul>

    // examples
    fun1();                             // valid (no parameter)
    //fun()                             // parser fails, as semicolon is missing
    fun2(1 + 2 + 4);                    // valid (expression as one parameter)    
    fun3(true && false);                // valid (condition as parameter - in this case, one set of brackets is enough)
    fun3((true && false), 1, 'abc');    // valid (multiple parameters)
    fun(fun());                         // valid (but probably not nice)

#### Print calls
<ul>
<li>Print calls work the same way as function calls, except:
<ul><li>they cannot have multiple parameters.</li>
<li>they do not have any return value. Therefore, they cannot be assigned to variables or be used in expressions.</li></ul></li>
</ul>

    // examples
    print();                            // valid (no parameter)
    print(1 + 2 + 4);                   // valid (expression as one parameter)    
    print(1 + 2 + 4, 'x');              // parser fails, as multiple parameters are not allowed
    print(fun());                       // valid
    x = print();                        // parser fails, as print calls cannot be used in assignments

#### Function definitions
<ul>
<li>Function definitions must start with the def keyword, a data type and an identifier.</li>
<li>After the identifier, a parameter declaration list (which must be enclosed in round brackets) follows.<ul>
<li>This declaration list can be empty or consist of one or multiple declarations.</li>
<li>A parameter declaration consists of a data type and an identifier.</li></ul></li>
<li>After the parameter declarations, curly brackets open and close.<ul>
<li>The function body may contain zero, one or multiple nestable statements.</li>
<li>Just before the curly brackets are closed, a return statement must be placed. <ul><li>The return statement consists 
of the return keyword, a return value (an expression) and a semicolon.</li></ul></li></ul></li>
</ul>

    // examples
    def number fun() { return 1; }      // valid (minimal example)
    boolean fun() { return x; }         // parser fails, as def keyword is missing
    def fun() { return 1 + 1; }         // parser fails, as data type is missing
    def string fun() { }                // parser fails, as return keyword is missing
    def boolean f1(string x) {          // valid
        print(x);
        return x;
    }

#### Conditional statements
<ul>
<li>Conditional statements must start with the if keyword and a conditional expression (in round brackets).</li>
<li>Then, a body in curly brackets follows, which can contain zero, one or more statements.</li>
<li>Optionally, an else keyword may follow, with another body as above.</li>
</ul>

    // examples
    if (true) {}                        // valid
    if (1 < 2) {};                      // parser fails, as no semicolon is allowed at end
    if (false) {                        // valid
        print('x');
    } else {}

#### While loops
<ul>
<li>While loops must start with the while keyword and a conditional expression (in round brackets).</li>
<li>Then, a body in curly braces follows, which can contain zero, one or more statements.</li>
</ul>

    // examples
    while (true) {}                     // valid (but maybe not smart)
    while false {}                      // parser fails, as conditional brackets are missing
    while (false) {                     // parser fails, as body is not closed
    while (false) {                     // valid
        print('x');
    }

#### Statements
<ul>
<li>Statements are basically all grammatical structures, which do not require another surrounding construct to be valid.
They always must end with a semicolon (if they do not end with a body in curly brackets). Implemented statements are:<ul>
<li>variable declarations</li>
<li>variable assignments</li>
<li>function calls (including print calls)</li>
<li>function definitions</li>
<li>conditional statements</li>
<li>while loops</li></ul></li>
<li>Additionally, break statements do also count as statements. They are meant to be used within while loop bodies.
As they cannot be strictly at the end of a while body (they could be nested deeper within if-then-structures), the parser
will not perform any further syntactical validations in this case.</li>
<li>Nested statements are a list of statements. This is a helper structure to fill bodies of conditional statements, while loops
 and function definitions.</li>
<li>Function definitions must be 'top level' statements (e.g. it is not possible to nest them in a while loop).</li>
<li>All 'top level' statements and function definitions will finally be added to the program statement list.</li>
</ul>

#### Program
<ul>
<li>The program is a container for the 'top level' list of program statements.</li>
<li>If the parser finishes without error, the program will be the root of the generated parse tree.</li>
</ul>

### Semantic rules
So far, our toy language is defined and we have the tools to validate if a code belongs to our language or not. But at 
this point, there is no type safety, variables can be assigned before they are declared and break statements
are a mere decoration.  
  
The required semantic validation must now be performed by a ``Interpreter`` (see ``src\main\java\parser\interpreter``).  
The interpreter will receive the parse tree from the parser and traverse it depth-first.

#### Identifier scope
<ul>
<li>A variable must be declared before it can be referenced.</li>
<li>The same rule applies to function definitions and function calls.</li>
<li>Identifiers are valid within the same and lower levels of the parse tree, starting from the location where they are declared.</li>
<li>Identifiers must be unique within their scope, except for functions, which can be overloaded.</li>
</ul>

    // examples
    number x = 1;                       // valid
    y = x;                              // interpreter fails as y was not instantiated
    def string fun(string z) {          // valid
        return z;
    }
    x = z;                              // interpreter fails, as z is out of scope
 
#### Type safety
<ul>
<li>A variable can only assign values of the declared type.</li>
<li>A function must return the same type as it defines as return value.</li>
<li>If expressions are nested, the types are evaluated for every segment (assignment has least precedence).</li>
<li>If a segment of an expression is a string, the resulting type will be cast to a string, if possible.</li>
<li>String casting will not work within subtractions, multiplications, divisions and conditional expressions.</li>
</ul>

    // examples
    string x = 1 + 2 + 'a';             // valid (nesting & string casting)
    number y = true;                    // interpreter fails, as boolean is assigned to number type
    x += true;                          // valid (string casting)
    x = true;                           // interpreter fails, string casting can only occur in binary expression
    x = 1 * 2;                          // interpreter fails, string casting can only occur in binary expression
    def number fun() {                  // interpreter fails, as returned string is not a number
        return 'x'; }

#### Operator validation
<ul>
<li>In assignments, operator = can be used for all data types, += for numeric and string values, other operators (-=, *=, /=) for numeric values only.</li>
<li>In conditional expressions, == and != may be used for all types, && and || may used for boolean values only, comparing operators (<, <=, >=, >) can be used for numeric values only.</li>
<li>In arithmetic expressions, + is valid for numeric values or if at least one of the components is a string. All other available operators (-,*, /) are for numeric values only.</li>
</ul>

    // examples
    string x = 'x' + 'b';               // valid
    x = 'x' - 'b';                      // interpreter fails, as minus cannot be used for string concatenation
    boolean y = (true && 1);            // interpreter fails, as the operator && can be used for booleans only
    boolean z = (1 < 2);                // valid
    x = 1 * 'x';                        // interpreter fails, multiplication operator is not valid for strings


#### Expressions
<ul>
<li>In general, both operands of a binary expression must have the same type.</li>
<li>The only exception is string casting.</li>
<li>The resulting value of a conditional expression must always be a boolean, arithmetic expressions can evaluate to strings or numeric values.</li>
</ul>

#### Function calls and function definitions
<ul>
<li>The declared return type must match the effective return type.</li>
<li>A function is identified by its identifier, return type, parameter count and parameter types. This means, overloading is possible.</li>
<li>Overloading may occur only, if the identifier matches, the return type matches and parameter count differs.</li>
<li>If a caller calls a function, the callee must have according parameter count, parameter types and return type.</li>
<li>A function can call itself, thus, recursion is allowed.</li>
</ul>

    // examples
        def number x() {                // interpreter fails, as 'seven' is a string
            return 'seven'; }
        def number y() {                // valid
            return 0; }
        def number z() {                // interpreter fails, as function y() is already defined with 0 params and same return type
            return 1; }

#### Conditional statements and while loops
<ul>
<li>Apart from the break statement validation, no additional semantic validation is required in this case, as the syntactical checks are sufficient.</li>
</ul>

#### Break statements
<ul>
<li>Per while loop and nesting level, one break statement is allowed.</li>
<li>Exception: if a break loop contains an if-then-else statement, two breaks are allowed.</li>
<li>A break must be the last statement of a statement list, but can be nested within other statement lists. Thus, a simple validation for
unreachable code occurs at this place.</li>
<li>Break statements are only allowed within while loops, and never as top-level-statement.</li>
</ul>

    // examples
    break;                              // interpreter fails, as break statement is dangling outside loop
    while(true) {                       // interpreter fails, as there is unreachable code
        break; number x = 1; }
    while(false) {                      // valid
        break; }        

### Execution
Please note that global variables and function definitions must be declared always before being referenced. Thus, declarations must be 
placed always before callers (not like Java, where global variables and functions may be defined anywhere in a file).

## Repository handling
This section contains a few technical notes about this repository.
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
                + execution               // code execution handling
                + main                      // samples (ready for execution)
                + parser                    // syntactical analysis & semantic logic
                    + exceptions
                    + interpreter               // semantic validation                     
                    + parsetree                 // parse tree components (syntax)
                        + interfaces
                + scanner                   // lexical analysis & token generation
            + resources                     // code sample files
                + lib                       // external dependecies (jflex & cup)

### Run
#### main.Compiler
The main class ``Compiler`` may be run with or without options. If there are no options given, it will
initialize the [interactive console mode](#console-mode).  
There's also a small menu available.

    // output of help menu (accessed with -h)
    Following options are available:
        -o scan
        -o parse
        -o interpret
        -o execute
    
    Optionally you may enter a file path after the option.

If a file path succeeds the option, this specific file will be processed. Otherwise, a sample file will be run.  
Several sample files are available in the directory ``src\main\resources``.

##### Scan mode
The Scanner will take a file and tokenize its content. The output is verbose, every processed token will be
printed accordingly to the console.

    // sample output
    scanning token {NUMTYPE}: found match <number> at line 1, column 0.
    scanning token {VAR}: found match <one> at line 1, column 7.
    scanning token {EQUAL}: found match <=> at line 1, column 11.
    scanning token {NUM}: found match <1> at line 1, column 13.
    scanning token {STOP}: found match <;> at line 1, column 14.
    ...end of file reached at line 1, column 1.

##### Parse mode
The parser will initialize a scanner. During processing, the parser will take token by token and validate if
the sequence follows the syntactical definition of the grammar. If no error occurs, the parser will generate
a parse tree. This means, that the syntax of the scanned code is valid.  
Additionally, it will print the parsed code to the console.

    // sample output
    ***** PARSER RESULT *****
    
    number one = 1;
    
    while (one < 5) {
        one += 1;
        print('hello world');
    }
    
##### Interpreter mode
This mode will run a parser as above.
As soon as the parse tree is ready, the interpreter will traverse the tree and validate semantic rules.  

    // sample output
    ***** PARSER RESULT *****
    
    number one = 1;
    
    while (one < 5) {
        one += 1;
        print('hello world');
    }

##### Execution mode
The executor is built on an interpreter. The process is similar to the interpreter mode, but the executor
will similarly validate and execute the interpreted code.

    // sample output
    ***** EXECUTION RESULT *****
    
    >>>>  
    hello world
    >>>>  
    hello world
    >>>>  
    hello world
    >>>>  
    hello world

##### Console mode
The console mode starts an executor in the console. This version is interactive. Every line of code 
will be scanned, parsed, interpreted and directly be executed. If no error occurred, the next line can be 
entered. This also means, that already executed code will execute again from start.    
In case of an error, the last entry will be ignored. A new line can be added to the so-far valid code.  
The console mode can be escaped with -h or -q.

    // sample output
    (press -h for help or -q to quit)
    **************************************************************
    *                      WELCOME TO JLANG                      *
    **************************************************************
    >  ... initialized & ready to code!                                 // from here on, user input is accepted
    >  number one = 1;
    >  
    >  while (one < 2) {one += 1; print('hello world'); }
    >  
    >>>>  
    hello world
    >  

#### Scanner generation
To generate a new scanner from the flex file, run following command (java path must be set).

    java -jar src/main/resources/lib/jflex-full-1.8.2.jar src/main/java/scanner/jscanner.flex

#### Parser generation
To generate a new parser from the cup file, run following command (java path must be set).

    java -jar src/main/resources/lib/java-cup-11b.jar -interface -destdir src/main/java/parser/ -symbols JSymbol -parser JParser src/main/java/parser/jparser.cup
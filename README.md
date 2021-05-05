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
Java-like comments are allowed. They will be ignored in further processing of the code.  
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
    ASSIGN 	        ::= VAR:e1 EQUAL:op EXPR:e2 STOP                {: RESULT = Statement.assgn(op, e1, e2); :}
                        | VAR:e1 ASSIGN_OP:op EXPR:e2 STOP          {: RESULT = Statement.assgn(op, e1, e2); :}
                        ;
    
    ASSIGN_OP       ::= PLUSEQ:op                                   {: RESULT = op; :}
                        | MINEQ:op                                  {: RESULT = op; :}
                        | DIVEQ:op                                  {: RESULT = op; :}
                        | MULEQ:op                                  {: RESULT = op; :}
                        ;

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
                + parser                    // syntactical analysis & grammatical logic
                    + exceptions 
                    + parsetree                 // parse tree components
                        + interfaces
                    + validation                // logical validation
                " scanner                   // lexical analysis & token generation
            + resources                     // code sample files

### Run
#### Samples
To execute specific components (scanner, parser, execution), there are prepared samples ready in
the directory ``src\main\java\main``.

#### Scanner generation
To generate a new scanner from the flex file, run following command (jflex path must be set).

    jflex src/main/java/scanner/jscanner.flex

#### Parser generation
To generate a new parser from the cup file, run following command (java path must be set).

    java -jar java-cup-11b.jar -interface -destdir src/main/java/parser/ -symbols JSymbol -parser JParser src/main/java/parser/jparser.cup
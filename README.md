# FAC

## Goal
The defined language should meet following goals:  
<ul>
<li>arithmetic expressions</li>
<li>variable declarations</li>
<li>conditional statements</li>
<li>loops</li>
<li>function calls</li>
<li>function definitions</li>
</ul>

## Repository content
<ul>
<li>The package ``scanner`` contains the tokenization of the programming language to-be-defined.</li>  
<li>The package ``parser`` will apply grammar rules to the scanned tokens. As the parser is context free, it
will require an additional validator for quality checks.</li>
</ul>

## Language rules
In the following subsections, we will explain which rules are enforced at which step.
### Scanner

<b>Comments</b>: Java-like comments are allowed. They will be ignored in further steps.  
  
<b>Whitespace</b>: Whitespace may consist of spaces and newlines. They will be ignored in further steps, but
are initially required to separate tokens from each other.   
  
<b>Reserved words</b>: Reserved words are: ``string``, ``number``, ``boolean`` (for data types), ``while``, ``break``,
``if``, ``else``, ``def``, ``return`` and ``print`` (for printing to the console).  
   
<b>Boolean values</b>: Boolean values may be either ``true`` or ``false``.  
  
<b>Numeric values</b>: Numeric values will be interpreted as Double. The length will not be 
checked. Pattern: ``-?[0-9]\d*(\.\d+)?``  
    
<b>String values</b>: Strings may contain lowercase letters, digits and a few special characters. The must be enclosed
in apostropes. Pattern: ``'[a-z0-9_\,\.\(\)\;\:\/\+\-\*\/ \s\t\f\r\n]*'``  

<b>Variables</b>: Variable may consist of lowercase letters and optional digits at the end. Pattern: ``[a-z_]+([0-9])*``. 
 
<b>Operators</b>: Additionally, a few basic arithmetic and conditional operators will be available. 

### Parser

#### Grammar rules
<b>Conditional Statements</b>: Conditional statements must have two operands and must be nested in brackets. It is 
possible to nest a conditional statement in another conditional statement.

<b>If-Then Statements</b>: Curly brackets are mandatory around code blocks. 

<b>While Statements</b>: Curly brackets are mandatory around code blocks. 
#### Validator
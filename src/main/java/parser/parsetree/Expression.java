package parser.parsetree;

/**
 * This is the superclass for binary and unary expressions.
 * It is meant to be used for arithmetic expressions, but is also used as container for
 * all types of values expressions and function calls.
 */
abstract class Expression extends Component {

    /**
     * This constructor is used to pass the location of the code fragment.
     *
     * @param left  the start index.
     * @param right the end index.
     */
    Expression(int left, int right) {
        super(left, right);
    }
}

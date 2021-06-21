package parser.parsetree;

/**
 * This is the superclass for binary and unary conditions.
 * It must evaluate to boolean values always.
 */
public abstract class ConditionalExpression extends Component {

    /**
     * This constructor is used to pass the location of the code fragment.
     *
     * @param left  the start index.
     * @param right the end index.
     */
    ConditionalExpression(int left, int right) {
        super(left, right);
    }
}

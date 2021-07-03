package parser.parsetree.interfaces;

/**
 * This interface is used as marker interface to identify conditional expressions (which can be of unary or binary nature).
 */
public interface ConditionalExpression {

    /**
     * This method allows to access the location of the component which implements this interface.
     *
     * @return returns the location of the conditional expression in the source file.
     */
    int[] getLocation();

}

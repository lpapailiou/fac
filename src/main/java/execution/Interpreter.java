package execution;

import parser.parsetree.*;
import parser.parsetree.interfaces.Declaration;
import parser.parsetree.interfaces.Traversable;
import validator.Validator;

import java.util.ArrayList;
import java.util.List;

/**
 * The purpose of this class is to execute parsed code.
 * It is implemented as visitor and walks the parse tree depth-first, while a node will be evaluated after
 * its children.
 * A semantic code validation will take place during
 * execution in parallel, as the Interpreter builds on the Validator. Like this, it is not possible
 * to execute not validated code.
 * Example for usage (where the rootSymbol is the resulting symbol of a parse process):
 * <code>Interpreter interpreter = new Interpreter();
 * ((Program) rootSymbol.value).accept(interpreter);</code>
 */
public class Interpreter extends Validator {

    private final List<String> output = new ArrayList<>();
    private boolean execute = true;         // to be switched off if validation only is required (e.g. for dead if-then-else branches)
    private boolean scriptMode = false;     // allows to change behavior for script mode vs. all-at-once-execution
    private boolean printActive = false;    // indicator to switch off execution for print statements (used in script mode)
    private int breakEvent = 0;             // counts the break statements found to be executed

    /**
     * This method will trigger the validation for the assignment statement first.
     * Then, if the code is executable, it will assign the new value to the variable.
     *
     * @param acceptor the AssignmentStatement to visit.
     */
    @Override
    public void visit(AssignmentStatement acceptor) {
        super.visit(acceptor);
        if (execute) {
            Declaration declaration = getDeclaration(acceptor, acceptor.getIdentifier());
            Object value1 = declaration.getValue();
            Object value2 = getValueOfOperand(acceptor, acceptor.getValue());
            declaration.setValue(acceptor.getOperator().apply(value1, value2));
        }
    }

    /**
     * This method visits a generic component (i.e. a component which has not an own specific visit method).
     * This is a fall-through action.
     *
     * @param acceptor the generic Component to visit.
     */
    @Override
    public void visit(Component acceptor) {
        super.visit(acceptor);
    }

    /**
     * This method will trigger the validation for the function call statement first.
     * Then, if the code is executable, it will evaluate the value of the function call. The value is not used any
     * further at this place, but in case of runtime exceptions, execution will stop accordingly.
     * This is important, as function calls can be independent statements.
     *
     * @param acceptor the FunctionCallStatement to visit.
     */
    @Override
    public void visit(FunctionCallStatement acceptor) {
        super.visit(acceptor);
        if (execute && acceptor.isStatement()) {
            getValueOfOperand(acceptor, acceptor);
        }
    }

    /**
     * This method triggers the validation for the function definition statement.
     *
     * @param acceptor the FunctionDefStatement to visit.
     */
    @Override
    public void visit(FunctionDefStatement acceptor) {
        super.visit(acceptor);
    }

    /**
     * This method triggers the validation for the if-then-else statement.
     *
     * @param acceptor the IfThenElseStatement to visit.
     */
    @Override
    public void visit(IfThenElseStatement acceptor) {
        super.visit(acceptor);
    }

    /**
     * This method triggers the execution for the if-then statement.
     *
     * @param acceptor the IfThenStatement to visit.
     */
    @Override
    public void visit(IfThenStatement acceptor) {
        super.visit(acceptor);
    }

    /**
     * This method triggers the validation for this parameter declaration.
     *
     * @param acceptor the ParamDeclaration to visit.
     */
    @Override
    public void visit(ParamDeclaration acceptor) {
        super.visit(acceptor);
    }

    /**
     * This method triggers the validation for this print call statement.
     * Then, depending on the current mode, the statement is executed, i.e. its resulting value is printed to the console.
     *
     * @param acceptor the PrintCallStatement to visit.
     */
    @Override
    public void visit(PrintCallStatement acceptor) {
        super.visit(acceptor);
        if (execute && (!scriptMode || printActive)) {
            Component component = (Component) acceptor.getValue();
            if (component != null) {
                String value = getValueOfOperand(acceptor, component).toString();
                if (getTypeOfOperand(acceptor, value) == Type.NUMERIC) {
                    double doubleValue = Double.parseDouble(value);
                    if (doubleValue == Math.floor(doubleValue)) {
                        value = ((int) doubleValue) + "";
                    }
                }
                String out = value.replaceAll("'", "");
                output.add(out);
                System.out.println(">>>>  " + out);
            } else {
                output.add("");
                System.out.println(">>>>  ");
            }
        }
    }

    /**
     * This method triggers the top-level validation for break statements. Then it will start the depth-first traversal
     * of the parse tree for validation and execution.
     *
     * @param acceptor the Program to visit.
     */
    @Override
    public void visit(Program acceptor) {
        checkBreakStatement(acceptor, acceptor.getStatements(), false);
        traverse(acceptor);
    }

    {
    }

    /**
     * This method triggers the validation of this variable declaration. If the code is executable, the
     * declaration is set to it's original value (in case the code was executed already) and then set the
     * according value.
     *
     * @param acceptor the VariableDeclaration to visit.
     */
    @Override
    public void visit(VariableDeclaration acceptor) {
        super.visit(acceptor);
        if (execute) {
            acceptor.reset();
            Object value = getValueOfOperand(acceptor, acceptor.getValue());
            acceptor.setValue(value);
        }
    }

    /**
     * This method will trigger the validation for this while statement.
     *
     * @param acceptor the WhileStatement to visit.
     */
    @Override
    public void visit(WhileStatement acceptor) {
        super.visit(acceptor);
    }

    /**
     * This method will perform thee depth-first traversal of the parse tree recursively.
     * It traverses first the nested components of a component, then the component itself.
     * As some of the program components are complex structures (i.e. execution of child components
     * is depending on conditions of the parent component), the processing is controlled within
     * the processStatements method.
     * If necessary, pre-validations are done for specific nodes.
     *
     * @param node the parse tree node to traverse
     */
    private void traverse(Traversable node) {
        if (node != null) {
            preValidate(node);

            List<Component> components = getStatements(node);   // select executable child components
            processStatements(node, components);                // process child components accordingly

            if (!(node instanceof Program)) {   // the program node starts the traversal and does not have to be visited again
                node.accept(this);
            }
        }
    }

    /**
     * This method allows to set the interpreter to 'script mode'. This means, that the code execution will be handled
     * a little differently: only the last print statement of the code is executed, even if the code runs multiple times (after entering a new line in the console, the full
     * code has to be validated and re-run again completely. Executing just the last print call give a more 'natural' flow to the application).
     *
     * @param scriptMode indicates if script mode should be turned on or off.
     */
    void setScriptMode(boolean scriptMode) {
        this.scriptMode = scriptMode;
    }

    // ------------------------------------------ helper methods ------------------------------------------

    /**
     * This methods takes care about the pre-validations before the content of a node and then the node itself are visited.
     * Entering a while loop will increase the whileDepth counter (e.g. one nested break statement is allowed). Entering
     * a function definition will add its declaration to scope. Entering a break statement in executable context will
     * increase the breakEvent threshold by one (which means one more break statement may be executed).
     *
     * @param node the parse tree node to pre-validate.
     */
    @Override
    protected void preValidate(Traversable node) {
        super.preValidate(node);
        if (node instanceof BreakStatement && execute) {
            breakEvent++;
        }
    }

    /**
     * This method will select the executable child component for the passed parent component.
     * If the code is executable, it will be processed as foreseen. If not (e.g. in a dead if-else-then branch), this method
     * takes care that the code will still be validated semantically.
     *
     * @param node the parse tree node to get child components from.
     * @return the selected executable child components of the passed parent node.
     */
    private List<Component> getStatements(Traversable node) {
        boolean switchExecutionOn = execute;        // indicates if execution must be switched on again if turned off temporarily
        List<Component> executableComponents = node.getStatements();
        if (!execute) {
            return executableComponents;            // if we are in a non-executable block, all child components are validated, but not executed
        }
        List<Component> validateOnlyComponents = new ArrayList<>();
        boolean condition;
        if (node instanceof IfThenElseStatement) {
            condition = (Boolean) getValueOfOperand(node, ((IfThenElseStatement) node).getCondition());
            if (condition) {
                executableComponents = ((IfThenElseStatement) node).getIfStatements();
                validateOnlyComponents = ((IfThenElseStatement) node).getElseStatements();
            } else {
                executableComponents = ((IfThenElseStatement) node).getElseStatements();
                validateOnlyComponents = ((IfThenElseStatement) node).getIfStatements();
            }
        } else if (node instanceof IfThenStatement) {
            condition = (Boolean) getValueOfOperand(node, ((IfThenStatement) node).getCondition());
            if (!condition) {
                validateOnlyComponents = executableComponents;
                executableComponents = new ArrayList<>();
            }
        } else if (node instanceof FunctionDefStatement) {
            validateOnlyComponents = executableComponents;
            executableComponents = new ArrayList<>();           // function definitions are executed only by callers, not when parsed
        }

        execute = false;
        for (Component st : validateOnlyComponents) {
            traverse(st);                                       // validation only for not executable code
        }

        if (node instanceof IfThenElseStatement) {
            closeCurrentScope();
            openNewScope();
        }

        if (switchExecutionOn) {
            execute = true;                                     // switch execution back on
        }
        return executableComponents;                            // return executable child components
    }

    /**
     * This method contains the top-level logic to traverse executable statements. The runs of the while loop can be repeated
     * within this method. Furthermore, the scriptMode indicator can change the behavior of print call executions here.
     *
     * @param node       the parse tree component as parent node.
     * @param components the child components of the parent node.
     */
    private void processStatements(Traversable node, List<Component> components) {
        if (node instanceof WhileStatement && execute) {
            int counter = 0;
            while ((Boolean) getValueOfOperand(node, ((WhileStatement) node).getCondition())) {   // while loop execution
                counter++;
                if (counter > 10000) {
                    throw new StackOverflowError();
                }
                openNewScope();
                if (breakEvent > 0) {                       // break statement execution
                    breakEvent--;
                    break;
                }
                for (Component st : components) {
                    traverse(st);                           // execution of child components
                }
                closeCurrentScope();                        // remove local variables from scope
            }
        } else {
            if (node instanceof Program) {
                for (int i = 0; i < components.size(); i++) {
                    if (i == components.size() - 1) {
                        printActive = true;                 // marks last print call statement as executable indirectly
                    }
                    traverse(components.get(i));            // execution of child components
                }
            } else {
                for (Component st : components) {
                    traverse(st);                           // execution of child components
                }
            }
        }
    }

    /**
     * This method will evaluate the value of an object, which can be any type of program component which results in a value (e.g. a 'raw value', an expression
     * or the result of a function call).
     * If the object to evaluate is more complex, this method will follow the component chain until the final result is found.
     *
     * @param parent  the parent parse tree component.
     * @param operand the operand of which the value should be evaluated.
     * @return the value of the operand.
     */
    private Object getValueOfOperand(Traversable parent, Object operand) {
        Object value;
        if (operand instanceof FunctionCallStatement) {
            value = getValue((FunctionCallStatement) operand);
        } else if (operand instanceof BinaryExpression) {
            value = getValue((BinaryExpression) operand);
        } else if (operand instanceof UnaryExpression) {
            value = getValue((UnaryExpression) operand);
        } else if (operand instanceof ValueWrapper) {
            value = getValue((ValueWrapper) operand);
        } else if (operand instanceof BinaryCondition) {
            value = getValue((BinaryCondition) operand);
        } else if (operand instanceof UnaryCondition) {
            value = getValue((UnaryCondition) operand);
        } else {
            if (Type.getByInput(operand) == Type.VARIABLE) {
                value = getDeclaration(parent, operand.toString()).getValue();
            } else {
                value = operand;
            }
        }
        return value;
    }

    /**
     * This method will evaluate the value of a function call.
     * First it will look up the according function definition, pass the according arguments and then
     * execute the function. Finally, the value of the return statement will be returned.
     *
     * @param operand the operand to evaluate.
     * @return the value of the operand.
     */
    private Object getValue(FunctionCallStatement operand) {
        openNewScope();
        FunctionDefStatement function = getFunction(operand, operand.getIdentifier(), operand.getArgumentCount());
        List<Component> callParams = operand.getArgumentList();
        List<Component> components = function.getStatements();
        for (int i = 0; i < components.size(); i++) {
            Component stmt = components.get(i);
            if (stmt instanceof ParamDeclaration) {
                ParamDeclaration paramDeclaration = ((ParamDeclaration) stmt);
                addDeclarationToScope(paramDeclaration);
                paramDeclaration.setValue(getValueOfOperand(operand, callParams.get(i)));
            } else {
                traverse(stmt);
            }
        }
        Object value = getValueOfOperand(operand, function.getReturnStatement());
        closeCurrentScope();
        return value;
    }

    /**
     * This method will evaluate the value of a binary arithmetic expression.
     *
     * @param operand the operand to evaluate.
     * @return the value of the operand.
     */
    private Object getValue(BinaryExpression operand) {
        Object value1 = getValueOfOperand(operand, operand.getOperand1());
        Object value2 = getValueOfOperand(operand, operand.getOperand2());
        return operand.getOperator().apply(value1, value2);
    }

    /**
     * This method will evaluate the value of a unary arithmetic expression.
     *
     * @param operand the operand to evaluate.
     * @return the value of the operand.
     */
    private Object getValue(UnaryExpression operand) {
        Object value = getValueOfOperand(operand, operand.getOperand());
        return operand.getOperator().apply(value);
    }

    /**
     * This method will evaluate the value of a constant.
     *
     * @param operand the operand to evaluate.
     * @return the value of the operand.
     */
    private Object getValue(ValueWrapper operand) {
        Type type = getTypeOfOperand(operand, operand.getValue());
        Object value = getValueOfOperand(operand, operand.getValue());
        if (type == Type.BOOLEAN) {
            return Boolean.parseBoolean(value.toString());
        }
        return value;
    }

    /**
     * This method will evaluate the value of a binary conditional expression.
     *
     * @param operand the operand to evaluate.
     * @return the value of the operand.
     */
    private Object getValue(BinaryCondition operand) {
        Object value1 = getValueOfOperand(operand, operand.getOperand1());
        Object value2 = getValueOfOperand(operand, operand.getOperand2());
        return operand.getOperator().apply(value1, value2);
    }

    /**
     * This method will evaluate the value of a unary conditional expression.
     *
     * @param operand the operand to evaluate.
     * @return the value of the operand.
     */
    private Object getValue(UnaryCondition operand) {
        Object value = getValueOfOperand(operand, operand.getOperand());
        return operand.getOperator().apply(value);
    }

    public List<String> getOutput() {
        return output;
    }

}

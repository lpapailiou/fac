package execution;

import exceptions.UniquenessViolationException;
import interpreter.Interpreter;
import parser.parsetree.*;
import parser.parsetree.interfaces.Declaration;
import parser.parsetree.interfaces.Traversable;

import java.util.ArrayList;
import java.util.List;

/**
 * The purpose of this class is to execute parsed code.
 * It is implemented as visitor and walks the parse tree depth-first, while a node will be evaluated after
 * its children.
 * A semantic code validation will take place during
 * execution in parallel, as the Executor builds on the Interpreter. Like this, it is not possible
 * to execute not validated code.
 * Example for usage (where the rootSymbol is the resulting symbol of a parse process):
 * <code>Executor executor = new Executor();
 * ((Program) rootSymbol.value).accept(executor);</code>
 */
public class Executor extends Interpreter {

    private boolean execute = true;         // to be switched off if validation only is required
    private boolean scriptMode = false;     // will prevent print statements from being executed multiple times if not placed in functions
    private boolean printActive = false;
    private int breakEvent = 0;
    private int breakOccurred = 0;

    @Override
    public void visit(Program acceptor) {
        checkBreakStatement(acceptor, acceptor.getStatements(), false);
        if (execute) {
            traverse(acceptor);
        }
    }

    @Override
    public void visit(Component acceptor) {
        super.visit(acceptor);
    }

    @Override
    public void visit(VariableDeclaration acceptor) {
        super.visit(acceptor);
        if (execute) {
            acceptor.reset();
            Object value = getValueOfOperand(acceptor.getValue());
            acceptor.setValue(value);
        }
    }

    @Override
    public void visit(ParamDeclaration acceptor) {
        super.visit(acceptor);
    }

    @Override
    public void visit(AssignmentStatement acceptor) {
        super.visit(acceptor);
        if (execute) {
            Declaration declaration = getDeclaration(acceptor.getIdentifier());
            Object value1 = declaration.getValue();
            Object value2 = getValueOfOperand(acceptor.getValue());
            declaration.setValue(acceptor.getOperator().apply(value1, value2));
        }
    }

    @Override
    public void visit(FunctionCallStatement acceptor) {
        super.visit(acceptor);
        if (execute) {
            getValueOfOperand(acceptor);
        }
    }

    @Override
    public void visit(PrintCallStatement acceptor) {
        super.visit(acceptor);
        if (execute && (!scriptMode || printActive)) {
            Component component = (Component) acceptor.getValue();
            if (component != null) {
                System.out.println(">>>>  " + getValueOfOperand(component).toString().replaceAll("'", ""));
            } else {
                System.out.println(">>>>  ");
            }
        }
    }

    @Override
    public void visit(FunctionDefStatement acceptor) {
        super.visit(acceptor);
    }

    @Override
    public void visit(IfThenStatement acceptor) {
        super.visit(acceptor);
    }

    @Override
    public void visit(IfThenElseStatement acceptor) {
        super.visit(acceptor);
    }

    @Override
    public void visit(WhileStatement acceptor) {
        super.visit(acceptor);
    }

    private Object getValue(BinaryExpression statement) {
        Object value1 = getValueOfOperand(statement.getOperand1());
        Object value2 = getValueOfOperand(statement.getOperand2());
        return statement.getOperator().apply(value1, value2);
    }

    private Object getValue(UnaryExpression statement) {
        Object value = getValueOfOperand(statement.getOperand());
        return statement.getOperator().apply(value);
    }

    private Object getValue(Constant statement) {
        Type type = getTypeOfOperand(statement.getValue());
        Object value = getValueOfOperand(statement.getValue());
        if (type == Type.BOOLEAN) {
            return Boolean.parseBoolean(value.toString());
        }
        return value;
    }

    private Object getValue(BinaryCondition statement) {
        Object value1 = getValueOfOperand(statement.getOperand1());
        Object value2 = getValueOfOperand(statement.getOperand2());
        return statement.getOperator().apply(value1, value2);
    }

    private Object getValue(UnaryCondition statement) {
        Object value = getValueOfOperand(statement.getOperand());
        return statement.getOperator().apply(value);
    }

    private Object getValue(FunctionCallStatement statement) {
        FunctionDefStatement function = getFunction(statement.getIdentifier(), statement.getArgumentCount());
        List<Component> callParams = statement.getArgumentList();
        List<Component> components = function.getStatements();
        boolean isNested = false;
        for (int i = 0; i < components.size(); i++) {
            Component stmt = components.get(i);
            if (stmt instanceof ParamDeclaration) {
                ParamDeclaration paramDeclaration = ((ParamDeclaration) stmt);
                try {
                    addDeclarationToScope(paramDeclaration);
                } catch (UniquenessViolationException e) {
                    isNested = true;
                }
                paramDeclaration.setValue(getValueOfOperand(callParams.get(i)));
            } else {
                traverse(stmt);
            }
        }
        Object value = getValueOfOperand(function.getReturnStatement());
        if (!isNested) {
            removeDeclarations(function);
        }
        return value;
    }

    private Object getValueOfOperand(Object operand) {
        Object value;
        if (operand instanceof FunctionCallStatement) {
            value = getValue((FunctionCallStatement) operand);
        } else if (operand instanceof BinaryExpression) {
            value = getValue((BinaryExpression) operand);
        } else if (operand instanceof UnaryExpression) {
            value = getValue((UnaryExpression) operand);
        } else if (operand instanceof Constant) {
            value = getValue((Constant) operand);
        } else if (operand instanceof BinaryCondition) {
            value = getValue((BinaryCondition) operand);
        } else if (operand instanceof UnaryCondition) {
            value = getValue((UnaryCondition) operand);
        } else {
            if (Type.getTypeForValue(operand) == Type.VARIABLE) {
                value = getDeclaration(operand.toString()).getValue();
            } else {
                value = operand;
            }
        }
        return value;
    }

    private void traverse(Traversable node) {
        if (node != null) {
            preValidate(node);

            List<Component> components = getStatements(node);
            processStatements(node, components);

            if (!(node instanceof Program)) {
                node.accept(this);
            }
        }
    }

    private List<Component> getStatements(Traversable node) {
        boolean switched = execute;
        List<Component> executableComponents = node.getStatements();
        if (!execute) {
            return executableComponents;
        }
        List<Component> validateOnlyComponents = new ArrayList<>();
        boolean condition;
        if (node instanceof IfThenElseStatement) {
            condition = (Boolean) getValueOfOperand(((IfThenElseStatement) node).getCondition());
            if (condition) {
                executableComponents = ((IfThenElseStatement) node).getIfStatements();
                validateOnlyComponents = ((IfThenElseStatement) node).getElseStatements();
            } else {
                executableComponents = ((IfThenElseStatement) node).getElseStatements();
                validateOnlyComponents = ((IfThenElseStatement) node).getIfStatements();
            }
        } else if (node instanceof IfThenStatement) {
            condition = (Boolean) getValueOfOperand(((IfThenStatement) node).getCondition());
            if (!condition) {
                validateOnlyComponents = executableComponents;
                executableComponents = new ArrayList<>();
            }
        } else if (node instanceof FunctionDefStatement) {
            validateOnlyComponents = executableComponents;
            executableComponents = new ArrayList<>();
        }

        execute = false;
        for (Component st : validateOnlyComponents) {
            traverse(st);
        }
        if (switched) {
            execute = true;
        }
        return executableComponents;
    }

    private void processStatements(Traversable node, List<Component> components) {
        if (node instanceof WhileStatement && execute) {
            while ((Boolean) getValueOfOperand(((WhileStatement) node).getCondition())) {
                if (breakEvent > breakOccurred) {
                    breakOccurred++;
                    break;
                }
                for (Component st : components) {
                    traverse(st);
                }
                removeDeclarations(node);
            }
        } else {
            if (node instanceof Program) {
                for (int i = 0; i < components.size(); i++) {
                    if (i == components.size() - 1) {
                        printActive = true;
                    }
                    traverse(components.get(i));
                }
            } else {
                for (Component st : components) {
                    traverse(st);
                }
            }
        }
    }

    @Override
    protected void preValidate(Traversable node) {
        super.preValidate(node);
        if (node instanceof BreakStatement && execute) {
            breakEvent++;
        }
    }

    public void setScriptMode(boolean scriptMode) {
        this.scriptMode = scriptMode;
    }


}

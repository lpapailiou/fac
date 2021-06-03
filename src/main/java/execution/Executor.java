package execution;

import exceptions.UniquenessViolationException;
import parser.parsetree.*;
import parser.parsetree.interfaces.Declaration;
import parser.parsetree.interfaces.Traversable;
import interpreter.Interpreter;

import java.util.ArrayList;
import java.util.List;

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
            //printDeclarations();
        }
    }

    @Override
    public void visit(Statement acceptor) {
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
            Object value2 = getValueOfOperand(acceptor.getStatements().get(0));
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
        if (execute  && (!scriptMode || printActive)) {
            Statement statement = (Statement) acceptor.getValue();
            if (statement != null) {
                System.out.println(">>>>  " + getValueOfOperand(statement).toString().replaceAll("'", ""));
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
        FunctionDefStatement function = getFunction(statement.getIdentifier(), statement.getParamCount());
        List<Statement> callParams = statement.getParameterList();
        List<Statement> statements = function.getStatements();
        boolean isNested = false;
        for (int i = 0; i < statements.size(); i++) {
            Statement stmt = statements.get(i);
            if (stmt instanceof  ParamDeclaration) {
                ParamDeclaration paramDeclaration = ((ParamDeclaration) stmt);
                try {
                    addDeclarationToScope(paramDeclaration);
                } catch (UniquenessViolationException e){
                    isNested = true;
                }
                paramDeclaration.setValue(getValueOfOperand(callParams.get(i)));
            } else {
                traverse(stmt);
            }
        }
        Object value = getValueOfOperand(function.getReturnValue());
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
            preValidation(node);

            List<Statement> statements = getStatements(node);
            processStatements(node, statements);

            if (!(node instanceof Program)) {
                node.accept(this);
            }
        }
    }

    private List<Statement> getStatements(Traversable node) {
        boolean switched = execute;
        List<Statement> executableStatements = node.getStatements();
        if (!execute) {
            return executableStatements;
        }
        List<Statement> validateOnlyStatements = new ArrayList<>();
        boolean condition;
        if (node instanceof IfThenElseStatement) {
            condition = (Boolean) getValueOfOperand(((IfThenElseStatement) node).getCondition());
            if (condition) {
                executableStatements = ((IfThenElseStatement) node).getIfStatements();
                validateOnlyStatements = ((IfThenElseStatement) node).getElseStatements();
            } else {
                executableStatements = ((IfThenElseStatement) node).getElseStatements();
                validateOnlyStatements = ((IfThenElseStatement) node).getIfStatements();
            }
        } else if (node instanceof IfThenStatement) {
            condition = (Boolean) getValueOfOperand(((IfThenStatement) node).getCondition());
            if (!condition) {
                validateOnlyStatements = executableStatements;
                executableStatements = new ArrayList<>();
            }
        } else if (node instanceof FunctionDefStatement) {
            validateOnlyStatements = executableStatements;
            executableStatements = new ArrayList<>();
        }

        execute = false;
        for (Statement st : validateOnlyStatements) {
            traverse(st);
        }
        if (switched) {
            execute = true;
        }
        return executableStatements;
    }

    private void processStatements(Traversable node, List<Statement> statements) {
        if (node instanceof WhileStatement && execute) {
            while ((Boolean) getValueOfOperand(((WhileStatement) node).getCondition())) {
                if (breakEvent > breakOccurred) {
                    breakOccurred++;
                    break;
                }
                for (Statement st : statements) {
                    traverse(st);
                }
                removeDeclarations(node);
            }
        } else {
            if (node instanceof Program) {
                for (int i = 0; i < statements.size(); i++) {
                    if (i == statements.size()-1) {
                        printActive = true;
                    }
                    traverse(statements.get(i));
                }
            } else {
                for (Statement st : statements) {
                    traverse(st);
                }
            }
        }
    }

    @Override
    protected void preValidation(Traversable node) {
        super.preValidation(node);
        if (node instanceof BreakStatement && execute) {
            breakEvent++;
        }
    }

    public void setScriptMode(boolean scriptMode) {
        this.scriptMode = scriptMode;
    }


}

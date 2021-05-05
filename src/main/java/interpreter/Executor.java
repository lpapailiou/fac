package interpreter;

import parser.parsetree.*;
import parser.parsetree.interfaces.Declaration;
import parser.parsetree.interfaces.Traversable;
import parser.validation.Validator;

import java.util.ArrayList;
import java.util.List;

public class Executor extends Validator {

    private int breakEvent = 0;
    private int breakOccurred = 0;

    @Override
    public void visit(Program acceptor) {
        traverse(acceptor);
        printDeclarations();
    }

    @Override
    public void visit(Statement acceptor) {
    }

    @Override
    public void visit(VariableDeclaration acceptor) {
        addDeclarationToScope(acceptor);
        Object value = getValueOfOperand(acceptor.getValue());
        acceptor.setValue(value);
    }

    @Override
    public void visit(ParamDeclaration acceptor) {
        addDeclarationToScope(acceptor);
        Object value = getValueOfOperand(acceptor.getValue());
        acceptor.setValue(value);
    }

    @Override
    public void visit(AssignmentStatement acceptor) {
        Declaration declaration = getDeclaration(acceptor.getIdentifier());
        Object value1 = declaration.getValue();
        Object value2 = getValueOfOperand(acceptor.getStatements().get(0));
        declaration.setValue(acceptor.getOperator().apply(value1, value2));

    }

    @Override
    public void visit(FunctionCallStatement acceptor) {
    }

    @Override
    public void visit(PrintCallStatement acceptor) {
        Statement statement = (Statement) acceptor.getValue();
        if (statement != null) {
            System.out.println("PRINTING " + getValueOfOperand(statement).toString().replaceAll("'", ""));
        } else {
            System.out.println("PRINTING NOTHING");
        }
    }

    @Override
    public void visit(FunctionDefStatement acceptor) {
        removeDeclarations(acceptor);
    }

    @Override
    public void visit(IfThenStatement acceptor) {
        removeDeclarations(acceptor);
    }

    @Override
    public void visit(IfThenElseStatement acceptor) {
        removeDeclarations(acceptor);
    }

    @Override
    public void visit(WhileStatement acceptor) {

    }

    private Object getValue(BinaryExpression statement) {
        Object value1 = getValueOfOperand(statement.getOperand1());
        Object value2 = getValueOfOperand(statement.getOperand2());
        System.out.println("binary result: " + statement.toString() + " = " + statement.getOperator().apply(value1, value2));
        return statement.getOperator().apply(value1, value2);
    }

    private Object getValue(UnaryExpression statement) {
        System.out.println("unary result: " + statement.toString() + " = " + getValueOfOperand(statement.getOperand()));
        return getValueOfOperand(statement.getOperand());
    }

    private Object getValue(ConditionalExpression statement) {
        Object value1 = getValueOfOperand(statement.getOperand1());
        Object value2 = getValueOfOperand(statement.getOperand2());
        //System.out.println(value1 + " " + statement.getOperator().getOperator() + " " + value2 +  " = " + statement.getOperator().apply(value1, value2));
/*
        if (Type.getTypeForValue(s1) == Type.STRING) {
            return s1.toString().equals(s2.toString());
        } else {
            return s1 == s2;
        }*/
        return statement.getOperator().apply(value1, value2);
    }

    private Object getValue(FunctionCallStatement statement) {
        Object value;
        FunctionDefStatement function = getFunction(statement.getIdentifier(), statement.getParamCount());
        value = function.getReturnValue();
        if (Type.getTypeForValue(value) == Type.VARIABLE) {
            value = getValueOfOperand(getDeclaration(value.toString()).getValue());
        }
        return value;
    }

    private Object getValueOfOperand(Object operand) {
        if (operand == null) {
            return null;
        }
        Object value;
        if (operand instanceof FunctionCallStatement) {
            value = getValue((FunctionCallStatement) operand);
        } else if (operand instanceof BinaryExpression) {
            value = getValue((BinaryExpression) operand);
        } else if (operand instanceof UnaryExpression) {
            value = getValue((UnaryExpression) operand);
        } else if (operand instanceof ConditionalExpression) {
            value = getValue((ConditionalExpression) operand);
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
            if (node instanceof FunctionDefStatement) {
                addFunDeclarationToScope((FunctionDefStatement) node);
            }

            List<Statement> statements = getStatements(node);
            processStatements(node, statements);

            if (!(node instanceof Program)) {
                //System.out.println("VISITING NODE " + node.getClass().toString().replaceAll("class parser.parsetree.", ""));
                node.accept(this);
            }
        }
    }

    private List<Statement> getStatements(Traversable node) {
        List<Statement> statements = node.getStatements();
        boolean condition;
        if (node instanceof IfThenElseStatement) {
            condition = (Boolean) getValueOfOperand(((IfThenElseStatement) node).getCondition());
            if (condition) {
                statements = ((IfThenElseStatement) node).getIfStatements();
            } else {
                statements = ((IfThenElseStatement) node).getElseStatements();
            }
        } else if (node instanceof IfThenStatement) {
            condition = (Boolean) getValueOfOperand(((IfThenStatement) node).getCondition());
            if (!condition) {
                statements = new ArrayList<>();
            }
        }
        return statements;
    }

    private void processStatements(Traversable node, List<Statement> statements) {
        if (node instanceof BreakStatement) {
            breakEvent++;
        }

        if (node instanceof WhileStatement) {
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
            for (Statement st : statements) {
                traverse(st);
            }
        }
    }

}

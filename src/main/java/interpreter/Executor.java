package interpreter;

import parser.parsetree.*;
import parser.parsetree.interfaces.Declaration;
import parser.parsetree.interfaces.Traversable;
import parser.validation.Validator;

import java.util.ArrayList;
import java.util.List;

public class Executor extends Validator {

    private boolean doBreak = false;

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
        List<Statement> statements = acceptor.getStatements();
        if (statements.size() > 0) {
            System.out.println("PRINTING " + getValueOfOperand(acceptor.getStatements().get(0)));
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
        removeDeclarations(acceptor);
    }

    private Object getValueOfExpression(BinaryExpression statement) {
        Object value1 = getValueOfOperand(statement.getOperand1());
        Object value2 = getValueOfOperand(statement.getOperand2());
        return statement.getOperator().apply(value1, value2);
    }

    private Object getValueOfExpression(UnaryExpression statement) {
        return getValueOfOperand(statement.getOperand());
    }

    private Object getValueOfCondition(ConditionalStatement statement) {
        Object value1 = getValueOfOperand(statement.getOperand1());
        Object value2 = getValueOfOperand(statement.getOperand2());
        return statement.getOperator().apply(value1, value2);
    }

    private Object getValueOfOperand(Object operand) {
        if (operand == null) {
            return null;
        }
        Object value;
        if (operand instanceof FunctionCallStatement) {
            FunctionDefStatement function = getFunction(((FunctionCallStatement) operand).getIdentifier(), ((FunctionCallStatement) operand).getParamCount());
            value = function.getReturnValue();
            if (Type.getTypeForValue(value) == Type.VARIABLE) {
                value = getValueOfOperand(getDeclaration(value.toString()).getValue());
            }
        } else if (operand instanceof BinaryExpression) {
            value = getValueOfExpression((BinaryExpression) operand);
        } else if (operand instanceof UnaryExpression) {
            value = getValueOfExpression((UnaryExpression) operand);
        } else if (operand instanceof ConditionalStatement) {
            value = getValueOfCondition((ConditionalStatement) operand);
        } else {
            //System.out.println("op: " + operand);        // TODO
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
            if (node instanceof WhileStatement) {
                whileDepth++;
            } else if (node instanceof FunctionDefStatement) {
                addFunDeclarationToScope((FunctionDefStatement) node);
            }
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
                System.out.println(statements.size() + " " + condition);
                if (!condition) {
                    statements = new ArrayList<>();
                }
            }

            if (node instanceof WhileStatement) {
                while((Boolean) getValueOfOperand(((WhileStatement) node).getCondition()) && !doBreak) {
                    for (Statement st : statements) {
                        traverse(st);
                        if (st instanceof BreakStatement) {
                            doBreak = true;
                        }
                    }
                }
            } else {
                for (Statement st : statements) {
                    traverse(st);
                    if (st instanceof BreakStatement) {
                        doBreak = true;
                    }
                }
            }
            doBreak = false;
            if (!(node instanceof Program)) {
                System.out.println("VISITING NODE " + node.getClass().toString().replaceAll("class parser.parsetree.", ""));
                node.accept(this);
            }
        }
    }

}

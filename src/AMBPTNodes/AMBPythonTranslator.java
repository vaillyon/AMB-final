package AMBPTNodes;

import AMBTokenPKG.*;
import java.util.List;

public class AMBPythonTranslator {

    public static void translate(AMBNodes root) {
        System.out.println("generated Python Code\n");
        translateNode(root, 0);
    }

    private static void translateNode(AMBNodes node, int indentLevel) {
        String nodeType = node.getClass().getSimpleName();
        List<Object> children = node.getChildren();

        switch (nodeType) {
            case "Statements" -> {
                for (Object child : children) {
                    if (child instanceof AMBNodes childNode) {
                        translateNode(childNode, indentLevel);
                    }
                }
            }
            case "Assignment" -> {
                System.out.print(indent(indentLevel));
                if (children.get(0) instanceof CODE varName) {
                    System.out.print(varName.getCode() + " = ");
                }
                // Second child
                if (children.size() > 1 && children.get(1) instanceof AMBNodes exprNode) {
                    translateExpression(exprNode);
                }
                System.out.println();
            }
            case "If" -> {
                System.out.print(indent(indentLevel) + "if ");
                translateCondition(children, indentLevel);


                for (int i = 0; i < children.size(); i++) {
                    if (children.get(i) instanceof KeyWords kw && kw.getKeyword().equals("ELSE")) {
                        System.out.println(indent(indentLevel) + "else:");
                        if (i + 1 < children.size() && children.get(i + 1) instanceof AMBNodes elseBlock) {
                            translateNode(elseBlock, indentLevel + 1);
                        }
                        break;
                    }
                }
            }
            case "While" -> {
                System.out.print(indent(indentLevel) + "while ");
                translateCondition(children, indentLevel);
            }
            case "For" -> {
                System.out.print(indent(indentLevel) + "for ");
                if (children.size() >= 5) {
                    if (children.get(1) instanceof CODE varName) {
                        System.out.print(varName.getCode() + " in range(");
                        if (children.get(3) instanceof AMBNodes startExpr) {
                            translateExpression(startExpr);
                        }
                        System.out.print(", ");
                        if (children.get(5) instanceof AMBNodes endExpr) {
                            translateExpression(endExpr);
                        }
                        System.out.println("):");

                        if (children.size() > 6 && children.get(7) instanceof AMBNodes body) {
                            translateNode(body, indentLevel + 1);
                        }
                    }
                }
            }
            case "Print" -> {
                System.out.print(indent(indentLevel) + "print(");
                for (int i = 1; i < children.size(); i++) {
                    if (children.get(i) instanceof AMBNodes exprNode) {
                        translateExpression(exprNode);
                        if (i < children.size() - 1 && children.get(i + 1) instanceof AMBNodes) {
                            System.out.print(", ");
                        }
                    }
                }
                System.out.println(")");
            }
            case "FunctionDef" -> {
                System.out.print(indent(indentLevel) + "def ");
                if (children.get(1) instanceof CODE funcName) {
                    System.out.print(funcName.getCode() + "(");
                }


                int paramStart = 3;
                int paramEnd = findClosingParenIndex(children, paramStart);

                for (int i = paramStart; i < paramEnd; i++) {
                    if (children.get(i) instanceof CODE paramName) {
                        System.out.print(paramName.getCode());
                        if (i + 2 < paramEnd && children.get(i + 1) instanceof Symbols && children.get(i + 2) instanceof AMBNodes) {
                            i += 2;
                            System.out.print(", ");
                        }
                    }
                }
                System.out.println("):");


                if (paramEnd + 1 < children.size() && children.get(paramEnd + 1) instanceof AMBNodes body) {
                    translateNode(body, indentLevel + 1);
                }
            }
            case "Return" -> {
                System.out.print(indent(indentLevel) + "return ");
                if (children.size() > 1 && children.get(1) instanceof AMBNodes exprNode) {
                    translateExpression(exprNode);
                }
                System.out.println();
            }
            case "Comment" -> {
                System.out.print(indent(indentLevel) + "# ");
                for (Object child : children) {
                    if (child instanceof CODE text) {
                        System.out.print(text.getCode());
                    }
                }
                System.out.println();
            }
            default -> {

                for (Object child : children) {
                    if (child instanceof AMBNodes childNode) {
                        translateNode(childNode, indentLevel);
                    } else {
                    }
                }
            }
        }
    }

    private static int findClosingParenIndex(List<Object> children, int start) {
        int parenCount = 0;
        for (int i = start; i < children.size(); i++) {
            Object obj = children.get(i);
            if (obj instanceof Symbols sym) {
                if (sym.toString().equals("(")) parenCount++;
                else if (sym.toString().equals(")")) {
                    parenCount--;
                    if (parenCount == 0) return i;
                }
            }
        }
        return children.size() - 1;
    }

    private static void translateExpression(AMBNodes expr) {
        String exprType = expr.getClass().getSimpleName();
        List<Object> terms = expr.getChildren();

        switch (exprType) {
            case "ArithmeticExpr", "BooleanExpr" -> {

                if (terms.size() >= 3) {
                    if (terms.get(0) instanceof AMBNodes leftExpr) {
                        if (shouldAddParentheses(leftExpr)) System.out.print("(");
                        translateExpression(leftExpr);
                        if (shouldAddParentheses(leftExpr)) System.out.print(")");
                    }

                    if (terms.get(1) instanceof Symbols op) {
                        System.out.print(" " + symbolToPython(op) + " ");
                    }

                    if (terms.get(2) instanceof AMBNodes rightExpr) {
                        if (shouldAddParentheses(rightExpr)) System.out.print("(");
                        translateExpression(rightExpr);
                        if (shouldAddParentheses(rightExpr)) System.out.print(")");
                    }
                }
            }
            case "FunctionCall" -> {

                if (terms.get(0) instanceof CODE funcName) {
                    System.out.print(funcName.getCode() + "(");
                }


                for (int i = 2; i < terms.size(); i++) {
                    if (terms.get(i) instanceof AMBNodes argExpr) {
                        translateExpression(argExpr);
                        // Add comma if more arguments follow
                        if (i + 2 < terms.size() && terms.get(i + 1) instanceof Symbols &&
                                terms.get(i + 2) instanceof AMBNodes) {
                            System.out.print(", ");
                            i++; // Skip the comma
                        }
                    } else if (terms.get(i) instanceof Symbols sym && sym.toString().equals(")")) {
                        System.out.print(")");
                        break;
                    }
                }
            }
            case "Literal" -> {

                for (Object term : terms) {
                    if (term instanceof CODE code) {
                        System.out.print(code.getCode());
                    } else if (term instanceof Symbols sym) {
                        System.out.print(sym);
                    }
                }
            }
            case "Variable" -> {

                if (!terms.isEmpty() && terms.get(0) instanceof CODE varName) {
                    System.out.print(varName.getCode());
                }
            }
            default -> {

                for (Object part : terms) {
                    if (part instanceof AMBNodes nested) {
                        translateExpression(nested);
                    } else {
                    }
                }
            }
        }
    }

    private static boolean shouldAddParentheses(AMBNodes expr) {

        String exprType = expr.getClass().getSimpleName();
        return exprType.equals("ArithmeticExpr") || exprType.equals("BooleanExpr");
    }

    private static void translateCondition(List<Object> children, int indentLevel) {
        AMBNodes condition = null;


        for (int i = 1; i < children.size(); i++) {
            if (children.get(i) instanceof AMBNodes node) {
                condition = node;
                break;
            }
        }

        if (condition != null) {
            translateExpression(condition);
            System.out.println(":");


            for (int i = 1 + 1; i < children.size(); i++) {
                if (children.get(i) instanceof AMBNodes body && !(children.get(i - 1) instanceof KeyWords kw && kw.getKeyword().equals("ELSE"))) {
                    translateNode(body, indentLevel + 1);
                    break;
                }
            }
        }
    }

    private static String findNextLabel(List<Object> children, int start) {
        for (int i = start; i < children.size(); i++) {
            Object o = children.get(i);
            if (o instanceof CODE code) return code.getCode();
            if (o instanceof KeyWords keyword) return keyword.getKeyword();
        }
        return "unknown_label";
    }

    private static String findPreviousLabel(List<Object> children, int start) {
        for (int i = start; i >= 0; i--) {
            Object o = children.get(i);
            if (o instanceof CODE code) return code.getCode();
            if (o instanceof KeyWords keyword) return keyword.getKeyword();
        }
        return "unknown_label";
    }

    private static String indent(int level) {
        return "    ".repeat(level);
    }

    private static String symbolToPython(Symbols symbol) {
        return switch (symbol.toString()) {
            case "*" -> "*";
            case "/" -> "/";
            case "+" -> "+";
            case "-" -> "-";
            case "<" -> "<";
            case ">" -> ">";
            case "=<" -> "<=";
            case "=>" -> ">=";
            case "=" -> "==";
            case "!=" -> "!=";
            case "&&" -> "and";
            case "||" -> "or";
            case "!" -> "not ";
            default -> symbol.toString();
        };
    }
}
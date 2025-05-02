package AMBPTNodes;

import AMBTokenPKG.*;
import java.util.List;

public class AMBPythonTranslator {

    public static void translate(AMBNodes root) {
        System.out.println("# Translated Python Code from AMB\n");


        List<Object> children = root.getChildren();
        if (children.size() >= 2) {
            translateInitVars((AMBNodes) children.get(1));
        }


        if (children.size() >= 3) {
            translateCodeBlock((AMBNodes) children.get(2), 0);
        }

        System.out.println("\nmain()");
    }

    private static void translateNode(AMBNodes node, int indentLevel) {
        List<Object> children = node.getChildren();

        if (children.isEmpty()) return;

        Object firstChild = children.get(0);


        if (firstChild instanceof KeyWords) {
            String keyword = ((KeyWords) firstChild).getKeyword();
            switch (keyword) {
                case "IF":
                    translateIfStatement(node, indentLevel);
                    break;
                case "WHILE":
                    translateWhileLoop(node, indentLevel);
                    break;
                case "PRINT":
                    System.out.print(indent(indentLevel) + "print( ");
                    if (children.size() > 1) {
                        translateExpression((AMBNodes) children.get(1));
                    }
                    System.out.println("  )");
                    break;
                default:

                    for (Object child : children) {
                        if (child instanceof AMBNodes) {
                            translateNode((AMBNodes) child, indentLevel);
                        } else if (child instanceof AMBTokens) {
                            translateToken((AMBTokens) child, indentLevel);
                        }
                    }
            }
        } else if (firstChild instanceof CODE) {

            if (children.size() >= 3 && children.get(1) instanceof Symbols &&
                    ((Symbols)children.get(1)).getType().equals("assignment")) {
                System.out.print(indent(indentLevel) + ((CODE) firstChild).getCode() + "= ");
                translateExpression((AMBNodes) children.get(2));
                System.out.println();

            } else if (children.size() >= 4 && children.get(1) instanceof HardOpen) {

                System.out.print(indent(indentLevel) + ((CODE) firstChild).getCode() + "[");
                translateExpression((AMBNodes) children.get(2));
                System.out.print("] = ");
                translateExpression((AMBNodes) children.get(4));
                System.out.println();
            }
        } else {
            for (Object child : children) {
                if (child instanceof AMBNodes) {
                    translateNode((AMBNodes) child, indentLevel);
                } else if (child instanceof AMBTokens) {
                    translateToken((AMBTokens) child, indentLevel);
                }
            }
        }
    }

    private static void translateToken(AMBTokens token, int indentLevel) {
        if (token instanceof IntToken) {

            System.out.print(((IntToken) token).getValue());
        } else if (token instanceof StringToken) {
            System.out.print("\"" + ((StringToken) token).getValue().replace("\"", "") + "\"");
        } else if (token instanceof KeyWords) {
            KeyWords keyword = (KeyWords) token;
            if (keyword.getKeyword().equals("INPUT_INT")) {
                System.out.print("int(input(\"\"))");
            } else if (keyword.getKeyword().equals("INPUT_STRING")) {
                System.out.print("input(\"\")");
            } else {
                System.out.print(keyword.getKeyword());
            }
        } else if (token instanceof CODE) {
            System.out.print(((CODE) token).getCode());
        } else if (token instanceof MultOp) {
            MultOp op = (MultOp) token;
            System.out.print(op.getOp() == MultOp.Operand.mult ? "*" : "/");
        } else if (token instanceof Symbols) {
            System.out.print(symbolToPython((Symbols) token));
        } else if (token instanceof HardOpen) {
            System.out.print("[");
        } else if (token instanceof HardClose) {
            System.out.print("]");
        }
    }

    private static void translateInitVars(AMBNodes varListNode) {
        List<Object> varNodes = varListNode.getChildren();
        for (Object obj : varNodes) {
            if (!(obj instanceof AMBNodes)) continue;

            AMBNodes node = (AMBNodes) obj;
            List<Object> children = node.getChildren();

            if (children.isEmpty()) continue;


            if (children.get(0) instanceof KeyWords) {
                String type = ((KeyWords) children.get(0)).getKeyword();
                if (children.size() >= 2 && children.get(1) instanceof CODE) {
                    String varName = ((CODE) children.get(1)).getCode();
                    switch (type) {
                        case "INT" -> System.out.println(varName + " = 0");
                        case "STRING" -> System.out.println(varName + " = \"\"");
                    }
                }
            }

            else if (children.get(0) instanceof HardOpen && children.size() >= 6) {
                if (children.get(1) instanceof KeyWords &&
                        children.get(3) instanceof CODE &&
                        children.get(5) instanceof IntToken) {

                    String type = ((KeyWords) children.get(1)).getKeyword();
                    String varName = ((CODE) children.get(3)).getCode();
                    int size = ((IntToken) children.get(5)).getValue();

                    String defaultVal = type.equals("STRING") ? "\"\"" : "0";
                    StringBuilder array = new StringBuilder("[" + defaultVal);
                    for (int i = 1; i < size; i++) {
                        array.append(", ").append(defaultVal);
                    }
                    array.append("]");

                    System.out.println(varName + " = " + array);
                }
            }
        }
    }

    private static void translateExpression(AMBNodes expr) {
        if (expr == null) return;

        List<Object> terms = expr.getChildren();
        if (terms.isEmpty()) return;


        if (terms.size() >= 3 && terms.get(0) instanceof CODE && terms.get(1) instanceof HardOpen) {
            System.out.print(((CODE) terms.get(0)).getCode() + "[");
            translateExpression((AMBNodes) terms.get(2));
            System.out.print("]");
            return;
        }


        for (int i = 0; i < terms.size(); i++) {
            Object part = terms.get(i);
            if (part instanceof AMBNodes) {
                translateExpression((AMBNodes) part);
            } else if (part instanceof AMBTokens) {
                translateToken((AMBTokens) part, 0);

                if (part instanceof Symbols || part instanceof MultOp) {
                    if (i < terms.size() - 1 && !(terms.get(i+1) instanceof SoftClose || terms.get(i+1) instanceof HardClose)) {
                        System.out.print(" ");
                    }
                    if (i > 0 && !(terms.get(i-1) instanceof SoftOpen || terms.get(i-1) instanceof HardOpen)) {
                        System.out.print(" ");
                    }
                }
            }
        }
    }

    private static void translateIfStatement(AMBNodes ifNode, int indentLevel) {
        List<Object> children = ifNode.getChildren();
        if (children.size() < 5) return;


        System.out.print(indent(indentLevel) + "if ");
        translateCondition(children, 1, indentLevel);


        int thenIndex = -1;
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) instanceof KeyWords &&
                    ((KeyWords)children.get(i)).getKeyword().equals("THEN")) {
                thenIndex = i;
                break;
            }
        }

        if (thenIndex >= 0 && thenIndex + 1 < children.size()) {
            translateNode((AMBNodes) children.get(thenIndex + 1), indentLevel + 1);
        }


        int elseIndex = -1;
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) instanceof KeyWords &&
                    ((KeyWords)children.get(i)).getKeyword().equals("ELSE")) {
                elseIndex = i;
                break;
            }
        }

        if (elseIndex >= 0 && elseIndex + 1 < children.size()) {
            System.out.println(indent(indentLevel) + "else:");
            translateNode((AMBNodes) children.get(elseIndex + 1), indentLevel + 1);
        }
    }

    private static void translateWhileLoop(AMBNodes whileNode, int indentLevel) {
        List<Object> children = whileNode.getChildren();
        if (children.size() < 5) return;


        System.out.print(indent(indentLevel) + "while ");
        translateCondition(children, 1, indentLevel);


        int doIndex = -1;
        for (int i = 0; i < children.size(); i++) {
            if (children.get(i) instanceof KeyWords &&
                    ((KeyWords)children.get(i)).getKeyword().equals("DO")) {
                doIndex = i;
                break;
            }
        }

        if (doIndex >= 0 && doIndex + 1 < children.size()) {
            translateNode((AMBNodes) children.get(doIndex + 1), indentLevel + 1);
        }
    }

    private static void translateCondition(List<Object> children, int startIdx, int indentLevel) {

        for (int i = startIdx; i < children.size(); i++) {
            Object child = children.get(i);
            if (child instanceof AMBNodes) {
                translateExpression((AMBNodes) child);
            } else if (child instanceof AMBTokens) {
                if (child instanceof KeyWords &&
                        (((KeyWords)child).getKeyword().equals("THEN") ||
                                ((KeyWords)child).getKeyword().equals("DO"))) {
                    break;
                }
                translateToken((AMBTokens) child, 0);
            }
        }
        System.out.println(":");
    }

    private static void translateCodeBlock(AMBNodes codeBlock, int indentLevel) {
        List<Object> children = codeBlock.getChildren();

        for (int i = 0; i < children.size(); i++) {
            Object child = children.get(i);

            if (child instanceof START_SUB) {

                if (i + 1 < children.size() && children.get(i + 1) instanceof AMBNodes) {
                    Object labelObj = ((AMBNodes)children.get(i + 1)).getChildren().get(0);
                    if (labelObj instanceof CODE) {
                        String subName = ((CODE)labelObj).getCode();
                        System.out.println("\ndef " + subName + "():");


                        for (int j = i + 2; j < children.size(); j++) {
                            if (children.get(j) instanceof AMBNodes &&
                                    !((AMBNodes)children.get(j)).getChildren().isEmpty()) {
                                translateNode((AMBNodes)children.get(j), 1);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    private static String indent(int level) {
        return "    ".repeat(level);
    }

    private static String symbolToPython(Symbols symbol) {
        return switch (symbol.getType()) {
            case "semi" -> ";";
            case "colon" -> ":";
            case "assignment" -> "=";
            case "addOp" -> symbol.toString().equals("+") ? "+" : "-";
            case "compOp" -> switch (symbol.toString()) {
                case "<" -> "<";
                case ">" -> ">";
                case "=<" -> "<=";
                case "=>" -> ">=";
                case "=" -> "==";
                case "!=" -> "!=";
                default -> symbol.toString();
            };
            default -> symbol.toString();
        };
    }
}
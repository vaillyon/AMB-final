package AMBTokenPKG;

import AMBPTNodes.AMBNodes;
import java.util.ArrayList;

public class AMBParseTreeGenerator {

    static int currentToken = 0;
    static ArrayList<AMBTokens> code;
    public static AMBNodes root = null;

    public static void generateParseTree(ArrayList<AMBTokens> code) {
        AMBParseTreeGenerator.code = code;
        currentToken = 0;
        root = program();
    }

    public static AMBNodes program() {
        AMBTokens cur = code.get(currentToken);
        if (cur instanceof START_PROGRAM) {
            AMBNodes node = new AMBNodes();
            node.addChild(cur);
            currentToken++;
            node.addChild(variableList());
            node.addChild(codeBlock());
            return node;
        } else {
            throwError("START_PROGRAM", cur);
            return null;
        }
    }

    public static AMBNodes codeBlock() {
        AMBNodes node = new AMBNodes();
        while (currentToken < code.size()) {
            AMBTokens cur = code.get(currentToken);
            if (cur instanceof END_PROGRAM) {
                node.addChild(cur);
                currentToken++;
                break;
            } else if (cur instanceof START_SUB || cur instanceof KeyWords kw && kw.getKeyword().equals("CODE")) {
                node.addChild(subList());
            } else {
                System.err.println("Unexpected token in codeBlock: " + cur.getClass().getSimpleName());
                currentToken++;
            }
        }
        return node;
    }

    public static AMBNodes variableList() {
        AMBTokens cur = code.get(currentToken);

        if (cur instanceof KeyWords) {
            String kw = ((KeyWords) cur).getKeyword();
            if (kw.equals("INT") || kw.equals("STRING")) {
                AMBNodes node = new AMBNodes();
                node.addChild(variable());
                node.addChild(variableList());
                return node;
            }
        }

        if (cur instanceof HardOpen) {
            AMBNodes node = new AMBNodes();
            node.addChild(variable());
            node.addChild(variableList());
            return node;
        }

        if (cur instanceof KeyWords && ((KeyWords) cur).getKeyword().equals("CODE")) {
            AMBNodes node = new AMBNodes();
            node.addChild(cur);
            currentToken++;
            node.addChild(subList());
            return node;
        }

        System.err.println("On token number " + currentToken + ": Expected INT, STRING, [ or CODE, but found " + cur.getClass().getSimpleName());
        System.exit(1);
        return null;
    }

    public static AMBNodes variable() {
        AMBTokens cur = code.get(currentToken);
        AMBNodes node = new AMBNodes();

        if (cur instanceof KeyWords) {
            String type = ((KeyWords) cur).getKeyword();
            if (type.equals("INT") || type.equals("STRING")) {
                node.addChild(cur);
                currentToken++;
                node.addChild(expectLabel());
                expectTokenType("semi");
                node.addChild(code.get(currentToken - 1));
                return node;
            }
        } else if (cur instanceof HardOpen) {
            currentToken++;
            AMBTokens typeToken = code.get(currentToken);
            if (typeToken instanceof KeyWords) {
                String type = ((KeyWords) typeToken).getKeyword();
                if (type.equals("INT") || type.equals("STRING")) {
                    node.addChild(new HardOpen());
                    node.addChild(typeToken);
                    currentToken++;
                    expectToken(HardClose.class);
                    node.addChild(code.get(currentToken - 1));
                    node.addChild(expectLabel());
                    expectToken(HardOpen.class);
                    node.addChild(code.get(currentToken - 1));
                    expectToken(IntToken.class);
                    node.addChild(code.get(currentToken - 1));
                    expectToken(HardClose.class);
                    node.addChild(code.get(currentToken - 1));
                    expectTokenType("semi");
                    node.addChild(code.get(currentToken - 1));
                    return node;
                }
            }
        }

        throwError("INT label; or [TYPE] label [number];", cur);
        return null;
    }

    public static AMBNodes expectLabel() {
        AMBTokens cur = code.get(currentToken);
        if (cur instanceof CODE || cur instanceof KeyWords) {
            currentToken++;
            return wrapNode(cur);
        }
        throwError("label", cur);
        return null;
    }


    public static AMBNodes subList() {
        AMBTokens cur = code.get(currentToken);
        if (cur instanceof START_SUB) {
            AMBNodes node = new AMBNodes();
            node.addChild(cur);
            currentToken++;
            node.addChild(expectLabel());
            expectTokenType("colon");
            node.addChild(code.get(currentToken - 1));
            node.addChild(codeList());
            node.addChild(subList());  
            return node;
        } else if (cur instanceof END_PROGRAM) {
            AMBNodes node = new AMBNodes();
            node.addChild(cur);
            currentToken++;
            return node;
        }
        throwError("START_SUB or END_PROGRAM", cur);
        return null;
    }

    private static AMBNodes codeLine() {
        AMBTokens cur = code.get(currentToken);
        AMBNodes node = new AMBNodes();

        node.addChild(cur);
        currentToken++;

        return node;
    }


    public static AMBNodes codeList() {
        AMBTokens cur = code.get(currentToken);
        AMBNodes node = new AMBNodes();

        if (cur instanceof END_SUB) {
            node.addChild(cur);
            currentToken++;
            return node;
        }

        node.addChild(codeLine());
        if (currentToken < code.size()) {
            node.addChild(codeList());
        }
        return node;
    }

    private static void expectToken(Class<?> expectedType) {
        AMBTokens cur = code.get(currentToken);
        if (!expectedType.isInstance(cur)) {
            throwError(expectedType.getSimpleName(), cur);
        }
        currentToken++;
    }

    private static void expectTokenType(String expectedType) {
        AMBTokens cur = code.get(currentToken);
        if (cur instanceof KeyWords && ((KeyWords) cur).getKeyword().equals(expectedType.toUpperCase())) {
            currentToken++;
            return;
        }
        if (cur instanceof Symbols && ((Symbols) cur).getType().equals(expectedType)) {
            currentToken++;
            return;
        }
        throwError(expectedType, cur);
    }

    private static void throwError(String expected, AMBTokens found) {
        System.err.println("Syntax error at token " + currentToken + ": Expected " + expected + ", but found " + found.getClass().getSimpleName());
        System.exit(1);
    }

    private static AMBNodes wrapNode(AMBTokens token) {
        AMBNodes node = new AMBNodes();
        node.addChild(token);
        return node;
    }
}

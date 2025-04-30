package AMBTokenPKG;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class AMBTokenizer {

    public static ArrayList<AMBTokens> tokenize(String fileName) {
        ArrayList<AMBTokens> tokens = new ArrayList<>();


        StringBuilder rawData = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = br.readLine()) != null) {
                rawData.append(line).append(" ");
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + fileName);
            e.printStackTrace();
            System.exit(1);
        }

        // Preprocess
        ArrayList<String> stringyTokens = preprocessTokens(rawData.toString());

        // Process based on type
        for (String str : stringyTokens) {
            AMBTokens tok = switch (str) {
                // keyword
                case "START_PROGRAM" -> new START_PROGRAM();
                case "END_PROGRAM." -> new END_PROGRAM();
                case "START_SUB" -> new START_SUB();
                case "END_SUB." -> new END_SUB();

                // Keywords
                case "CODE", "IF", "THEN", "ELSE", "END_IF", "WHILE", "DO", "END_WHILE", "GOSUB", "INT", "STRING",
                     "INPUT_INT", "INPUT_STRING", "PRINT" -> new KeyWords(str);

                // Symbols
                case "(" -> new SoftOpen();
                case ")" -> new SoftClose();
                case "[" -> new HardOpen();
                case "]" -> new HardClose();
                case ";" -> new Symbols("semi");
                case ":" -> new Symbols("colon");
                case ":=" -> new Symbols("assignment");
                case "*" -> new MultOp(MultOp.Operand.mult);
                case "/" -> new MultOp(MultOp.Operand.divide);
                case "+", "-" -> new Symbols("addOp");
                case "<", ">", "=<", "=>", "=", "!=" -> new Symbols("compOp");
                default -> determineTokenType(str);
            };

            if (tok == null) {
                System.err.println("Tokenizing error: Invalid token \"" + str + "\"");
                System.exit(1);
            }

            tokens.add(tok);
        }

        return tokens;
    }

    // Determines type
    private static AMBTokens determineTokenType(String str) {
        if (str.matches("-?[1-9][0-9]*|0")) { // Integer number
            return new IntToken(Integer.parseInt(str));
        } else if (str.matches("\"[^\"]*\"")) { // String literal
            return new StringToken(str);
        } else if (str.matches("[a-zA-Z][a-zA-Z0-9_]*")) { // Label (e.g., variable or function name)
            return new CODE(str); // Labels are treated as CODE tokens
        } else {
            return null;
        }
    }


    private static ArrayList<String> preprocessTokens(String raw) {
        ArrayList<String> result = new ArrayList<>();
        String[] parts = raw.trim().split("\\s+");

        boolean inString = false;
        StringBuilder stringBuilder = new StringBuilder();

        for (String part : parts) {
            if (inString) {
                stringBuilder.append(" ").append(part);
                if (part.endsWith("\"") && !part.endsWith("\\\"")) {
                    result.add(stringBuilder.toString());
                    inString = false;
                }
            } else {
                if (part.startsWith("\"")) {
                    if (part.endsWith("\"") && part.length() > 1) {
                        result.add(part); // full string on one word
                    } else {
                        inString = true;
                        stringBuilder = new StringBuilder(part);
                    }
                } else {
                    result.add(part);
                }
            }
        }

        return result;
    }
}

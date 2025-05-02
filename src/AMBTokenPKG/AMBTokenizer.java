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

        ArrayList<String> stringyTokens = preprocessTokens(rawData.toString());


        for (String str : stringyTokens) {
            AMBTokens tok = switch (str) {

                case "START_PROGRAM" -> new START_PROGRAM();
                case "END_PROGRAM" -> new END_PROGRAM();
                case "START_SUB" -> new START_SUB();
                case "END_SUB" -> new END_SUB();


                case "CODE", "IF", "THEN", "ELSE", "END_IF", "WHILE", "DO", "END_WHILE", "GOSUB",
                     "INT", "STRING", "INPUT_INT", "INPUT_STRING", "PRINT" -> new KeyWords(str);


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


                case "." -> null;


                default -> determineTokenType(str);
            };

            if (tok == null) {

                if (!str.equals(".")) {
                    System.err.println("Tokenizing error: Invalid token \"" + str + "\"");
                    System.exit(1);
                }
            } else {
                tokens.add(tok);
            }
        }

        return tokens;
    }


    private static AMBTokens determineTokenType(String str) {

        if (str.matches("-?[0-9]+")) {
            return new IntToken(Integer.parseInt(str));
        }

        else if (str.matches("\"[^\"]*\"")) {
            return new StringToken(str);
        }

        else if (str.matches("[a-zA-Z][a-zA-Z0-9_]*")) {
            return new CODE(str);
        }

        else {
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
                continue;
            }

            if (part.startsWith("\"")) {
                if (part.endsWith("\"") && part.length() > 1) {
                    result.add(part);
                } else {
                    inString = true;
                    stringBuilder = new StringBuilder(part);
                }
                continue;
            }


            while (part.length() > 1 && (part.endsWith(":") || part.endsWith("."))) {
                result.add(part.substring(0, part.length() - 1));
                part = part.substring(part.length() - 1);
            }

            result.add(part);
        }

        return result;
    }
}

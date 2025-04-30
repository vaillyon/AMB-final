import AMBTokenPKG.*;

import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // sample amb
        String fileName = "sample.amb"; // checks that its there

        ArrayList<AMBTokens> tokens = AMBTokenizer.tokenize(fileName);

        System.out.println("=== tokenized output ===");
        for (AMBTokens token : tokens) {

            System.out.println("type: " + token.getType() + " | value: " + token);
        }

        // validation checks
        System.out.println("\n=== validation ===");
        if (!tokens.isEmpty()) {
            System.out.println("Tokenizer run successful");
        } else {
            System.out.println("Token not found, check input file");
        }
    }
}

import AMBPTNodes.AMBPythonTranslator;
import AMBTokenPKG.AMBTokenizer;
import AMBTokenPKG.AMBTokens;
import AMBTokenPKG.AMBParseTreeGenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Starter {
    public static void main(String[] args) {
        String fileName = "sample.amb"; // input
        
        File file = new File(fileName);
        if (!file.exists() || file.isDirectory()) {
            System.err.println("Error: file '" + fileName + "' not found. Make sure it's in the project root.");
            System.exit(1);
        }

        // Tokenize
        ArrayList<AMBTokens> tokens = AMBTokenizer.tokenize(fileName);

        // parse tree
        AMBParseTreeGenerator.generateParseTree(tokens);

        // Translate
        AMBPythonTranslator.translate(AMBParseTreeGenerator.root);

        // Confirmation
        System.out.println("Tokenization, Parse Tree generation, and Python translation successful.");

        // Read and print
        File outputFile = new File("output.py");
        if (outputFile.exists()) {
            System.out.println("\nGenerated Python Code:");
            try {
                Files.readAllLines(Paths.get("output.py")).forEach(System.out::println);
            } catch (IOException e) {
                System.err.println("Error reading output.py: " + e.getMessage());
            }
            System.out.println("\noutput.py exists? true");
            System.out.println("output.py length: " + outputFile.length());
        } else {
            System.err.println("output.py was not created.");
        }
    }
}

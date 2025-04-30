import AMBPTNodes.AMBPythonTranslator;
import AMBTokenPKG.AMBTokenizer;
import AMBTokenPKG.AMBTokens;
import AMBTokenPKG.AMBParseTreeGenerator;

import java.io.File;
import java.util.ArrayList;

public class Starter {
    public static void main(String[] args) {
        // Read in file and produce token list
        // Take in Token List and produce Parse Tree
        // Take in parse tree and produce python code

        String fileName = "sample.amb"; // file name

        File file = new File(fileName);
        if (!file.exists() || file.isDirectory()) {
            System.err.println("Error: file '" + fileName + "' not found, make sure everything is in project.");
            System.exit(1);
        }

        // Tokenize
        ArrayList<AMBTokens> tokens = AMBTokenizer.tokenize(fileName);

        //  parse tree from token
        AMBParseTreeGenerator.generateParseTree(tokens);

        //  parse tree to Python
        AMBPythonTranslator.translate(AMBParseTreeGenerator.root);

        System.out.println("Tokenization, Parse Tree generation, and Python translation successful.");
    }
}



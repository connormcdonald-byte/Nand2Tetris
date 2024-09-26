
/**
 * Extracts syntax of high-level program.
 * 
 * @Connor
 */

import java.io.*;
import java.util.*;

public class GenerateSyntax {
    public void main(){
        
        String input = "";
        String output = "";
        String [] inputExtensions = {"Main.jack"}; //, "Square.jack", "SquareGame.jack"};
        String [] outputExtensions = {"SyntaxMain.xml"}; //, "SyntaxSquare.xml", "SyntaxSquareGame.xml"};
        for (int i = 0; i < inputExtensions.length; i++){
            File inputFile = new File(input + inputExtensions[i]);
            File outputFile = new File(output + outputExtensions[i]);
            try{
                CompilationEngine ce = new CompilationEngine(inputFile, outputFile);
                ce.compileClass();
            }
            catch (IOException e){
                System.out.println("Something went wrong!");
                e.printStackTrace();
            }
        }
    }
}



/**
 * Control Program
 * 
 * @Connor
 */

import java.io.*;
import java.util.*;

public class GenerateCode {
    public void main(){
        
        String input = "";
        String output = "";
        String [] inputExtensions = {"Main.jack"}; //, "Ball.jack", "Bat.jack", "PongGame.jack"}; //, "Square.jack", "SquareGame.jack"};
        String [] outputExtensions = {"SyntaxMain.xml"}; //, "SyntaxBall.xml", "SyntaxBat.xml", "SyntaxPongGame.xml"};//, "SyntaxSquare.xml", "SyntaxSquareGame.xml"};
        String [] vmExtensions ={"Main.vm"}; //, "Ball.vm", "Bat.vm", "PongGame.vm"};//, "Square.vm", "SquareGame.vm"};
        for (int i = 0; i < inputExtensions.length; i++){
            File inputFile = new File(input + inputExtensions[i]);
            File outputFile = new File(output + outputExtensions[i]);
            File vmOutputFile = new File(output + vmExtensions[i]);
            try{
                CodeGenerator ce = new CodeGenerator(inputFile, outputFile, vmOutputFile);
                ce.compileClass();
                ce.listClassEntries();
                ce.listSubroutineEntries();
            }
            catch (IOException e){
                System.out.println("Something went wrong!");
                e.printStackTrace();
            }
        }
    }
}


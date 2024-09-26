/**
 * Controls the VM compilations.
 * 
 * @Connor
 */

import java.io.*;
import java.util.*;

public class Main {
    public void generateASM(){
        File file = new File("");
        String filePath = ("");
     
        Parser parser = new Parser(file);
        try{
        CodeWriter cw = new CodeWriter(filePath);
        while (parser.hasMoreCommands()){
            parser.advance();
            String currentCommand = parser.getCurrentCommand();
            String commandType = parser.commandType();
            if (commandType.equals("C_ARITHMETIC")){
                cw.writeArithmetic(currentCommand);
            }
            if (commandType.equals("C_POP")){
                cw.writePushPop("pop", parser.arg1().toUpperCase(), Integer.parseInt(parser.arg2()));
            }
            if (commandType.equals("C_PUSH")){
                cw.writePushPop("push", parser.arg1().toUpperCase(), Integer.parseInt(parser.arg2()));
            }
        }
        cw.close();
        }
        catch (IOException e) {
             System.out.println("An error occurred.");
             e.printStackTrace();
        }
    }
}

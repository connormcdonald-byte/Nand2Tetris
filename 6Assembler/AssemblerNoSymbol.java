
/**
 * Translates the assembly code, excluding references, into its binary value.
 * 
 * @Connor
 */

import java.io.*;
import java.util.*;

public class AssemblerNoSymbol {
    public void generateBinary(){
        File readFile = new File("");
        ParserNoSymbol pns = new ParserNoSymbol(readFile);
        CodeNoSymbol cns = new CodeNoSymbol();
        try {
             File myObj = new File("");
             if (myObj.createNewFile()) {
                 System.out.println("File created: " + myObj.getName());
             } 
             else {
                 System.out.println("File already exists.");
             }
        } 
        catch (IOException e) {
             System.out.println("An error occurred.");
             e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter("");
            while (pns.hasMoreCommands()){
                pns.advance();
                if (pns.commandType().equals("A_COMMAND")){
                    String padding = "00000000000000";
                    StringBuilder sb = new StringBuilder();
                    String symbol = pns.symbol();
                    int baseTen = Integer.parseInt(symbol);
                    String binary = Integer.toBinaryString(baseTen);
                    sb.append("0");
                    sb.append(padding.substring(0, 15 - binary.length()));
                    sb.append(binary);
                    myWriter.write(sb.toString());
                    myWriter.write("\r\n");
                }
                else if (pns.commandType().equals("C_COMMAND")){
                    String dest = pns.dest();
                    String comp = pns.comp();
                    String jump = pns.jump();
                    myWriter.write("111" + cns.comp(comp) + cns.dest(dest) + cns.jump(jump));
                    myWriter.write("\r\n");
                }
            }
            myWriter.close();
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        
    }
}

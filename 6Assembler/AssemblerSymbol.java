
/**
 * Translates the assembly code, including references, into its binary value.
 * 
 * @Connor
 */
import java.io.*;
import java.util.*;

public class AssemblerSymbol {
    public void generateBinary(){
        File readFile = new File("");
        ParserNoSymbol pns = new ParserNoSymbol(readFile);
        CodeNoSymbol cns = new CodeNoSymbol();
        SymbolTable st = new SymbolTable();
        st.initialize();
        int n = 16;
        int line = -1;
        String digits = "0123456789";
        while (pns.hasMoreCommands()){
                pns.advance();
                if (!pns.commandType().equals("L_COMMAND")){
                    line = line + 1;
                }
                if (pns.commandType().equals("L_COMMAND")){
                    String symbol = pns.symbol();
                    System.out.println(symbol);
                    st.addEntry(symbol, line + 1);
                }
        }
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
        pns = new ParserNoSymbol(readFile);
        try {
            FileWriter myWriter = new FileWriter("");
            while (pns.hasMoreCommands()){
                pns.advance();
                if (pns.commandType().equals("A_COMMAND")){
                    String symbol = pns.symbol();
                    if ((!st.contains(symbol)) && (digits.indexOf(symbol.charAt(0)) == -1)){
                        st.addEntry(symbol, n);
                        n = n + 1;
                    }
                    if (digits.indexOf(symbol.charAt(0)) == -1){
                        symbol = Integer.toString(st.getAddress(symbol));
                    }
                    String padding = "00000000000000";
                    StringBuilder sb = new StringBuilder();
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

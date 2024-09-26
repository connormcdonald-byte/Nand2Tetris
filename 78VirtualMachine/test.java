
/**
 * Test.
 * 
 * @Connor
 */

import java.util.*;
import java.io.*;


public class test {
    public void tester(){
        String string = "    ";
        //System.out.println(string);
        //System.out.println(string.trim());
        String[] arithCommands = {"a", "b"};
        File file = new File("");
        Parser myParser = new Parser(file);

        String filePath = ("");
        try {
             File output = new File(filePath);
             if (output.createNewFile()) {
                 System.out.println("File created: " + file.getName());
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
            CodeWriter cw = new CodeWriter(filePath);
            while (myParser.hasMoreCommands()){
                myParser.advance();
                //System.out.print(myParser.getCurrentCommand() + " " + myParser.commandType());
                //myParser.printArgs();
                //System.out.println();
                System.out.println(myParser.getCurrentCommand());
            }
            cw.close();
        }
        catch (IOException e) {
             System.out.println("An error occurred.");
             e.printStackTrace();
        }
        
    }
    
    public void directoryTest(){
        String directory = "";
        File file = new File(directory);
        System.out.println(file.isDirectory());
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++){
            System.out.println(files[i].getName());
        }
    }

}

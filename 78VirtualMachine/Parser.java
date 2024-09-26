
/**
 * From VM to high-level
 * 
 * @Connor
 */

import java.io.*;
import java.util.*;

public class Parser {
    private String[] arithCommands = {"add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"};
    private String currentCommand;
    private Scanner myReader;
    private String[] args;
    private String[] arg2 = {"C_PUSH", "C_POP", "C_FUNCTION", "C_CALL"};  
    public Parser(File file){
        currentCommand = "";
        args = currentCommand.split(" ");
        try {
            myReader = new Scanner(file);
            System.out.println("Object created!");
        } 
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public String getCurrentCommand(){
        return currentCommand;
    }
    public Boolean hasMoreCommands(){
        return myReader.hasNext();
    }
    public void advance(){
        int temp = 0;
        while (currentCommand.startsWith("/") || currentCommand.isEmpty() || temp == 0){
            currentCommand = myReader.nextLine().trim();
            temp += 1;
        }
    }
    public String commandType(){
        for (int i = 0; i < arithCommands.length; i++){
            if (currentCommand.startsWith(arithCommands[i])){
                return "C_ARITHMETIC";
            }
        }
        if (currentCommand.startsWith("pop")){
            return "C_POP";
        }
        if (currentCommand.startsWith("push")){
            return "C_PUSH";
        }
        if (currentCommand.startsWith("label")){
            return "C_LABEL";
        }
        if (currentCommand.startsWith("goto")){
            return "C_GOTO";
        }
        if (currentCommand.startsWith("if-goto")){
            return "C_IF";
        }
        if (currentCommand.startsWith("function")){
            return "C_FUNCTION";
        }
        if (currentCommand.startsWith("call")){
            return "C_CALL";
        }
        if (currentCommand.startsWith("return")){
            return "C_RETURN";
        }
        return "ERROR";
    }
    private void getArgs(){
       args = currentCommand.split(" ");
    }
    public void printArgs(){
        getArgs();
        for (int i = 0; i < args.length; i++){
            System.out.print(" " + args[i]);
        }
    }
    public String arg1(){
        if (commandType().equals("C_RETURN")){
            return "N/A";
        }
        getArgs();
        if (commandType().equals("C_ARITHMETIC")){
            return args[0];
        }
        return args[1];
    }
    public String arg2(){
        getArgs();
        for (int i = 0; i < arg2.length; i++){
            if (arg2[i].equals(commandType())){
                return args[2];
            }
        }
        return "N/A";
    }
        
}

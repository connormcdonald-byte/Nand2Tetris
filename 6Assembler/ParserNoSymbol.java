
/**
 * Unpacks each instruction into underlying fields.
 * 
 * @Connor
 */

import java.io.*;
import java.util.*;

public class ParserNoSymbol {
    private Scanner myReader;
    private int lineNumber;
    private String command;
    public ParserNoSymbol(File input){
        lineNumber = 0;
        command = "/";
        try {
            myReader = new Scanner(input);
            System.out.println("Object created!");
        } 
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    public String getCommand() {
        return command;
    }
    public boolean hasMoreCommands(){
        return myReader.hasNextLine();
    }
    public void advance(){
        int temp = 0;
        while (command.trim().startsWith("/") || command.isEmpty() || temp == 0){
            command = myReader.nextLine().trim();
            temp += 1;
        }
    }
    public String commandType(){
        if (command.trim().startsWith("@")){
            return "A_COMMAND";
        }
        else if (command.trim().startsWith("(")){
            return "L_COMMAND";
        }
        else {
            return "C_COMMAND";
        }
    }
    public String symbol(){
        if (commandType().equals("C_COMMAND")){
            return "N/A";
        }
        if (commandType().equals("A_COMMAND")){
            return command.trim().substring(1);
        }
        return command.trim().substring(1, command.length() - 1);
    }
    public String dest(){
        if (!commandType().equals("C_COMMAND")){
            return "N/A";
        }
        int index = command.indexOf("=");
        if (index == -1){
            return "";
        }
        return command.substring(0, index);
    }
    public String comp(){
        if (!commandType().equals("C_COMMAND")){
            return "N/A";
        }
        int index1 = command.indexOf("=");
        int index2 = command.indexOf(";");
        if (index2 == -1){
            return command.substring(index1 + 1);
        }
        if (index1 == -1){
            return command.substring(0, index2);
        }
        return command.substring(index1 + 1, index2);
    }
    public String jump(){
        if (!commandType().equals("C_COMMAND")){
            return "N/A";
        }
        int index = command.indexOf(";");
        if (index != -1){
            return command.substring(index + 1);
        }
        return "";
    }
}

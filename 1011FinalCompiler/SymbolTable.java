
/**
 * Symbol Table for Objects and Subroutines!
 * 
 * @Connor
 */

import java.io.*;
import java.util.*;

public class SymbolTable {
    public HashMap<String, String[]> classSymbolTable;
    public HashMap<String, String[]> methodSymbolTable;
    private ArrayList<HashMap<String, String[]>> tables;
    private String[] varKinds = {"static", "field", "argument", "local"};
    private HashMap<String, Integer> indexNumber;

    public SymbolTable(){
        classSymbolTable = new HashMap<String, String[]>();
        methodSymbolTable = new HashMap<String, String[]>();
        indexNumber = new HashMap<String, Integer>();
        for (int i = 0; i < varKinds.length; i++){
            indexNumber.put(varKinds[i], 0);
        }
        tables = new ArrayList<HashMap<String, String[]>>();
        tables.add(methodSymbolTable);
        tables.add(classSymbolTable);
    }
    
    public void startSubroutine(){
        methodSymbolTable.clear();
        indexNumber.replace("argument", 0);
        indexNumber.replace("local", 0);
    }
    
    public boolean inTable
    (String identifier, HashMap<String, String[]> table){
        for (String key : table.keySet()){
            if (key.equals(identifier)){
                return true;
            }
        }
        return false;
    }
    
    public void define(String name, String type, String kind){
        int currentIndex = indexNumber.get(kind);
        String[] descriptors = {type, kind, Integer.toString(currentIndex)};
        indexNumber.replace(kind, currentIndex + 1);
        if (kind.equals("static") || kind.equals("field")){
            classSymbolTable.put(name, descriptors);
            return;
        }
        methodSymbolTable.put(name, descriptors);
    }
    
    public int varCount(String kind){
        return indexNumber.get(kind);
    }
    
    public String kindOf(String identifier){
        for (HashMap<String, String[]> table : tables){
            if (inTable(identifier, table)){
                return table.get(identifier)[1];
            }
        }
        return "NONE";
    }
    
    public String typeOf(String identifier){
        for (HashMap<String, String[]> table : tables){
            if (inTable(identifier, table)){
                return table.get(identifier)[0];
            }
        }
        return "NONE";
    }
    
    public int indexOf(String identifier){
        for (HashMap<String, String[]> table : tables){
            if (inTable(identifier, table)){
                return Integer.parseInt(table.get(identifier)[2]);
            }
        }
        return -1;
    }
    
    public boolean isPrimitive(String identifier){
        String type = typeOf(identifier);
        if (type.equals("boolean") || type.equals("int") || type.equals("char")){
            return true;
        }
        return false;
    }
    
    public void listClassEntries(){
        System.out.println("Listing class symbols: \n");
        for (String identifier : classSymbolTable.keySet()){
            System.out.println(identifier + ": " 
            + classSymbolTable.get(identifier)[0] + ": "
            + classSymbolTable.get(identifier)[1] + ": "
            + classSymbolTable.get(identifier)[2] + "\n");
        }
        System.out.println("Done listing class symbols. \n");
    }
    
    public void listSubroutineEntries(){
        System.out.println("Listing subroutine symbols: \n");
        for (String identifier : methodSymbolTable.keySet()){
            System.out.println(identifier + ": " 
            + methodSymbolTable.get(identifier)[0] + ": "
            + methodSymbolTable.get(identifier)[1] + ": "
            + methodSymbolTable.get(identifier)[2] + "\n");
        }
        System.out.println("Done listing subroutine symbols. \n");
    }
}



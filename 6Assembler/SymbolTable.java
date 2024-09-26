
/**
 * Translates from symbols to hardware memory addresses.
 * 
 * @Connor
 */

import java.util.*;

public class SymbolTable {
    private HashMap<String, Integer> symbolTable = new HashMap<String, Integer>();
    public void initialize(){
        symbolTable.put("SP", 0);
        symbolTable.put("LCL", 1);
        symbolTable.put("ARG", 2);
        symbolTable.put("THIS", 3);
        symbolTable.put("THAT", 4);
        symbolTable.put("SCREEN", 16384);
        symbolTable.put("KBD", 24576);
        for (int i = 0; i < 16; i++){
            symbolTable.put("R" + i, i);
        }
    }
    public void addEntry(String symbol, Integer address){
        symbolTable.put(symbol, address);
    }
    public boolean contains(String symbol){
        return symbolTable.containsKey(symbol);
    }
    public int getAddress(String symbol){
        return symbolTable.get(symbol);
    }
}

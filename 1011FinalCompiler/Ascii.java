
/**
 * Create a reference ASCII hashmap.
 * 
 * @Connor
 */

import java.util.*;

public class Ascii {
    public void main(){
        String asciiChars = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\b";
        HashMap<String, Integer> charsToAscii = new HashMap<String, Integer>();
        for (int i = 32; i < asciiChars.length() + 32; i++){
            charsToAscii.put(asciiChars.substring(i - 32, i - 32 + 1), i);
        }
        for (String asciiChar : charsToAscii.keySet()){
            System.out.println(asciiChar + ": " + charsToAscii.get(asciiChar));
        }
    }
    
    public void main2(){
        System.out.println("//");
    }
}

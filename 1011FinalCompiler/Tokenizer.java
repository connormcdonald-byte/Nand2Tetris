
/**
 * Tokenizes High Level for Compilation
 * 
 * @author (your name) 
 * @version (a version number or a date)
 */

import java.io.*;
import java.util.*;

public class Tokenizer {
    private String[] keywords = {"class", "constructor", "function", "method",
            "field", "static", "var", "int", "char", "boolean", "void", "true", "false",
            "null", "this", "let", "do", "if", "else", "while", "return"};
    private char[] symbols = {'{', '}', '(', ')', '[', ']', '.', ',', ';',
            '+', '-', '*', '/', '&', '|', '<', '>', '=', '~'};
    String[] specialChars = {"<", ">", "\"", "&"};
    String[] html = {"&lt;", "&gt;", "&quot;", "&amp;"}; 
    HashMap<String, String> specialCharsToHtml = new HashMap<String, String>(); ;
    private BufferedReader br; 
    private char currentChar;
    private int currentNum;
    private String currentToken;
    private String currentTokenType;
    private int tokenCount = 0;
    private int numOpen = -1;
    private int numClose = 0;

    public Tokenizer(File input) throws IOException{
        FileReader fr = new FileReader(input);
        br = new BufferedReader(fr);
        currentChar = ' ';
        currentNum = 0;
        currentToken = "";
        for (int i = 0; i < specialChars.length; i++){
            specialCharsToHtml.put(specialChars[i], html[i]);
        }
    }

    public boolean hasMoreTokens() throws IOException{
        if (numOpen == numClose){
            return false;
        }
        return true;
    }

    public void advanceToToken() throws IOException{
        boolean beginningOfToken = false;
        while (beginningOfToken == false){
            br.mark(0);
            currentNum = br.read();
            currentChar = (char) currentNum;
            while (Character.isWhitespace(currentChar)){
                currentNum = br.read();
                currentChar = (char) currentNum;
            }
            if (currentChar == '/'){
                int nextNum = br.read();
                char nextChar = (char) nextNum;
                if (Character.isWhitespace(nextChar)){
                    br.reset();
                    while (currentChar != '/'){
                        currentNum = br.read();
                        currentChar = (char) currentNum;
                    }
                    br.read();
                    br.read();
                    return;
                }
                else if (nextChar == '/'){
                    br.readLine();
                }
                else if (nextChar == '*'){
                    int numA = br.read();
                    char charA = (char) numA;
                    int numB = br.read();
                    char charB = (char) numB;
                    while ((charA != '*') || (charB != '/')){
                        numA = numB;
                        charA = charB;
                        numB = br.read();
                        charB = (char) numB;
                    }
                }
                else{
                    br.reset();
                    currentNum = br.read();
                    currentChar = (char) currentNum;
                    beginningOfToken = true;
                }
            }

            else{
                beginningOfToken = true;
            }
        }
    }

    public void advance() throws IOException{
        advanceToToken();
        StringBuilder sb = new StringBuilder();
        if (currentNum == -1){
            return;
        }
        if (currentChar == '\"'){
            currentNum = br.read();
            currentChar = (char) currentNum;
            sb.append(currentChar);
            while (currentChar != '\"'){
                currentNum = br.read();
                currentChar = (char) currentNum;
                sb.append(currentChar);
            }
            sb.deleteCharAt(sb.length() - 1);
            currentToken = sb.toString();
            currentTokenType = "STRING_CONST";
            return;
        }
        for (int i = 0; i < symbols.length; i++){
            if (currentChar == symbols[i]){
                currentToken = Character.toString(currentChar);
                if (currentToken.equals("{") & numOpen < 0){
                    numOpen = 0;
                }
                if (currentToken.equals("{")){
                    numOpen += 1;
                }
                if (currentToken.equals("}")){
                    numClose += 1;
                }
                for (String key : specialCharsToHtml.keySet()){
                    if (key.equals(currentToken)){
                        currentToken = specialCharsToHtml.get(key);
                    }
                }
                currentTokenType = "SYMBOL";
                return;
            }
        }
        if (Character.isDigit(currentChar)){
            while (Character.isDigit(currentChar)){
                sb.append(currentChar);
                br.mark(0);
                currentNum = br.read();
                currentChar = (char) currentNum;
            }
            currentToken = sb.toString();
            currentTokenType = "INT_CONST";
            br.reset();
            return;
        }
        while (!Character.isWhitespace(currentChar)){
            for (int i = 0; i < symbols.length; i++){
                if (currentChar == symbols[i]){
                    currentToken = sb.toString();
                    br.reset();
                    for (int j = 0; i < keywords.length; i++){
                        if (currentToken.equals(keywords[i])){
                            currentTokenType = "KEYWORD";
                            return;
                        }
                    }
                    currentTokenType = "IDENTIFIER";
                    return;
                }
            }
            sb.append(currentChar);
            br.mark(0);
            currentNum = br.read();
            currentChar = (char) currentNum;
        }
        currentToken = sb.toString();
        currentTokenType = "IDENTIFIER";
        for (int i = 0; i < keywords.length; i++){
            if (currentToken.equals(keywords[i])){
                currentTokenType = "KEYWORD";
                return;
            }
        }
    }

    public int getCurrentNum(){
        return currentNum;
    }

    public int intVal(){
        return Integer.parseInt(currentToken);
    }

    public String keyWord(){
        return currentToken.toUpperCase();
    }

    public String identifier(){
        return currentToken;
    }

    public char symbol(){
        return currentToken.charAt(0);
    }

    public String getTokenType(){
        return currentTokenType;
    }

    public String stringVal(){
        return currentToken;
    }

    public char getCurrentChar(){
        return currentChar;
    }

    public String getCurrentToken(){
        return currentToken;
    }
}

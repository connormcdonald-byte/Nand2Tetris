
/**
 * Extracts tokens from Syntax
 * 
 * @Connor
 */

import java.util.*;
import java.io.*;

public class GenerateTokens {
    public void main(){

        String[] inputs = {"Main.jack", "Bat.jack", "Ball.jack", "PongGame.jack"};
        String[] outputs = {"MyMainT.xml", "MyBatT.xml", "MyBallT.xml", "MyPongGameT.xml"};
        for (int k = 0; k < inputs.length; k++){
            String input = ";
            String output = "";
            input = input + inputs[k];
            output = output + outputs[k];
            File inputFile = new File(input);
            File outputFile = new File(output);
            String[] tags = {"keyword", "symbol", "identifier", "stringConstant", "integerConstant"};
            String[] tokenTypes = {"KEYWORD", "SYMBOL", "IDENTIFIER", "STRING_CONST", "INT_CONST"};
            HashMap<String, String> tokensToTypes = new HashMap<String, String>();
            String[] specialChars = {"<", ">", "\"", "&"};
            String[] html = {"&lt;", "&gt;", "&quot;", "&amp;"}; 
            HashMap<String, String> specialCharsToHtml = new HashMap<String, String>();
            for (int i = 0; i < tags.length; i++){
                tokensToTypes.put(tokenTypes[i], tags[i]);
            }
            for (int i = 0; i < specialChars.length; i++){
                specialCharsToHtml.put(specialChars[i], html[i]);
            }
            try{
                Tokenizer tr = new Tokenizer(inputFile);
                FileWriter writer = new FileWriter(outputFile);
                writer.write("<tokens>\n");
                while (tr.hasMoreTokens()){
                    tr.advance();
                    String tokenType = tr.getTokenType();
                    String tag = tokensToTypes.get(tokenType);
                    String currentToken = tr.getCurrentToken();
                    if (tokenType.equals("SYMBOL")){
                        for (String key : specialCharsToHtml.keySet()){
                            if (key.equals(currentToken)){
                                currentToken = specialCharsToHtml.get(key);
                            }
                        }
                    }
                   System.out.println(tr.getCurrentToken() + " " + tr.hasMoreTokens());
                    writer.write("\t<" + tag +  "> " + currentToken + 
                            " </" + tag + ">\n");
                }
                writer.write("</tokens>");
                writer.close();
            }
            catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }      
}

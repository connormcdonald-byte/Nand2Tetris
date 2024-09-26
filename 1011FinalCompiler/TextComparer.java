
/**
 * Comparison Against Precompiled Files
 * 
 * @Connor
 */

import java.util.*;
import java.io.*;

public class TextComparer {
    public void compareTexts(File fileOne, File fileTwo) throws IOException{
        FileReader fr1 = new FileReader(fileOne);
        FileReader fr2 = new FileReader(fileTwo);
        BufferedReader br1 = new BufferedReader(fr1);
        BufferedReader br2 = new BufferedReader(fr2);
        String line = "bb";
        int lineNum = 0;
        int numInequalities = 0;
        while (!line.isEmpty()){
            String line1 = br1.readLine();
            String line2 = br2.readLine();
            line = line1;
            lineNum += 1;
            if (!line1.equals(line2)){
                System.out.println(line1);
                numInequalities += 1;
            }
        }
        if (numInequalities == 0){
            System.out.println("Files are equal!");
        }
    }
    
    public void main(){
        String basePath = "";
        String firstFile = "";
        String secondFile = "";
        String finalPath = "Square\\Square.vm";
        File fileOne = new File(basePath + firstFile + finalPath);
        File fileTwo = new File(basePath + secondFile + finalPath);
        try{
            compareTexts(fileOne, fileTwo);
        }
        catch (IOException e){
                System.out.println("Something went wrong!");
                e.printStackTrace();
        }
    }
}

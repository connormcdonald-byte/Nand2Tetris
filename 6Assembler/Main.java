
/**
 * Intializes I/O and drives the process. Files need to be input seperately.
 * 
 * @Connor
 */

import java.io.*;
import java.util.*;

public class FileTesting {
    public void createFile(){
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
    }
    
    public void writeFile(){
        try {
             myWriter = new FileWriter("");
             String[] letters = {"a", "b", "c", "d"};
             for (int i = 0; i < letters.length; i++){
                 myWriter.write(letters[i]);
             }
             myWriter.close();
             System.out.println("Successfully wrote to the file.");
        } 
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
    
    public void readFile(){
        try {
            File myObj = new File("");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        } 
        catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}

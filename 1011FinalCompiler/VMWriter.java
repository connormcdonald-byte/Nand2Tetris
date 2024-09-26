
/**
 * High Level to VM
 * 
 * @Connor
 */

import java.io.*;
import java.util.*;

public class VMWriter {
    private FileWriter writer;
    
    public VMWriter(File outputFile) throws IOException{
        writer = new FileWriter(outputFile);
    }
    
    public void writePush(String segment, int index) throws IOException{
        writer.write("push " + segment + " " + index + "\n");
    }
    
    public void writePop(String segment, int index) throws IOException{
        writer.write("pop " + segment + " " + index + "\n");
    }
    
    public void writeArithmetic(String command) throws IOException{
        writer.write(command + "\n");
    }
    
    public void writeLabel(String label) throws IOException{
        writer.write("label " + label + "\n");
    }
    
    public void writeGoTo(String label) throws IOException{
        writer.write("goto " + label + "\n");
    }
    
    public void writeIf(String label) throws IOException{
        writer.write("if-goto " + label + "\n");
    }
    
    public void writeCall(String name, int nArgs) throws IOException{
        writer.write("call " + name + " " + nArgs+ "\n");
    }
    
    public void writeFunction(String name, int nLocals) throws IOException{
        writer.write("function " + name + " " + nLocals+ "\n");
    }
    
    public void writeReturn() throws IOException{
        writer.write("return\n");
    }
    
    public void close() throws IOException{
        writer.close();
    }
}

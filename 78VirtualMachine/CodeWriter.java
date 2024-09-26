
/**
 * VM to Assembly
 * 
 * @Connor
 */

import java.io.*;
import java.util.*;

public class CodeWriter{
    private String currentFunction;
    private static int count;
    private String name;
    private FileWriter writer = null;
    private String readName;
    private String[] arithCommands = {"add", "sub", "neg", "eq", "gt", "lt", "and", "or", "not"};
    private String[] memSegments = {"LCL", "ARG", "THIS", "THAT"};
    private int numCall = 0;
    public CodeWriter(String fileName) throws IOException{
        writer = new FileWriter(fileName);
        name = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length() - 4);
        count = 0;
        readName = "";
    }
    public void writeArithmetic(String command) throws IOException{
        if (command.equals("add")){
            writer.write("//add\n@SP\nM=M-1\nA=M\nD=M\nA=A-1\nM=M+D\n");
        }
        if (command.equals("sub")){
            writer.write("//sub\n@SP\nM=M-1\nA=M\nD=M\nA=A-1\nM=M-D\n");
        }
        if (command.equals("neg")){
            writer.write("//neg\n@SP\nA=M\nA=A-1\nM=-M\n");
        }
        if (command.equals("eq")){
            writer.write
            ("//eq\n@result\nM=-1\n@SP\nM=M-1\nA=M\nD=M\nA=A-1\nD=M-D\n"
            +"@END"+count+"\nD;JEQ\n@result\nM=0\n(END"+count
            +")\n@result\nD=M\n@SP\nA=M\nA=A-1\nM=D\n");
            count += 1;
        }
        if (command.equals("gt")){
            writer.write
            ("//gt\n@result\nM=-1\n@SP\nM=M-1\nA=M\nD=M\nA=A-1\nD=M-D\n"
            +"@END"+count+"\nD;JGT\n@result\nM=0\n(END"+count
            +")\n@result\nD=M\n@SP\nA=M\nA=A-1\nM=D\n");
            count += 1;
        }
        if (command.equals("lt")){
            writer.write
            ("//lt\n@result\nM=-1\n@SP\nM=M-1\nA=M\nD=M\nA=A-1\nD=M-D\n"
            +"@END"+count+"\nD;JLT\n@result\nM=0\n(END"+count
            +")\n@result\nD=M\n@SP\nA=M\nA=A-1\nM=D\n");
            count += 1;
        }
        if (command.equals("and")){
            writer.write("//and\n@SP\nM=M-1\nA=M\nD=M\nA=A-1\nM=M&D\n");
        }
        if (command.equals("or")){
            writer.write("//or\n@SP\nM=M-1\nA=M\nD=M\nA=A-1\nM=M|D\n");
        }
        if (command.equals("not")){
            writer.write("//not\n@SP\nA=M\nA=A-1\nM=!M\n");
        }
    }
    public void writePushPop(String command, String segment, int index) throws IOException{
        writer.write("//"+command+" "+segment+" "+index+"\n");
        if (segment.equals("ARGUMENT")){
            segment = "ARG";
        }
        if (segment.equals("LOCAL")){
            segment = "LCL";
        }
        if (command.equals("push")){
        for (int i = 0; i < memSegments.length; i++){
            if (memSegments[i].equals(segment)){
                writer.write("@"+index+"\nD=A\n@"+segment
                +"\nA=D+M\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                return;
            }
        }
        if (segment.equals("TEMP")){
            writer.write("@"+index+"\nD=A\n@"+5
            +"\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            return;
        }
        if (segment.equals("CONSTANT")){
            writer.write("@"+index+"\n"+"D=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            return;
        }
        if (segment.equals("STATIC")){
            int methodSplit = currentFunction.indexOf(".");
            String className = currentFunction.substring(0, methodSplit);
            writer.write("@"+name+"."+className+"."+index+"\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            return;
        }
        if (segment.equals("POINTER")){
            writer.write("@"+memSegments[index+2]+
            "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
            return;
        }
        }
        if (command.equals("pop")){
        for (int i = 0; i < memSegments.length; i++){
            if (memSegments[i].equals(segment)){
                writer.write("@"+index+"\nD=A\n@"+segment
                +"\nD=D+M\n@addr\nM=D\n@SP\nM=M-1\nA=M\nD=M\n@addr\nA=M\nM=D\n");
                return;
            }
        }
        if (segment.equals("TEMP")){
            writer.write("@"+index+"\nD=A\n@"+5
            +"\nD=D+A\n@addr\nM=D\n@SP\nM=M-1\nA=M\nD=M\n@addr\nA=M\nM=D\n");
            return;
        }
        if (segment.equals("STATIC")){
            int methodSplit = currentFunction.indexOf(".");
            String className = currentFunction.substring(0, methodSplit);
            writer.write("@SP\nM=M-1\nA=M\nD=M\n@"+name+"."+className+"."+index+"\nM=D\n");
            return;
        }
        if (segment.equals("POINTER")){
            writer.write("@SP\nM=M-1\nA=M\nD=M\n@"+memSegments[index+2]+"\nM=D\n");
            return;
        }
        }
    }
    private String generateLabel(String label){
        label = currentFunction + "$" + label;
        return label;
    }
    public void writeInit() throws IOException{
        writer.write("//writeInit\n@256\nD=A\n@SP\nM=D\n");
        writeCall("Sys.init", 0);
    }
    public void writeLabel(String label) throws IOException{
        label = generateLabel(label);
        writer.write("//writeLabel\n("+label+")\n");
    }
    public void writeGoTo(String label) throws IOException{
        label = generateLabel(label);
        writer.write("//writeGoTo\n@"+label+"\n");
        writer.write("0;JMP\n");
    }
    public void writeIf(String label) throws IOException{
        label = generateLabel(label);
        writer.write("//writeIf\n@SP\nM=M-1\nA=M\nD=M\n@"+label+"\nD;JNE\n");
    }
    public void writeFunction(String functionName, int numLocals) throws IOException{
        currentFunction = functionName;
        writer.write("//writeFunction\n("+functionName+")\n");
        for (int i = 0; i < numLocals; i++){
            writer.write("@SP\nA=M\nM=0\n@SP\nM=M+1\n");
        }
    }
    public void writeCall(String functionName, int numArgs) throws IOException{
         String returnAddress = functionName+"$ret."+numCall;
         numCall += 1;
         writer.write("//writeCall\n@"+returnAddress+"\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
         for (int i = 0; i < memSegments.length; i++){
             writer.write("@"+memSegments[i]+"\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
         }
         writer.write("@SP\nD=M\n@5\nD=D-A\n@"+numArgs+"\nD=D-A\n@ARG\nM=D\n");
         writer.write("@SP\nD=M\n@LCL\nM=D\n");
         writer.write("@"+functionName+"\n");
         writer.write("0;JMP\n");
         writer.write("("+returnAddress+")\n");
    }
    public void writeReturn() throws IOException{
        writer.write("//writeReturn\n@LCL\nD=M\n@endFrame\nM=D\n");
        writer.write("@endFrame\nD=M\n@5\nD=D-A\nA=D\nD=M\n@retAddr\nM=D\n");
        writer.write("@SP\nM=M-1\nA=M\nD=M\n@ARG\nA=M\nM=D\n");
        writer.write("@ARG\nD=M\nD=D+1\n@SP\nM=D\n");
        for (int i = 0; i < memSegments.length; i++){
            int temp = 4 - i;
            writer.write("@endFrame\nD=M\n@"+temp+"\nD=D-A\nA=D\nD=M\n@"
            +memSegments[i]+"\nM=D\n");
        }
        writer.write("@retAddr\nA=M\n0;JMP\n");
    }       
    public void setFileName(String fileName){
         readName = fileName.substring(fileName.lastIndexOf("\\") + 1, fileName.length() - 3);
    }
    public void close() throws IOException{
        writer.close();
    }
    public String getCurrentReadName(){
        return readName;
    }
}


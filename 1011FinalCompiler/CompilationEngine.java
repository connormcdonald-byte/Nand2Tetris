
/**
 * From High Level to Binary (Putting everything together)
 * 
 * @Connor
 */

import java.io.*;
import java.util.*;

public class CodeGenerator {
    private File input;
    private File output;
    private Tokenizer tr;
    private FileWriter writer;
    private int numTabs = 0;
    private String tabs = "";
    private String[] ops = {"+", "-", "*", "/", "&", "|", "<", ">", "="}; 
    private String[] unaryOps = {"-", "~"};
    private String[] tags = {"keyword", "symbol", "identifier", "stringConstant", "integerConstant"};
    private String[] tokenTypes = {"KEYWORD", "SYMBOL", "IDENTIFIER", "STRING_CONST", "INT_CONST"};
    private HashMap<String, String> tokensToTags = new HashMap<String, String>();
    String[] specialChars = {"<", ">", "\"", "&"};
    String[] html = {"&lt;", "&gt;", "&quot;", "&amp;"}; 
    HashMap<String, String> specialCharsToHtml = new HashMap<String, String>();
    private ArrayList<Integer> compilationCounts = new ArrayList<Integer>(); 
    private SymbolTable symbolTable;
    private String identifierKind = "";
    private String identifierType = "";
    private boolean functionArgs = false;
    private boolean justDefined = false;
    private String previousKeyword = "";
    private String fileName;
    private int numParams = 0;
    private int numArgs = 0;
    private int numLocals = 0;
    private VMWriter vmWriter;
    private String subRoutine;
    private String functionType;
    private int classLabel = 0; 
    private String functionName;
    private boolean inVarDec = false;
    private String subroutineCategory = "";
    //watch out for unary/binary "-"
    private String[] jackArith = {"+", "-", "*", "/", "&amp;", "|", "&lt;", "&gt;", "=", "~"};
    private String[] vmArith = {"add", "sub", "call Math.multiply 2", 
        "call Math.divide 2", "and", "or", "lt", "gt", "eq", "not"};
    private HashMap<String, String> arithJackToVm = new HashMap<String, String>();
    private int numClassVars = 0;
    private boolean inClassVarDec = false;
    String asciiChars = " !\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~\b";
    HashMap<String, Integer> charsToAscii = new HashMap<String, Integer>();
    private boolean inLet = false;
    
    public CodeGenerator(File inputFile, File outputFile, File vmOutputFile) throws IOException{
        input = inputFile;
        output = outputFile;
        fileName = inputFile.getName().substring(0, inputFile.getName().indexOf("."));
        symbolTable = new SymbolTable();
        for (int i = 0; i < tags.length; i++){
            tokensToTags.put(tokenTypes[i], tags[i]);
        }
        for (int i = 0; i < html.length; i++){
            specialCharsToHtml.put(specialChars[i], html[i]);
        }
        for (int i = 0; i < 14; i++){
            compilationCounts.add(0);
        }
        for (int i = 0; i < jackArith.length; i++){
            arithJackToVm.put(jackArith[i], vmArith[i]);
        }
        for (int i = 32; i < asciiChars.length() + 32; i++){
            charsToAscii.put(asciiChars.substring(i - 32, i - 32 + 1), i);
        }
        for (String asciiChar : charsToAscii.keySet()){
            System.out.println(asciiChar + ": " + charsToAscii.get(asciiChar));
        }
        try {
            tr = new Tokenizer(input);
            writer = new FileWriter(output);
            vmWriter = new VMWriter(vmOutputFile);
        }
        catch (IOException e){
            System.out.println("An error occured.");
            e.printStackTrace();
        }
    }

    private void addTabs(){
        numTabs += 1;
        for (int i = 0; i < numTabs; i++){
            tabs = tabs + "\t";
        }
    }

    private void minusTabs(){
        numTabs -= 1;
        for (int i = 0; i < numTabs; i++){
            tabs = tabs + "\t";
        }
    }

    private void writeTag() throws IOException{
        String tokenType = tr.getTokenType();
        String currentToken = tr.getCurrentToken();
        String tokenTag = tokensToTags.get(tokenType);
        System.out.println("currenttoken : " + currentToken);
        if (currentToken.isEmpty()){
            return;
        }
        if (functionArgs && identifierType.isEmpty() && 
        (tokenType.equals("KEYWORD") || tokenType.equals("IDENTIFIER"))){
            identifierType = currentToken;
        }
        else if (functionArgs && tokenType.equals("IDENTIFIER")){
            symbolTable.define(currentToken, identifierType, "argument");
            justDefined = true;
            identifierType = "";
        }
        else if (tokenType.equals("IDENTIFIER") &&
                ((previousKeyword.equals("class") 
                || previousKeyword.equals("method") 
                || previousKeyword.equals("function")
                || previousKeyword.equals("constructor")))){
            writer.write(tabs + "<" + tokenTag + "> "+ currentToken + " </" + 
            tokenTag + "> category: " + previousKeyword + "\n");
            previousKeyword = "";
            return;
        }           
        else if (tokenType.equals("KEYWORD") && 
                (currentToken.equals("class") 
                || currentToken.equals("method") 
                || currentToken.equals("function"))
                || currentToken.equals("constructor")){
            previousKeyword = currentToken;
        }
        else if (currentToken.equals(";")){
            identifierType = "";
            identifierKind = "";
        }
        else if (tokenType.equals("IDENTIFIER") &&
        (!(identifierKind.isEmpty())) && 
        (!(identifierType.isEmpty()))){
            symbolTable.define(currentToken, identifierType, identifierKind);
            justDefined = true;
            if (inVarDec){
                numLocals += 1;
            }
            if (inClassVarDec && !identifierKind.equals("static")){
                numClassVars += 1;
            }
        }
        else if ((!(identifierKind.isEmpty())) && !(tokenType.equals("SYMBOL"))){
            identifierType = currentToken;
        }   
        else if (tokenType.equals("KEYWORD") && 
                (currentToken.equals("field") 
                || currentToken.equals("static") 
                || currentToken.equals("var"))){
            identifierKind = currentToken;
            if (identifierKind.equals("var")){
                identifierKind = "local";
            }
        }   
        if (tokenType.equals("SYMBOL")){
            for (String key : specialCharsToHtml.keySet()){
                if (key.equals(currentToken)){
                    currentToken = specialCharsToHtml.get(key);
                }
            }
        }
        if (tokenType.equals("IDENTIFIER") 
        && !(symbolTable.kindOf(currentToken).equals("NONE"))){
            writer.write(tabs + "<" + tokenTag + "> "+ currentToken + " </" + 
            tokenTag + "> category: " + symbolTable.typeOf(currentToken)
            + ", just defined: " + justDefined
            + ", kind: " + symbolTable.kindOf(currentToken)
            + ", index: " + symbolTable.indexOf(currentToken)
            + "\n");
            justDefined = false;
            return;
        }
        if (tokenType.equals("IDENTIFIER") 
        && (symbolTable.kindOf(currentToken).equals("NONE"))
        && Character.isUpperCase(currentToken.charAt(0))){
            writer.write(tabs + "<" + tokenTag + "> "+ currentToken + " </" + 
            tokenTag + "> category: class\n");
            return;
        }
        if (tokenType.equals("IDENTIFIER") 
        && (symbolTable.kindOf(currentToken).equals("NONE"))
        && !Character.isUpperCase(currentToken.charAt(0))){
            writer.write(tabs + "<" + tokenTag + "> "+ currentToken + " </" + 
            tokenTag + "> category: method\n");
            return;
        }
        writer.write(tabs + "<" + tokenTag + "> "+ currentToken + " </" + 
            tokenTag + ">\n");
    }

    private void compileStart(String compileName) throws IOException{
        writer.write(tabs + "<" + compileName + ">\n");
        addTabs();
    }

    private void compileEnd(String compileName) throws IOException{
        minusTabs();
        writer.write(tabs + "</" + compileName + ">\n");
    }

    private void writeUntil(String token) throws IOException{
        tr.advance();
        while (!tr.getCurrentToken().equals(token)){
            writeTag();
            tr.advance();
        }
        writeTag();
    }

    public void compileClass() throws IOException{
        System.out.println("compileClass");
        compileStart("class");
        writeUntil("{");
        tr.advance();
        inClassVarDec = true;
        while (tr.getCurrentToken().equals("static") || 
        tr.getCurrentToken().equals("field")){
            compileStart("classVarDec");
            compileClassVarDec();
            compileEnd("classVarDec");
        }
        inClassVarDec = true;
        while (tr.getCurrentToken().equals("constructor") ||
        tr.getCurrentToken().equals("function") ||
        tr.getCurrentToken().equals("method")){
            symbolTable.startSubroutine();
            subroutineCategory = tr.getCurrentToken();
            compileStart("subroutineDec");
            compileSubroutineDec();
            compileEnd("subroutineDec");
        }
        writeTag();
        compileEnd("class");
        writer.close();
        vmWriter.close();
        System.out.println(fileName);
    }

    public void compileClassVarDec() throws IOException{
        System.out.println("compileClassVarDec");
        writeTag();
        writeUntil(";");
        tr.advance();
    }

    public void compileSubroutineDec() throws IOException{
        System.out.println("compileSubroutineDec");
        System.out.println(tr.getCurrentToken());
        writeTag();
        tr.advance();
        writeTag();
        functionType = tr.getCurrentToken();
        tr.advance();
        writeTag();
        functionName = tr.getCurrentToken();
        compileStart("parameterList");
        tr.advance();
        compileParameterList();
        compileEnd("parameterList");
        writeTag();
        tr.advance();
        compileStart("subroutineBody");
        compileSubroutineBody();
        compileEnd("subroutineBody");
        tr.advance();
    }

    public void compileParameterList() throws IOException{
        System.out.println("compileParameterList");
        functionArgs = true;
        if (subroutineCategory.equals("method")){
            symbolTable.define("this", functionType , "argument");
        }
        while (!tr.getCurrentToken().equals(")")){
            writeTag();
            String pToken = tr.getCurrentToken();
            String pType = tr.getTokenType();
            if (symbolTable.inTable(pToken, symbolTable.methodSymbolTable)){
                numParams += 1;
            }
            tr.advance();
        }
        functionArgs = false;
    }

    public void compileSubroutineBody() throws IOException{
        System.out.println("compileSubroutineBody");
        writeTag();
        tr.advance();
        numLocals = 0;
        inVarDec = true;
        while (tr.getCurrentToken().equals("var")){
            compileStart("varDec");
            compileVarDec();
            compileEnd("varDec");
        }
        inVarDec = false;
        if (subroutineCategory.equals("method")){
            vmWriter.writeFunction(fileName + "." + functionName, numLocals);
            vmWriter.writePush("argument", 0);
            vmWriter.writePop("pointer", 0);
        }
        else if (subroutineCategory.equals("constructor")){
            vmWriter.writeFunction(fileName + "." + functionName, numLocals);
            vmWriter.writePush("constant", numClassVars);
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop("pointer", 0);
        }
        else{
            vmWriter.writeFunction(fileName + "." + functionName, numLocals);
        }
        compileStart("statements");
        compileStatements();
        compileEnd("statements");
        writeTag();
    }

    public void compileVarDec() throws IOException{
        System.out.println("compileVarDec");
        writeTag();
        writeUntil(";");
        tr.advance();
    }

    public void compileStatements() throws IOException{
        System.out.println("compileStatements");
        while (tr.getCurrentToken().equals("let") || 
        tr.getCurrentToken().equals("if") || 
        tr.getCurrentToken().equals("while") || 
        tr.getCurrentToken().equals("do") ||
        tr.getCurrentToken().equals("return")){
            if (tr.getCurrentToken().equals("let")){
                compileStart("letStatement");
                compileLet();
                compileEnd("letStatement");
            }
            else if (tr.getCurrentToken().equals("if")){
                compileStart("ifStatement");
                compileIf();
                compileEnd("ifStatement");
            }
            else if (tr.getCurrentToken().equals("while")){
                compileStart("whileStatement");
                compileWhile();
                compileEnd("whileStatement");
            }
            else if (tr.getCurrentToken().equals("do")){
                compileStart("doStatement");
                compileDo();
                compileEnd("doStatement");
            }
            else if (tr.getCurrentToken().equals("return")){
                compileStart("returnStatement");
                compileReturn();
                compileEnd("returnStatement");
            }
        }
    }

    public void compileDo() throws IOException{
        System.out.println("compileDo");
        writeTag();
        tr.advance();
        String doClass = tr.getCurrentToken();
        writeTag();
        tr.advance();
        if (tr.getCurrentToken().equals("(")){
            writeTag();
            tr.advance();
            vmWriter.writePush("pointer", 0);
            compileStart("expressionList");
            compileExpressionList();
            compileEnd("expressionList");
            writeTag();
            vmWriter.writeCall(fileName+ "." + doClass, numArgs + 1);
        }
        else if (tr.getCurrentToken().equals(".")){
            writeTag();
            tr.advance();
            String doMethod = tr.getCurrentToken();
            if (!Character.isUpperCase(doClass.charAt(0))){
                String tokenKind = symbolTable.kindOf(doClass);
                if (tokenKind.equals("field")){
                    tokenKind = "this";
                    }
                int tokenIndex = symbolTable.indexOf(doClass);
                vmWriter.writePush(tokenKind, tokenIndex);
            }
            writeTag();
            tr.advance();
            writeTag();
            tr.advance();
            String arrayPush = tr.getCurrentToken();
            compileStart("expressionList");
            compileExpressionList();
            compileEnd("expressionList");
            writeTag(); 
            if (!Character.isUpperCase(doClass.charAt(0))){
                doClass = symbolTable.typeOf(doClass);
                vmWriter.writeCall(doClass + "." + doMethod, numArgs + 1);
            }
            else {
                vmWriter.writeCall(doClass + "." + doMethod, numArgs);
            }
        }
        vmWriter.writePop("temp", 0);
        writeUntil(";");
        tr.advance();
    }

    public void compileLet() throws IOException{
        //might need to edit this for array considerations
        System.out.println("compileLet");
        writeTag();
        tr.advance();
        writeTag();
        String firstToken = tr.getCurrentToken();
        String firstTokenKind = symbolTable.kindOf(firstToken);
        String firstTokenType = symbolTable.typeOf(firstToken);
        int firstTokenIndex = symbolTable.indexOf(firstToken);
        tr.advance();
        if (tr.getCurrentToken().equals("[")){
            inLet = true;
            writeTag();
            tr.advance();
            compileStart("expression");
            compileExpression();
            compileEnd("expression");
            writeTag();
            tr.advance();
            vmWriter.writePush(firstTokenKind, firstTokenIndex);
            vmWriter.writeArithmetic("add");
        }
        writeTag();
        tr.advance();
        compileStart("expression");
        compileExpression();
        compileEnd("expression");
        writeTag();
        //if (inLet){
         //   vmWriter.writePop("temp", 0);
          //  vmWriter.writePop("pointer", 1);
           // vmWriter.writePush("temp", 0);
            //vmWriter.writePop("that", 0);
            //inLet = false;
            //tr.advance();
            //return;
        //}
        if (inLet){
           vmWriter.writePop("temp", 0);
           vmWriter.writePop("pointer", 1);
           vmWriter.writePush("temp", 0);
           vmWriter.writePop("that", 0);
           inLet = false;
           tr.advance();
           return;
        }
        if (subroutineCategory.equals("constructor") && 
        !firstTokenKind.equals("static")){
            vmWriter.writePop("this", firstTokenIndex);
        }
        else {
            if (firstTokenKind.equals("field")){
                firstTokenKind = "this";
            }
            vmWriter.writePop(firstTokenKind, firstTokenIndex);
        }
        tr.advance();
    }

    public void compileWhile() throws IOException{
        System.out.println("compileWhile");
        writeTag();
        writeUntil("(");
        tr.advance();
        String label1 = fileName + "." + functionName + classLabel;
        classLabel += 1;
        String label2 = fileName + "." + functionName + classLabel;
        classLabel += 1;
        vmWriter.writeLabel(label1);
        compileStart("expression");
        compileExpression();
        compileEnd("expression");
        vmWriter.writeArithmetic("not");
        vmWriter.writeIf(label2);
        writeTag();
        writeUntil("{");
        tr.advance();
        if (!tr.getCurrentToken().equals("}")){
            compileStart("statements");
            compileStatements();
            compileEnd("statements");
        }
        vmWriter.writeGoTo(label1);
        vmWriter.writeLabel(label2);
        writeTag();
        tr.advance();
    }

    public void compileReturn() throws IOException{
        System.out.println("compileReturn");
        writeTag();
        tr.advance();
        if (!tr.getCurrentToken().equals(";")){
            compileStart("expression");
            compileExpression();
            compileEnd("expression");
        }
        if (functionType.equals("void")){
            vmWriter.writePush("constant", 0);
        }
        vmWriter.writeReturn();
        writeTag();
        tr.advance();
    }

    public void compileIf() throws IOException{
        System.out.println("compileIf");
        writeTag();
        writeUntil("(");
        tr.advance();
        compileStart("expression");
        compileExpression();
        compileEnd("expression");
        String label1 = fileName + "." + functionName + classLabel;
        classLabel += 1;
        String label2 = fileName + "." + functionName + classLabel;
        classLabel += 1;
        vmWriter.writeArithmetic("not");
        vmWriter.writeIf(label1);
        writeTag();
        writeUntil("{");
        tr.advance();
        compileStart("statements");
        compileStatements();
        compileEnd("statements");
        vmWriter.writeGoTo(label2);
        vmWriter.writeLabel(label1);
        writeTag();
        tr.advance();
        if (tr.getCurrentToken().equals("else")){
            System.out.println("Compile If-Else");
            writeTag();
            writeUntil("{");
            tr.advance();
            compileStart("statements");
            compileStatements();
            compileEnd("statements");
            writeTag();
            tr.advance();
            System.out.println("endof");
        }
        vmWriter.writeLabel(label2);
    }

    public void compileExpression() throws IOException{
        System.out.println("compileExpression");
        System.out.println(tr.getCurrentToken());
        compileStart("term");
        compileTerm();
        compileEnd("term");
        String c = tr.getCurrentToken();
        System.out.println(c.equals("|"));
        while (c.equals("+") || c.equals("-") || c.equals("*") || c.equals("/") ||
        c.equals("&amp;") || c.equals("|") || c.equals("&lt;") || c.equals("&gt;") 
        || c.equals("=")){
            String termOp = tr.getCurrentToken();
            writeTag();
            tr.advance();
            c = tr.getCurrentToken();
            compileStart("term");
            compileTerm();
            compileEnd("term");
            vmWriter.writeArithmetic(arithJackToVm.get(termOp));
        }
    }

    public void compileTerm() throws IOException{
        //System.out.println("compileTerm");
        //System.out.println(tr.getCurrentToken());
        if (tr.getCurrentToken().equals("(")){
            writeTag();
            tr.advance();
            compileStart("expression");
            compileExpression();
            compileEnd("expression");
            writeTag();
            tr.advance();
            return;
        }
        if (tr.getCurrentToken().equals("-") || tr.getCurrentToken().equals("~")){
            String unaryOp = tr.getCurrentToken();
            if (unaryOp.equals("-")){
                unaryOp = "neg";
            }
            else{
                unaryOp = "not";
            }
            writeTag();
            tr.advance();
            compileStart("term");
            compileTerm();
            compileEnd("term");
            vmWriter.writeArithmetic(unaryOp);
            return;
        }
        if (!tr.getTokenType().equals("IDENTIFIER")){
            if (tr.getTokenType().equals("INT_CONST")){
                vmWriter.writePush("constant", Integer.parseInt(tr.getCurrentToken()));
            }
            else if (tr.getTokenType().equals("STRING_CONST")){
                vmWriter.writePush("constant", tr.getCurrentToken().length());
                vmWriter.writeCall("String.new", 1);
                for (int i = 0; i < tr.getCurrentToken().length(); i++){
                    vmWriter.writePush("constant", 
                    charsToAscii.get(tr.getCurrentToken().substring(i, i + 1)));
                    vmWriter.writeCall("String.appendChar", 2);
                }
            }
            else if (tr.getTokenType().equals("KEYWORD")){
                if (tr.getCurrentToken().equals("true")){
                    vmWriter.writePush("constant", 1);
                    vmWriter.writeArithmetic("neg");
                }
                else if (tr.getCurrentToken().equals("false") 
                || tr.getCurrentToken().equals("null") ){
                    vmWriter.writePush("constant", 0);
                }
                else if (tr.getCurrentToken().equals("this")){
                    vmWriter.writePush("pointer", 0);
                }
            }
            writeTag();
            tr.advance();
            return;
        }
        if (tr.getTokenType().equals("IDENTIFIER")){
            writeTag();
            if (!symbolTable.typeOf(tr.getCurrentToken()).equals("NONE") 
            && (!symbolTable.typeOf(tr.getCurrentToken()).equals("Array"))){
                String token = tr.getCurrentToken();
                String tokenKind = symbolTable.kindOf(token);
                int tokenIndex = symbolTable.indexOf(token);
                if (tokenKind.equals("field")){
                    tokenKind = "this";
                }
                vmWriter.writePush(tokenKind, tokenIndex);
            }
            String letClass = tr.getCurrentToken();
            tr.advance();
            String token = tr.getCurrentToken();
            if (symbolTable.typeOf(letClass).equals("Array") && !token.equals("[")
             && !token.equals(".")  && !token.equals("(")){
                String tokenKind = symbolTable.kindOf(letClass);
                int tokenIndex = symbolTable.indexOf(letClass);
                vmWriter.writePush(tokenKind, tokenIndex);
            }
            if (token.equals("[")){
                writeTag();
                tr.advance();
                String tokenKind = symbolTable.kindOf(letClass);
                int tokenIndex = symbolTable.indexOf(letClass);
                //if (!inLet){
                //    vmWriter.writePush(tokenKind, tokenIndex);
                //}
                compileStart("expression");
                compileExpression();
                compileEnd("expression");
                //if (!inLet){
                //    vmWriter.writeArithmetic("add");
                //    vmWriter.writePop("pointer", 1);
                //    vmWriter.writePush("that", 0);
                //}
                vmWriter.writePush(tokenKind, tokenIndex);
                vmWriter.writeArithmetic("add");
                vmWriter.writePop("pointer", 1);
                vmWriter.writePush("that", 0);
                writeTag();
                tr.advance();
                return;
            }
            if (token.equals(".")){
                writeTag(); 
                tr.advance();
                String letMethod = tr.getCurrentToken();
                tr.advance();
                tr.advance();
                if (!Character.isUpperCase(letClass.charAt(0))){
                    String tokenKind = symbolTable.kindOf(letClass);
                    int tokenIndex = symbolTable.indexOf(letClass);
                    if (tokenKind.equals("field")){
                        tokenKind = "this";
                    }
                    vmWriter.writePush(tokenKind, tokenIndex);
                }
                compileStart("expressionList");
                compileExpressionList();
                compileEnd("expressionList");
                writeTag();
                tr.advance();
                if (!Character.isUpperCase(letClass.charAt(0))){
                    letClass = symbolTable.typeOf(letClass);
                    vmWriter.writeCall(letClass + "." + letMethod, numArgs + 1);
                }
                else {
                    vmWriter.writeCall(letClass + "." + letMethod, numArgs);
                }
                return;
            }
            if (token.equals("(")){
                writeTag();
                tr.advance();
                compileStart("expressionList");
                compileExpressionList();
                compileEnd("expressionList");
                writeTag();
                tr.advance();
                return;
            }
        }
    }

    public void compileExpressionList() throws IOException{
        System.out.println("compileExpressionList");
        numArgs = 0;
        if (!tr.getCurrentToken().equals(")")){
            numArgs = 1;
            compileStart("expression");
            compileExpression();
            compileEnd("expression");
            while (tr.getCurrentToken().equals(",")){
                numArgs += 1;
                writeTag();
                tr.advance();
                compileStart("expression");
                compileExpression();
                compileEnd("expression");
            }
        }
    }
    
    public void listClassEntries(){
        symbolTable.listClassEntries();
    }
    
    public void listSubroutineEntries(){
       symbolTable.listSubroutineEntries();
    }
}


package my_src;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.nio.file.Paths;
import syntaxtree.*;
import visitor.*;
import parser.*;


public class Main {
    public static void main (String [] args){
        if(args.length != 1){
            System.err.println("Usage: java Driver <inputFile>");
            System.exit(1);
        }
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(args[0]);
            MiniJavaParser parser = new MiniJavaParser(fis);
            SemanticCheckVisitor check = new SemanticCheckVisitor();
            Goal tree = parser.Goal();
			tree.accept(check);
			check.finalCheck();
        }
        catch(ParseException ex){
            System.out.println(ex.getMessage());
        }
        catch(FileNotFoundException ex){
            System.err.println(ex.getMessage());
        }
        catch(SemanticError ex){
            System.err.println(ex.getMessage());
        }
		catch(Exception e){
			System.out.println("Internal Error.");
		}
        finally{
            try{
                if(fis != null) fis.close();
            }
            catch(IOException ex){
                System.err.println(ex.getMessage());
            }
        }
    }
}
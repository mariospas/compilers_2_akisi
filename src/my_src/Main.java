package my_src;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

import parser.MiniJavaParser;
import parser.ParseException;
import syntaxtree.Goal;


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
		    Goal root = parser.Goal();
		    System.err.println("Program parsed successfully.");
		    //handle_main
		    Handle_main eval = new Handle_main();
		    root.accept(eval);
		    /*for (String name: eval.mainTable.keySet()){

	            String key =name.toString();
	            String value = eval.mainTable.get(name).toString();  
	            System.out.println(key + " " + value);  
	        } */
		    Set keysetMain = eval.mainTable.keySet();
		    System.out.println("Main args : " + keysetMain);
		    Set keysetClass = eval.Table.keySet();
		    System.out.println("Classes : " + keysetClass);
		    
		    //inside_class
		    Inside_class eval2 = new Inside_class(root,eval.Table);
		    
		    
        }
        catch(ParseException ex){
            System.out.println(ex.getMessage());
        }
        catch(FileNotFoundException ex){
            System.err.println(ex.getMessage());
        }
        catch(SemError ex){
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
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
        FileInputStream fis = null;
        System.out.println(args.length);
        for(int i=0;i < args.length;i++)
    	{
	        try{
	            fis = new FileInputStream(args[i]);
	            MiniJavaParser parser = new MiniJavaParser(fis);
			    Goal root = parser.Goal();
			    System.err.println("Program parsed successfully.");
			    //handle_main
			    Handle_main eval = new Handle_main();
			    root.accept(eval);
			    
			    //inside_class
			    //System.out.println("^^^^^^^^^^ before inside class ^^^^^^^^^^^^");
			    Inside_class eval2 = new Inside_class(root,eval.Table);
			    //System.out.println("^^^^^^^^^^ after inside class ^^^^^^^^^^^^");
			    
			    //inside_method
			    //System.out.println("^^^^^^^^^^ before inside methods ^^^^^^^^^^^^");
			    Inside_methods eval3 = new Inside_methods(root, eval2.DeclClasses, eval2.Table);
			    //System.out.println("^^^^^^^^^^ after inside methods ^^^^^^^^^^^^");
			    
			    //type check
			    //System.out.println("^^^^^^^^^^ before type checking ^^^^^^^^^^^^");
			    Type_check eval4 = new Type_check(root, eval3.DeclClasses, eval3.Table, eval.mainTable);
			    System.out.println("^^^File : "+args[i]+"   Success ^^^^^^^^^^");
	        }
	        catch(ParseException ex){
	        	System.out.print("^^^File : "+args[i]+"   ");
	            System.out.println(ex.getMessage());
	        }
	        catch(FileNotFoundException ex){
	            System.err.println(ex.getMessage());
	        }
	        catch(SemError ex){
	        	System.out.print("^^^File : "+args[i]+"   ");
	            System.out.println(ex.getMessage());
	        }
			catch(Exception e){
				System.out.print("^^^File : "+args[i]+"   ");
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
}
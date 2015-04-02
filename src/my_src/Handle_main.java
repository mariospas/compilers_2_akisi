package my_src;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.lang.Exception;

import syntaxtree.*;
import visitor.*;

public class Handle_main extends DepthFirstVisitor
{
	HashMap<String,String> Table = new HashMap<String,String>();
	
	HashMap<String,String> mainTable = new HashMap<String,String>();
	
	
	/* Constructor */
	public void visit(Goal n) throws Exception 
	{
		n.f0.accept(this);
	}
	
	
	//main handle
	/**
	 * f0 -> "class"
	 * f1 -> Identifier()
	 * f2 -> "{"
	 * f3 -> "public"
	 * f4 -> "static"
	 * f5 -> "void"
	 * f6 -> "main"
	 * f7 -> "("
	 * f8 -> "String"
	 * f9 -> "["
	 * f10 -> "]"
	 * f11 -> Identifier()
	 * f12 -> ")"
	 * f13 -> "{"
	 * f14 -> ( VarDeclaration() )*
	 * f15 -> ( Statement() )*
	 * f16 -> "}"
	 * f17 -> "}"
	 */
	public void visit(MainClass n) throws Exception
	{
		
		this.mainTable.put(n.f11.f0.toString(), "String");
		System.out.println(n.f11.f0.toString());
		for (String name: this.mainTable.keySet()){

            String key =name.toString();
            String value = this.mainTable.get(name).toString();  
            System.out.println(key + " " + value);  
        } 
		n.f14.accept(this);	
	}
	
	
	/**
	 * Grammar production:
	 * f0 -> "class"
	 * f1 -> Identifier()
	 * f2 -> "{"
	 * f3 -> ( VarDeclaration() )*
	 * f4 -> ( MethodDeclaration() )*
	 * f5 -> "}"
	 */
	
	
	
}
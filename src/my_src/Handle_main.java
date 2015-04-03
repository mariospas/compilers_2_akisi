package my_src;

import java.util.*;
import java.lang.Exception;

import my_src.Assume;
import syntaxtree.*;
import visitor.*;

public class Handle_main extends DepthFirstVisitor
{
	HashMap<String,String> Table = new HashMap<String,String>();
	
	HashMap<String,String> mainTable = new HashMap<String,String>();
	
	
	/* Constructor */
	public void visit(Goal n) throws Exception, SemError
	{
		n.f0.accept(this);
		n.f1.accept(this);
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
	public void visit(MainClass n) throws Exception, SemError
	{
		
		this.mainTable.put(n.f11.f0.toString(), "String");
		System.out.println(n.f11.f0.toString());
		/*for (String name: this.mainTable.keySet()){

            String key =name.toString();
            String value = this.mainTable.get(name).toString();  
            System.out.println(key + " " + value);  
        }*/ 
		n.f14.accept(this);	
		n.f15.accept(this);
	}
	
	/**
	 * Grammar production:
	 * f0 -> Type()
	 * f1 -> Identifier()
	 * f2 -> ";"
	 */
	public void visit(VarDeclaration n) throws Exception, SemError
	{
		Assume.assumeTrue(this.mainTable.containsKey(n.f1.f0.toString())) ;
		this.mainTable.put(n.f1.f0.toString(), n.f0.toString()); 
		System.out.println(n.f1.f0.toString());
	}
	
	//Class decl
	/**
	 * Grammar production:
	 * f0 -> "class"
	 * f1 -> Identifier()
	 * f2 -> "{"
	 * f3 -> ( VarDeclaration() )*
	 * f4 -> ( MethodDeclaration() )*
	 * f5 -> "}"
	 */
	public void visit(ClassDeclaration n) throws Exception, SemError
	{
		Assume.assumeTrue(this.Table.containsKey(n.f1.f0.toString())) ;
		this.Table.put(n.f1.f0.toString(), null); 
		System.out.println(n.f1.f0.toString());
	}
	
	//Class extends
	/**
	 * Grammar production:
	 * f0 -> "class"
	 * f1 -> Identifier()
	 * f2 -> "extends"
	 * f3 -> Identifier()
	 * f4 -> "{"
	 * f5 -> ( VarDeclaration() )*
	 * f6 -> ( MethodDeclaration() )*
	 * f7 -> "}"
	 */
	
	public void visit(ClassExtendsDeclaration n) throws Exception, SemError
	{
		Assume.assumeTrue(this.Table.containsKey(n.f1.f0.toString())) ;
		this.Table.put(n.f1.f0.toString(), n.f3.f0.toString()); 
		System.out.println(n.f1.f0.toString());
		this.PreDeclClass(n.f3.f0.toString());
	}
	
	//check class be defined before other class extends it
	public void PreDeclClass(String extendClass) throws Exception, SemError
	{
		boolean flag = false;  //no found class pre declared
		
		Set<String> keysetClass = this.Table.keySet();
		for(Iterator<String> it = keysetClass.iterator(); it.hasNext();)
		{
			String className = it.next().toString();
			if(className.equals(extendClass))
			{
				flag = true;     //declared
			}
		}
		Assume.assumeTrue(!flag);
	}
	
}
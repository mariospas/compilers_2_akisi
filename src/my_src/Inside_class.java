package my_src;

import java.util.*;
import java.lang.Exception;

import my_src.Assume;
import syntaxtree.*;
import visitor.*;

public class Inside_class extends DepthFirstVisitor
{
	HashMap<String,HashMap<String,Fun_or_Ident>> Table = new HashMap<String,HashMap<String,Fun_or_Ident>>();
	HashMap<String,Fun_or_Ident> temp = new HashMap<String,Fun_or_Ident>();
	HashMap<String,String> arg = new HashMap<String,String>();
	
	String className;
	String type;
	int count = 0;
	
	public void visit(Goal n) throws Exception, SemError
	{
		n.f0.accept(this);
		n.f1.accept(this);
	}
	//variables matter
	
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
		HashMap<String,Fun_or_Ident> temp = new HashMap<String,Fun_or_Ident>(); 
		this.temp = temp;
		
		//isos den xreiazete
		n.f1.accept(this);
		
		System.out.println("yeah3");
		this.className = n.f1.f0.toString();  
		n.f3.accept(this);
		n.f4.accept(this);
		this.Table.put(className, temp); //(ClassName,(Name,Fun_or_Ident))
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
		HashMap<String,Fun_or_Ident> temp = new HashMap<String,Fun_or_Ident>(); 
		this.temp = temp;
		
		//isos den xreiazete
		n.f1.accept(this);
		
		System.out.println("yeah2");
		this.className = n.f1.f0.toString(); 
		n.f5.accept(this);
		n.f6.accept(this);
		this.Table.put(className, temp); //(ClassName,(Name,Fun_or_Ident))
	}
	
	//var decl
	/**
	 * Grammar production:
	 * f0 -> Type()
	 * f1 -> Identifier()
	 * f2 -> ";"
	 */
	public void visit(VarDeclaration n) throws Exception, SemError
	{
		Fun_or_Ident foi = new Fun_or_Ident();
		
		//isos den xreiazontai
		n.f0.accept(this);
		n.f1.accept(this);
		
		foi.function = false;
		n.f0.accept(this);
		foi.Type = this.type;
		System.out.println(foi.Type);
		//System.out.println("yeah1");
		
		System.out.println(n.f1.f0.toString());
		Assume.assumeTrue(this.temp.containsKey(n.f1.f0.toString()));
		this.temp.put(n.f1.f0.toString(), foi); //(name,foi)
	}
	
	
	//methods matter
	/**
	 * Grammar production:
	 * f0 -> "public"
	 * f1 -> Type()
	 * f2 -> Identifier()
	 * f3 -> "("
	 * f4 -> ( FormalParameterList() )?
	 * f5 -> ")"
	 * f6 -> "{"
	 * f7 -> ( VarDeclaration() )*
	 * f8 -> ( Statement() )*
	 * f9 -> "return"
	 * f10 -> Expression()
	 * f11 -> ";"
	 * f12 -> "}"
	**/
	
	public void visit(MethodDeclaration n) throws Exception, SemError
	{
		HashMap<String,String> arg = new HashMap<String,String>(); 
		this.arg = arg;
		
		Fun_or_Ident foi = new Fun_or_Ident();
		this.count = 0;
		
		foi.function = true;
		n.f0.accept(this);   //na balo ena global type
		foi.Type = this.type;
		
		n.f4.accept(this);
		foi.numOfArgs = this.count;
		foi.arg = this.arg;
		
		System.out.println("#"+n.f2.f0.toString());
		Assume.assumeTrue(this.temp.containsKey("*"+n.f2.f0.toString()));
		this.temp.put("#"+n.f2.f0.toString(), foi); //(name,foi)
	}
	
	
	/**
	 * Grammar production:
	 * f0 -> Type()
	 * f1 -> Identifier()
	**/
	public void visit(FormalParameter n) throws Exception, SemError
	{
		this.count++;
		Assume.assumeTrue(this.arg.containsKey(n.f1.f0.toString()));
		n.f0.accept(this);
		//type
		this.arg.put(n.f1.f0.toString(), type); // <Name,Type>		
	}
	
	
	//Now types
	/**
	 * Grammar production:
	 * f0 -> ArrayType()
	 *       | BooleanType()
	 *       | IntegerType()
	 *       | Identifier()
	**/
	public void visit(Type n) throws Exception, SemError
	{
		n.f0.accept(this);
		//return n.f0.accept(this,null);
	}
	
	/**
	 * Grammar production:
	 * f0 -> "int"
	 * f1 -> "["
	 * f2 -> "]"
	**/
	public void visit(ArrayType n) throws Exception, SemError
	{	
		this.type = n.f0.toString()+n.f1.toString()+n.f2.toString();
		//return n.f0.toString()+n.f1.toString()+n.f2.toString();
	}
	
	/**
	 * Grammar production:
	 * f0 -> "boolean"
	 */	
	public void visit(BooleanType n) throws Exception, SemError
	{	
		this.type = n.f0.toString();
		//return n.f0.toString();
	}
	
	/**
	 * Grammar production:
	 * f0 -> "int"
	**/
	public void visit(IntegerType n) throws Exception, SemError
	{	
		this.type = n.f0.toString();
		//return n.f0.toString();
	}
	
	/**
	 * Grammar production:
	 * f0 -> IDENTIFIER;
	**/
	public void visit(Identifier n) throws Exception, SemError
	{	
		this.type = n.f0.toString();
		//return n.f0.toString();
	}
	
	
}
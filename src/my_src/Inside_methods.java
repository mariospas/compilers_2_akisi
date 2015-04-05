package my_src;

import java.util.*;
import java.lang.Exception;

import my_src.Assume;
import syntaxtree.*;
import visitor.*;

public class Inside_methods extends DepthFirstVisitor
{
	HashMap<String,String> DeclClasses;  //classname,extend class 
	HashMap<String,HashMap<String,Fun_or_Ident>>  Table; //classname,fun or var name and foi
	boolean isclass;
	boolean isthis;
	boolean isnew;
	String className;
	String function;
	String primaryExpr;
	
	public Inside_methods(Goal n, HashMap<String,String> DecClasses,HashMap<String,HashMap<String,Fun_or_Ident>>  Table1) throws Exception, SemError
	{
		DeclClasses = DecClasses;
		Table = Table1;
		isclass = false;   //flag in order to ignore mainclass
		isthis = false;    //understand if this expression
		isnew = false;    //understand if new expression
		n.f0.accept(this);
		n.f1.accept(this);
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
	public void visit(ClassDeclaration n) throws Exception, SemError
	{
		this.isclass = true;
		this.className = n.f1.f0.toString();
		n.f4.accept(this);
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
		this.isclass = true;
		this.className = n.f1.f0.toString();
		n.f6.accept(this);
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
		this.function = "#"+n.f2.f0.toString();
		n.f8.accept(this);
		n.f10.accept(this);
	}
	
	
	/**
	 * Grammar production:
	 * f0 -> IntegerLiteral()
	 *       | TrueLiteral()
	 *       | FalseLiteral()
	 *       | Identifier()
	 *       | ThisExpression()
	 *       | ArrayAllocationExpression()
	 *       | AllocationExpression()
	 *       | NotExpression()
	 *       | BracketExpression()
	**/
	public void visit(PrimaryExpression n) throws Exception, SemError
	{
		n.f0.accept(this);
		if(this.primaryExpr == "this")
		{
			this.isthis = true;
		}
	}
	
	
	/**
	 * Grammar production:
	 * <PRE>
	 * f0 -> PrimaryExpression()
	 * f1 -> "."
	 * f2 -> Identifier()
	 * f3 -> "("
	 * f4 -> ( ExpressionList() )?
	 * f5 -> ")"
	 * </PRE>
	**/
	public void visit(MessageSend n) throws Exception, SemError
	{
		return;
	}
	
	/**
    * f0 -> "new"
    * f1 -> Identifier()
    * f2 -> "("
    * f3 -> ")"
    */
	public void visit(AllocationExpression n) throws Exception, SemError
	{
		this.isnew = true;
		n.f1.accept(this);
	}
	
	/**
	 * Grammar production:
	 * f0 -> "this"
	**/
	public void visit(ThisExpression n) throws Exception, SemError
	{
		this.isthis = true;
		this.primaryExpr = "this";
	}
	
	/**
	 * Grammar production:
	 * f0 -> "("
	 * f1 -> Expression()
	 * f2 -> ")"
	**/
	public void visit(BracketExpression n) throws Exception, SemError
	{
		this.isthis = false;
		n.f1.accept(this);
	}
	
	/**
	 * Grammar production:
	 * f0 -> IDENTIFIER;
	**/	
	public void visit(Identifier n) throws Exception, SemError
	{
		if(this.isclass)
		{
			//check if exist constructor
			if(this.isnew)
			{
				this.isnew = false;
				Assume.assumeTrue(!(this.DeclClasses.containsKey(n.f0.toString())));
				return;
			}
			
			//check all
			HashMap<String,Fun_or_Ident> func = this.Table.get(this.className);
			Fun_or_Ident foi = func.get(this.function);
			
			if(this.isthis == false)
			{
				if(foi.var.containsKey(n.f0.toString())) return;
				else if(foi.arg.containsKey(n.f0.toString())) return;
				else if(func.containsKey(n.f0.toString())) return;
				else
				{
					boolean flag = false;
					String extendedClass = this.DeclClasses.get(this.className);
					while(extendedClass != null)
					{
						func = this.Table.get(extendedClass);
						if(func.containsKey(n.f0.toString()))
						{
							flag = true;
							break;
						}
						extendedClass = this.DeclClasses.get(extendedClass);
					}
					Assume.assumeTrue(flag == false);
					
				}
			}
			else
			{
				this.isthis = false;
				Assume.assumeTrue(!func.containsKey("#"+n.f0.toString()));
			}
			
			this.isthis = false;
			return;
			
		}
	}
}
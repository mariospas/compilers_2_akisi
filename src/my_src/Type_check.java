package my_src;

import java.util.*;
import java.lang.Exception;

import my_src.Assume;
import syntaxtree.*;
import visitor.*;

public class Type_check extends DepthFirstVisitor
{
	HashMap<String,String> mainTable;  //var name, type
	HashMap<String,String> DeclClasses; //class name,extend class
	HashMap<String,HashMap<String,Fun_or_Ident>> Table;  //class name, fun or var name, more details
	boolean isclass;
	boolean isthis;
	boolean isnew;
	String className;
	String function;
	String primaryExpr;
	String type;
	ArrayList<String> arg;  //arguments with order of insertion
	
	
	
	public Type_check(Goal n, HashMap<String,String> DecClasses,HashMap<String,HashMap<String,Fun_or_Ident>>  Table1, HashMap<String,String> Main) throws Exception, SemError
	{
		DeclClasses = DecClasses;
		Table = Table1;
		mainTable = Main;
		isclass = false;   //flag in order to ignore mainclass
		isthis = false;    //understand if this expression
		isnew = false;    //understand if new expression
		n.f0.accept(this);
		n.f1.accept(this);
	}
	
	
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
	**/
	public void visit(MainClass n) throws Exception, SemError
	{
		this.className = "main";
		n.f15.accept(this);
	}
	
	/**
	 * f0 -> ClassDeclaration()
	 *       | ClassExtendsDeclaration()
	**/
	/*public void visit(TypeDeclaration n) throws Exception, SemError
	{
		n.f0.accept(this);
	}*/
	
	/**  
    * f0 -> "class"
    * f1 -> Identifier()
    * f2 -> "{"
    * f3 -> ( VarDeclaration() )*
    * f4 -> ( MethodDeclaration() )*
    * f5 -> "}"
    **/
	
	public void visit(ClassDeclaration n) throws Exception, SemError
	{
		this.isclass = true;
		this.className = n.f1.f0.toString();
		n.f4.accept(this);
	}
	
	/**
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
	
	/**
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
		n.f1.accept(this);    // keep type
		String type1 = this.type;
		
		System.out.println("#####  "+n.f0.toString());
		//System.out.println("Classname = "+this.className);
		System.out.println("Function = "+this.function);
		HashMap<String,Fun_or_Ident> func = this.Table.get(this.className);
		Fun_or_Ident foi = func.get(this.function);
		
		//collect argumnets with order
		this.arg = new ArrayList<>();
		n.f4.accept(this);
		
		Assume.assumeTrue(foi.arg.size() != this.arg.size());
		
		//arguments checks
		int i = 0;
		Set<String> arguments = foi.arg.keySet();
		for(Iterator<String> it = arguments.iterator(); it.hasNext();)
		{
			
			String arg_name = it.next().toString();
			Assume.assumeTrue(!(foi.arg.get(arg_name).equals(this.arg.get(i))));
			i++;
		}
		this.arg.clear();    //isos den xreiazete
		
		//statement
		n.f9.accept(this);
		
		//return type
		n.f10.accept(this);
		String return_type = this.type;
		
		Assume.assumeTrue(!(return_type.equals(type1)));
		
		
	}
	
	 /**
    * f0 -> FormalParameter()
    * f1 -> ( FormalParameterRest() )*
    */
	public void visit(FormalParameterList n) throws Exception, SemError
	{
		n.f0.accept(this);
		String type1 = this.type;
		
		n.f1.accept(this);
		
		this.type = type1;
	}
	
	/**
    * f0 -> Type()
    * f1 -> Identifier()
    */
	public void visit(FormalParameter n) throws Exception, SemError
	{
		n.f1.accept(this);
		String type1 = this.type;
		this.arg.add(type1);
		
		this.type = type1;
	}
	
	/**
	 * Grammar production:
	 * f0 -> ( FormalParameterTerm() )*
	 */
	public void visit(FormalParameterTail n) throws Exception, SemError
	{
		n.f0.accept(this);
	}
	
	/**
	 * Grammar production:
	 * f0 -> ","
	 * f1 -> FormalParameter()
	 */
	public void visit(FormalParameterTerm n) throws Exception, SemError
	{
		n.f1.accept(this);
	}
	
	
	
	
	
	
	/**
	 * f0 -> ArrayType()
	 *       | BooleanType()
	 *       | IntegerType()
	 *       | Identifier()
	 */
	public void visit(Type n) throws Exception, SemError
	{
		n.f0.accept(this);
	}
	
	
	/**
    * f0 -> "int"
    */
	public void visit(IntegerType n) throws Exception, SemError
	{
		this.type = "int";
	}
	
	/**
    * f0 -> "int"
    * f1 -> "["
    * f2 -> "]"
    */

	public void visit(ArrayType n) throws Exception, SemError
	{
		this.type = "intArray";
	}
	
	/**
    * f0 -> "boolean"
    */
	public void visit(BooleanType n) throws Exception, SemError
	{
		this.type = "boolean";
	}
	
	
	
	
	
	/**
    * f0 -> Block()
    *       | AssignmentStatement()
    *       | ArrayAssignmentStatement()
    *       | IfStatement()
    *       | WhileStatement()
    *       | PrintStatement()
    */
	public void visit(Statement n) throws Exception, SemError
	{
		n.f0.accept(this);
	}
	
	/**
    * f0 -> "{"
    * f1 -> ( Statement() )*
    * f2 -> "}"
    */
	public void visit(Block n) throws Exception, SemError
	{
		n.f1.accept(this);
	}
	
	/**
    * f0 -> Identifier()
    * f1 -> "="
    * f2 -> Expression()
    * f3 -> ";"
    */
	public void visit(AssignmentStatement n) throws Exception, SemError
	{
		n.f0.accept(this);
		String idType = this.type;
		n.f2.accept(this);
		String exprType = this.type;
		
		if(this.isnew)
		{
			this.isnew = false;
			Assume.assumeTrue(!this.DeclClasses.containsKey(exprType));
		}
		else if(!(idType.equals(exprType)))
		{
			if(exprType.equals("this"))
			{
				Assume.assumeTrue(!idType.equals(this.className));
			}
			else Assume.assumeTrue(true);
		}
		this.type = idType;
		
	}
	
	/**
    * f0 -> Identifier()
    * f1 -> "["
    * f2 -> Expression()
    * f3 -> "]"
    * f4 -> "="
    * f5 -> Expression()
    * f6 -> ";"
    */
	
	public void visit(ArrayAssignmentStatement n) throws Exception, SemError
	{
		n.f0.accept(this);
		String idType = this.type;
		n.f2.accept(this);
		String inExprType = this.type;
		n.f5.accept(this);
		String outExprType = this.type;
		
		
		Assume.assumeTrue(!idType.equals("intArray"));  
		Assume.assumeTrue(( !outExprType.equals("intArray") && !outExprType.equals("int") ));
		Assume.assumeTrue(( !inExprType.equals("intArray") && !inExprType.equals("int") ) );
	      
		this.type = idType;
	}
	
	/**
    * f0 -> "if"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    * f5 -> "else"
    * f6 -> Statement()
    */
	public void visit(IfStatement n) throws Exception, SemError
	{
		n.f2.accept(this);
		String exprType = this.type;
		
		Assume.assumeTrue(!exprType.equals("boolean"));
		
		n.f4.accept(this);
		n.f6.accept(this);
		
		this.type = "boolean";
	}
	
	/**
    * f0 -> "while"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> Statement()
    */
	public void visit(WhileStatement n) throws Exception, SemError
	{
		n.f2.accept(this);
		String exprType = this.type;
		
		Assume.assumeTrue(!exprType.equals("boolean"));
		
		n.f4.accept(this);
		
		this.type = "boolean";
	}
	
	/**
    * f0 -> "System.out.println"
    * f1 -> "("
    * f2 -> Expression()
    * f3 -> ")"
    * f4 -> ";"
    */
	public void visit(PrintStatement n) throws Exception, SemError
	{
		n.f2.accept(this);
	}
	
	
	
	
	
	
	/**
    * f0 -> AndExpression()
    *       | CompareExpression()
    *       | PlusExpression()
    *       | MinusExpression()
    *       | TimesExpression()
    *       | ArrayLookup()
    *       | ArrayLength()
    *       | MessageSend()
    *       | PrimaryExpression()
    */
	public void visit(Expression n) throws Exception, SemError
	{
		n.f0.accept(this);
		this.arg.add(this.type);
	}
	
	/**
    * f0 -> PrimaryExpression()
    * f1 -> "&&"
    * f2 -> PrimaryExpression()
    */
	public void visit(AndExpression n) throws Exception, SemError
	{
		n.f0.accept(this);
		String inExprType = this.type;
		n.f2.accept(this);
		String outExprType = this.type;
		
		Assume.assumeTrue(!inExprType.equals("boolean"));
		Assume.assumeTrue(!outExprType.equals("boolean"));
		
		this.type = "boolean";
	}
	
	/**
    * f0 -> PrimaryExpression()
    * f1 -> "&lt;"
    * f2 -> PrimaryExpression()
    */
	public void visit(CompareExpression n) throws Exception, SemError
	{
		n.f0.accept(this);
		String inExprType = this.type;
		n.f2.accept(this);
		String outExprType = this.type;
		
		Assume.assumeTrue(!inExprType.equals("int") && !inExprType.equals("intArray"));
		Assume.assumeTrue(!outExprType.equals("int") && !outExprType.equals("intArray"));
		
		this.type = "boolean";
	}
	
	/**
    * f0 -> PrimaryExpression()
    * f1 -> "+"
    * f2 -> PrimaryExpression()
    */
	public void visit(PlusExpression n) throws Exception, SemError
	{
		n.f0.accept(this);
		String inExprType = this.type;
		n.f2.accept(this);
		String outExprType = this.type;
		
		Assume.assumeTrue(!inExprType.equals("int") && !inExprType.equals("intArray"));
		Assume.assumeTrue(!outExprType.equals("int") && !outExprType.equals("intArray"));
		
		this.type = "int";
	}
	
	/**
    * f0 -> PrimaryExpression()
    * f1 -> "-"
    * f2 -> PrimaryExpression()
    */
	public void visit(MinusExpression n) throws Exception, SemError
	{
		n.f0.accept(this);
		String inExprType = this.type;
		n.f2.accept(this);
		String outExprType = this.type;
		
		Assume.assumeTrue(!inExprType.equals("int") && !inExprType.equals("intArray"));
		Assume.assumeTrue(!outExprType.equals("int") && !outExprType.equals("intArray"));
		
		this.type = "int";
	}
	
	/**
    * f0 -> PrimaryExpression()
    * f1 -> "*"
    * f2 -> PrimaryExpression()
    */
	public void visit(TimesExpression n) throws Exception, SemError
	{
		n.f0.accept(this);
		String inExprType = this.type;
		n.f2.accept(this);
		String outExprType = this.type;
		
		Assume.assumeTrue(!inExprType.equals("int") && !inExprType.equals("intArray"));
		Assume.assumeTrue(!outExprType.equals("int") && !outExprType.equals("intArray"));
		
		this.type = "int";
	}
	
	/**
    * f0 -> PrimaryExpression()
    * f1 -> "["
    * f2 -> PrimaryExpression()
    * f3 -> "]"
    */
	public void visit(ArrayLookup n) throws Exception, SemError
	{
		n.f0.accept(this);
		String inExprType = this.type;
		n.f2.accept(this);
		String outExprType = this.type;
		
		Assume.assumeTrue(!inExprType.equals("intArray"));
		Assume.assumeTrue(!outExprType.equals("int") && !outExprType.equals("intArray"));
		
		this.type = "int";
	}
	
	/**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> "length"
    */
	public void visit(ArrayLength n) throws Exception, SemError
	{
		n.f0.accept(this);
		String inExprType = this.type;
		
		Assume.assumeTrue(!inExprType.equals("intArray"));
		
		this.type = "int";
	}
	
	/**
    * f0 -> PrimaryExpression()
    * f1 -> "."
    * f2 -> Identifier()
    * f3 -> "("
    * f4 -> ( ExpressionList() )?
    * f5 -> ")"
    */
	public void visit(MessageSend n) throws Exception, SemError
	{
		String clas;
		n.f0.accept(this);
		
		if(this.isthis != true)
		{
			Assume.assumeTrue(!this.DeclClasses.containsKey(this.type));
			clas = this.type;
		}
		else clas = this.className;
		
		String meth = "#"+n.f2.f0.toString();  //i have the method
		
		System.out.println("#####  "+meth);
		//System.out.println("Classname = "+this.className);
		HashMap<String,Fun_or_Ident> func = this.Table.get(clas);
		Fun_or_Ident foi = func.get(meth);
		
		//collect argumnets with order
		this.arg = new ArrayList<String>();
		n.f4.accept(this);
		
		Assume.assumeTrue(foi.arg.size() != this.arg.size());
		
		//arguments checks
		int i = 0;
		Set<String> arguments = foi.arg.keySet();
		for(Iterator<String> it = arguments.iterator(); it.hasNext();)
		{
			
			String arg_name = it.next().toString();
			String type1 = foi.arg.get(arg_name);
			String argFromList = this.arg.get(i);
			if(!type1.equals(argFromList))
			{
				if(argFromList == "this")
				{
					if(type1.equals(this.className))
					{
						this.arg.clear();
						this.type = foi.Type;
						return;
					}
					else //father-extend
					{
						boolean flag = true;
						String extendname = this.DeclClasses.get(this.className);
						while(extendname != null)
						{
							if(type.equals(extendname))
							{
								this.arg.clear();
								this.type = foi.Type;
								flag = false;
								break;
							}
							extendname = this.DeclClasses.get(extendname);
						}
						Assume.assumeTrue(flag);
					}
				}
				String extend = this.DeclClasses.get(argFromList);
				boolean flag2 = true;
				while(extend != null)
				{
					extend = this.DeclClasses.get(argFromList);
					if(type1.equals(extend))
					{
						this.arg.clear();
						this.type = foi.Type;
						flag2 = false;
						break;
					}
					extend = this.DeclClasses.get(extend);
				}
				Assume.assumeTrue(flag2);

			}
			i++;
		}
		this.arg.clear();
		this.type = foi.Type;
		
	}
	
	
	/**
	 * Grammar production:
	 * f0 -> Expression()
	 * f1 -> ExpressionTail()
	 */
	public void visit(ExpressionList n) throws Exception, SemError
	{
		n.f0.accept(this);
		n.f1.accept(this);
	}
	
	/**
	 * Grammar production:
	 * f0 -> ( ExpressionTerm() )*
	 */
	public void visit(ExpressionTail n) throws Exception, SemError
	{
		n.f0.accept(this);
	}
	
	/**
	 * Grammar production:
	 * f0 -> ","
	 * f1 -> Expression()
	 */
	public void visit(ExpressionTerm n) throws Exception, SemError
	{
		n.f1.accept(this);
	}
	
	
	
	
	
	/**
    * f0 -> IntegerLiteral()
    *       | TrueLiteral()
    *       | FalseLiteral()
    *       | Identifier()
    *       | ThisExpression()
    *       | ArrayAllocationExpression()
    *       | AllocationExpression()
    *       | NotExpression()
    *       | BracketExpression()
    */
	public void visit(PrimaryExpression n) throws Exception, SemError
	{
		n.f0.accept(this);
	}
	
	/**
    * f0 -> &lt;INTEGER_LITERAL&gt;
    */
	public void visit(IntegerLiteral n) throws Exception, SemError
	{
		this.type = "int";
	}
	
	/**
    * f0 -> "true"
    */
	public void visit(TrueLiteral n) throws Exception, SemError
	{
		this.type = "boolean";
	}
	
	/**
    * f0 -> "false"
    */
	public void visit(FalseLiteral n) throws Exception, SemError
	{
		this.type = "boolean";
	}
	
	/**
    * f0 -> "this"
    */
	public void visit(ThisExpression n) throws Exception, SemError
	{
		this.type = "this";
	}
	
	/**
    * f0 -> "new"
    * f1 -> "int"
    * f2 -> "["
    * f3 -> Expression()
    * f4 -> "]"
    */
	public void visit(ArrayAllocationExpression n) throws Exception, SemError
	{
		this.type = "intArray";
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
    * f0 -> "!"
    * f1 -> Expression()
    */
	public void visit(NotExpression n) throws Exception, SemError
	{
		n.f1.accept(this);
	}
	
	/**
    * f0 -> "("
    * f1 -> Expression()
    * f2 -> ")"
    */
	public void visit(BracketExpression n) throws Exception, SemError
	{
		n.f1.accept(this);
	}
	
	
	
	
	/* Identifier */
	   
   /**
    * f0 -> &lt;IDENTIFIER&gt;
    */
	public void visit(Identifier n) throws Exception, SemError
	{
		String var = n.f0.toString();
		String type = null;
		
		if(this.isclass == false)
		{
			type = this.mainTable.get(var);
		}
		else if(this.isclass == true)
		{
			HashMap<String,Fun_or_Ident> func = this.Table.get(this.className);
			Fun_or_Ident foi = func.get(this.function);
			Fun_or_Ident foi2 = func.get(var);
			
			if(foi.var.containsKey(var))
			{
				type = foi.var.get(var);
			}
			else if(foi.arg.containsKey(var))
			{
				type = foi.arg.get(var);
			}
			else if(foi2 != null && foi2.function == false)
			{
				type = foi2.Type;
			}
			else
			{
				boolean flag = false;
				String extendedClass = this.DeclClasses.get(this.className);
				while(extendedClass != null)
				{
					//System.out.println(extendedClass);
					func = this.Table.get(extendedClass);
					if(func.containsKey(n.f0.toString()))
					{
						foi = func.get(var);
						if(foi.function == false) type = foi.Type;
						flag = true;
						break;
					}
					extendedClass = this.DeclClasses.get(extendedClass);
				}
			}
			
			if(type == null)
			{
				type = n.f0.toString();
			}
		}
		this.type = type;
	}
	
}
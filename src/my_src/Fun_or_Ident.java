package my_src;

import java.util.HashMap;
import java.util.List;


public class Fun_or_Ident
{
	boolean function;   //true if function false if identifier
	String Type;         // identifier(int,float,etc) or function's return type
	int numOfArgs;     //if function
	HashMap<String,String> arg = new HashMap<String,String>();
	HashMap<String,String> var = new HashMap<String,String>();
	List<String> argTypes;
	
}
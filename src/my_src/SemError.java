package my_src;

class SemError extends Exception
{
	public String getMessage(){
		return "Semantic Error!";
	}
}
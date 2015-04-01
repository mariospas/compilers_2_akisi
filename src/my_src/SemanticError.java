package my_src;

class SemanticError extends Exception {
	
	private static final long serialVersionUID = 1L;

	public String getMessage(){
		return "Semantic Error.";
	}
}
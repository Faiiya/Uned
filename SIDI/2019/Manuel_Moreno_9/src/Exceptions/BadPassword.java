package Exceptions;

public class BadPassword extends Exception{
	private static final long serialVersionUID = 1L;
	
	public BadPassword(String s) 
	{ 
		super(s); 
	} 
}

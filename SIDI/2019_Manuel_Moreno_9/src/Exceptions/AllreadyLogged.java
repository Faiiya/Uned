package Exceptions;

public class AllreadyLogged extends Exception{
	private static final long serialVersionUID = 1L;
	
	public AllreadyLogged(String s) 
	{ 
		super(s); 
	} 
} 


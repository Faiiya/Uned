package Exceptions;

public class AllreadyFollowed extends Exception{ 
	private static final long serialVersionUID = 1L;
	
	public AllreadyFollowed(String s) 
	{ 
		super(s); 
	} 
} 

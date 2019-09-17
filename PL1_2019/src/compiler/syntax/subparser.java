package compiler.syntax;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.lang.reflect.Constructor;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import compiler.lexical.Token;
import es.uned.lsi.compiler.lexical.ScannerIF;
import java_cup.runtime.Symbol;

public class subparser extends parser{
	//Insertar aqui la extensión de los ficheros de tests
	public static String extension=".pl1";
	
	public subparser(ScannerIF s) {super(s);}
	String msg="";
	  public void debug_message(String mess)
	  {
			try {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				PrintStream ps = new PrintStream(baos, true, "utf-8");
			    PrintStream err= System.err;
			    PrintStream out= System.out;
			    System.setErr(ps);
			    System.setOut(ps);
				System.out.println(mess);
				System.setErr(err);
			    System.setOut(out);
				msg+=new String(baos.toByteArray(), StandardCharsets.UTF_8);
			    
			} catch (Exception e) {
				e.printStackTrace();
			}
	  }
	  public String getMSG() {
		  return msg;
	  }
	  public void syntax_error(Symbol symbol)
	  { 
		  Token token = (Token) symbol.value;
		  msg+=token+" Error sintactico\n";	    
	  }
			
	  public void unrecovered_syntax_error(java_cup.runtime.Symbol symbol)
	  {	
		  Token token = (Token) symbol.value;
		  msg+=token+" Error fatal\n";	
	  }	  public static void main(String[] args) throws Exception {
		  String input="/home/faiya/eclipse-workspace/ArquitecturaPLI-2018-2019/doc"+File.separator+"test"+File.separator;
		  String output=input+"debug"+File.separator;
		  File carpeta = new File(output);
		  carpeta.mkdir();
		  File folder = new File(input);
		  File[] listofFiles = folder.listFiles();
		  for(int i=0; i<listofFiles.length; i++) {
			  String fileName=listofFiles[i].getName();
			  if(fileName.endsWith(extension)) {
				  String fileDebug=fileName.replace(extension,".debug");
				  FileReader aFileReader = new FileReader (input+fileName);
			      Class scannerClass = Class.forName ("compiler.lexical.Scanner"); 
			      Constructor scannerConstructor = scannerClass.getConstructor(Reader.class);
			      ScannerIF aScanner = (ScannerIF) scannerConstructor.newInstance(aFileReader);
				  subparser s=new subparser(aScanner);
				  s.debug_parse();
				  System.out.println(listofFiles[i].getName());
				  FileWriter fileWriter = new FileWriter(output+fileDebug);
		          BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
		          bufferedWriter.write(s.getMSG());
		          bufferedWriter.close();
			  }
		  }
	  }
}
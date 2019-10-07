package compiler.lexical;
import compiler.syntax.sym;
import compiler.lexical.Token;
import es.uned.lsi.compiler.lexical.ScannerIF;
import es.uned.lsi.compiler.lexical.LexicalError;
import es.uned.lsi.compiler.lexical.LexicalErrorManager;

//Produced by MV tools 2019-08-01 18:15:29
// incluir aqui, si es necesario otras importaciones

%%

%public
%class Scanner
%char
%line
%column
%cup
%ignorecase


%implements ScannerIF
%scanerror LexicalError

// incluir aqui, si es necesario otras directivas
%{
 LexicalErrorManager lexicalErrorManager = new LexicalErrorManager ();
 private int commentCount = 0;
 int linecom=0;
 int columncom=0;
 int contadorstring=0;


 //Funcion para crear tokens

 Token createToken (int x)	{
 	Token token = new Token (x);
 	 token.setLine (yyline + 1);
 	 token.setColumn (yycolumn + 1);
 	 token.setLexema (yytext ());
 	 return token;
}

 LexicalError createError(String mensaje){
	LexicalError error = new LexicalError (mensaje);
	error.setLine (yyline + 1);
	error.setColumn (yycolumn + 1);
	error.setLexema (yytext ());
	lexicalErrorManager.lexicalError (mensaje);
	 return error;
}
%}  


//Declaracion de expresiones:

ESPACIO_BLANCO = [ \t\r\n\f]
ID = [a-zA-Z_][a-zA-Z0-9_]*
LineTerminator = \r\n|\f|\r|\n
lanecomment = "#".*{LineTerminator}
num = [0-9]+
string = \".*\"

//FIN Declaracion de expresiones:

%x COMMENT



%%

<YYINITIAL> 
{


//Declaracion de tokens:

	"("  				{return createToken (sym.PARL);}
	")"  				{return createToken (sym.PARR);}
	"*"  				{return createToken (sym.MULT);}
	"+"  				{return createToken (sym.PLUS);}
	"-"  				{return createToken (sym.MINUS);}
	"."  				{return createToken (sym.PUNTO);}
	".."				{return createToken (sym.PUNTOP);}
	":"  				{return createToken (sym.DOSP);}
	","  				{return createToken (sym.COMA);}
	";"  				{return createToken (sym.PCOMA);}
	"<"  				{return createToken (sym.LESS);}
	"="  				{return createToken (sym.ASIGN);}
	"=="  				{return createToken (sym.EQUALS);}
	"["					{return createToken (sym.BRACKETL);}
	"]"					{return createToken (sym.BRACKETR);}
	"Subprogramas"  	{return createToken (sym.SUBPROGRAMAS);}
	"booleano"  		{return createToken (sym.BOOLEANO);}
	"cierto"  			{return createToken (sym.CIERTO);}
	"comienzo"  		{return createToken (sym.COMIENZO);}
	"constantes"  		{return createToken (sym.CONSTANTES);}
	"de"  				{return createToken (sym.DE);}
	"devolver"  		{return createToken (sym.DEVOLVER);}
	"en"  				{return createToken (sym.EN);}
	"entero"  			{return createToken (sym.ENTERO);}
	"entonces"  		{return createToken (sym.ENTONCES);}
	"escribir"  		{return createToken (sym.ESCRIBIR);}
	"falso"  			{return createToken (sym.FALSO);}
	"fin si"			{return createToken (sym.FINSI);}
	"fin"  				{return createToken (sym.FIN);}
	"funcion"  			{return createToken (sym.FUNCION);}
	"if"  				{return createToken (sym.IF);}
	"no"  				{return createToken (sym.NO);}
	"para"  			{return createToken (sym.PARA);}
	"procedimiento"  	{return createToken (sym.PROCEDIMIENTO);}
	"programa"  		{return createToken (sym.PROGRAMA);}
	"si"  				{return createToken (sym.SI);}
	"sino"  			{return createToken (sym.SINO);}
	"tipos"  			{return createToken (sym.TIPOS);}
	"var"  				{return createToken (sym.VAR);}
    "variables"  		{return createToken (sym.VARIABLES);}
	"vector"  			{return createToken (sym.VECTOR);}
	"while"  			{return createToken (sym.WHILE);}
	"y"  				{return createToken (sym.Y);}
	{ID}  				{return createToken (sym.ID);}
	{num}  				{return createToken (sym.NUM);}
	{string}			{return createToken (sym.STRING);}
	{lanecomment} 		{}

//FIN Declaracion de tokens:

{ESPACIO_BLANCO}	{}


//Declaracion de errores:

[^] {
		LexicalError error = new LexicalError ();
		error.setLine (yyline + 1);
		error.setColumn (yycolumn + 1);
		error.setLexema (yytext ());
		lexicalErrorManager.lexicalError (error);
	}

//FIN Declaracion de errores:

 }



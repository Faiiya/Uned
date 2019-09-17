package compiler.syntax;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import es.uned.lsi.compiler.semantic.Scope;
import es.uned.lsi.compiler.semantic.ScopeIF;
import es.uned.lsi.compiler.semantic.ScopeManagerIF;
import es.uned.lsi.compiler.semantic.symbol.SymbolIF;
import es.uned.lsi.compiler.semantic.symbol.SymbolTableIF;
import es.uned.lsi.compiler.semantic.type.TypeIF;
import es.uned.lsi.compiler.semantic.type.TypeTableIF;

public class ScopeManagerWorker {
    public static void logSemantics (ScopeManagerIF scopeManager,ArrayList<String>tipos, ArrayList<String>syms)
    {
        tipos.add(logTypeTables (scopeManager));
        syms.add(logSymbolTables (scopeManager));
    }
    private static String logTypeTables (ScopeManagerIF scopeManager)
    {
        Iterator<ScopeIF> scopesIt = scopeManager.getOpenScopes().iterator ();
        //Que sentido tiene la lista dumped?
        List<TypeTableIF> dumpedTypeTables = new ArrayList<TypeTableIF> ();
        String cadena="";
        while (scopesIt.hasNext ())
        {
            Scope aScope = (Scope) scopesIt.next ();
            TypeTableIF aTypeTable = aScope.getTypeTable ();
            if (!dumpedTypeTables.contains (aTypeTable)) {
            	cadena+=logTypeTable (aTypeTable)+"\n";
            	
            }
        }
        return cadena;
    }
    private static String logTypeTable(TypeTableIF typeTable)
    {	
        Iterator<TypeIF> typesIt = typeTable.getTypes ().iterator ();
        String cadena="";
        while (typesIt.hasNext ())
        {
            TypeIF aType = (TypeIF) typesIt.next ();
            cadena+=aType.toString()+"\n";
        }
        return cadena;
    }
    private static String logSymbolTables (ScopeManagerIF scopeManager)
    {
        Iterator<ScopeIF> scopesIt = scopeManager.getOpenScopes().iterator ();
        String cadena="";
        while (scopesIt.hasNext ())
        {
            ScopeIF scope = (ScopeIF) scopesIt.next ();
            cadena+=logSymbolTable (scope.getSymbolTable ());
        }
        return cadena;
    }
    private static String logSymbolTable (SymbolTableIF symbolTable)
    {
        Iterator<SymbolIF> symbolsIt = symbolTable.getSymbols ().iterator ();
        String cadena="";
        while (symbolsIt.hasNext ())
        {
            SymbolIF aSymbol = (SymbolIF) symbolsIt.next ();
            cadena+=aSymbol.toString()+"\n";
        }
        return cadena;
    }
}

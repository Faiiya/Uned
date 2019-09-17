package Servidor;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import Common.ServicioAutenticacionInterface;
import Common.ServicioGestorInterface;
import Common.ServidorInterface;

/**
* Clase principal del servidor que contiene su main
* y lanza los servicios gestor y autenticacion
* ademas de poder controlarse por la linea de comandos
*
* @author  Manuel Moreno Martinez
* @author  mmoreno2223@alumno.uned.es
* @version 1.0
* @since   2019-3-6
*/
public class Servidor extends UnicastRemoteObject implements ServidorInterface{
	
	private static ServicioGestorImpl gestor;
	private static ServicioAutenticacionImpl auth;
	private static final long serialVersionUID = 1L;
	// consola
	private static Console console = System.console();
	private static BufferedReader reader = new BufferedReader(
			new InputStreamReader(System.in));
	
	protected Servidor() throws RemoteException {
		super();
	}
	
	public static void main(String[] args) throws Exception {		
		Registry registry = null;
		
		try {
			registry = LocateRegistry.createRegistry(ServidorInterface.PUERTO);
		}
		catch(ExportException e){
			registry = LocateRegistry.getRegistry(ServidorInterface.PUERTO);
		}
		int count=0;
		int max_tries=10;	
		boolean ok = false;
		while(!ok) {
			try 
			{
				// añadimos el servicio auth
				auth = new ServicioAutenticacionImpl();
				registry.rebind(ServicioAutenticacionInterface.NOMBRE_SERVICIO, auth);
				// añadimos el servicio gestor
				gestor = new ServicioGestorImpl();
				registry.rebind(ServicioGestorInterface.NOMBRE_SERVICIO, gestor);
				System.out.printf("[Info] Se ha iniciado el servidor correctamente.%n%n");
				ok = true;
			}
			catch(ConnectException e) 
			{
				System.out.print("[Error] No se ha podido conectar a la base de datos, reintentando en 5 segundos");
				for(int i=0; i<5;i++) {
					System.out.print(" .");
					Thread.sleep(1000);
				}
				System.out.println("");
				
				count++;
				System.out.printf("[Info] Intento %d de %d.%n",count,max_tries);
				if(count >= max_tries) {
					throw e;
				}			
			}
		}
			boolean on = true;
			
			while (on) {
				on = inicio_consola();
			}			
			System.out.println("[Info] Apagando servidor twitter");
			// removemos los servicios
			registry.unbind(ServicioGestorInterface.NOMBRE_SERVICIO);
			registry.unbind(ServicioAutenticacionInterface.NOMBRE_SERVICIO);
			UnicastRemoteObject.unexportObject(registry, true);
			
			System.out.println("[Info] Apagado con exito");
			System.exit(0);
		}	
	
	private static boolean inicio_consola() throws RemoteException {
		// menu de inicio
	    System.out.println("Elija la operación:");
	    System.out.println("1.- Información del Servidor.");
	    System.out.println("2.- Listar Usuarios Logeados.");
	    System.out.println("3.- Salir.");
	    String opt = leerConsola();
	    
		switch (opt) {
			case "1": info(); break;
			case "2": online_users();  break;	
			case "3": return false;
		}
		return true;
	}
	
	private static void info() {
		System.out.println("Servicios rmi del servidor:");
		String host = ServidorInterface.MAQUINA;
		int port = ServidorInterface.PUERTO;
		
		String url_auth = String.format("rmi://%s:%d/%s",host,port,ServicioAutenticacionInterface.NOMBRE_SERVICIO);
		String url_gestor = String.format("rmi://%s:%d/%s",host,port,ServicioGestorInterface.NOMBRE_SERVICIO);
		
		System.out.printf("%-25s:	 %s%n","ServicioAutenticacion",url_auth);
		System.out.printf("%-25s:	 %s%n%n","ServicioGestor",url_gestor);
	}
	
	private static void online_users() throws RemoteException {
		List<String> users = gestor.online_users();
		
		System.out.println();
		if(users.isEmpty()) {
			System.out.println("[Info] No hay ningun usuario conectado.");
		}
		else {
			System.out.println("[Info] Los usuarios conectados son los siguientes:");
			String separator = "";
			for(String usr:users) {
				System.out.printf("%s",separator+"@"+usr);
				separator = ", ";
			}
			System.out.println();
		}
		System.out.println();
	}
	
	private static String leerConsola() {	
		if (console != null) return console.readLine();
		
		try {
			return reader.readLine();
		} 
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

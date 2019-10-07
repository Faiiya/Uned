package Usuario;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.rmi.ConnectException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.ExportException;
import java.util.List;

import Common.CallbackUsuarioInterface;
import Common.ServicioAutenticacionInterface;
import Common.ServicioGestorInterface;
import Common.ServidorInterface;
import Common.Trino;
import Common.User;
import Exceptions.AllreadyFollowed;
import Exceptions.AllreadyLogged;
import Exceptions.BadPassword;
import Exceptions.NotFollowing;
import Exceptions.UserRegistered;
/**
* Clase principal del servidor que contiene su main
* y lanza los servicios de callback
* ademas de poder controlarse por la linea de comandos
* permitiendo utilizar la aplicacion
*
* @author  Manuel Moreno Martinez
* @author  mmoreno2223@alumno.uned.es
* @version 1.0
* @since   2019-3-6
*/
public class Usuario {
	
	private static ServicioAutenticacionInterface auth;
	private static ServicioGestorInterface gestor;
	// console
	private static Console console = System.console();
	private static BufferedReader reader = new BufferedReader(
			new InputStreamReader(System.in));
	private User usr = null;
	private static Registry registry = null;
	public static void main(String[] args) throws Exception {
		// cogemos los datos default de la interfaz del servidor
		String host = ServidorInterface.MAQUINA;
		int port = ServidorInterface.PUERTO;
		
		int count=0;
		int max_tries=10;
		boolean ok = false;
		while(!ok) {
			try {
				// creamos la url donde estara el servidor
				String url_gestor = String.format("rmi://%s:%d/%s",host,port,ServicioGestorInterface.NOMBRE_SERVICIO);
				gestor = (ServicioGestorInterface)Naming.lookup(url_gestor);
				// creamos la url para conectaros al servicio auth y nos conectamos
				String url_auth = String.format("rmi://%s:%d/%s",host,port,ServicioAutenticacionInterface.NOMBRE_SERVICIO);
				auth = (ServicioAutenticacionInterface)Naming.lookup(url_auth);
				System.out.printf("[Info] Se ha conectado al servidor perfectamente.%n%n");
				ok = true;
			}
			catch(ConnectException e) 
			{
				System.out.print("[Error] No se ha podido conectar al servidor, reintentando en 5 segundos");
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
		
		Usuario usuario = new Usuario();
		// inicia la consola de login
		usuario.inicio_consola();
		
		try {
			registry = LocateRegistry.createRegistry(CallbackUsuarioInterface.PUERTO);
		}
		catch (ExportException e) {
			registry = LocateRegistry.getRegistry(CallbackUsuarioInterface.PUERTO);
		}	
		
		usuario.inicia_callback();
		boolean on = true;
		// inicia el menu principal
		while(on) {
			on = usuario.menu_principal();
	    }
		usuario.apaga_callback();
		usuario.exit();
	   }
	
	/**
	 * metodo que apaga el servicio de callback de un usuario 
	 * y proceded a hacer logout
	 * @throws Exception
	 */
	private void apaga_callback() throws Exception {
		System.out.println(CallbackUsuarioInterface.NOMBRE_SERVICIO+"/"+usr.getNick());
		registry.unbind(CallbackUsuarioInterface.NOMBRE_SERVICIO+"/"+usr.getNick());
		auth.logout(this.usr);
	}
	
	/**
	 * metodo que inicia el servicio de callback de un usuario
	 * @throws RemoteException
	 */
	private void inicia_callback() throws RemoteException {
		// añadimos el servicio auth
		CallbackUsuarioImpl call = new CallbackUsuarioImpl();
		registry.rebind(CallbackUsuarioInterface.NOMBRE_SERVICIO+"/"+usr.getNick(), call);
		gestor.callback(usr.getNick());
	}

	/**
	 * Menu principal del programa para el usuario
	 * permite escoger las diferentes opciones
	 * @return boolean para indicar cuando el usuario quiere salir
	 * @throws RemoteException
	 */
	private boolean menu_principal() throws RemoteException {
		// opciones del menu
		System.out.println("");
		System.out.println("Elija la operación:");
		System.out.println("1.- Información del Usuario.");
		System.out.println("2.- Enviar Trino.");
		System.out.println("3.- Listar Usuarios del Sistema.");
		System.out.println("4.- Seguir a");
		System.out.println("5.- Dejar de seguir a.");
		System.out.println("6.- Borrar trino a los usuarios que todavía no lo han recibido");
		System.out.println("7.- Logout.");
		String opt = leerConsola();
	
		// switch con la opcion seleccionada
		switch (opt) {
			case "1": info(); break;
			case "2": envia_trino();break;		
			case "3": list_users(); break;
			case "4": follow(); break;
			case "5": unfollow(); break;
			case "6": borra_trino(); break;
			case "7": return false;
			default:System.out.println("[Error] Porfavor introduzca una opcion valida.");
		}
		return true;
	}
	
	/**
	 * metodo que permite a un usuario borrar un trino
	 * a los usuarios que no lo han recibido
	 * @throws RemoteException 
	 */
	private void borra_trino() throws RemoteException {
		System.out.println("Por favor eliga uno de los siguientes trinos:");
		List<Trino> trinos = gestor.lista_trinos(usr.getNick());
		for(Trino t:trinos) {
			System.out.printf("%d.- %s%n",trinos.indexOf(t),t.ObtenerTrino());
		}	
		int tmp = Integer.parseInt(leerConsola());
		gestor.borra_trino(trinos.get(tmp));
		System.out.printf("Se ha borrado con exito el trino %s",trinos.get(tmp).ObtenerTrino());
	}

	/**
	 * Metodo que permite a un usuario enviar un trino
	 * @throws RemoteException
	 */
	private void envia_trino() throws RemoteException {
		System.out.println("Introduzca el mensaje que quiere enviar:");
		String msg = leerConsola();	
		gestor.envia(new Trino(msg, usr.getNick()));
	}
	
	/**
	 * metodo que permite seguir a un usuario
	 * @throws RemoteException
	 */
	private void follow() throws RemoteException {
		System.out.println("Introduzca el nick del usuario al que quiere seguir:");
		String followed = leerConsola();	
		try {
			gestor.follow(usr.getNick(), followed);
		} catch (UserRegistered | AllreadyFollowed e) {
			System.out.println(e.getMessage());
			System.out.printf("%nIntentelo de nuevo %n%n");
		}

	}
	
	/**
	 * metodo para dejar de seguir a un usuario
	 * @throws RemoteException
	 */
	private void unfollow() throws RemoteException {
		System.out.println("Introduzca el nick del usuario al que quiere dejar de seguir:");
		String followed = leerConsola();	
		try {
			gestor.unfollow(usr.getNick(), followed);
		} catch (UserRegistered | NotFollowing e) {
			System.out.println(e.getMessage());
			System.out.printf("%nIntentelo de nuevo %n%n");
			unfollow();
		}
	}
	
	
	/**
	 * metodo que imprime por pantalla una lista de los usuarios registrados
	 * @throws RemoteException
	 */
	private void list_users() throws RemoteException {
		List<User> list_users = auth.list_users();		
		String separator = "";
		for(User usr:list_users) {
			System.out.printf("%s",separator+usr);
			separator = ", ";
		}
		System.out.println();
	}

	/**
	 * inicia la consola para hacer login o registrar
	 * @throws RemoteException
	 */
	private void inicio_consola() throws RemoteException {
		// menu de inicio
	    System.out.println("Elija la operación:");
	    System.out.println("1.- Registrar un nuevo usuario.");
	    System.out.println("2.- Hacer login.");
	    System.out.println("3.- Salir");
	    String opt = leerConsola();
	    
		switch (opt) {
			case "1": register(); break;
			case "2": login();  break;		
			case "3": exit(); break;
		}
	}
	
	/**
	 * metodo que imprime los servicios que tiene bindeados el usuario
	 * @throws RemoteException
	 */
	private void info() throws RemoteException {	
		String host = CallbackUsuarioInterface.MAQUINA;
		int port = CallbackUsuarioInterface.PUERTO;
		String[] names = registry.list();
		
		System.out.println("Servicios rmi del cliente:");
		for(String name:names) {
			System.out.printf("rmi://%s:%d/%s%n",host,port,name);
		}
		System.out.println();
	}
	
	/**
	 * metodo que permite registrar a un usuario
	 * @throws RemoteException
	 */
	private void register() throws RemoteException {	
		System.out.println("Introduzca su nombre:");
		String nombre = leerConsola();	
		System.out.println("Introduzca su nick:");
		String nick = leerConsola();
		System.out.println("Introduzca su contraseña:");
		String pswd = leerConsola();
		User usr = new User(nombre, nick, pswd);		
		try_register(usr);
	}
	
	private void try_register(User usr) throws RemoteException {
		try {
			auth.register(usr);
			this.usr = usr;
			System.out.println("usuario registrado con exito");
		} catch (UserRegistered e) {
			System.out.println(e.getMessage());
			retry_register(usr.getNombre(),usr.getContraseña());
		}
	}
	
	private void retry_register(String name, String pswd) throws RemoteException{
		System.out.println("Introduzca otro nick (Introduzca \"0\" para cancelar):");
		String nick = leerConsola();
		if (nick.equals("0"))
		{
			inicio_consola();
		}
		else {
			User usr = new User(name, nick, pswd);		
			try_register(usr);
		}	
	}
	
	
	private void login() throws RemoteException {	
		System.out.println("Introduzca su nick:");
		String nick = leerConsola();
		System.out.println("Introduzca su contraseña:");
		String pswd = leerConsola();
		printf("Logging...%n");
		User usr;
		try {
			usr = auth.login(nick,pswd);
			this.usr = usr;
			System.out.printf("[Info] Se ha loggeado correctamente %n%n 			bienvenido %s%n%n", usr.getNombre());
		} catch (BadPassword | AllreadyLogged | UserRegistered e) {
			System.out.println(e.getMessage());
			System.out.println("Intentelo de nuevo.");
			login();	
		}	
	}
	
	private void exit() throws RemoteException {	
		System.out.println("Se ha desconectado con exito");	
		System.exit(0);
	}
	
	private static void printf(String msg) {
		System.out.printf(msg);
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

package Database;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Common.CallbackUsuarioInterface;
import Common.ServicioDatosInterface;
import Common.Trino;
import Common.User;
import Exceptions.AllreadyFollowed;
import Exceptions.AllreadyLogged;
import Exceptions.BadPassword;
import Exceptions.NotFollowing;
import Exceptions.UserRegistered;

/**
* Clase principal del servidor que contiene su main
* y lanza el servicio de datos
* ademas de poder controlarse por la linea de comandos
*
* @author  Manuel Moreno Martinez
* @author  mmoreno2223@alumno.uned.es
* @version 1.0
* @since   2019-3-6
*/
public class Basededatos {
	//
	private static Registry registry;
	// consola
	private static Console console = System.console();
	private static BufferedReader reader = new BufferedReader(
			new InputStreamReader(System.in));
	
	private static ServicioDatosImpl datos;
	// mapa de todos los usuarios registrados
	private Map<String,User> lista_users = new HashMap<String, User>();
	// lista de los usuarios conectados
	private List<String> online_users = new ArrayList<String>();
	// mapa para los usuarios que siguen a otros
	private Map<String,List<String>> lista_seguidores = new HashMap<String, List<String>>();
	// mapa de los usuarios y sus trinos
	private Map<String,List<Trino>> trinos_users = new HashMap<String,List<Trino>>();
	// cola de trinos para usuarios desconectados
	private Map<String,List<Trino>> cola_trinos = new HashMap<String,List<Trino>>();
	
	protected Basededatos() throws RemoteException {
		super();
	}
	
	private boolean inicio_consola() throws RemoteException {
		// menu de inicio
	    System.out.println("Elija la operación:");
	    System.out.println("1.- Información de la Base de Datos.");
	    System.out.println("2.- Listar Usuarios Registrados.");
	    System.out.println("3.- Listar Trinos");
	    System.out.println("4.- Salir.");
	    String opt = leerConsola();
	    
		switch (opt) {
			case "1": info(); break;
			case "2": listar_registrados();  break;		
			case "3": listar_trinos(); break;
			case "4": return false;
		}
		return true;
	}
	
	/**
	 * metodo que imprime los servicios que tiene bindeados la base de datos
	 * @throws RemoteException
	 */
	private void info() throws RemoteException {	
		String host = ServicioDatosInterface.MAQUINA;
		int port = ServicioDatosInterface.PUERTO;
		String[] names = registry.list();
		
		System.out.println("Servicios rmi de la base de datos:");
		for(String name:names) {
			System.out.printf("rmi://%s:%d/%s%n",host,port,name);
		}
		System.out.println();
	}
	
	/**
	 * muestra los usuarios registrados por pantalla
	 */
	private void listar_registrados() {
		ArrayList<User> tmp = new ArrayList<User>(lista_users.values());
		
		System.out.println();
		if(tmp.isEmpty()) {
			System.out.println("[Info] No hay ningun usuario registrado");
		}
		else {
			String separator = "";
			for(User usr:tmp) {
				System.out.printf("%s",separator+usr);
				separator = ", ";
			}
		}
		System.out.println();
	}
	
	private void listar_trinos() {
		System.out.println();
		if(trinos_users.isEmpty()) {
			System.out.println("[Info] No hay ningun usuario registrado");
		}
		System.out.println("[Info] Mostrando trinos.");
		// recorre el mapa de usuarios trinos
		for (Map.Entry<String,List<Trino>> entry : trinos_users.entrySet()) {
			// mira si tiene trinos el usuario
			if(entry.getValue().isEmpty()) {
				System.out.printf("[Info] @%s no tiene ningun trino%n", entry.getKey());
			}
			// muestra los trinos por pantalla
			else {
				System.out.printf("Trinos de @%s:%n", entry.getKey());
				for(Trino t:entry.getValue()) {
					System.out.printf("\t>%s%n",t.ObtenerTrino());
				}
			}	
		}
		System.out.println();
	}
	
	/**
	 * metodo getter de los usuarios online
	 * @return una lista de nicks de los usuarios online
	 */
	public List<String> online_users(){
		return online_users;
	}
	
	
	public boolean register_user(User user) throws UserRegistered {
		
		if(lista_users.containsKey(user.getNick())) {
			throw new UserRegistered("Ya hay un usuario registrado con ese nick");
		}
	
		else if(lista_users.containsValue(user)){
			throw new UserRegistered("El usuario ya esta registrado con otro nick");
		}
		
		// añade el usuario a la lista de registrados y crea su lista de seguidores
		lista_users.put(user.getNick(), user);
		online_users.add(user.getNick());
		inicializa_listas(user.getNick());
		
		return true;
	}
	
	/**
	 * metodo que inicializa las listas de seguidores
	 * y la cola de trinos pendientes de un usuario
	 * @param nick , usuario que acaba de registrarse
	 */
	private void inicializa_listas(String nick) {
		List<String> seg = new ArrayList<String>();
		lista_seguidores.put(nick, seg);
		List<Trino> tmp = new ArrayList<Trino>();
		trinos_users.put(nick, tmp);
		tmp = new ArrayList<Trino>();
		cola_trinos.put(nick, tmp);
	}

	/**
	 * Metodo que permite a un usuario seguir a otro
	 * 
	 * @param following , el usuario que intenta seguir a otro
	 * @param followed , el usuario a seguir
	 * @throws UserRegistered
	 * @throws AllreadyFollowed , cuando ya se sigue a ese usuario
	 */
	public void follow(String following,String followed) throws UserRegistered, AllreadyFollowed {
		
		// comprueba que el usuario que se va a seguir existe
		if(!lista_users.containsKey(followed)) {
			System.out.println("[Error] Se ha intentado seguir a un usuario que no existe");
			throw new UserRegistered("No existe un usuario con nick: \""+followed+"\"");
		}
		
		List<String> seg = lista_seguidores.get(followed);
		
		// comprueba que no se siga ya al usuario
		if(seg.contains(following)) {
			System.out.println("[Error] Se ha intentado seguir a alguien que ya seguias");
			throw new AllreadyFollowed("Ya estas siguiendo al usuario con nick: \""+followed+"\"");
		}
		
		seg.add(following);
		System.out.printf("[Info] Usuario @%s ha seguido a @%s correctamente. %n",following, followed);
	}
	
	/**
	 * Metodo para dejar de seguir a un usuario
	 * 
	 * @param unfollowing usuario que quiere dejar de seguir
	 * @param followed usuario que recibe la accion
	 * @throws UserRegistered cuando followed no existe
	 * @throws NotFollowing cuando no sigues a followed
	 */
	public void unfollow(String unfollowing,String followed) throws UserRegistered, NotFollowing {
		
		// comprueba que el usuario que se va a seguir existe
		if(!lista_users.containsKey(followed)) {
			System.out.println("[Error] Se ha intentado deja de seguir a un usuario que no existe");
			throw new UserRegistered("No existe un usuario con nick: \""+followed+"\"");
		}
		
		List<String> seg = lista_seguidores.get(followed);
		
		// comprueba que no se siga ya al usuario
		if(!seg.contains(unfollowing)) {
			System.out.println("[Error] Se ha intentado dejar de seguir a alguien que no seguias");
			throw new NotFollowing("No sigues a ningun usuario con nick: \""+followed+"\"");
		}
		
		seg.remove(unfollowing);
		System.out.printf("[Info] Usuario @%s ha dejado de seguir a @%s correctamente. %n",unfollowing, followed);
	}
	
	/**
	 * metodo que hace logout a un usuario
	 * @param nick del usuario que hace logout
	 */
	public void logout(String nick) {
		if(online_users.contains(nick)) {
			online_users.remove(nick);
		}
	}

	/**
	 * Metodo que permite borrar un trino
	 * de la lista de pendientes de los usuarios
	 * a los cuales todavia no les ha llegado
	 * 
	 * @param t trino que hay que borrar
	 * @throws RemoteException
	 */
	public void borra_trino(Trino t){
		System.out.printf("[Info] Intentando borrar trino %s de @%s para sus seguidores%n",t,t.ObtenerNickPropietario());
		// obtiene una lista de seguidores del propietario del trino
		List<String> seguidores = lista_seguidores.get(t.ObtenerNickPropietario());
		// por cada seguidor, si su lista de trinos pendientes contiene el trino lo elimina
		for(String seg:seguidores) {
			System.out.printf("[Info] Cola de trinos del seguidor @%s:%s%n",seg,cola_trinos.get(seg));
			if(cola_trinos.get(seg).contains(t)){
				cola_trinos.get(seg).remove(t);
				System.out.printf("[Info] Borrado con exito%n",seg,cola_trinos.get(seg));
			}
			else {
				System.out.println("[Info] El trino no esta en la lista.");
			}
		}
	}
	
	/**
	 * Metodo que permite enviar un trino a los usuarios
	 * seguidores del dueño del trino
	 * si estan online se envia al momento utilizando el callback
	 * si estan offline se añaden a la lista de pendientes
	 * @param t trino que se quiere enviar
	 * @throws RemoteException
	 */
	public void envia_trino(Trino t) throws RemoteException {
		System.out.println("[Info] trino recibido");
		System.out.println("[Info] "+t);
		// añade el trino a la lista de trinos del usuario
		trinos_users.get(t.ObtenerNickPropietario()).add(t);
		
		String host = CallbackUsuarioInterface.MAQUINA;
		int port = CallbackUsuarioInterface.PUERTO;
		// coge la lista de seguidores del creador del trino
		List<String> lista= lista_seguidores.get(t.ObtenerNickPropietario());
		for(String seguidor:lista) {
			// si el seguidor esta online
			if(online_users.contains(seguidor)) {
				// url unica del usuario
				String url_user =String.format("rmi://%s:%d/%s",host,port,CallbackUsuarioInterface.NOMBRE_SERVICIO+"/"+seguidor);
				CallbackUsuarioInterface usercallback;
				try {
					// coge el callback y envia el trino al usuario online
					usercallback = (CallbackUsuarioInterface)Naming.lookup(url_user);
					usercallback.trino(t);
				} catch (MalformedURLException | NotBoundException e) {
					e.printStackTrace();
				}	
			}
			else {
				// si el usuario no esta conectado se añade el trino a la lista de pendientes
				List<Trino> pendientes = cola_trinos.get(seguidor);
				pendientes.add(t);
			}	
		}
	}
	
	
	public User getUser(String nick) {
		return lista_users.get(nick);
	}
	
	/**
	 * Permite a un usuario logearse en el sistema
	 * @param nick del usuario
	 * @param pswd del usuario
	 * @return User, usuario ya logeado
	 * @throws UserRegistered, cuando la contraseña es incorrecta o no existe alguien con ese nick
	 * @throws BadPassword 
	 */
	public User login(String nick, String pswd) throws UserRegistered, AllreadyLogged, BadPassword {
		if(lista_users.containsKey(nick)) {
			User usr = lista_users.get(nick);
			if(usr.getContraseña().equals(pswd)) {
				if(online_users.contains(nick)) {
					throw new AllreadyLogged("Ya hay un usuario online con esta cuenta.");
				}
				return usr;
			}
			else {
				throw new BadPassword("La contraseña es incorrecta, intentelo de nuevo.");
			}
		}
		else {
			throw new UserRegistered("No existe usuario con ese nick, intentelo de nuevo.");
		}
	}
	
	public List<User> get_users(){
		return new ArrayList<User>(lista_users.values());
	}
	
	public void callback(String nick) throws RemoteException {
		
		String host = CallbackUsuarioInterface.MAQUINA;
		int port = CallbackUsuarioInterface.PUERTO;
		String url_user =String.format("rmi://%s:%d/%s",host,port,CallbackUsuarioInterface.NOMBRE_SERVICIO+"/"+nick);
		CallbackUsuarioInterface usercallback;
		List<Trino> trinos = cola_trinos.get(nick);
		try {
			usercallback = (CallbackUsuarioInterface)Naming.lookup(url_user);
			usercallback.trinos(trinos);
		} catch (MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		}	
	}
	
	public List<Trino> lista_trinos(String nick) throws RemoteException{
		return trinos_users.get(nick);
	}
	
	public static void main(String[] args) throws Exception{
		Basededatos bd = new Basededatos();
		registry = LocateRegistry.createRegistry(ServicioDatosInterface.PUERTO);
		// añadimos el servicio de datos
		datos = new ServicioDatosImpl(bd);
		registry.rebind(ServicioDatosInterface.NOMBRE_SERVICIO, datos);

		System.out.println(registry.list().toString());
		System.out.println("Iniciado base de datos");
		boolean on = true;
		while(on) {
			on = bd.inicio_consola();
		}
		
		System.out.println("Apagando base de datos");
		// removemos los servicios
		registry.unbind(ServicioDatosInterface.NOMBRE_SERVICIO);
		UnicastRemoteObject.unexportObject(registry, true);
		
		System.out.println("Apagado con exito");
		System.exit(0);
		
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


package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import Exceptions.AllreadyFollowed;
import Exceptions.AllreadyLogged;
import Exceptions.BadPassword;
import Exceptions.NotFollowing;
import Exceptions.UserRegistered;

/**
* Interfaz del servicio de datos de la base de datos
*
* @author  Manuel Moreno Martinez
* @author  mmoreno2223@alumno.uned.es
* @version 1.0
* @since   2019-3-6
*/
public interface ServicioDatosInterface extends Remote{
	public static final String NOMBRE_SERVICIO = "Database";
	public static final String MAQUINA = "localhost";
	public static final int PUERTO = 8823;
	
	public void info() throws RemoteException;
	
	public void follow(String following, String followed) throws RemoteException, UserRegistered, AllreadyFollowed;
	
	public void unfollow(String unfollowing, String followed) throws RemoteException, UserRegistered, NotFollowing;
	
	public List<String> onlineUser() throws RemoteException;
	
	public void envia_trino(Trino t) throws RemoteException;
	
	public List<Trino> lista_trinos(String nick) throws RemoteException;
	
	public boolean register(User usr) throws RemoteException, UserRegistered;
	
	public User login(String nick, String pswd) throws RemoteException, BadPassword, AllreadyLogged, UserRegistered;
	
	public void logout(String nick) throws RemoteException;
	
	public void callback(String nick) throws RemoteException;

	public List<User> list_users() throws RemoteException;
	
	public void borra_trino(Trino t) throws RemoteException;
}

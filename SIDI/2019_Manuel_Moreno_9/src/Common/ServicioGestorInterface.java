package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import Exceptions.AllreadyFollowed;
import Exceptions.NotFollowing;
import Exceptions.UserRegistered;

/**
* Interfaz del gestor del servidor
*
* @author  Manuel Moreno Martinez
* @author  mmoreno2223@alumno.uned.es
* @version 1.0
* @since   2019-3-6
*/
public interface ServicioGestorInterface extends Remote{
	public static final String NOMBRE_SERVICIO = "gestor_service";
	
	public void envia(Trino trino) throws RemoteException;
	
	public void follow(String following, String followed) throws RemoteException, UserRegistered, AllreadyFollowed;
	
	public void unfollow(String unfollowing, String followed) throws RemoteException, UserRegistered, NotFollowing;
	
	public void callback(String nick) throws RemoteException;
	
	public void userInfo() throws RemoteException;
	
	public void borra_trino(Trino t) throws RemoteException;
	
	public List<String> online_users() throws RemoteException;
	
	public List<Trino> lista_trinos(String nick) throws RemoteException;
}

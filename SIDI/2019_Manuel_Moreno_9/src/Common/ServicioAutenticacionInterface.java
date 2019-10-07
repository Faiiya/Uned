package Common;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import Exceptions.AllreadyLogged;
import Exceptions.BadPassword;
import Exceptions.UserRegistered;
/**
* Interfaz del servicio ded autenticacion
*
* @author  Manuel Moreno Martinez
* @author  mmoreno2223@alumno.uned.es
* @version 1.0
* @since   2019-3-6
*/
public interface ServicioAutenticacionInterface extends Remote{
	public static final String NOMBRE_SERVICIO = "Auth_service";
	
	public User login(String nick, String pswd) throws RemoteException, BadPassword, AllreadyLogged, UserRegistered;
	
	public String register(User usr) throws RemoteException, UserRegistered;
	
	public void logout(User usr) throws RemoteException;

	public List<User> list_users() throws RemoteException;
	
}

package Database;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import Common.ServicioDatosInterface;
import Common.Trino;
import Common.User;
import Exceptions.AllreadyFollowed;
import Exceptions.AllreadyLogged;
import Exceptions.BadPassword;
import Exceptions.NotFollowing;
import Exceptions.UserRegistered;

/**
* Implementacion de la interfaz del servicio de datos
* de la base de datos
*
* @author  Manuel Moreno Martinez
* @author  mmoreno2223@alumno.uned.es
* @version 1.0
* @since   2019-3-6
*/
public class ServicioDatosImpl extends UnicastRemoteObject implements ServicioDatosInterface{
	
	private Basededatos database;
	
	protected ServicioDatosImpl(Basededatos db) throws RemoteException {
		super();
		this.database = db;
	}

	private static final long serialVersionUID = 1L;

	@Override
	public void info() throws RemoteException {
		
		
	}

	@Override
	public List<String> onlineUser() throws RemoteException {
		return database.online_users();	
	}

	@Override
	public List<User> list_users() throws RemoteException {
		return database.get_users();
	}

	@Override
	public void envia_trino(Trino t) throws RemoteException {
		database.envia_trino(t);
		
	}

	@Override
	public boolean register(User usr) throws RemoteException, UserRegistered {
		
		if( database.register_user(usr)){
			System.out.println("[Info] Se ha registrado con exito al usuario: @"+usr.getNick());
			return true;
		}
		else {
			System.out.println("[Error] No se ha podido registrar al usuario: "+usr.getNombre()+" + @"+usr.getNick());
			return false;
		}
		
	}

	@Override
	public User login(String nick, String pswd) throws RemoteException, BadPassword, AllreadyLogged, UserRegistered {
		User usr = null;
		try {
			usr = database.login(nick, pswd);
		} catch (UserRegistered e) {
			System.out.printf("[Error] Intento de login con un nick que no existe, @%s",nick);
			throw e;
		} catch (AllreadyLogged e) {
			System.out.printf("[Error] Intento de login con el usuario @%s ya conectado",nick);
			throw e;
		} catch (BadPassword e) {
			System.out.printf("[Error] Intento de login fallido para el usuario @%s",nick);
			throw e;
		}
		return usr;
	}

	@Override
	public void follow(String following, String followed) throws RemoteException, UserRegistered, AllreadyFollowed {
		database.follow(following, followed);	
	}

	@Override
	public void unfollow(String unfollowing, String followed) throws RemoteException, UserRegistered, NotFollowing {
		database.unfollow(unfollowing, followed);
	}

	@Override
	public void callback(String nick) throws RemoteException {
		database.callback(nick);		
	}

	@Override
	public void logout(String nick) throws RemoteException {
		database.logout(nick);
	}

	@Override
	public List<Trino> lista_trinos(String nick) throws RemoteException {
		return database.lista_trinos(nick);
	}

	@Override
	public void borra_trino(Trino t) throws RemoteException {
		database.borra_trino(t);
		
	}

}

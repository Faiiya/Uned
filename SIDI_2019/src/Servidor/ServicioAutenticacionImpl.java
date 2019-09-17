package Servidor;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import Common.ServicioDatosInterface;
import Common.User;
import Exceptions.AllreadyLogged;
import Exceptions.BadPassword;
import Exceptions.UserRegistered;

/**
* Implementacion del servicio de atenticacion
* del servidor
*
* @author  Manuel Moreno Martinez
* @author  mmoreno2223@alumno.uned.es
* @version 1.0
* @since   2019-3-6
*/
public class ServicioAutenticacionImpl extends UnicastRemoteObject implements Common.ServicioAutenticacionInterface{
	private static final long serialVersionUID = 1L;
	private static ServicioDatosInterface database;
	
	public ServicioAutenticacionImpl() throws RemoteException, MalformedURLException, NotBoundException{
		super();
		// cargamos la base de datos
		load_database();
	}

	private void load_database() throws NotBoundException, MalformedURLException, RemoteException {
		String host = ServicioDatosInterface.MAQUINA;
		int port = ServicioDatosInterface.PUERTO;
		String name = ServicioDatosInterface.NOMBRE_SERVICIO;
		// creamos la url donde estara el servidor
		String url_datos = String.format("rmi://%s:%d/%s",host,port,name);
		ServicioDatosInterface database = (ServicioDatosInterface) Naming.lookup(url_datos);
		ServicioAutenticacionImpl.database=database;
		
	}

	@Override
	public User login(String nick, String pswd) throws RemoteException, BadPassword, AllreadyLogged, UserRegistered {
		User usr =  database.login(nick, pswd);
		if(usr == null) {
			System.out.println("[Error] No hay un usuario registrado con ese nick.");
		}
		return usr;
	}

	@Override
	public String register(User user) throws RemoteException, UserRegistered {
		database.register(user);
		return user.getNombre();
	}

	@Override
	public void logout(User user) throws RemoteException {
		database.logout(user.getNick());
	}
	
	@Override
	public List<User> list_users() throws RemoteException {
		return database.list_users();
	}
	
	
}

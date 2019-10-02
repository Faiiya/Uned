package Servidor;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import Common.ServicioDatosInterface;
import Common.Trino;
import Exceptions.AllreadyFollowed;
import Exceptions.NotFollowing;
import Exceptions.UserRegistered;

/**
* Implementacion del servicio gestor del servidor
*
* @author  Manuel Moreno Martinez
* @author  mmoreno2223@alumno.uned.es
* @version 1.0
* @since   2019-3-6
*/
public class ServicioGestorImpl extends UnicastRemoteObject implements Common.ServicioGestorInterface{

	private static final long serialVersionUID = 1L;
	private static ServicioDatosInterface servicio_datos;
	
	protected ServicioGestorImpl() throws RemoteException, MalformedURLException, NotBoundException {
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
		ServicioDatosInterface tmp = (ServicioDatosInterface) Naming.lookup(url_datos);
		ServicioGestorImpl.servicio_datos=tmp;
	}

	@Override
	public void envia(Trino t) throws RemoteException {
		servicio_datos.envia_trino(t);
		
	}

	@Override
	public void follow(String following, String followed) throws RemoteException, UserRegistered, AllreadyFollowed {
		servicio_datos.follow(following, followed);
	}

	@Override
	public void unfollow(String unfollowing, String followed) throws RemoteException, UserRegistered, NotFollowing {
		servicio_datos.unfollow(unfollowing, followed);
	}

	@Override
	public void userInfo() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void callback(String nick) throws RemoteException {
		servicio_datos.callback(nick);
	}

	@Override
	public List<String> online_users() throws RemoteException {
		return servicio_datos.onlineUser();
	}

	@Override
	public List<Trino> lista_trinos(String nick) throws RemoteException {
		return servicio_datos.lista_trinos(nick);
	}

	@Override
	public void borra_trino(Trino t) throws RemoteException {
		servicio_datos.borra_trino(t);
	}

}

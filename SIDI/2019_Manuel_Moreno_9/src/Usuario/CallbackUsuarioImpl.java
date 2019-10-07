package Usuario;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;

import Common.CallbackUsuarioInterface;
import Common.Trino;

/**
* Implementazion de la interfaz del callback
*
* @author  Manuel Moreno Martinez
* @version 1.0
* @since   2019-3-6
*/
public class CallbackUsuarioImpl extends UnicastRemoteObject implements CallbackUsuarioInterface{

	private static final long serialVersionUID = 1L;

	public CallbackUsuarioImpl() throws RemoteException {
		super();
	}
	
	@Override
	public void trinos(List<Trino> trinos) throws RemoteException {
		System.out.println("");
		for(Trino t:trinos) {
			System.out.printf("> %s# %s%n",t.ObtenerNickPropietario(),t.ObtenerTrino());
		}	
		System.out.println("");
	}

	@Override
	public void trino(Trino t) throws RemoteException {
		System.out.printf("%n> %s# %s%n%n",t.ObtenerNickPropietario(),t.ObtenerTrino());	
	}

}

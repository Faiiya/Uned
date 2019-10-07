package Common;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;
/**
* Interfaz del callback del usuario
*
* @author  Manuel Moreno Martinez
* @author  mmoreno2223@alumno.uned.es
* @version 1.0
* @since   2019-3-6
*/
public interface CallbackUsuarioInterface extends Remote{
	public static final String NOMBRE_SERVICIO = "Callback";
	public static final String MAQUINA = "localhost";
	public static final int PUERTO = 8821;
	
	public void trinos(List<Trino> trinos) throws RemoteException;
	
	public void trino(Trino trino) throws RemoteException;
}

package Common;
import java.rmi.Remote;

/**
* Interfaz del servidor
* que contiene algunas variables
*
* @author  Manuel Moreno Martinez
* @author  mmoreno2223@alumno.uned.es
* @version 1.0
* @since   2019-3-6
*/
public interface ServidorInterface extends Remote{
	public static final String NOMBRE_SERVICIO = "ServidorTwitter";
	public static final String MAQUINA = "localhost";
	public static final int PUERTO = 8822;
	
}

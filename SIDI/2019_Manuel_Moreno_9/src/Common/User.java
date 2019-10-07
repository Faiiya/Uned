package Common;

import java.io.Serializable;

/**
* Clase que serializa al usuario
*
* @author  Manuel Moreno Martinez
* @version 1.0
* @since   2019-3-6
*/
public class User implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4154390831340783437L;
	// datos del usuario
	private String nombre;
	private String nick;
	private String contraseña;
	
	public User(String nombre,String nick,String contraseña) {
		this.nombre = nombre;
		this.nick = nick;
		this.contraseña = contraseña;

	}
	
	public String getNombre() {
		return nombre;
	}

	public String getNick() {
		return nick;
	}

	public String getContraseña() {
		return contraseña;
	}

	@Override
	public String toString(){
		return "@"+nick;
	}
	
}

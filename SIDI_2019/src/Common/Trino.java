package Common;

import java.io.Serializable;
import java.util.Date;

public class Trino implements Serializable{

	private static final long serialVersionUID = 1L;
	private String trino;
	private String nickPropietario;	//Ojo no pueden haber varios usuarios con el mismo nick
	private long timestamp; //momento en el que se produce el evento (tiempo en el servidor)
	
	public Trino(String trino,String nickPropietario)
	{
		this.trino=trino;
		this.nickPropietario=nickPropietario;
		Date date = new Date();
		this.timestamp=date.getTime();
	}
	public String ObtenerTrino()
	{
		return (trino);
	}
	public String ObtenerNickPropietario()
	{
		return(nickPropietario);
	}
	public long ObtenerTimestamp()
	{
		return (timestamp);
	}
	public String toString(){
		return (getClass().getName()+"@"+trino+"|"+nickPropietario+"|"+timestamp+"|");
	}
	
	@Override
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if (!(o instanceof Trino)) {
		    return false;
		}
		Trino other = (Trino) o;
		return trino.contentEquals(other.trino) && nickPropietario.equals(other.nickPropietario) && timestamp==other.timestamp; 
	}
}

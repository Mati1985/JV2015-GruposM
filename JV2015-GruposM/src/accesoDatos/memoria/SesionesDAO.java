package accesoDatos.memoria;
/** 
 * Proyecto: Juego de la vida.
 *  Resuelve todos los aspectos del almacenamiento del
 *  DTO SesionesUsuario utilizando un ArrayList persistente en fichero.
 *  Colabora en el patron Fachada.
 *  @since: prototipo2.2
 *  @source: SesionesDAO.java 
 *  @version: 2.2 - 18/05/2015 
 *  @author: ajp
 */
import java.util.ArrayList;
import java.util.List;

import accesoDatos.DatosException;
import accesoDatos.OperacionesDAO;
import modelo.SesionUsuario;
import modelo.Simulacion;
import modelo.Usuario;

public class SesionesDAO implements OperacionesDAO {

	// Requerido por el Singleton.
	private static SesionesDAO instancia = null;

	// Elemento de almacenamiento. 
	private ArrayList<SesionUsuario> sesionesUsuario;

	/**
	 * Constructor por defecto de uso interno.
	 * Sólo se ejecutará una vez.
	 */
	private SesionesDAO() {
		sesionesUsuario = new ArrayList<SesionUsuario>();
	}

	/**
	 *  Método estático de acceso a la instancia única.
	 *  Si no existe la crea invocando al constructor interno.
	 *  Utiliza inicialización diferida.
	 *  Sólo se crea una vez; instancia única -patrón singleton-
	 *  @return instancia
	 */
	public static SesionesDAO getInstancia() {
		if (instancia == null) {
			instancia = new SesionesDAO();
		}
		return instancia;
	}

	//OPERACIONES DAO
	/**
	 * Búsqueda de sesión por idSesion.
	 * @param idSesion - el idUsr+fecha a buscar.
	 * @return - la sesión encontrada; null si no existe.
	 */
	@Override
	public SesionUsuario obtener(String idSesion)  {
		int inicio = 0;
		int fin = sesionesUsuario.size() - 1;
		int medio;
		int comparacion;					// auxiliar para la comparación de String
		while (inicio <= fin) {
			medio = (inicio + fin) / 2;
			comparacion = sesionesUsuario.get(medio).getIdSesion().compareToIgnoreCase(idSesion);
			if (comparacion == 0) {
				return sesionesUsuario.get(medio);
			}
			if (comparacion < 0) {
				inicio = medio + 1;
			}
			else {
				fin = medio - 1;
			}
		}
		return null;
	}

	/**
	 * Búsqueda de Sesion dado un objeto, reenvía al método que utiliza idSesion.
	 * @param obj - la SesionUsuario a buscar.
	 * @return - la Sesion encontrada; null si no existe.
	 */
	@Override
	public SesionUsuario obtener(Object obj)  {
		return this.obtener(((SesionUsuario) obj).getIdSesion());
	}	

	/**
	 * Búsqueda de todas la sesiones de un usuario.
	 * @param idUsr - el identificador de usuario a buscar.
	 * @return - Sublista con las sesiones encontrada; null si no existe ninguna.
	 */
	public List<SesionUsuario> obtenerTodas(String idUsr)  {
		int inicio = 0;
		int fin = sesionesUsuario.size() - 1;
		int medio;	
		int comparacion;					// auxiliar para la comparación de String
		while (inicio <= fin) {
			medio = (inicio + fin) / 2;
			comparacion = sesionesUsuario.get(medio).getUsr().getIdUsr().compareToIgnoreCase(idUsr);
			if (comparacion == 0) {
				return separarSesiones(medio);
			}
			if (comparacion < 0) { 
				inicio = medio + 1;
			}
			else {
				fin = medio - 1;
			}
		}
		return null;	
	}

	/**
	 * Separa en una lista independiente de todas las sesiones de un mismo usuario.
	 * @param medio - el indice de una sesion almacenada.
	 * @return - Sublista con las sesiones encontrada; null si no existe ninguna.
	 */
	private List<SesionUsuario> separarSesiones(int medio) {
		int primera = medio;
		String idUsr = sesionesUsuario.get(medio).getUsr().getIdUsr();
		// Localiza primera sesion del usuario.
		for (int i = medio+1; i > sesionesUsuario.size()
				&& sesionesUsuario.get(i).getUsr().getIdUsr().equals(idUsr); i++) {
			primera = i;
		}
		// Localiza ultima sesion del usuario.
		int ultima = medio;
		for (int i = medio-1; i < 0
				&& sesionesUsuario.get(i).getUsr().getIdUsr().equals(idUsr); i--) {
			ultima = i;
		}
		// devuelve la sublista de sesiones buscadas.
		return sesionesUsuario.subList(primera, ultima+1);
	}
	
	/**
	 * Alta de una nueva SesionUsuario en orden y sin repeticiones según IdUsr + fecha. 
	 * Busca previamente la posición que le corresponde por búsqueda binaria.
	 * @param obj - la SesionUsuario a almacenar.
	 * @throws DatosException - si ya existe.
	 */
	@Override
	public void alta(Object obj) throws DatosException {
		assert obj != null;
		SesionUsuario sesion = (SesionUsuario) obj;
		int inicio = 0;
		int fin = sesionesUsuario.size() - 1;
		int medio = 0;
		int comparacion;					    // auxiliar para la comparación de String
		while (inicio <= fin) {
			medio = (inicio + fin) / 2;			// Calcula posición central.
			// compara los dos id. Obtiene < 0 si id va después que medio.
			comparacion = sesionesUsuario.get(medio).getIdSesion().compareToIgnoreCase(sesion.getIdSesion());
			if (comparacion == 0) {			
				throw new DatosException("La SesionUsuario ya existe...");   				  
			}		
			if (comparacion < 0) {
				inicio = medio + 1;
			}			
			else {
				fin = medio - 1;
			}
		}	
		sesionesUsuario.add(inicio, sesion); 	// Inserta la sesion en orden.
	}

	@Override
	public Object baja(String id) throws DatosException {

		return null;
	}

	@Override
	public void actualizar(Object obj) throws DatosException {

	} 

	/**
	 * Obtiene el listado de todos las sesiones almacenadas.
	 * @return el texto con el volcado de datos.
	 */
	@Override
	public String listarDatos() {
		StringBuilder sb = new StringBuilder();
		for (SesionUsuario s: sesionesUsuario) {
			if (s != null) {
				sb.append("\n" + s);
			}
		}
		return sb.toString();
	}

	/**
	 * Serializa en una cadena de caracteres los datos de todos los usuarios almacenados.
	 * @return el texto
	 */
	public String datosSesionesTexto() {
		StringBuilder aux = new StringBuilder();
		for (SesionUsuario s: sesionesUsuario) {
			if (s != null) {
				aux.append(s.toString() + ';');
			}
		}
		return aux.toString();
	}

	/**
	 * Búsqueda simple de todas las sesiones por IdUsr de usuario.
	 * @param id - el idUsr a buscar.
	 * @return - las sesiones encontradas o null si no existe.
	 */
	public ArrayList<SesionUsuario> buscarTodasSesiones(String id) {
		ArrayList<SesionUsuario> aux = new ArrayList<SesionUsuario>() ;
		for (int i = 0, j = 0; i < sesionesUsuario.size(); i++) {
			if (id.equals(sesionesUsuario.get(i).getUsr().getIdUsr())) {
				aux.add(sesionesUsuario.get(i));
				j++;
			}
		}
		return aux;
	}

}//class

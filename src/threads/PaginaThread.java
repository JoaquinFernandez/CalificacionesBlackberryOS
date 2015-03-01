/*
 * Copyright (C) 2011  Joaquín Fernández Moreno.
 * 				All rights reserved.
 */
package threads;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import staticMethods.Network;
import calificaciones.Storage;

import net.rim.device.api.io.Base64OutputStream;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

/**
 * The Class PaginaThread. It's a thread that retrieves the grades info from the
 * web app
 */
public class PaginaThread extends Thread {

	/**
	 * The store for the user and pass, i used
	 * "com.rim..net.calificaciones.jfm"
	 */
	private static PersistentObject store = PersistentStore
	.getPersistentObject(0x61863855aa9e3ccfL);

	/** The stack. */
	private Stack stack;

	/**
	 * Gets the stack.
	 * 
	 * @return the stack
	 */
	public Stack getStack() {
		return stack;
	}

	/**
	 * Instantiates a new pagina thread.
	 */
	public PaginaThread() {
	}

	/**
	 * Gets the pagina, public. You need to give it an url with the options for
	 * Wifi or GPRS connection
	 * 
	 * @return the pagina stack with the grades info
	 */
	public void run() {
		Stack stack = null;
		Storage storage;
		synchronized (store) {
			storage = (Storage) store.getContents();
		}
		if (storage != null) {
			if (storage.userinfo == null) {
				stack = new Stack();
				stack.addElement("No hay un perfil guardado");
			} else {
				String user = storage.userinfo[0];
				String pass = storage.userinfo[1];
				if (!storage.storeUserInfo) {
					synchronized (store) {
						storage.userinfo = null;
						store.setContents(storage);
						store.commit();
					}
				}
				stack = null;
				try {
					stack = getPaginaAux(user, pass);
					if (stack == null) {
						// There has been a connection error
						stack = new Stack();
						stack.addElement("Usuario y/o contraseña inválidos");
						synchronized (store) {
							storage.userinfo = null;
							store.setContents(storage);
							store.commit();
						}
					}
				} catch (IOException e) {
					// There has been an authentification error
					stack = new Stack();
					stack.addElement("Ha ocurrido un error");
				}
			}
		}
		else {
			stack = new Stack();
			stack.addElement("Error inesperado");
		}
		this.stack = stack;
	}

	/**
	 * Just manages the relation between the public method and the retrieving
	 * method
	 * 
	 * @param urlWifi
	 *            Just the connection options
	 * @param user
	 *            the user
	 * @param pass
	 *            the pass
	 * @return the pagina stack with the grades info
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private static Stack getPaginaAux(String user, String pass)
	throws IOException {
		HttpConnection conn = conectar(user, pass);
		Stack pagina = null;
		if (conn == null) {
			return null;
		}
		// If I get authentificated
		else {
			pagina = VisorDeNotas(conn);
		}
		return pagina;
	}

	/**
	 * Visor de notas. It retrieves the stack
	 * @param conn 
	 * 
	 * @return the pagina stack with the grades info
	 */
	private static Stack VisorDeNotas(HttpConnection conn) {
		try {
			InputStream input = conn.openInputStream();
			byte[] data = IOUtilities.streamToBytes(input);
			input.close();
			conn.close();
			String codigo = null;
			codigo = new String(data);
			Stack pagina = procesar(codigo);
			return pagina;
		} catch (IOException e) {
		} catch (IndexOutOfBoundsException e) {
		} catch (Exception e) {
			// won't ever happen, UTF-8 is supported
		}
		return null;
	}

	/**
	 * Procesar. It process the information to leave it into the form I need
	 * 
	 * @param codigo
	 *            the codigo
	 * @return the pagina stack with the grades info
	 */
	private static Stack procesar(String codigo) {

		// I initialize to 0 so I don't missuse space
		Stack notas = new Stack();
		notas.setSize(0);

		// Header, name of the user
		int principio = codigo.indexOf("12puntos");
		codigo = codigo.substring(principio + 11);

		// End of the user name
		int indexTitle = codigo.indexOf("</div>");
		String title = codigo.substring(0, indexTitle);
		title = title.trim();

		// Cleans the white space
		int j = title.indexOf("  ");
		while (j != -1) {
			title = title.substring(0, j) + title.substring(j + 1);
			j = title.indexOf("  ");
		}
		notas.push(title);

		// Start of grades
		int index25 = codigo.indexOf("Estilo25");
		String contador = codigo.substring(index25);

		// I add to the stack, the title and info of a subject in order
		int iniAsignaturas = contador.indexOf("<b>");
		while (iniAsignaturas != -1) {
			int endAsignaturas = contador.indexOf("</b>");
			int iniInfo = contador.indexOf("<br>");
			int endInfo = contador.indexOf("<br><br>");
			String titulo = contador.substring(iniAsignaturas + 3,
					endAsignaturas);
			titulo = limpiar(titulo);
			notas.push(titulo);
			String info = contador.substring(iniInfo + 4, endInfo);
			info = limpiar(info);
			notas.push(info);
			contador = contador.substring(endInfo + 8);
			iniAsignaturas = contador.indexOf("<b>");
		}
		notas.trimToSize();
		return notas;
	}

	/**
	 * Limpiar. It seachs the text for unwanted characters from html format and
	 * removes them
	 * 
	 * @param texto
	 *            string
	 * @return the "clean" string
	 */
	private static String limpiar(String texto) {
		int k = texto.indexOf("\r");
		while (k != -1) {
			String cadena1 = texto.substring(0, k);
			String cadena2 = texto.substring(k + 2);
			texto = cadena1 + cadena2;
			k = texto.indexOf("\r");

		}
		int y = texto.indexOf("<br>");
		while (y != -1) {
			String cadena1 = texto.substring(0, y);
			String cadena2 = texto.substring(y + 4);
			texto = cadena1 + cadena2;
			y = texto.indexOf("<br>");
		}
		int j = texto.indexOf("&nbsp;");
		while (j != -1) {
			String cadena1 = texto.substring(0, j);
			String cadena2 = texto.substring(j + 6);
			texto = cadena1 + cadena2;
			j = texto.indexOf("&nbsp;");
		}
		return texto;
	}

	/**
	 * Conectar. It sets the connection and the authentification
	 * 
	 * @param urlWifi
	 *            Just the connection options
	 * @param user
	 *            the user
	 * @param pass
	 *            the pass
	 * @return true, if successful
	 * @throws IOException 
	 */
	private static HttpConnection conectar(String user, String pass) throws IOException {

		boolean conectado = false;
		int x = 0;
		HttpConnection conn = null;
			boolean autenticado = true;
			while (autenticado) {
				// configuration for https conection
				String login = user + ":" + pass;
				byte[] codificado = Base64OutputStream.encode(login.getBytes(),
						0, login.length(), false, false);
				conn = (HttpConnection) Connector.open("http://www-app.etsit.upm.es/notas/" + Network.getUrl());
				// ir requests autorization
				conn.setRequestProperty("Authorization", "Basic "
						+ new String(codificado));
				// If i'm authorized it ends, else, i try 2 times (arbitrary)
				if (conn.getResponseCode() == HttpConnection.HTTP_OK) {
					autenticado = false;
					conectado = true;
				} else if (x == 2) {
					autenticado = false;
					conectado = false;
				}
				x++;
			}
		if (conectado)
			return conn;
		else {
			if (conn != null) {
				try {
					conn.close();
				} catch (IOException e) {
				}
			}
			return null;
		}
	}
}

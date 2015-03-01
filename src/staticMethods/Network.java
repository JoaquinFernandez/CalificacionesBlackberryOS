/*
 * Copyright (C) 2011  Joaquín Fernández Moreno.
 * 				All rights reserved.
 */
package staticMethods;

import java.io.DataInputStream;
import java.io.IOException;
import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import calificaciones.Storage;
import net.rim.blackberry.api.browser.Browser;
import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.servicebook.ServiceBook;
import net.rim.device.api.servicebook.ServiceRecord;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.CoverageInfo;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.system.RadioInfo;
import net.rim.device.api.system.WLANInfo;
import net.rim.device.api.system.WLANInfo.WLANAPInfo;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * The Class Network. It's a static class and you can use it to search for
 * updates or to see the state of the connection (wifi or gprs, up or down)
 */
public class Network {

	/**
	 * The store for the user and pass, i used
	 * "com.rim..net.calificaciones.jfm"
	 */
	private static PersistentObject store = PersistentStore
	.getPersistentObject(0x61863855aa9e3ccfL);

	/**
	 * Buscar actualizacion. It searchs for an available update of the program
	 * (new version)
	 * 
	 * @throws NullPointerException
	 *             the null pointer exception
	 */
	public static void buscarActualizacion()
	throws NullPointerException {
		ApplicationDescriptor descriptor = ApplicationDescriptor
		.currentApplicationDescriptor();
		// Retrieve the name of a running application.
		String appVersion = descriptor.getVersion();
		try {
			HttpConnection conn = (HttpConnection) Connector.open("http://mini.webfactional.com/calificaciones/app/ETSIT.ver" + Network.getUrl());
			DataInputStream stream = conn.openDataInputStream();
			int length = stream.available();
			byte data[] = new byte[length];
			data = IOUtilities.streamToBytes(stream);
			stream.close();
			String newVersion = new String(data, "UTF-8");
			// In the string there must be a new version and with no more of
			// five characters(This is for if there's an update error
			if (appVersion.compareTo(newVersion) < 0 && newVersion.length() < 5) {
				int answer = Dialog.ask(Dialog.D_YES_NO, "Versión "
						+ newVersion + " disponible \n ¿Desea descargarla?");
				if (answer == Dialog.YES) {
					// If the user wants to update y get a browser session
					Browser.getDefaultSession().displayPage("http://mini.webfactional.com/calificaciones/app/ETSIT.jad");
				}
			}
		} catch (IOException e) {
		}
	}

	/**
	 * Red. It checks for an available data network
	 * 
	 * @return true, if successful
	 */
	public static boolean red() {

		boolean gprs = false;
		boolean type = RadioInfo.isDataServiceOperational();
		WLANAPInfo wifi = WLANInfo.getAPInfo();

		synchronized (store) {
			Storage storage = (Storage) store.getContents();
			if (storage != null)
				gprs = storage.storedGPRS;
			store.commit();
		}

		// If there's no data network (wifi or gprs) entonces false
		if (!type && wifi == null) {
			return false;
		}

		// If there's wifi network
		if (wifi != null) {
			return true;
		}

		// If gprs is not null (comprobation so there's no nullpointer exception
		// in the next check, and if it's equal to a string i used
		if (gprs) {
			return true;
		}

		// If there's no wifi, but there's gprs, but there is no configuration
		// saved, i ask
		synchronized (UiApplication.getEventLock()) {
			String message = "No hay red Wi-Fi \n ¿Desea utilizar GRPS?";
			int respuesta = Dialog.ask(Dialog.D_YES_NO, message);
			if (respuesta == Dialog.YES) {
				String message2 = "¿Desea guardar esta opcion?";
				int respuesta2 = Dialog.ask(Dialog.D_YES_NO, message2);
				if (respuesta2 == Dialog.YES) {
					synchronized (store) {
						Storage storage = (Storage) store.getContents();
						if (storage == null)
							storage = new Storage();
						storage.storedGPRS = true;
						store.setContents(storage);
						store.commit();
					}
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * Determines what connection type to use and returns the necessary string to use it.
	 * @return A string with the connection info
	 */
	public static String getUrl()
	{
	    // This code is based on the connection code developed by Mike Nelson of AccelGolf.
	    // http://blog.accelgolf.com/2009/05/22/blackberry-cross-carrier-and-cross-network-http-connection
	    String connectionString = null;                                  
	 
	    // Wifi is the preferred transmission method
	    if(WLANInfo.getWLANState() == WLANInfo.WLAN_STATE_CONNECTED)
	    {
	        connectionString = ";interface=wifi";
	    }
	 
	    // Is the carrier network the only way to connect?
	    else if((CoverageInfo.getCoverageStatus() & CoverageInfo.COVERAGE_DIRECT) == CoverageInfo.COVERAGE_DIRECT)
	    {
	        String carrierUid = getCarrierBIBSUid();
	        if(carrierUid == null)
	        {
	            // Has carrier coverage, but not BIBS.  So use the carrier's TCP network
	            connectionString = ";deviceside=true";
	        }
	        else
	        {
	            // otherwise, use the Uid to construct a valid carrier BIBS request
	            connectionString = ";deviceside=false;connectionUID="+carrierUid + ";ConnectionType=mds-public";
	        }
	    }                
	    // Check for an MDS connection instead (BlackBerry Enterprise Server)
	    else if((CoverageInfo.getCoverageStatus() & CoverageInfo.COVERAGE_MDS) == CoverageInfo.COVERAGE_MDS)
	    {
	        connectionString = ";deviceside=false";
	    }
	    // If there is no connection available abort to avoid bugging the user unnecssarily.
	    else if(CoverageInfo.getCoverageStatus() == CoverageInfo.COVERAGE_NONE)
	    {
	    }
	 
	    // In theory, all bases are covered so this shouldn't be reachable.
	    else
	    {
	        connectionString = ";deviceside=true";
	    }        
	 
	    return connectionString;
	}
	 
	/**
	 * Looks through the phone's service book for a carrier provided BIBS network
	 * @return The uid used to connect to that network.
	 */
	private static String getCarrierBIBSUid()
	{
	    ServiceRecord[] records = ServiceBook.getSB().getRecords();
	    int currentRecord;
	 
	    for(currentRecord = 0; currentRecord < records.length; currentRecord++)         {             if(records[currentRecord].getCid().toLowerCase().equals("ippp"))             {                 if(records[currentRecord].getName().toLowerCase().indexOf("bibs") >= 0)
	            {
	                return records[currentRecord].getUid();
	            }
	        }
	    }
	 
	    return null;
	}

}

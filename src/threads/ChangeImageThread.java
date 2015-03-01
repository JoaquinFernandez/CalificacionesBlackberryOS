/*
 * Copyright (C) 2011  Joaquín Fernández Moreno.
 * 				All rights reserved.
 */
package threads;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.HttpConnection;

import staticMethods.Network;

import calificaciones.Storage;

import net.rim.device.api.io.IOUtilities;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

/**
 * The Class ChangeImageThread. It's a Thread and it retrieves an image from web
 * and stores it so it can be used on next program runs
 */
public class ChangeImageThread extends Thread {
	/**
	 * The store for the user and pass, i used
	 * "com.rim..net.calificaciones.jfm"
	 */
	private static PersistentObject store = PersistentStore
	.getPersistentObject(0x61863855aa9e3ccfL);

	/**
	 * Instantiates a new change image thread.
	 */
	public ChangeImageThread() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run()
	 */
	public void run() {
		byte[] data = null;
		try {
			HttpConnection conn = (HttpConnection) Connector.open("http://mini.webfactional.com/calificaciones/app/images.png" + Network.getUrl());
			InputStream input = conn.openInputStream();
			int length = input.available();
			data = new byte[length];
			data = IOUtilities.streamToBytes(input);
			input.close();
			conn.close();
			synchronized (store) {
				Storage storage = (Storage) store.getContents();
				if (storage == null) {
					storage = new Storage();
				}
				storage.data = data;
				store.setContents(storage);
				store.commit();
			}
		} catch (IOException e) {
			// We do nothing cause it will be managed upwards
		}
	}
}

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
import net.rim.device.api.system.EncodedImage;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.component.BitmapField;

/**
 * The Class ImageThread.
 */
public class ImageThread extends Thread {
	/**
	 * The store for the user and pass, i used
	 * "com.rim..net.calificaciones.jfm"
	 */
	private static PersistentObject store = PersistentStore
	.getPersistentObject(0x61863855aa9e3ccfL);
	
	/** The bitmap field. */
	private BitmapField bitmapField;

	/**
	 * Gets the bitmap field.
	 * 
	 * @return the bitmap field
	 */
	public BitmapField getBitmapField() {
		return bitmapField;
	}

	/**
	 * Instantiates a new image thread.
	 */
	public ImageThread() {
	}

	/**
	 * Gets the imagen. Public
	 */
	public void run() {
		byte data[] = getData();
		EncodedImage image = null;
		BitmapField bitmapField = null;
		if (data != null) {
			try {
				image = EncodedImage.createEncodedImage(data, 0, -1,"image/png");
				bitmapField = new BitmapField();
				// The dimensions for a image to display well 170 width x 90 height
				bitmapField.setImage(image);
			} catch (IllegalArgumentException e) {
			}
		}
		this.bitmapField = bitmapField;
	}

	/**
	 * Gets the data. It manages the interaction between public method and
	 * retrieving the data
	 * 
	 * @return the image in byte[] format
	 */
	private byte[] getData() {
		Storage storage;
		int cont = 0;
		byte[] data = null;
		synchronized (store) {
			storage = (Storage) store.getContents();
			if (storage != null && storage.data != null) {
				data = storage.data;
				cont = storage.cont;
			}
		}
		if (cont == 0 || data == null) {
			data = getImage();
			if (data == null)
				return null;
			cont++;// It increments twice so on the next run it doesn't get
			// updated again
		}
		if (cont > 5)
			cont = 0; // it increments just next, so it never stays 0 (wich
		// is only initial state)
		cont++;
		synchronized (store) {
			if (storage == null) {
				storage = new Storage();
			}
			storage.cont = cont;
			store.setContents(storage);
			store.commit();
		}

		return data;
	}

	/**
	 * Gets the cont. It retrieves the image counter from the memory
	 * 
	 * @return the cont
	 */
	public int getCont() {
		int cont = 0;
		synchronized (store) {
			Storage storage = (Storage) store.getContents();
			if (storage != null)
				cont = storage.cont;
		}
		return cont;
	}
	
	public byte[] getImage() {
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
		return data;
	}
}

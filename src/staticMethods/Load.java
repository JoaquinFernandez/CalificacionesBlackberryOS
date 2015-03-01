/*
 * Copyright (C) 2011  Joaquín Fernández Moreno.
 * 				All rights reserved.
 */
package staticMethods;

import java.util.Stack;

import calificaciones.Main;
import threads.*;
import screens.*;

import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.Dialog;

/**
 * The Class Load. Manages between View and Model
 */
public class Load {

	/**
	 * Load. it manages the main function of the app
	 */
	public static void load() {
		boolean wifi = Network.red(); // Check if there's network
		if (!wifi) {
			Dialog.ask(Dialog.OK, "No hay red disponible");
		} else {
			// create and start a thread so the main thread can keep working
			ImageThread imageThread = new ImageThread();
			imageThread.start();

			PaginaThread pagina = new PaginaThread();
			pagina.start();
			// It checks if the thread has finished
			checkThread(imageThread);
			BitmapField bitmapField = imageThread.getBitmapField();
			LoadScreen pantalla = new LoadScreen(bitmapField);
			UiApplication.getUiApplication().pushScreen(pantalla);
			UiApplication.getUiApplication().repaint();
			// I paint the image so i keep with the processing
			// Update search
			Network.buscarActualizacion();
			checkThread(pagina);
			Stack stack = pagina.getStack();
			int largo = stack.size();
			// This is a way to get the error message and process it afterwards
			if (largo == 1) {
				Main.dialog((String) stack.elementAt(0));
			} else {
				NotasScreen screen = new NotasScreen(stack);
				UiApplication.getUiApplication().pushScreen(screen);
				UiApplication.getUiApplication().repaint();
			}
			// If it's time to update the screen image, it does it in background
			if (imageThread.getCont() == 1) {
				ChangeImageThread changeImageThread = new ChangeImageThread();
				changeImageThread.setPriority(Thread.MIN_PRIORITY);
				changeImageThread.start();
			}
		}
	}

	/**
	 * Check thread. It checks if the thread already finished or waits for it to
	 * end
	 * 
	 * @param thread
	 *            the thread
	 */
	private static void checkThread(Thread thread) {
		while (thread.isAlive()) {
		}

	}
}

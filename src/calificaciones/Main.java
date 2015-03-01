/*
 * Copyright (C) 2011  Joaquín Fernández Moreno.
 * 				All rights reserved.
 */
package calificaciones;

import screens.InitScreen;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;

/**
 * The Class Main. It runs the app
 */
public final class Main extends UiApplication implements Runnable {
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the arguments
	 */
	public static void main(String[] args) {
		Main app = new Main();
		app.enterEventDispatcher();
	}

	/**
	 * Instantiates a new main. It just creates a InitScreen and pushes it
	 */
	public Main() {
		super();
		InitScreen screen = new InitScreen();
		pushScreen(screen);
		UiApplication.getUiApplication().repaint();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		main(null);
	}

	/**
	 * Dialog. It shows a dialog, trying it to be executed synchronized with the
	 * event dispatcher
	 * 
	 * @param texto
	 *            the text you want to show
	 */
	public static void dialog(final String texto) {
		final UiApplication uiApplication = UiApplication.getUiApplication();
		uiApplication.invokeAndWait(new Runnable() {
			public void run() {
				Dialog screen = new Dialog(Dialog.D_OK, texto, Dialog.OK,
						Bitmap.getPredefinedBitmap(Bitmap.EXCLAMATION),
						Dialog.VERTICAL_SCROLL);
				uiApplication.pushGlobalScreen(screen,
						UiApplication.GLOBAL_MODAL,
						UiApplication.GLOBAL_SHOW_LOWER);
			}
		});
	}
}

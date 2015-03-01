/*
 * Copyright (C) 2011  Joaquín Fernández Moreno.
 * 				All rights reserved.
 */
package screens;

import gui.ListListener;
import gui.ListStyleButtonField;

import java.util.Stack;

import calificaciones.Main;
import calificaciones.Storage;

import staticMethods.Load;

import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;

import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.LabelField;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

/**
 * Class NotasScreen it manages the use of the information about the grades and
 * displays it on the screen
 */
public final class NotasScreen extends MainScreen {

	/**
	 * The store for the user and pass, i used
	 * "com.rim..net.calificaciones.jfm"
	 */
	private static PersistentObject store = PersistentStore
	.getPersistentObject(0x61863855aa9e3ccfL);

	/**
	 * Instantiates a new notas screen.
	 * 
	 * @param pagina
	 *            stack with the content of all the subjects, the way the
	 *            information is kept is 1st the names, 2nd the first subject
	 *            title 3rd info about the first subject, 4th title of the
	 *            second subject... etc
	 */
	public NotasScreen(Stack pagina) {

		int largo = pagina.size() - 1;

		String titulo = (String) pagina.elementAt(0); // I get the title
		LabelField title = new LabelField(titulo, LabelField.FIELD_HCENTER
				| LabelField.ELLIPSIS);
		setTitle(title);
		// I add white spaces cause i needed the subjects to have at least
		// two lines or they lost format
		String visual = "";
		int ancho = Display.getWidth();
		for (int i = 0; i < ancho; i++) {
			visual += " ";
		}
		// Set on the display the subject's titles
		// 2 by 2 because between them is the info
		for (int i = 1; i < largo; i = i + 2) {

			String label = (String) pagina.elementAt(i);
			label = label.toUpperCase();
			// New List
			ListStyleButtonField notasButton = new ListStyleButtonField("",
					LabelField.FIELD_HCENTER);
			notasButton.setText(label + '\n' + visual);
			String text = (String) pagina.elementAt(i + 1);
			// I set the listener so that when it's pressed, it
			// desplegates the info
			ListListener listener = new ListListener(pagina, i, notasButton);
			notasButton.setChangeListener(listener);
			notasButton = changeColor(notasButton, text);
			add(notasButton);
		}
	}

	/**
	 * Change color. it changes the color of the list
	 * 
	 * @param notasButton
	 *            the notas "button" it means the field of the subject
	 * @param text
	 *            text of the field
	 * @return the list style button field
	 */
	private ListStyleButtonField changeColor(ListStyleButtonField notasButton,
			String text) {
		Font titleFont = Font.getDefault();
		titleFont = titleFont.derive(Font.PLAIN, 15);
		titleFont = titleFont.derive(Font.BOLD);
		if (text.indexOf("provisional)") != -1 || text.indexOf("provisionales)") != -1) {
			if (text.indexOf("AP ") != -1 || text.indexOf("APROBADO") != -1 || 
				text.indexOf("NT ") != -1 || text.indexOf("NOTABLE") != -1 ||
				text.indexOf("SB ") != -1 || text.indexOf("SOBRESALIENTE") != -1 ||
				text.indexOf("MH ") != -1 || text.indexOf("MATRICULA") != -1)
				
				notasButton.setFontColor(0x008419);
			else if (text.indexOf("SS ") != -1 || text.indexOf("SUSPENSO") != -1)
				
				notasButton.setFontColor(0xac2b2b);
			else if (text.indexOf("NP ") != -1 || text.indexOf("NO PRESENTADO") != -1)
				
				notasButton.setFontColor(0x6d6c6c);
		}
		else if (text.indexOf("definitivas)") != -1 || text.indexOf("definitiva)") != -1) {
			if (text.indexOf("AP ") != -1 || text.indexOf("APROBADO") != -1 || 
				text.indexOf("NT ") != -1 || text.indexOf("NOTABLE") != -1 ||
				text.indexOf("SB ") != -1 || text.indexOf("SOBRESALIENTE") != -1 ||
				text.indexOf("MH ") != -1 || text.indexOf("MATRICULA") != -1)
				
				notasButton.setFontColor(0x00844b);
			else if (text.indexOf("SS ") != -1 || text.indexOf("SUSPENSO") != -1)
				
				notasButton.setFontColor(0xac2b55);
			else if (text.indexOf("NP ") != -1 || text.indexOf("NO PRESENTADO") != -1)
				
				notasButton.setFontColor(0x6d6c6c);
		}
		notasButton.setFont(titleFont);
		return notasButton;
	}

	/** The menu item1. */
	private MenuItem menuItem1 = new MenuItem("Cambiar Usuario", 1, 2) {
		public void run() {
			synchronized (store) {
				Storage storage = (Storage) store.getContents();
				if (storage != null && storage.userinfo != null) {
					storage.userinfo = null;
					storage.storeUserInfo = false;
					store.setContents(storage);
					store.commit();
				}
			}
			UiApplication.getUiApplication().pushScreen(new InitScreen());
		}
	};

	/** The menu item2. */
	private MenuItem menuItem2 = new MenuItem("Actualizar", 2, 1) {
		public void run() {
			Load.load();
		}
	};

	/** The menu item3. */
	private MenuItem menuItem3 = new MenuItem("Opciones", 1, 2) {
		public void run() {

			String message = "¿Desea borrar sus datos de conexión?";
			int respuesta = Dialog.ask(Dialog.D_YES_NO, message);
			if (respuesta == Dialog.YES) {

				synchronized (store) {
					Storage storage = (Storage) store.getContents();
					if (storage != null) {
						storage.storedGPRS = false;
						store.setContents(storage);
						store.commit();
					}
				}
			}
		}
	};

	/** The menu item4. */
	MenuItem menuItem4 = new MenuItem("About", 3, 1) {
		public void run() {
			ApplicationDescriptor descriptor = ApplicationDescriptor
			.currentApplicationDescriptor();
			// Retrieve the name of a running application.
			String appVersion = descriptor.getVersion();
			Main.dialog("Version " + appVersion + '\n' + '\n'
					+ "Joaquín Fernández Moreno ©");
		}
	};

	/** The menu item5. */
	MenuItem menuItem5 = new MenuItem("Salir", 4, 1) {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			System.exit(0);
		}
	};

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.rim.device.api.ui.container.MainScreen#makeMenu(net.rim.device.api
	 * .ui.component.Menu, int)
	 */
	protected void makeMenu(Menu menu, int instance) {
		menu.deleteAll();
		menu.add(menuItem1);
		menu.add(menuItem2);
		menu.add(menuItem3);
		menu.addSeparator();
		menu.add(menuItem4);
		menu.addSeparator();
		menu.add(menuItem5);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.rim.device.api.ui.Screen#keyChar(char, int, int)
	 */
	public boolean keyChar(char key, int status, int time) {
		// intercept the ESC key - exit the app on its receipt
		if (key == Characters.ESCAPE || key == Keypad.KEY_END) {
			this.close();
			System.exit(0);
		}
		boolean cerrar = super.keyChar(key, status, time);
		return cerrar;
	}
}
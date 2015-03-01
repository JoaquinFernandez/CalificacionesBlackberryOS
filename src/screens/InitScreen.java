/*
 * Copyright (C) 2011  Joaquín Fernández Moreno.
 * 				All rights reserved.
 */
package screens;

import calificaciones.Main;
import calificaciones.Storage;
import gui.*;
import net.rim.device.api.system.ApplicationDescriptor;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Keypad;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.MenuItem;
import net.rim.device.api.ui.UiApplication;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.Dialog;
import net.rim.device.api.ui.component.Menu;
import net.rim.device.api.ui.container.MainScreen;

/**
 * The Class InitScreen. It creates the initial Screen, for introducing your
 * user and password.
 */
public class InitScreen extends MainScreen {

	/**
	 * The store for the user and pass, i used
	 * "com.rim..net.calificaciones.jfm"
	 */
	private static PersistentObject store = PersistentStore
	.getPersistentObject(0x61863855aa9e3ccfL);

	/** The user. */
	UserManager usr;

	/** The password. */
	PasswordManager pwd;

	/** The my checkbox. */
	CheckboxField myCheckbox = new CheckboxField("Recordar Credenciales", false) {
		// Overruned paint so it has white colored text
		protected void paint(Graphics g) {
			g.setColor(0xFFFFFF);
			super.paint(g);
		}

		// Overruned getFont so it has smaller letters
		public Font getFont() {
			Font font = Font.getDefault();
			font = font.derive(Font.PLAIN, 17);
			return font;
		}
	};

	/**
	 * Instantiates a new inits the screen.
	 */
	public InitScreen() {

		super(NO_VERTICAL_SCROLL);
		usr = new UserManager();
		pwd = new PasswordManager();
		ButtonField aceptar = new ButtonField("Aceptar",
				ButtonField.FIELD_HCENTER | ButtonField.CONSUME_CLICK);
		// listener "escuha" cuando ocurre algun evento
		FieldListener listener = new FieldListener(myCheckbox, usr.editField,
				pwd.editField);
		InitManager manager = new InitManager(Manager.NO_VERTICAL_SCROLL);
		EtiquetaField usuario = new EtiquetaField("Usuario");
		EtiquetaField password = new EtiquetaField("Contraseña");
		Bitmap bitmap = Bitmap.getBitmapResource("notas.png");
		BitmapField notas = new BitmapField(bitmap, BitmapField.NON_FOCUSABLE);
		manager.add(notas);
		manager.add(usuario);
		manager.add(usr);
		manager.add(password);
		manager.add(pwd);
		manager.add(myCheckbox);
		// Asigna listener al boton aceptar
		aceptar.setChangeListener(listener);
		manager.add(aceptar);
		add(manager);
		if (buscarPerfil()) {
			aceptar.setFocus();
		}
	}

	/**
	 * Buscar perfil. It searchs if there is an user already stored
	 * 
	 * @return true, if successful
	 */
	public boolean buscarPerfil() {
		synchronized (store) {
			Storage storage = (Storage) store.getContents();
			if (storage == null || storage.userinfo == null) {
				return false;
			} 
			else {
				usr.setText(storage.userinfo[0]);
				pwd.setText(storage.userinfo[1]);
				myCheckbox.setChecked(true);
				if (storage.storedGPRS) {
					return true;
				}
				return false;
			}
		}
	}

	/**
	 * Clear gprs. It clears the gprs connection options
	 */
	private void clearGprs() {
		synchronized (store) {
			Storage storage = (Storage) store.getContents();
			if (storage != null) {
			storage.storedGPRS = false;
			store.setContents(storage);
			store.commit();
			}
		}

	}

	/** The menu item1. */
	private MenuItem menuItem1 = new MenuItem("Opciones", 1, 2) {
		public void run() {

			synchronized (UiApplication.getEventLock()) {
				String message = "¿Desea borrar sus datos de conexión?";
				int respuesta = Dialog.ask(Dialog.D_YES_NO, message);
				if (respuesta == Dialog.YES) {
					clearGprs();
				}
			}
		}
	};

	/** The menu item2. */
	MenuItem menuItem2 = new MenuItem("About", 3, 1) {
		public void run() {
			ApplicationDescriptor descriptor = ApplicationDescriptor
					.currentApplicationDescriptor();
			// Retrieve the name of a running application.
			String appVersion = descriptor.getVersion();
			Main.dialog("Version " + appVersion + '\n' + '\n'
					+ "Joaquín Fernández Moreno ©");
		}
	};

	/** The menu item3. */
	MenuItem menuItem3 = new MenuItem("Salir", 4, 1) {
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
		menu.addSeparator();
		menu.add(menuItem2);
		menu.addSeparator();
		menu.add(menuItem3);
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

	/**
	 * The Class InitManager. It's the manager for the initial screen
	 */
	private class InitManager extends Manager {

		/** The gradient bitmap */
		Bitmap gradient = gradientBitmap(getPreferredWidth(),
				getPreferredHeight());

		/**
		 * Instantiates a new initmanager.
		 * 
		 * @param style
		 *            the style
		 */
		protected InitManager(long style) {
			super(style);
		}

		/**
		 * Gradient bitmap. It creates a gradient bitmap with selected width and
		 * height
		 * 
		 * @param width
		 *            the width
		 * @param height
		 *            the height
		 * @return the bitmap
		 */
		private Bitmap gradientBitmap(int width, int height) {
			Bitmap gradient = new Bitmap(width, height); // width, height =
															// columns, height
			int startColor = 0x4f768e;
			int endColor = 0x7ab2d5;
			int redStart = (startColor & 0x00FF0000) >> 16;
			int greenStart = (startColor & 0x0000FF00) >> 8;
			int blueStart = startColor & 0x000000FF;
			int redFinish = (endColor & 0x00FF0000) >> 16;
			int greenFinish = (endColor & 0x0000FF00) >> 8;
			int blueFinish = endColor & 0x000000FF;
			int[] rgb = new int[width * height];
			for (int row = 0; row < height; ++row) {
				int redComp = ((redFinish - redStart) * row / height)
						+ redStart;
				int greenComp = ((greenFinish - greenStart) * row / height)
						+ greenStart;
				int blueComp = ((blueFinish - blueStart) * row / height)
						+ blueStart;
				int rowColor = 0xFF000000 | (redComp << 16) | (greenComp << 8)
						| blueComp;
				for (int col = 0; col < width; ++col) {
					rgb[row * width + col] = rowColor;
				}
			}

			gradient.setARGB(rgb, 0, width, 0, 0, width, height);
			return gradient;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * net.rim.device.api.ui.Manager#paint(net.rim.device.api.ui.Graphics)
		 */
		protected void paint(Graphics g) {
			g.drawBitmap(0, 0, getPreferredWidth(), getPreferredHeight(),
					gradient, 0, 0);
			super.paint(g);
		}

		/**
		 * Gets the total field height. It gets the whole height of the manager
		 * elements so i can calculate the margin between them
		 * 
		 * @return the total field height
		 */
		private int getTotalFieldHeight() {
			int numberFields = getFieldCount();
			int height = 0;
			for (int i = 0; i < numberFields; i++) {
				Field field = getField(i);
				height += field.getPreferredHeight();
			}
			return height;

		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.rim.device.api.ui.Manager#sublayout(int, int)
		 */
		protected void sublayout(int width, int height) {
			Field field;
			int verticalMargin = height / 20; // Margen arriba y abajo a guardar
			int horizontalMargin = width / 8; // Margen a los lados a guardar
			int fieldMargin = (height - (getTotalFieldHeight() - (2 * verticalMargin)))
					/ (2 * getFieldCount()); // Margen entre fields

			field = getField(0); // get the field
			setPositionChild(field, (width / 2)
					- (field.getPreferredWidth() / 2), verticalMargin); // set
																		// the
																		// position
																		// for
																		// the
																		// field
			layoutChild(field, field.getPreferredWidth(),
					field.getPreferredHeight()); // lay out the field
			int x = horizontalMargin; // Posicion por defecto, despues de notas
			int y = verticalMargin + field.getPreferredHeight() + fieldMargin;

			field = getField(1); // get the field
			setPositionChild(field, x, y); // set the position for the field
			layoutChild(field, field.getPreferredWidth(),
					field.getPreferredHeight()); // lay out the field
			y += field.getPreferredHeight() + fieldMargin;

			field = getField(2); // campo editable para el user
			setPositionChild(field, x, y); // set the position for the field
			layoutChild(field, field.getPreferredWidth(),
					field.getPreferredHeight()); // lay out the field
			y += field.getPreferredHeight() + fieldMargin;

			field = getField(3); // get the field
			setPositionChild(field, x, y); // set the position for the field
			layoutChild(field, field.getPreferredWidth(),
					field.getPreferredHeight()); // lay out the field
			y += field.getPreferredHeight() + fieldMargin;

			field = getField(4); // campo editable para el password
			setPositionChild(field, x, y); // set the position for the field
			layoutChild(field, field.getPreferredWidth(),
					field.getPreferredHeight()); // lay out the field
			y += field.getPreferredHeight() + fieldMargin;

			field = getField(5); // get the field
			setPositionChild(field, x, y); // set the position for the field
			layoutChild(field, field.getPreferredWidth(),
					field.getPreferredHeight()); // lay out the field
			y += field.getPreferredHeight() + fieldMargin;

			field = getField(6); // El boton tiene un altoxancho de 92x30,
			//y le dejo un margen extra
			setPositionChild(field, (width / 2) - (48), y); // set the position
															// for the field
			layoutChild(field, 98, 32); // lay out the field
			setExtent(width, height);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.rim.device.api.ui.Field#getPreferredWidth()
		 */
		public int getPreferredWidth() {
			return Display.getWidth();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.rim.device.api.ui.Field#getPreferredHeight()
		 */
		public int getPreferredHeight() {
			return Display.getHeight();
		}
	}
}

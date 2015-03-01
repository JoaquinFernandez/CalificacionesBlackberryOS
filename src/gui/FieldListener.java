/*
 * Copyright (C) 2011  Joaquín Fernández Moreno.
 * 				All rights reserved.
 */
package gui;

import staticMethods.Load;
import calificaciones.*;
import net.rim.device.api.system.PersistentObject;
import net.rim.device.api.system.PersistentStore;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.component.ButtonField;
import net.rim.device.api.ui.component.CheckboxField;
import net.rim.device.api.ui.component.EditField;
import net.rim.device.api.ui.component.PasswordEditField;

/**
 * The listener interface for receiving field events. The class that is
 * interested in processing a field event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addFieldListener<code> method. When
 * the field event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see FieldEvent
 */
public final class FieldListener extends ButtonField implements
		FieldChangeListener {

	/** The user. */
	private EditField usr;

	/** The password. */
	private PasswordEditField pwd;

	/** The my checkbox. */
	private CheckboxField myCheckbox;

	/**
	 * The store for the user and pass, i used
	 * "com.rim..net.calificaciones.jfm"
	 */
	private static PersistentObject store = PersistentStore
	.getPersistentObject(0x61863855aa9e3ccfL);

	/**
	 * Instantiates a new field listener.
	 * 
	 * @param myCheckbox
	 *            the my checkbox
	 * @param usr
	 *            the usr
	 * @param pwd
	 *            the pwd
	 */
	public FieldListener(CheckboxField myCheckbox, EditField usr,
			PasswordEditField pwd) {
		this.myCheckbox = myCheckbox;
		this.usr = usr;
		this.pwd = pwd;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.rim.device.api.ui.FieldChangeListener#fieldChanged(net.rim.device
	 * .api.ui.Field, int)
	 */
	public void fieldChanged(Field f, int context) {
		// If Edit field empty
		boolean usrVacio = usr.getText().equals("");
		boolean passVacio = pwd.getText().equals("");
		if (usrVacio)
			Main.dialog("Por favor, introduce tu nombre");
		else if (passVacio)
			Main.dialog("Por favor, introduce tu contraseña");
		// I get the user and pass introduced and remove spaces from sides
		else {
			String user = usr.toString();
			String pass = pwd.toString();
			user = user.trim();
			user.toLowerCase();
			pass = pass.trim();
			String[] userinfo = {user, pass};
			synchronized (store) {
				Storage storage = (Storage) store.getContents();
				if (storage == null) {
					storage = new Storage();
				}
				storage.userinfo = userinfo;
				storage.storeUserInfo = myCheckbox.getChecked();
				store.setContents(storage);
				store.commit();
			}
			Load.load();
		}
	}
}
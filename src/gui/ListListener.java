/*
 * Copyright (C) 2011  Joaquín Fernández Moreno.
 * 				All rights reserved.
 */
package gui;

import java.util.Stack;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.FieldChangeListener;
import net.rim.device.api.ui.Font;

/**
 * The listener interface for receiving list events. The class that is
 * interested in processing a list event implements this interface, and the
 * object created with that class is registered with a component using the
 * component's <code>addListListener<code> method. When
 * the list event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see ListEvent
 */
public final class ListListener implements FieldChangeListener {

	/** The pagina. */
	private Stack pagina;

	/** The index. */
	private int index;

	/** The button. */
	private ListStyleButtonField button;
	/** The notExtended comprobator. */
	private boolean notExtended = false;

	/**
	 * Instantiates a new list listener.
	 * 
	 * @param pagina
	 *            the pagina
	 * @param index
	 *            the index
	 * @param button
	 *            the button
	 */
	public ListListener(Stack pagina, int index, ListStyleButtonField button) {
		this.button = button;
		this.pagina = pagina;
		this.index = index;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.rim.device.api.ui.FieldChangeListener#fieldChanged(net.rim.device
	 * .api.ui.Field, int)
	 */
	public void fieldChanged(Field field, int context) {
		// It's just a listener for the fields of ListSyleButtonField that
		// expands with the info if the user clicks on it, and contracts if he
		// clicks again
		if (field == button) {
			if (notExtended) {
				String label = (String) pagina.elementAt(index);
				label = label.toUpperCase();
				Font titleFont = Font.getDefault();
				titleFont = titleFont.derive(Font.PLAIN, 15);
				titleFont = titleFont.derive(Font.BOLD);
				button.setFont(titleFont);
				String visual = "";
				int ancho = Display.getWidth();
				// It is necessary because it needs to fill two lines to not
				// loose
				// the wished format
				for (int i = 0; i < ancho; i++) {
					visual += " ";
				}
				button.setText(label + '\n' + visual);
				notExtended = false;
			} else {
				String label = (String) pagina.elementAt(index);
				label = label.toUpperCase();
				String texto = (String) pagina.elementAt(index + 1);
				button.setText(label + '\n' + texto);
				Font textFont = Font.getDefault();
				textFont = textFont.derive(Font.PLAIN, 15);
				button.setFont(textFont);
				notExtended = true;
			}
		}
	}
}

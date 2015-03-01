/*
  * Copyright (C) 2011  Joaquín Fernández Moreno.
 * 				All rights reserved.
 */
package gui;

import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;

/**
 * The Class EtiquetaField. It just creates a labelField centered and with white
 * colored text
 */
public class EtiquetaField extends LabelField {

	/**
	 * Instantiates a new etiqueta field.
	 * 
	 * @param string
	 *            the string
	 */
	public EtiquetaField(String string) {
		super(string, LabelField.FIELD_HCENTER | LabelField.ELLIPSIS);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * net.rim.device.api.ui.component.LabelField#paint(net.rim.device.api.ui
	 * .Graphics)
	 */
	protected void paint(Graphics g) {
		g.setColor(0xFFFFFF);
		super.paint(g);
	}
}

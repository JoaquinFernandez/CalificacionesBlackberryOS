/*
 * Copyright (C) 2011  Joaquín Fernández Moreno.
 * 				All rights reserved.
 */
package gui;

import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.PasswordEditField;
import net.rim.device.api.ui.container.VerticalFieldManager;

/**
 * The Class PasswordField. Is a manager cause I needed the borders to not be in
 * the password editfield
 */
public class PasswordManager extends Manager {

	/** The enable. */
	private boolean enable = false;

	/** The vfm. */
	private VerticalFieldManager vfm = new VerticalFieldManager(
			NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL);

	/** The edit field. */
	public PasswordEditField editField = new PasswordEditField("", "",
			(getPreferredWidth() - 6) / 10, Field.FIELD_HCENTER) {
		public void onFocus(int direction) {
			repaint(true);
		}

		public void onUnfocus() {
			repaint(false);
		}
	};

	/**
	 * Instantiates a new password field.
	 */
	public PasswordManager() {
		super(NO_VERTICAL_SCROLL | NO_HORIZONTAL_SCROLL | NON_FOCUSABLE);
		vfm.add(editField);
		add(vfm);
	}

	/**
	 * Repaint.
	 * 
	 * @param border
	 *            the border
	 */
	private void repaint(boolean border) {
		enable = border;
		invalidate();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.rim.device.api.ui.Field#getPreferredWidth()
	 */
	public int getPreferredWidth() {
		int horizontalMargin = (Display.getWidth() / 8) * 2; // Margen a los
																// lados que
																// guardar
		return Display.getWidth() - horizontalMargin;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.rim.device.api.ui.Field#getPreferredHeight()
	 */
	public int getPreferredHeight() {
		int vertical = editField.getPreferredHeight() + getMarginTop()
				+ getMarginBottom() + 8; // Margen arriba y abajo que guardar
		return vertical;
	}

	/**
	 * Sets the border. It sets a border for the PasswordeditField
	 * 
	 * @param g
	 *            the Graphics
	 * @return the graphics
	 */
	private Graphics setBorder(Graphics g) {
		if (enable) {
			g.setColor(0x2a00ff);
			g.fillRoundRect(0, 0, getPreferredWidth(), getPreferredHeight(), 5,
					40);
			g.setColor(0x30b1a9);
			g.fillRoundRect(1, 1, getPreferredWidth() - 2,
					getPreferredHeight() - 2, 5, 40);
		}
		g.setColor(0x2a00ff);
		g.fillRoundRect(2, 2, getPreferredWidth() - 4,
				getPreferredHeight() - 4, 5, 40);
		g.setColor(0xFFFFFF);
		g.fillRoundRect(3, 3, getPreferredWidth() - 6,
				getPreferredHeight() - 6, 5, 40);
		g.setColor(0x000000);
		return g;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.rim.device.api.ui.Manager#paint(net.rim.device.api.ui.Graphics)
	 */
	protected void paint(Graphics g) {
		g = setBorder(g);
		super.paint(g);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.rim.device.api.ui.Manager#sublayout(int, int)
	 */
	protected void sublayout(int w, int h) {
		int actWidth = Math.min(getPreferredWidth(), w);
		int actHeight = Math.min(getPreferredHeight(), h);
		layoutChild(vfm, actWidth - 6, actHeight - 6); // Leave room for border
		setPositionChild(vfm, 3, 3); // again, careful not to stomp over the
										// border
		setExtent(actWidth, actHeight);
	}

	/**
	 * Sets the text. It sets the text on the passwordEditField
	 * 
	 * @param string
	 *            the new text
	 */
	public void setText(String string) {
		editField.setText(string);
	}
}

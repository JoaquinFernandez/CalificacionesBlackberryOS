/*
 * Copyright (C) 2011  Joaquín Fernández Moreno.
 * 				All rights reserved.
 */
package screens;

import gui.EtiquetaField;
import net.rim.device.api.system.Bitmap;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.BitmapField;
import net.rim.device.api.ui.container.MainScreen;

/**
 * The Class LoadScreen. It shows the info in the display
 */
public class LoadScreen extends MainScreen {

	/**
	 * Instantiates a new load screen.
	 * 
	 * @param bitmapField
	 *            the central image of the screen
	 */
	public LoadScreen(BitmapField bitmapField) {
		super(NO_VERTICAL_SCROLL);
		LoadManager manager = new LoadManager(Manager.NO_VERTICAL_SCROLL);
		if (bitmapField != null)
			manager.add(bitmapField);
		else {
			Bitmap bitmap = Bitmap.getBitmapResource("notas.png");
			BitmapField notas = new BitmapField(bitmap,
					BitmapField.NON_FOCUSABLE);
			manager.add(notas);
		}
		EtiquetaField cargando = new EtiquetaField("Cargando...");
		Bitmap bitmap = Bitmap.getBitmapResource("smiley.png");
		BitmapField smiley = new BitmapField(bitmap, BitmapField.NON_FOCUSABLE);
		manager.add(cargando);
		manager.add(smiley);
		add(manager);
	}

	/**
	 * The Class LoadManager. It's the manager for the loading screen
	 */
	private class LoadManager extends Manager {

		/** Bitmap made of gradient color */
		Bitmap gradient = gradientBitmap(getPreferredWidth(),
				getPreferredHeight());

		/**
		 * Instantiates a new load manager.
		 * 
		 * @param style
		 *            the style
		 */
		protected LoadManager(long style) {
			super(style);
		}

		/**
		 * Gradient bitmap.
		 * 
		 * @param width
		 *            the width of the bitmap
		 * @param height
		 *            the height of the bitmap
		 * @return the bitmap
		 */
		private Bitmap gradientBitmap(int width, int height) {
			Bitmap gradient = new Bitmap(width, height);
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

		/*
		 * (non-Javadoc)
		 * 
		 * @see net.rim.device.api.ui.Manager#sublayout(int, int)
		 */
		protected void sublayout(int width, int height) {

			Field field = getField(0);
			setPositionChild(field, width / 2 - field.getPreferredWidth() / 2,
					height / 3 - field.getPreferredHeight() / 2);
			layoutChild(field, field.getPreferredWidth(),
					field.getPreferredHeight());
			field = getField(1);
			setPositionChild(field, (width / 2)
					- (field.getPreferredWidth() / 2),
					((height * 2 / 3) - 21 / 2));
			layoutChild(field, field.getPreferredWidth(), 21);
			field = getField(2);
			setPositionChild(field, width - field.getPreferredWidth()
					- getMarginRight(), height - field.getPreferredHeight()
					- getMarginBottom());
			layoutChild(field, 20, 21);
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

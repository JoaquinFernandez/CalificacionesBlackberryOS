package gui;

import net.rim.device.api.system.Characters;
import net.rim.device.api.system.Display;
import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.component.LabelField;

public class ListStyleButtonField extends Field {

	public static int DRAWPOSITION_TOP = 0;
	public static int DRAWPOSITION_BOTTOM = 1;
	public static int DRAWPOSITION_MIDDLE = 2;
	public static int DRAWPOSITION_SINGLE = 3;

	private static final int HPADDING = Display.getWidth() <= 320 ? 6 : 8;
	private static final int VPADDING = 4;

	private static final int COLOR_TEXT_ON_FOCUS = 0xFFFFFF;
	private static final int COLOR_BACKGROUND = 0xFFFFFF;

	private int width;
	private int height;
	private int targetHeight;
	private int rightOffset;
	private int leftOffset;
	private int labelHeight;
	private int RGB = 0x000000;
	private int previousRGB = 0x000000;
	private int bgrRGB = 0xFFFFFF;
	private int bgrFocus = 0x186DEF;


	private PublicLabelField labelField;
	private boolean focus;
	
	public ListStyleButtonField(String label, long style) {
		super(USE_ALL_WIDTH | Field.FOCUSABLE);
		labelField = new PublicLabelField(label, style);
		
	}

	/**
	 * DRAWPOSITION_TOP | DRAWPOSITION_BOTTOM | DRAWPOSITION_MIDDLE Determins
	 * how the field is drawn (borders) If none is set, then no borders are
	 * drawn
	 */

	public String toString() {
		return labelField.toString();
	}

	public void layout(int width, int height) {

		targetHeight = getFont().getHeight() / 2 * 3 + 2 * VPADDING;
		// #ifndef VER_4.6.1 | VER_4.6.0 | VER_4.5.0 | VER_4.2.1 | VER_4.2.0

		// #endif

		leftOffset = HPADDING;

		rightOffset = HPADDING;

		labelField.layout(width - leftOffset - rightOffset, height);
		labelHeight = labelField.getHeight();
		int labelWidth = labelField.getWidth();

		if (labelField.isStyle(DrawStyle.HCENTER)) {
			leftOffset = (width - labelWidth) / 2;
		} else if (labelField.isStyle(DrawStyle.RIGHT)) {
			leftOffset = width - labelWidth - HPADDING - rightOffset;
		}

		int extraVPaddingNeeded = 0;
		if (labelHeight < targetHeight) {
			// Make sure that they are at least 1.5 times font height
			extraVPaddingNeeded = (targetHeight - labelHeight) / 2;
		}

		this.width = width;
		this.height = labelHeight + 2 * extraVPaddingNeeded;
		setExtent(width, this.height);
	}

	public void setText(String text) {
		labelField.setText(text);
		updateLayout();
	}

	public void setFont(Font font) {
		labelField.setFont(font);
		updateLayout();
	}

	public void setFontColor(int color) {
		RGB = color;
		previousRGB = color;
		bgrFocus = color;
	}

	public int getPreferredHeight() {
		return labelField.getHeight();
	}

	public int getPreferredWidth() {
		return labelField.getWidth();
	}

	protected void paint(Graphics g) {
		if (focus) {
			g.setColor(bgrRGB);
			g.fillRect(0, 0, width, height);
		}
		// Left Bitmap
		g.setColor(RGB);
		// Text
		try {
			g.pushRegion(leftOffset, (getHeight() - labelHeight) / 2,
					getWidth() - leftOffset - rightOffset, labelHeight, 0, 0);
			labelField.paint(g);
		} finally {
			g.popContext();
		}
	}

	protected boolean keyChar(char character, int status, int time) {
		if (character == Characters.ENTER) {
			clickButton();
			return true;
		}
		return super.keyChar(character, status, time);
	}

	protected boolean navigationClick(int status, int time) {
		clickButton();
		return true;
	}

	protected void onFocus(int direction) {
		RGB = COLOR_TEXT_ON_FOCUS;
		bgrRGB = bgrFocus;
		focus = true;
	}

	protected void onUnfocus() {
		RGB = previousRGB;
		bgrRGB = COLOR_BACKGROUND;
		focus = false;
		invalidate();
	}

	protected boolean trackwheelClick(int status, int time) {
		clickButton();
		return true;
	}

	protected boolean invokeAction(int action) {
		switch (action) {
		case ACTION_INVOKE: {
			clickButton();
			return true;
		}
		}
		return super.invokeAction(action);
	}

	// #endif

	/**
	 * Hago los metodos publicos publica
	 */
	public void clickButton() {
		fieldChangeNotify(0);
	}

	private static class PublicLabelField extends LabelField {

		public PublicLabelField(String text, long style) {
			super(text, style);
		}

		public void layout(int width, int height) {
			super.layout(width, height);
		}

		public void paint(Graphics g) {
			super.paint(g);
		}
	}
}
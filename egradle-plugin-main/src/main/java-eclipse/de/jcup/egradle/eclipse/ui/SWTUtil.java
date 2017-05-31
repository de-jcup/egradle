package de.jcup.egradle.eclipse.ui;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;

public class SWTUtil {

	/**
	 * Shows the given control or does hide it. If hidden the component will no
	 * longer be rendered in layout container and UI space is free. Currently
	 * supported only GridLayout
	 * 
	 * @param control
	 * @param visible
	 */
	public static void showControl(Control control, boolean visible) {
		if (control == null) {
			return;
		}
		if (control.isDisposed()) {
			return;
		}
		Object layoutData = control.getLayoutData();
		if (layoutData instanceof GridData) {
			GridData gridData = (GridData) layoutData;
			gridData.exclude = !visible;
		}
		control.requestLayout();
		control.setVisible(visible);
	}

	/**
	 * Set font data style for given component. E.g.
	 * 
	 * <pre>
	 * Label label = ...
	 * SWTUtil.setFontDataStyle(label, SWT.ITALIC);
	 * </pre>
	 * 
	 * @param control
	 * @param style
	 */
	public static void setFontDataStyle(Control control, int style) {

		if (control == null) {
			return;
		}
		if (control.isDisposed()) {
			return;
		}
		Font font = control.getFont();
		if (font == null) {
			return;
		}
		FontData fontData = font.getFontData()[0];
		Font newFont = new Font(control.getDisplay(), new FontData(fontData.getName(), fontData.getHeight(), style));
		control.setFont(newFont);
	}
}

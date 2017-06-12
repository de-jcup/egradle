/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
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

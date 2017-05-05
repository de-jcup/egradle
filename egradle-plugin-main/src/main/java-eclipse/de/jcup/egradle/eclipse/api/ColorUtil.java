package de.jcup.egradle.eclipse.api;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

public class ColorUtil {

	/**
	 * Returns a web color in format "#RRGGBB"
	 * 
	 * @param color
	 * @return web color as string
	 */
	public static String convertToHexColor(Color color) {
		if (color == null) {
			return null;
		}
		return convertToHexColor(color.getRGB());
	}

	public static String convertToHexColor(RGB rgb) {
		if (rgb == null) {
			return null;
		}
		String hex = String.format("#%02x%02x%02x", rgb.red, rgb.green, rgb.blue);
		return hex;
	}
}

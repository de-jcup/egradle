package de.jcup.egradle.core.api;

import org.apache.commons.lang3.StringUtils;

/**
 * Simple possibility to use apache string utils from lib inside main plugin - avoids additional binary 
 * lib folder in this plugin...
 * @author Albert Tregnaghi
 *
 */
public class StringUtilsAccess {

	public static boolean isBlank(CharSequence cs){
		return StringUtils.isBlank(cs);
	}
	
	public static String substring(String str, int start){
		return StringUtils.substring(str, start);
	}
	
	public static String substring(String str, int start, int end){
		return StringUtils.substring(str, start, end);
	}

	public static String abbreviate(String currentHTML, int maxWidth) {
		return StringUtils.abbreviate(currentHTML, maxWidth);
	}
}

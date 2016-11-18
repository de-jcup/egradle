package de.jcup.egradle.eclipse.api;

/**
 * Provides states of debug features to enable
 * @author Albert Tregnaghi
 *
 */
public interface EclipseDevelopmentSettings {
	/**
	 * Feature toggle - shows more informatino on some points
	 */
	public static final boolean DEBUG_ADD_SPECIAL_TEXTS = Boolean.parseBoolean(System.getProperty("egradle.debug.texts"));
	/**
	 * Feature toggle - allows outline reloading as full antlr parts also. interesting for debug purpose only. For normal usage uninteresting
	 */
	public static final boolean DEBUG_ADD_SPECIAL_MENUS = Boolean.parseBoolean(System.getProperty("egradle.debug.menus"));
	

}

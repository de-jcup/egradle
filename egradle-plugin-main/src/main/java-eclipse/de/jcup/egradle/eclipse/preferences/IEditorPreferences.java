package de.jcup.egradle.eclipse.preferences;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.RGB;

import de.jcup.egradle.eclipse.util.PreferenceIdentifiable;

public interface IEditorPreferences {
	public String getStringPreference(PreferenceIdentifiable id) ;

	public boolean getBooleanPreference(PreferenceIdentifiable id) ;

	public void setBooleanPreference(PreferenceIdentifiable id, boolean value) ;

	public IPreferenceStore getPreferenceStore() ;

	public boolean getDefaultBooleanPreference(PreferenceIdentifiable id) ;

	public RGB getColor(PreferenceIdentifiable identifiable) ;
	
	/**
	 * Returns color as a web color in format "#RRGGBB"
	 * @param identifiable
	 * @return web color string
	 */
	public String getWebColor(PreferenceIdentifiable identifiable) ;

	public void setDefaultColor(PreferenceIdentifiable identifiable, RGB color) ;

	public boolean isCodeAssistProposalsEnabled() ;
	
	public boolean isCodeAssistNoProposalsForGetterOrSetter() ;
	
	public boolean isCodeAssistTooltipsEnabled() ;

	public boolean isEditorAutoCreateEndBracketsEnabled();

	public boolean isLinkOutlineWithEditorEnabled();

	public PreferenceIdentifiable getP_EDITOR_MATCHING_BRACKETS_ENABLED();

	public PreferenceIdentifiable getP_EDITOR_MATCHING_BRACKETS_COLOR();

	public PreferenceIdentifiable getP_EDITOR_HIGHLIGHT_BRACKET_AT_CARET_LOCATION();

	public PreferenceIdentifiable getP_EDITOR_ENCLOSING_BRACKETS();
}

package de.jcup.egradle.codeassist;

import java.util.List;

public interface TypeTemplatesProvider {

	/**
	 * Returns a list of templates
	 * @param string
	 * @return list, never <code>null</code>
	 */
	public List<Template> getTemplatesForType(String string) ;
	
	
}

package de.jcup.egradle.core.api;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.dsl.DSLConstants;
public class LinkToTypeConverter {

	/**
	 * Converts to link
	 * 
	 * @param link
	 * @return converted type name, or <code>null</code> if not convertable
	 */
	public String convertLink(String link) {
		if (!isLinkSchemaConvertable(link)) {
			return null;
		}

		String typeName = StringUtils.substring(link, DSLConstants.HYPERLINK_TYPE_PREFIX.length());
		if (StringUtils.isBlank(typeName)) {
			return null;
		}
		if (typeName.endsWith("/")) {
			typeName = StringUtils.substring(typeName, 0, typeName.length() - 1);
		}
		return typeName;
	}

	
	public boolean isLinkSchemaConvertable(String target) {
		if (target == null) {
			return false;
		}
		return target.startsWith(DSLConstants.HYPERLINK_TYPE_PREFIX);
	}
}

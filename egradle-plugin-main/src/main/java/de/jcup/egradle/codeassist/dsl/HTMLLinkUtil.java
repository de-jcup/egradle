package de.jcup.egradle.codeassist.dsl;

public class HTMLLinkUtil {
	/**
	 * Appends link to type
	 * 
	 * @param descSb
	 * @param withEndingDot
	 *            - when true at the end of string a dot will be placed - e.g.
	 *            "java.util.String."
	 * @param type
	 * @param linkPostfix
	 *            - can be null
	 */
	public static void appendLinkToType(StringBuilder descSb, boolean withEndingDot, Type type, String linkPostfix) {
		if (type == null) {
			descSb.append("void");
			return;
		}
		descSb.append("<a href='type://");
		descSb.append(type.getName());
		if (linkPostfix != null) {
			descSb.append(linkPostfix);
		}
		descSb.append("'>");
		descSb.append(type.getName());
		descSb.append("</a>");
		if (withEndingDot) {
			descSb.append('.');
		}
	}
}

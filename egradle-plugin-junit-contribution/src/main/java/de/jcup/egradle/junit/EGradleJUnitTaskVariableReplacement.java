package de.jcup.egradle.junit;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EGradleJUnitTaskVariableReplacement {
	
	public static final String TASKS_VARIABLE = "clean test"; // backward compatible - the "variable" is same as the default before, so it will always be replaced
	
	private static final Pattern PATTERN = Pattern.compile(TASKS_VARIABLE);
	

	/**
	 * Replaces origin parts where {@link #TASKS_VARIABLE} is used
	 * @param origin
	 * @param replacement
	 * @return replaced tasks string
	 */
	public String replace(String origin, String replacement) {
		if (origin==null){
			return null;
		}
		if (replacement==null){
			return origin;
		}
		Matcher matcher = PATTERN.matcher(origin);
		String result = matcher.replaceAll(replacement);
		return result;
	}
	
	

}

package de.jcup.egradle.core.text;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public abstract class AbstractTextMatcher<T>  implements FilterPatternMatcher<T> {

	private Pattern PATTERN_DEREGEX_ASTERISK = Pattern.compile("\\*");
	private Pattern PATTERN_DEREGEX_DOT = Pattern.compile("\\.");
	protected Pattern filterPattern;

	public AbstractTextMatcher() {
		super();
	}
	
	@Override
	public boolean matches(T item) {
		if (item == null) {
			return false;
		}

		if (filterPattern == null) {
			/* no filter matches all... */
			return true;
		}

		String itemText=createItemText(item);
		return matchesString(itemText);
	}

	protected abstract String createItemText(T item);

	public boolean hasFilterPattern() {
		return filterPattern != null;
	}

	protected boolean matchesString(String itemText) {
		if (itemText.length() == 0) {
			return false;
		}
	
		/* filter pattern set */
		boolean filterPatternMatches = filterPattern.matcher(itemText).matches();
		return filterPatternMatches;
	}

	public void setFilterText(String filterText) {
		this.filterPattern = null;
		if (filterText == null) {
			return;
		}
		filterText = filterText.trim();
	
		if (filterText.length() == 0) {
			return;
		}
		/*
		 * make user entry not being a regular expression but simple wild card
		 * handled
		 */
		// change "bla*" to "bla.*"
		// change "bla." to "bla\."
		String newPattern = filterText;
		if (!newPattern.endsWith("*")) {
			newPattern += "*";
		}
		newPattern = PATTERN_DEREGEX_ASTERISK.matcher(newPattern).replaceAll(".*");
		newPattern = PATTERN_DEREGEX_DOT.matcher(newPattern).replaceAll("\\.");
	
		try {
			Pattern filterPattern = Pattern.compile(newPattern, Pattern.CASE_INSENSITIVE);
			this.filterPattern = filterPattern;
		} catch (PatternSyntaxException e) {
			/* ignore - filterPattern is now null, fall back is used */
		}
	}

}
package de.jcup.egradle.core.model;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import de.jcup.egradle.core.api.Matcher;

public class ItemTextMatcher implements Matcher<Item> {
	
	
		private Pattern PATTERN_DEREGEX_ASTERISK = Pattern.compile("\\*");
		private Pattern PATTERN_DEREGEX_DOT = Pattern.compile("\\.");

		private Pattern filterPattern;

		public boolean hasFilterPattern() {
			return filterPattern != null;
		}

		@Override
		public boolean matches(Item item) {
			if (item == null) {
				return false;
			}
			
			if (filterPattern == null) {
				/* no filter matches all...*/
				return true;
			}
			
			String itemText = buildCompareString(item);
			if (itemText.length()==0){
				return false;
			}

			/* filter pattern set */
			boolean filterPatternMatches = filterPattern.matcher(itemText).matches();
			return filterPatternMatches;
		}

		private String buildCompareString(Item item) {
			StringBuilder sb = new StringBuilder();
			String name = item.getName();
			if (name!=null){
				sb.append(name);
			}
			String type = item.getType();
			if (type!=null){
				sb.append(type);
			}
			String target = item.getTarget();
			if (target!=null){
				sb.append(target);
			}
			String itemText = sb.toString();
			return itemText;
		}

		public void setFilterText(String filterText) {
			this.filterPattern = null;
			if (filterText == null) {
				return;
			}
			filterText = filterText.trim();

			if (filterText.length()==0){
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
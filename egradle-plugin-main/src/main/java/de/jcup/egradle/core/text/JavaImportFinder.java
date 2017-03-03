package de.jcup.egradle.core.text;

import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

public class JavaImportFinder {
	
	private static final Pattern PATTERN = Pattern.compile("\\s*import ([a-zA-Z_]*[a-zA-Z0-9\\\\._]*[\\*]?);");

	public Set<String> findImportedPackages(String text) {
		Set<String> set = new TreeSet<>();
		if (text==null){
			return set;
		}
		Matcher m= PATTERN.matcher(text);
		while (m.find()){
			String fullImportName = m.group(1);
			String packageName = StringUtils.substringBeforeLast(fullImportName, ".");
			set.add(packageName);
		}
		return set;
	}
}

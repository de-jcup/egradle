package de.jcup.egradle.core.model.groovyantlr;

import org.codehaus.groovy.antlr.parser.GroovyTokenTypes;

import de.jcup.egradle.core.api.Filter;

public class GradleModelFilters {

	public static final Filter FILTER_IMPORTS = new GroovyTokenTypefilter(GroovyTokenTypes.IMPORT);
}

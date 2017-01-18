package de.jcup.egradle.core.codecompletion.model.gradledsl;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.jcup.egradle.core.api.ErrorHandler;
import de.jcup.egradle.core.codecompletion.model.Type;
import de.jcup.egradle.core.codecompletion.model.TypeProvider;

public class GradleDSLTypeProvider implements TypeProvider {
	/*
	 * FIXME ATR, 13.01.2017: make parts here abstract and rename test - the
	 * resolving is not gradle dsl specific
	 */
	private GradleDSLFileLoader fileLoader;
	private Map<String, Type> nameToTypeMapping;
	private Set<String> unresolveableNames;

	public GradleDSLTypeProvider(GradleDSLFileLoader loader) {
		if (loader == null) {
			throw new IllegalArgumentException("loader may never be null!");
		}
		this.fileLoader = loader;
		nameToTypeMapping = new TreeMap<>();
		unresolveableNames = new TreeSet<>();
	}

	private ErrorHandler errorHandler;

	public void setErrorHandler(ErrorHandler errorHandler) {
		this.errorHandler = errorHandler;
	}

	protected ErrorHandler getErrorHandler() {
		if (errorHandler == null) {
			return ErrorHandler.IGNORE_ERRORS;
		}
		return errorHandler;
	}

	@Override
	public Type getType(String name) {
		Type type = nameToTypeMapping.get(name);
		if (type == null) {
			if (unresolveableNames.contains(name)) {
				/* already tried to load */
				return null;
			}
			/* try to load */
			try {
				type = fileLoader.load(name);
			} catch (IOException e) {
				getErrorHandler().handleError("Cannot load dsl type:"+name, e);
			}
			if (type == null) {
				unresolveableNames.add(name);
			} else {
				nameToTypeMapping.put(name, type);
			}
		}
		return type;
	}

}

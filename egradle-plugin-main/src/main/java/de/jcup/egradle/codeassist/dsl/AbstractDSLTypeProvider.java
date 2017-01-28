package de.jcup.egradle.codeassist.dsl;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import de.jcup.egradle.core.api.ErrorHandler;

public abstract class AbstractDSLTypeProvider implements TypeProvider {

	protected DSLFileLoader fileLoader;
	protected Map<String, Type> nameToTypeMapping;
	protected Set<String> unresolveableNames;
	private ErrorHandler errorHandler;
	private Set<Plugin> plugins;

	public AbstractDSLTypeProvider(DSLFileLoader loader) {
		if (loader == null) {
			throw new IllegalArgumentException("loader may never be null!");
		}
		this.fileLoader = loader;
		nameToTypeMapping = new TreeMap<>();
		unresolveableNames = new TreeSet<>();
	}

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
		if (type != null) {
			return type;
		}
		if (unresolveableNames.contains(name)) {
			/* already tried to load */
			return null;
		}
		/* try to load */
		try {
			type = fileLoader.loadType(name);
		} catch (IOException e) {
			/*
			 * FIXME ATR, 28.01.2017: it can often be normal to not resolve an
			 * external type - handle this  better! e.g. java.lang.* cannot be available...
			 */
			getErrorHandler().handleError("Cannot load dsl type:" + name, e);
		}
		if (type == null) {
			unresolveableNames.add(name);
			return null;
		}

		/* put uninitialized type - so avoiding endless loops ... */
		nameToTypeMapping.put(name, type);
		
		if (plugins==null){
			/* load plugins.xml */
			try {
				plugins = fileLoader.loadPlugins();
			} catch (IOException e) {
				if (errorHandler!=null){
					errorHandler.handleError("Cannot load plugins.xml", e);
				}
				plugins = new LinkedHashSet<>();
			}
		}
		if (!(type instanceof XMLType)) {
			return type;
		}
		
		PluginMerger merger = new PluginMerger(this,getErrorHandler());
		merger.merge(type, plugins);
		
		/* inititialize xml type */
		for (Method m : type.getMethods()) {
			XMLMethod xm = (XMLMethod) m;
			Type resolvedReturnType = getType(xm.getReturnTypeAsString());
			xm.setReturnType(resolvedReturnType);

			for (Parameter p : m.getParameters()) {
				XMLParameter xp = (XMLParameter) p;
				Type resolvedParamType = getType(xp.getTypeAsString());
				xp.setType(resolvedParamType);
			}
		}
		for (Property p : type.getProperties()) {
			XMLProperty xp = (XMLProperty) p;
			Type resolvedReturnType = getType(xp.getTypeAsString());
			xp.setType(resolvedReturnType);
		}
		return type;
	}

}
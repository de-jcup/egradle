package de.jcup.egradle.codecompletion.dsl;

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
			getErrorHandler().handleError("Cannot load dsl type:" + name, e);
		}
		if (type == null) {
			unresolveableNames.add(name);
			return null;
		}

		/* put uninitialized type - so avoiding endless loops ... */
		nameToTypeMapping.put(name, type);
		if (!(type instanceof XMLType)) {
			return type;
		}
		
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
		/* FIXME ATR, 28.01.2017: go on implementation! */
//		for (Plugin plugin: plugins){
//			if (plugin.get)
//		}
		
		/*
		 * FIXME ATR, 19.01.2017: handle mixins etc. - should be done in gralde
		 * impl, or this abstract class has to be removed instead
		 */
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
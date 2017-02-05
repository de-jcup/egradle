package de.jcup.egradle.codeassist.dsl.gradle;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.CodeCompletionRegistry.RegistryEvent;
import de.jcup.egradle.codeassist.CodeCompletionRegistry.RegistryListener;
import de.jcup.egradle.codeassist.CodeCompletionService;
import de.jcup.egradle.codeassist.dsl.DSLFileLoader;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Parameter;
import de.jcup.egradle.codeassist.dsl.Plugin;
import de.jcup.egradle.codeassist.dsl.PluginMerger;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.TypeProvider;
import de.jcup.egradle.codeassist.dsl.XMLMethod;
import de.jcup.egradle.codeassist.dsl.XMLParameter;
import de.jcup.egradle.codeassist.dsl.XMLProperty;
import de.jcup.egradle.codeassist.dsl.XMLType;
import de.jcup.egradle.core.api.ErrorHandler;

public class GradleDSLTypeProvider implements CodeCompletionService, RegistryListener, TypeProvider {

	protected DSLFileLoader fileLoader;
	protected Map<String, Type> nameToTypeMapping;
	protected Set<String> unresolveableNames;
	private ErrorHandler errorHandler;
	private Set<Plugin> plugins;
	private Map<String, String> apiMapping;

	public GradleDSLTypeProvider(DSLFileLoader loader) {
		if (loader == null) {
			throw new IllegalArgumentException("loader may never be null!");
		}
		this.fileLoader = loader;
		nameToTypeMapping = new TreeMap<>();
		unresolveableNames = new TreeSet<>();
	}

	@Override
	public void onRebuild(RegistryEvent event) {
		nameToTypeMapping.clear();
		unresolveableNames.clear();
		plugins = null;
		apiMapping=null;
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
		ensurePluginsLoaded();
		ensureApiMappingLoaded();
		if (StringUtils.isBlank(name)){
			return null;
		}
		Type type = nameToTypeMapping.get(name);
		if (type != null) {
			return type;
		}
		if (unresolveableNames.contains(name)) {
			/* already tried to load */
			return null;
		}
		/* try to load */
		String nameToUseForLoading = name;
		String longName = apiMapping.get(name);
		
		if (longName != null) {
			nameToUseForLoading = longName;
		}
		try {
			type = fileLoader.loadType(nameToUseForLoading);
		} catch (IOException e) {
			// /*
			// * FIXME ATR, 28.01.2017: it can often be normal to not resolve an
			// * external type - handle this better! e.g. java.lang.* cannot be
			// available...
			// */
			// getErrorHandler().handleError("Cannot load dsl type:" + name, e);
			/* ignore */
		}
		if (type == null) {
			unresolveableNames.add(name);
			if (longName != null) {
				unresolveableNames.add(longName);
			}
			return null;
		}

		/* put uninitialized type - so avoiding endless loops ... */
		nameToTypeMapping.put(name, type);

		if (!(type instanceof XMLType)) {
			return type;
		}

		PluginMerger merger = new PluginMerger(this, getErrorHandler());
		merger.merge(type, plugins);

		/* inititialize xml type */
		for (Method m : type.getMethods()) {
			XMLMethod xm = (XMLMethod) m;
			xm.setParent(type);
			Type resolvedReturnType = getType(xm.getReturnTypeAsString());
			xm.setReturnType(resolvedReturnType);
			
			String delegationTargetAsString = xm.getDelegationTargetAsString();
			if (!StringUtils.isBlank(delegationTargetAsString)){
				Type resolvedDelegationTargetType = getType(delegationTargetAsString);
				xm.setDelegationTarget(resolvedDelegationTargetType);
			}
			
			for (Parameter p : m.getParameters()) {
				XMLParameter xp = (XMLParameter) p;
				Type resolvedParamType = getType(xp.getTypeAsString());
				xp.setType(resolvedParamType);
			}
		}
		for (Property p : type.getProperties()) {
			XMLProperty xp = (XMLProperty) p;
			xp.setParent(type);
			Type resolvedReturnType = getType(xp.getTypeAsString());
			xp.setType(resolvedReturnType);
		}
		return type;
	}

	private void ensureApiMappingLoaded() {
		if (apiMapping != null) {
			return;
		}
		try {
			apiMapping = fileLoader.loadApiMappings();
		} catch (IOException e) {
			if (errorHandler != null) {
				errorHandler.handleError("Cannot load api-mappings", e);
			}
			apiMapping = new HashMap<>();
		}
	}

	private void ensurePluginsLoaded() {
		if (plugins != null) {
			return;
		}
		/* load plugins.xml */
		try {
			plugins = fileLoader.loadPlugins();
		} catch (IOException e) {
			if (errorHandler != null) {
				errorHandler.handleError("Cannot load plugins.xml", e);
			}
			plugins = new LinkedHashSet<>();
		}
	}
}

package de.jcup.egradle.codeassist.dsl.gradle;

import java.io.IOException;
import java.util.HashMap;
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
import de.jcup.egradle.codeassist.dsl.ModifiableMethod;
import de.jcup.egradle.codeassist.dsl.ModifiableParameter;
import de.jcup.egradle.codeassist.dsl.ModifiableProperty;
import de.jcup.egradle.codeassist.dsl.ModifiableType;
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
	PluginMerger merger;

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
		apiMapping = null;
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

	protected PluginMerger getPluginMerger() {
		if (merger == null) {
			merger = new PluginMerger(this, getErrorHandler());
		}
		return merger;
	}

	@Override
	public Type getType(String name) {
		ensurePluginsLoaded();
		ensureApiMappingLoaded();
		if (StringUtils.isBlank(name)) {
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

		if (!(type instanceof ModifiableType)) {
			return type;
		}
		ModifiableType modifiableType = (ModifiableType) type;
		String superTypeAsString = type.getSuperTypeAsString();
		if (StringUtils.isNotBlank(superTypeAsString)) {
			Type superType = getType(superTypeAsString);
			if (superType != null) {
				modifiableType.extendBySuperType(superType);
			}
		}

		getPluginMerger().merge(type, plugins);

		/* inititialize xml type */
		for (Method m : type.getMethods()) {
			if (!(m instanceof ModifiableMethod)) {
				continue;
			}
			ModifiableMethod modifiableMethod = (ModifiableMethod) m;
			modifiableMethod.setParent(type);
			Type resolvedReturnType = getType(modifiableMethod.getReturnTypeAsString());
			modifiableMethod.setReturnType(resolvedReturnType);

			String delegationTargetAsString = modifiableMethod.getDelegationTargetAsString();
			if (!StringUtils.isBlank(delegationTargetAsString)) {
				Type resolvedDelegationTargetType = getType(delegationTargetAsString);
				modifiableMethod.setDelegationTarget(resolvedDelegationTargetType);
			}

			for (Parameter p : m.getParameters()) {
				if (!(p instanceof ModifiableParameter)){
					continue;
				}
				ModifiableParameter modifiableParam = (ModifiableParameter) p;
				Type resolvedParamType = getType(modifiableParam.getTypeAsString());
				modifiableParam.setType(resolvedParamType);
			}
		}
		for (Property p : type.getProperties()) {
			if ( !(p instanceof ModifiableProperty)){
				continue;
			}
			ModifiableProperty modifiableProperty = (ModifiableProperty) p;
			modifiableProperty.setParent(type);
			Type resolvedReturnType = getType(modifiableProperty.getTypeAsString());
			modifiableProperty.setType(resolvedReturnType);
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

package de.jcup.egradle.codeassist.dsl.gradle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.CodeCompletionService;
import de.jcup.egradle.codeassist.CodeCompletionRegistry.RegistryEvent;
import de.jcup.egradle.codeassist.CodeCompletionRegistry.RegistryEventType;
import de.jcup.egradle.codeassist.CodeCompletionRegistry.RegistryListener;
import de.jcup.egradle.codeassist.dsl.DSLFileLoader;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.ModifiableMethod;
import de.jcup.egradle.codeassist.dsl.ModifiableParameter;
import de.jcup.egradle.codeassist.dsl.ModifiableProperty;
import de.jcup.egradle.codeassist.dsl.ModifiableType;
import de.jcup.egradle.codeassist.dsl.ModifiableTypeReference;
import de.jcup.egradle.codeassist.dsl.Parameter;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.TypeProvider;
import de.jcup.egradle.codeassist.dsl.TypeReference;
import de.jcup.egradle.core.api.ErrorHandler;

public class GradleDSLTypeProvider implements TypeProvider, RegistryListener,CodeCompletionService {

	protected DSLFileLoader fileLoader;
	protected Map<String, Type> nameToTypeMapping;
	protected Set<String> unresolveableNames;
	private ErrorHandler errorHandler;
	private Map<String, String> apiMapping;

	public GradleDSLTypeProvider(DSLFileLoader loader) {
		if (loader == null) {
			throw new IllegalArgumentException("loader may never be null!");
		}
		this.fileLoader = loader;
		nameToTypeMapping = new TreeMap<>();
		unresolveableNames = new TreeSet<>();

		/* add primitives */
		unresolveableNames.add("void");
		unresolveableNames.add("int");
		unresolveableNames.add("short");
		unresolveableNames.add("char");
		unresolveableNames.add("long");
		unresolveableNames.add("double");
		unresolveableNames.add("byte");
		unresolveableNames.add("float");
		unresolveableNames.add("boolean");
	}

	@Override
	public void onCodeCompletionEvent(RegistryEvent event) {
		if (event.getType() != RegistryEventType.DESTROY) {
			return;
		}
		nameToTypeMapping.clear();
		unresolveableNames.clear();
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

	@Override
	public Type getType(String name) {
		ensureApiMappingLoaded();
		if (StringUtils.isBlank(name)) {
			return null;
		}
		Type type = nameToTypeMapping.get(name);
		if (type != null) {
			return type;
		}
		/* currently no support for java types */
		if (StringUtils.startsWith(name, "java.")) {
			return null;
		}
		/* no support for sun parts too */
		if (StringUtils.startsWith(name, "sun.")) {
			return null;
		}
		if (unresolveableNames.contains(name)) {
			/* already tried to load */
			return null;
		}
		/* try to load */
		String nameToUseForLoading = name;
		String longName = apiMapping.get(name);

		if (longName != null) {
			/* long name wellknown - so load with this one and register short name as well*/
		
			type = getType(longName);
			if (type==null){
				unresolveableNames.add(name);
				return null;
			}
			/* found by long name - so register short name too:*/
			nameToTypeMapping.put(name, type);
			return type;
		}
		
		
		try {
			type = fileLoader.loadType(nameToUseForLoading);
		} catch (IOException e) {
			getErrorHandler().handleError("Cannot load dsl type:" + name, e);
		}
		if (type == null) {
			unresolveableNames.add(name);
			return null;
		}

		/*
		 * Put uninitialized type - so avoiding endless loops while collecting.
		 * This means: no inherited methods etc. But when doing this and a loop
		 * exists, at this time not all information is available and inheritance
		 * will not work
		 */
		nameToTypeMapping.put(name, type);
		System.err.println(hashCode() + ":loaded:" + name + ", nameToUseForLoading=" + nameToUseForLoading + ", type="
				+ type.getName());
		if (!(type instanceof ModifiableType)) {
			return type;
		}
		ModifiableType modifiableType = (ModifiableType) type;

		/* inheritance */
		if (type.isInterface()) {
			/* interface logic */
			Set<TypeReference> interfaces = type.getInterfaces();
			for (TypeReference ref : interfaces) {
				String interfaceAsString = ref.getTypeAsString();
				Type interfaceType = getType(interfaceAsString);
				if (interfaceType != null) {
					modifiableType.extendFromInterface(interfaceType);
				}
			}

		} else {
			String superTypeAsString = type.getSuperTypeAsString();
			/* class */
			if (StringUtils.isNotBlank(superTypeAsString)) {
				Type superType = getType(superTypeAsString);
				if (superType != null) {
					modifiableType.extendFromSuperClass(superType);
				}
			}
		}

		/* initialize type */
		initInterfaceReferences(modifiableType);
		initMethods(type);
		initProperties(type);
		return type;
	}

	/* resolve interface references and setup types */
	private void initInterfaceReferences(ModifiableType modifiableType) {
		for (TypeReference interfaceRef : modifiableType.getInterfaces()) {
			if (!(interfaceRef instanceof ModifiableTypeReference)) {
				continue;
			}
			ModifiableTypeReference modInterfaceRef = (ModifiableTypeReference) interfaceRef;
			String interfaceTypeAsString = interfaceRef.getTypeAsString();

			if (StringUtils.isBlank(interfaceTypeAsString)) {
				continue;
			}
			Type resolvedInterfaceRefType = getType(interfaceTypeAsString);
			modInterfaceRef.setType(resolvedInterfaceRefType);
		}
	}

	private void initMethods(Type type) {
		for (Method m : type.getDefinedMethods()) {
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
				if (!(p instanceof ModifiableParameter)) {
					continue;
				}
				ModifiableParameter modifiableParam = (ModifiableParameter) p;
				Type resolvedParamType = getType(modifiableParam.getTypeAsString());
				modifiableParam.setType(resolvedParamType);
			}
		}
	}

	private void initProperties(Type type) {
		for (Property p : type.getDefinedProperties()) {
			if (!(p instanceof ModifiableProperty)) {
				continue;
			}
			ModifiableProperty modifiableProperty = (ModifiableProperty) p;
			modifiableProperty.setParent(type);
			Type resolvedReturnType = getType(modifiableProperty.getTypeAsString());
			modifiableProperty.setType(resolvedReturnType);
		}
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

}

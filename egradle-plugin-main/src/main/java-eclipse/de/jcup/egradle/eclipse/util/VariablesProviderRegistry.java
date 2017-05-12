package de.jcup.egradle.eclipse.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

import de.jcup.egradle.eclipse.api.VariableProvider;

public class VariablesProviderRegistry {

	private static final String VARIABLE_PROVIDER_ID = "de.jcup.egradle.eclipse.extension.variableprovider";
	public static VariablesProviderRegistry INSTANCE = new VariablesProviderRegistry(); 
	
	private List<VariableProvider> providers;
	
	public VariablesProviderRegistry() {
	
		List<VariableProvider> variableProviders=new ArrayList<>();
		
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IConfigurationElement[] config = registry.getConfigurationElementsFor(VARIABLE_PROVIDER_ID);
		try {
			for (IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("class");
				if (o instanceof VariableProvider) {
					VariableProvider provider = (VariableProvider) o;
					variableProviders.add(provider);
				}
			}
			
		} catch (CoreException ex) {
			EclipseUtil.logError("Was not able to initialize variable providers registry", ex);
		}
		
		providers = Collections.unmodifiableList(variableProviders);
		
	}

	/**
	 * Returns registered providers as an immutable list
	 * @return unmodifiable list of providers
	 */
	public List<VariableProvider> getProviders() {
		return providers;
	}
}

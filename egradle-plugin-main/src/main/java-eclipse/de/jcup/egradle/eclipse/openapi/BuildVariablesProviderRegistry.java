package de.jcup.egradle.eclipse.openapi;

public class BuildVariablesProviderRegistry {

	private static BuildVariablesProvider provider;
	
	public static void setProvider(BuildVariablesProvider provider) {
		BuildVariablesProviderRegistry.provider = provider;
	}
	
	public static BuildVariablesProvider getProvider() {
		return provider;
	}
}

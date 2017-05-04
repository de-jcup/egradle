package de.jcup.egradle.eclipse.openapi;

public class BuildVariablesProviderRegistry {

	private static BuildVariablesProvider provider;
	
	/* TODO ATR, 04.05.2017: this should be solved by an extension point */
	public static void setProvider(BuildVariablesProvider provider) {
		BuildVariablesProviderRegistry.provider = provider;
	}
	
	public static BuildVariablesProvider getProvider() {
		return provider;
	}
}

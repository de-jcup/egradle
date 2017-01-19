package de.jcup.egradle.codecompletion.dsl.gradle;

import de.jcup.egradle.codecompletion.CodeCompletionService;
import de.jcup.egradle.codecompletion.CodeCompletionRegistry.RegistryEvent;
import de.jcup.egradle.codecompletion.CodeCompletionRegistry.RegistryListener;
import de.jcup.egradle.codecompletion.dsl.AbstractDSLTypeProvider;
import de.jcup.egradle.codecompletion.dsl.DSLFileLoader;

public class GradleDSLTypeProvider extends AbstractDSLTypeProvider implements CodeCompletionService, RegistryListener {

	public GradleDSLTypeProvider(DSLFileLoader loader) {
		super(loader);
	}

	@Override
	public void onRebuild(RegistryEvent event) {
		nameToTypeMapping.clear();
		unresolveableNames.clear();
	}
	
	

}

package de.jcup.egradle.codeassist.dsl.gradle;

import de.jcup.egradle.codeassist.CodeCompletionService;
import de.jcup.egradle.codeassist.CodeCompletionRegistry.RegistryEvent;
import de.jcup.egradle.codeassist.CodeCompletionRegistry.RegistryListener;
import de.jcup.egradle.codeassist.dsl.AbstractDSLTypeProvider;
import de.jcup.egradle.codeassist.dsl.DSLFileLoader;

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

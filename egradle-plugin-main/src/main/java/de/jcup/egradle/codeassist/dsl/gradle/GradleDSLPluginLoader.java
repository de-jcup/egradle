package de.jcup.egradle.codeassist.dsl.gradle;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import de.jcup.egradle.codeassist.CodeCompletionRegistry;
import de.jcup.egradle.codeassist.CodeCompletionRegistry.RegistryEvent;
import de.jcup.egradle.codeassist.CodeCompletionRegistry.RegistryEventType;
import de.jcup.egradle.codeassist.CodeCompletionRegistry.RegistryListener;
import de.jcup.egradle.codeassist.CodeCompletionService;
import de.jcup.egradle.codeassist.dsl.DSLFileLoader;
import de.jcup.egradle.codeassist.dsl.ModifiableType;
import de.jcup.egradle.codeassist.dsl.Plugin;
import de.jcup.egradle.codeassist.dsl.PluginMerger;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.TypeExtension;
import de.jcup.egradle.core.api.ErrorHandler;

public class GradleDSLPluginLoader implements CodeCompletionService, RegistryListener {

	protected DSLFileLoader fileLoader;
	private ErrorHandler errorHandler;

	public GradleDSLPluginLoader(DSLFileLoader loader) {
		if (loader == null) {
			throw new IllegalArgumentException("loader may never be null!");
		}
		this.fileLoader = loader;
	}

	@Override
	public void onCodeCompletionEvent(RegistryEvent event) {
		if (event.getType()!=RegistryEventType.LOAD_PLUGINS){
			return;
		}
		CodeCompletionRegistry registry = event.getRegistry();
		GradleDSLTypeProvider typeProvider = registry.getService(GradleDSLTypeProvider.class);
		PluginMerger merger = new PluginMerger(typeProvider, getErrorHandler());
		
		Set<Plugin> plugins;
		/* load plugins.xml */
		try {
			plugins = fileLoader.loadPlugins();
		} catch (IOException e) {
			if (errorHandler != null) {
				errorHandler.handleError("Cannot load plugins.xml", e);
			}
			plugins = new LinkedHashSet<>();
		}
		
		for (Plugin plugin: plugins){
			for (TypeExtension extension: plugin.getExtensions()){
				String targetTypeAsString = extension.getTargetTypeAsString();
				Type targetType = typeProvider.getType(targetTypeAsString);
				if (targetType==null){
					getErrorHandler().handleError("Plugin loader:Target type:"+targetTypeAsString+" for plugin:"+plugin.getId()+" not found");
					continue;
				}
				if (!( targetType instanceof ModifiableType)){
					getErrorHandler().handleError("Plugin loader:Target type:"+targetTypeAsString+" for plugin:"+plugin.getId()+" is not modifiable!");
					continue;
				}
				ModifiableType modifiableType = (ModifiableType) targetType;
				
				merger.merge(modifiableType, plugin, extension);
				
			}
		}
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

	

}

package de.jcup.egradle.codecompletion.dsl;

import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.api.ErrorHandler;

public class PluginMerger{

	private TypeProvider provider;
	private ErrorHandler errorHandler;

	public PluginMerger(TypeProvider provider, ErrorHandler errorHandler) {
		if (provider==null){
			throw new IllegalArgumentException("provider must not be null!");
		}
		if (errorHandler==null){
			throw new IllegalArgumentException("error handler must not be null!");
		}
		this.provider=provider;
		this.errorHandler = errorHandler;
	}
	
	public void merge(Type type, Set<Plugin> plugins) {
		if (type==null){
			return;
		}

		if (plugins==null){
			return;
		}
		
		if (plugins.isEmpty()){
			return;
		}
	
		String typeAsString = type.getName();
		
		for (Plugin plugin: plugins){
			Set<TypeExtension> extensions = plugin.getExtensions();
			if (extensions.isEmpty()){
				continue;
			}
			for (TypeExtension typeExtension: extensions){
				if (!typeAsString.equals(typeExtension.getTargetTypeAsString())){
					continue;
				}
				handleMixin(plugin, type, typeExtension);
				handleExtension(plugin, type, typeExtension);
				
			}
		
		}
	}

	private String handleMixin(Plugin plugin, Type type, TypeExtension typeExtension) {
		String mixinTypeAsString = typeExtension.getMixinTypeAsString();
		if (! StringUtils.isBlank(mixinTypeAsString)){
			/* resolve type by provider*/
			Type mixinType = provider.getType(mixinTypeAsString);
			if (mixinType==null){
				errorHandler.handleError("mixin type not found by provider:"+mixinTypeAsString);
			}else{
				type.mixin(mixinType, new ReasonImpl().setPlugin(plugin));
			}
		}
		return mixinTypeAsString;
	}
	
	private String handleExtension(Plugin plugin, Type type, TypeExtension typeExtension) {
		String extensionTypeAsString = typeExtension.getExtensionTypeAsString();
		if (! StringUtils.isBlank(extensionTypeAsString)){
			/* resolve type by provider*/
			Type extensionType = provider.getType(extensionTypeAsString);
			if (extensionType==null){
				errorHandler.handleError("extension type not found by provider:"+extensionTypeAsString);
			}else{
				String extensionId= typeExtension.getId();
				type.addExtension(extensionId, extensionType, new ReasonImpl().setPlugin(plugin));
			}
		}
		return extensionTypeAsString;
	}

}

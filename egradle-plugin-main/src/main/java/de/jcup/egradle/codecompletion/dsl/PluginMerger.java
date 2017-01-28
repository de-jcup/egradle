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
			for (TypeExtension extension: extensions){
				if (!typeAsString.equals(extension.getTargetTypeAsString())){
					continue;
				}
				String mixinTypeAsString = extension.getMixinTypeAsString();
				if (! StringUtils.isBlank(mixinTypeAsString)){
					/* resolve type by provider*/
					Type mixinType = provider.getType(mixinTypeAsString);
					if (mixinType==null){
						errorHandler.handleError("mixin type not found by provider:"+mixinTypeAsString);
					}else{
						type.mixin(mixinType);
					}
				}
			}
		
		}
	}

}

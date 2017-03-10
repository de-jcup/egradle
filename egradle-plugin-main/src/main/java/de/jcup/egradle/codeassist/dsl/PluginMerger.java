package de.jcup.egradle.codeassist.dsl;

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
	
	/**
	 * Merges mixin types from plugins into target type if necessary
	 * @param potentialTargetType
	 * @param plugins
	 */
	public void merge(Type potentialTargetType, Set<Plugin> plugins) {
		if (potentialTargetType==null){
			return;
		}

		if (plugins==null){
			return;
		}
		
		if (plugins.isEmpty()){
			return;
		}
		
		if (! (potentialTargetType instanceof ModifiableType)){
			return;
		}
	
		ModifiableType modifiableType = (ModifiableType) potentialTargetType;
		String typeAsString = modifiableType.getName();
		
		for (Plugin plugin: plugins){
			Set<TypeExtension> extensions = plugin.getExtensions();
			if (extensions.isEmpty()){
				continue;
			}
			for (TypeExtension typeExtension: extensions){
				if (!typeAsString.equals(typeExtension.getTargetTypeAsString())){
					continue;
				}
				/* ok, is target type so do mixin and extension */
				merge(modifiableType, plugin, typeExtension);
				
			}
		
		}
	}

	public void merge(ModifiableType modifiableType, Plugin plugin, TypeExtension typeExtension) {
		handleMixin(plugin, modifiableType, typeExtension);
		handleExtension(plugin, modifiableType, typeExtension);
	}

	private String handleMixin(Plugin plugin, ModifiableType type, TypeExtension typeExtension) {
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
	
	private String handleExtension(Plugin plugin, ModifiableType type, TypeExtension typeExtension) {
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

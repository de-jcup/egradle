/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.egradle.codeassist.dsl;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.core.util.ErrorHandler;

/**
 * The plugin merger integrates plugin meta information into types provided by
 * type provider. So e.g. plugin "alpha" which has an extension for
 * "org.gradle.api.Project" with "de.jcup.gradle.plugin1.ExtensionClass" and
 * extension id "beta", will add methods and properties from "ExtensionClass" to
 * "Project" for extension "beta", so its available in code assistance.<br>
 * <br>
 * The older concept of "conventions" is also done by this merger.
 * 
 * @author Albert Tregnaghi
 *
 */
public class PluginMerger {

    private TypeProvider provider;
    private ErrorHandler errorHandler;

    public PluginMerger(TypeProvider provider, ErrorHandler errorHandler) {
        if (provider == null) {
            throw new IllegalArgumentException("provider must not be null!");
        }
        if (errorHandler == null) {
            throw new IllegalArgumentException("error outputHandler must not be null!");
        }
        this.provider = provider;
        this.errorHandler = errorHandler;
    }

    public void merge(Set<Plugin> plugins) {
        Set<Type> set = new LinkedHashSet<>();

        /* collect all target types */
        for (Plugin plugin : plugins) {
            for (TypeExtension extension : plugin.getExtensions()) {
                String targetTypeAsString = extension.getTargetTypeAsString();
                Type targetType = provider.getType(targetTypeAsString);
                set.add(targetType);
            }
        }

        /* now for each type we do the merge */
        for (Type type : set) {
            merge(type, plugins);
        }
    }

    /**
     * Merges mixin types from plugins into target type if necessary
     * 
     * @param potentialTargetType
     * @param plugins
     */
    void merge(Type potentialTargetType, Set<Plugin> plugins) {
        if (potentialTargetType == null) {
            return;
        }

        if (plugins == null) {
            return;
        }

        if (plugins.isEmpty()) {
            return;
        }

        if (!(potentialTargetType instanceof ModifiableType)) {
            return;
        }

        ModifiableType modifiableType = (ModifiableType) potentialTargetType;
        String typeAsString = modifiableType.getName();

        for (Plugin plugin : plugins) {
            Set<TypeExtension> extensions = plugin.getExtensions();
            if (extensions.isEmpty()) {
                continue;
            }
            for (TypeExtension typeExtension : extensions) {
                if (!typeAsString.equals(typeExtension.getTargetTypeAsString())) {
                    continue;
                }
                /* ok, is target type so do mixin and extension */
                merge(modifiableType, plugin, typeExtension);

            }

        }
    }

    public void merge(ModifiableType modifiableType, Plugin plugin, TypeExtension typeExtension) {
        handleConvention(plugin, modifiableType, typeExtension);
        handleExtension(plugin, modifiableType, typeExtension);
    }

    /**
     * Handle convention parts. <br>
     * <br>
     * A can look <a href=
     * "http://hamletdarcy.blogspot.de/2010/03/gradle-plugin-conventions-groovy-magic.html">here</a>
     * for a good explanation about conventions and usage. Conventions are an older
     * feature of gradle - newer implementations do normally use extensions. <br>
     * <br>
     * Here is an example:
     * 
     * <pre>
     apply plugin: GreetingPlugin
    
    	greet {
    	  message = 'Hi from Gradle'
    	}
    	
    	class GreetingPlugin implements Plugin<Project> {
    	  def void apply(Project project) {
    	
    	      project.convention.plugins.greeting = new GreetingPluginConvention()
    	      project.task('hello') << {
    	          println project.convention.plugins.greeting.message
    	      }
    	  }
    	}
    	
    	class GreetingPluginConvention {
    	  String message
    	
    	  def greet(Closure closure) {
    	      closure.delegate = this
    	      closure()
    	  }
    	}
     * </pre>
     * 
     * So what is different to extensions? It is directly added to the target class
     * and not as new property like extensions!
     * 
     * Example in DSL for <a href=
     * "https://docs.gradle.org/3.0/dsl/org.gradle.api.Project.html#org.gradle.api.Project:allprojects(groovy.lang.Closure)">allProjects</a>.
     * Look at "Methods added by the ear plugin" has "appDirName(..). which is
     * directly added to projects.
     * 
     * 
     * @param plugin
     * @param type
     * @param typeExtension
     * @return
     */
    private String handleConvention(Plugin plugin, ModifiableType type, TypeExtension typeExtension) {
        String mixinTypeAsString = typeExtension.getMixinTypeAsString();
        if (!StringUtils.isBlank(mixinTypeAsString)) {
            /* resolve type by provider */
            Type mixinType = provider.getType(mixinTypeAsString);
            if (mixinType == null) {
                errorHandler.handleError("mixin type not found by provider:" + mixinTypeAsString);
            } else {
                ReasonImpl reason = new ReasonImpl().setPlugin(plugin);
                String mixinId = typeExtension.getId();
                if (mixinId == null) {
                    /* fallback to plugin id */
                    mixinId = plugin.getId();
                }
                reason.setMixinId(mixinId);
                type.mixin(mixinType, reason);
            }
        }
        return mixinTypeAsString;
    }

    private String handleExtension(Plugin plugin, ModifiableType type, TypeExtension typeExtension) {
        String extensionTypeAsString = typeExtension.getExtensionTypeAsString();
        if (!StringUtils.isBlank(extensionTypeAsString)) {
            /* resolve type by provider */
            Type extensionType = provider.getType(extensionTypeAsString);
            if (extensionType == null) {
                errorHandler.handleError("extension type not found by provider:" + extensionTypeAsString);
            } else {
                String extensionId = typeExtension.getId();
                type.addExtension(extensionId, extensionType, new ReasonImpl().setPlugin(plugin));
            }
        }
        return extensionTypeAsString;
    }

}

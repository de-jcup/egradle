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
package de.jcup.egradle.integration;

import static org.junit.Assert.*;

import java.util.Set;

import de.jcup.egradle.codeassist.dsl.Plugin;
import de.jcup.egradle.codeassist.dsl.TypeExtension;

public class PluginsAssert {

	private Set<Plugin> plugins;

	public PluginsAssert(Set<Plugin> plugins) {
		this.plugins = plugins;
	}

	public static PluginsAssert assertPlugins(Set<Plugin> plugins) {
		if (plugins == null) {
			throw new IllegalArgumentException("plugins parameter may not be null!");
		}
		PluginsAssert pluginsAssert = new PluginsAssert(plugins);
		return pluginsAssert;
	}

	public PluginAssert hasPlugin(String pluginId) {
		if (pluginId == null) {
			throw new IllegalArgumentException("no plugin id given - test corrupt?");
		}
		for (Plugin plugin : plugins) {
			if (plugin.getId().equals(pluginId)) {
				return new PluginAssert(plugin);
			}
		}
		fail("No plugin found with id:" + pluginId);
		return null;
	}

	private enum CheckType {
		MIXIN, EXTENSION
	}

	public class PluginAssert {

		private Plugin plugin;

		public PluginAssert(Plugin plugin) {
			this.plugin = plugin;
		}

		public PluginAssert hasExtensions(int amount) {
			Set<TypeExtension> extensions = plugin.getExtensions();
			assertNotNull(extensions);
			assertEquals(amount, extensions.size());
			return this;
		}

		public PluginAssert withExtensionType(String targetClass, String extensionType) {
			return hasMixinOrExtension(CheckType.EXTENSION, null, extensionType, targetClass);
		}

		public PluginAssert withMixinType(String targetClass, String mixinType) {
			return hasMixinOrExtension(CheckType.MIXIN, mixinType, null, targetClass);
		}

		private PluginAssert hasMixinOrExtension(CheckType type, String mixinType, String extensionType,
				String targetClass) {
			Set<TypeExtension> extensions = plugin.getExtensions();
			assertNotNull(extensions);
			if (targetClass == null) {
				throw new IllegalArgumentException("test case corrupt- no target class set");
			}
			if (type == null) {
				throw new IllegalArgumentException("test case corrupt");
			}
			if (type == CheckType.MIXIN) {
				if (mixinType == null) {
					throw new IllegalArgumentException("mixin type not set in test - tescase corrupt?");
				}
			}
			if (type == CheckType.EXTENSION) {
				if (extensionType == null) {
					throw new IllegalArgumentException("mixin type not set in test - tescase corrupt?");
				}
			}
			for (TypeExtension te : extensions) {
				if (targetClass.equals(te.getTargetTypeAsString())) {
					if (type == CheckType.MIXIN && mixinType.equals(te.getMixinTypeAsString())) {
						/* ok */
						return this;
					}
					if (type == CheckType.EXTENSION && extensionType.equals(te.getExtensionTypeAsString())) {
						/* ok */
						return this;
					}
				}
			}
			if (type == CheckType.MIXIN) {
				fail("Plugin '" + plugin.getId() + "' has not mixin type:" + mixinType);
			}
			if (type == CheckType.EXTENSION) {
				/* ok */
				fail("Plugin '" + plugin.getId() + "' has not extension type:" + extensionType);
			}
			throw new IllegalStateException("This point should be never reached!");
		}

	}
}

/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
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
 package de.jcup.egradle.codeassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class DefaultTypeTemplatesProvider implements TypeTemplatesProvider {

	/*
	 * TODO ATR, 27.02.2017: THINK ABOUT: maybe getTemplateFor(Type type) would
	 * be better! This would make inheritance possible - e.g. for Project and
	 * plugin aware!
	 */
	private static final String DEPENDENCY_HANDLER = "org.gradle.api.artifacts.dsl.DependencyHandler";
	private static final String PLUGIN_AWARE = "org.gradle.api.plugins.PluginAware";
	private static final String PROJECT = "org.gradle.api.Project";
	private Map<String, List<Template>> map = new TreeMap<>();

	public DefaultTypeTemplatesProvider() {
		addDefaultsForDependencies();
		addDefaultsForApplys();

	}

	private void addDefaultsForDependencies() {
		createAndAddTemplate(DEPENDENCY_HANDLER, "compile '$cursor'");
		createAndAddTemplate(DEPENDENCY_HANDLER, "compile project(':$cursor')");
		createAndAddTemplate(DEPENDENCY_HANDLER, "runtime '$cursor'");
		createAndAddTemplate(DEPENDENCY_HANDLER, "testCompile '$cursor'");
		createAndAddTemplate(DEPENDENCY_HANDLER, "testCompile project(':$cursor')");
		createAndAddTemplate(DEPENDENCY_HANDLER, "testRuntime '$cursor'");
	}

	private void addDefaultsForApplys() {
		Template applyPluginTemplate = createTemplate("apply plugin:'$cursor'");
		addTemplate(PLUGIN_AWARE, applyPluginTemplate);
		addTemplate(PROJECT, applyPluginTemplate); // necessary, because
													// currently no inheritance
													// supported

		Template applyFromTemplate = createTemplate("apply from '$cursor'");
		addTemplate(PLUGIN_AWARE, applyFromTemplate);
		addTemplate(PROJECT, applyFromTemplate); // necessary, because currently
													// no inheritance supported
	}

	@Override
	public List<Template> getTemplatesForType(String type) {
		List<Template> templates = map.get(type);
		if (templates == null) {
			return Collections.emptyList();
		}
		return templates;
	}

	private Template createTemplate(String content) {
		return createTemplate(content, content);
	}

	private void createAndAddTemplate(String type, String content) {
		createAndAddTemplate(type, content, content);
	}

	private Template createTemplate(String name, String content) {
		Template template = new Template(name, content);
		return template;
	}

	private Template createAndAddTemplate(String type, String name, String content) {
		Template template = createTemplate(name, content);
		addTemplate(type, template);
		return template;
	}

	private void addTemplate(String type, Template template) {
		List<Template> templates = map.get(type);
		if (templates == null) {
			templates = new ArrayList<>();
			map.put(type, templates);
		}

		templates.add(template);
	}

}

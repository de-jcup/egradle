package de.jcup.egradle.codeassist;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class DefaultTypeTemplatesProvider implements TypeTemplatesProvider {

	private static final String DEPENDENCY_HANDLER = "org.gradle.api.artifacts.dsl.DependencyHandler";
	private Map<String, List<Template>> map = new TreeMap<>();

	public DefaultTypeTemplatesProvider() {
		createAndAddTemplate(DEPENDENCY_HANDLER, "compile '$cursor'");
		createAndAddTemplate(DEPENDENCY_HANDLER, "compile project(':$cursor'");
		createAndAddTemplate(DEPENDENCY_HANDLER, "runtime '$cursor'");
		createAndAddTemplate(DEPENDENCY_HANDLER, "testCompile '$cursor'");
		createAndAddTemplate(DEPENDENCY_HANDLER, "testCompile project(':$cursor'");
		createAndAddTemplate(DEPENDENCY_HANDLER, "testRuntime '$cursor'");
	}

	@Override
	public List<Template> getTemplatesForType(String type) {
		List<Template> templates = map.get(type);
		if (templates == null) {
			return Collections.emptyList();
		}
		return templates;
	}

	private void createAndAddTemplate(String type, String content) {
		createAndAddTemplate(type, content, content);
	}

	private void createAndAddTemplate(String type, String name, String content) {
		List<Template> templates = map.get(type);
		if (templates == null) {
			templates = new ArrayList<>();
			map.put(type, templates);
		}
		Template template = new Template(name, content);

		templates.add(template);
	}

}

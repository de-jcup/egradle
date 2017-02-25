package de.jcup.egradle.codeassist;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class DefaultTypeTemplatesProviderTest {
	
	private TypeTemplatesProvider providerToTest;

	@Before
	public void before() {
		providerToTest = new DefaultTypeTemplatesProvider();
	}

	@Test
	public void test_contains_data_for_dependency_handler() {
		List<Template> templates = providerToTest.getTemplatesForType("org.gradle.api.artifacts.dsl.DependencyHandler");
		assertNotNull(templates);
		assertTrue((templates.size()>1));
	}

}

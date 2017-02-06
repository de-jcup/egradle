package de.jcup.egradle.codeassist;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater.EstimationResult;
import de.jcup.egradle.codeassist.hover.HoverData;

public class HoverIntegrationTest {

	private IntegrationTestComponents components;

	@Before
	public void before() {
		components = IntegrationTestComponents.INSTANCE;
	}

	@Test
	public void test_hoverdata__for_buildfile__with_dependencies_in_root__when_cursor_is_at_dependencies() {
		testDependenciesElementAvailableWithAllData("integration/test1-dependencies-block-inside-root.gradle");
	}
	
	@Test
	public void test_hoverdata__for_buildfile__with_dependencies_in_subprojects__when_cursor_is_at_dependencies() {
		testDependenciesElementAvailableWithAllData("integration/test2-dependencies-block-inside-subprojects.gradle");
	}
	
	@Test
	public void test_hoverdata__for_buildfile__with_dependencies_in_allprojects__when_cursor_is_at_dependencies() {
		testDependenciesElementAvailableWithAllData("integration/test3-dependencies-block-inside-allprojects.gradle");
	}

	private void testDependenciesElementAvailableWithAllData(String path) {
		/* prepare */
		String text = components.loadTestFile(path);
		assertNotNull("testcase corrupt", text);
		int offset = text.indexOf("dependencies") + 1;

		/* execute */
		HoverData hoverData = components.getHoverSupport().caclulateHoverData(text, offset, components.getRelevantCodeCutter(),
				components.buildModel(text), GradleFileType.GRADLE_BUILD_SCRIPT, components.getEstimator());
		
		/* test */
		assertNotNull(hoverData);
		EstimationResult result = hoverData.getResult();
		assertNotNull(result);
		Type type = result.getElementType();
		
		assertNotNull(type);
		assertEquals("org.gradle.api.artifacts.dsl.DependencyHandler",type.getName());
		
		LanguageElement element = result.getElement();
		assertNotNull(element);
		assertEquals("dependencies",element.getName());
		
		assertTrue(element instanceof Method);
		Method method = (Method) element;
		
		Type parent = method.getParent();
		assertNotNull(parent);
		assertEquals("org.gradle.api.Project",parent.getName());
	}
}

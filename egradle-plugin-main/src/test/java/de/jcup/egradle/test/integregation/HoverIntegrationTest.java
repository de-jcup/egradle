package de.jcup.egradle.test.integregation;

import static de.jcup.egradle.test.integregation.HoverDataAssert.assertThat;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.hover.HoverData;

public class HoverIntegrationTest {

	private IntegrationTestComponents components;

	@Before
	public void before() {
		components = IntegrationTestComponents.INSTANCE;
	}

	@Test
	public void buildfile__with_dependencies_in_root__when_cursor_is_at_dependencies_offset_exactly__has_correct_hoverdata() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test1-dependencies-block-inside-root.gradle");
		int offset = text.indexOf("dependencies");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		assertThat(hoverData).isForMethod("org.gradle.api.Project.dependencies", "groovy.lang.Closure");
	}
	
	@Test
	public void buildfile__with_dependencies_in_root__when_cursor_is_at_gradleApi_offset_exactly__has_correct_hoverdata() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test7-dependencies-block-inside-root-with-gradleApi.gradle");
		int offset = text.indexOf("gradleApi");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		assertThat(hoverData).isForMethod("org.gradle.api.artifacts.dsl.DependencyHandler.gradleApi");
	}
	
	@Test
	public void buildfile__with_dependencies_in_root__when_cursor_is_at_dependencies_offset_negative_one__no_hoverdata() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test1-dependencies-block-inside-root.gradle");
		int offset = text.indexOf("dependencies");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset-1);

		/* test */
		assertNull(hoverData);
		
	}
	
	@Test
	public void buildfile__with_dependencies_in_root__when_cursor_is_at_dependencies_offset_more_than_length_of_dependencies_string__no_hoverdata() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test1-dependencies-block-inside-root.gradle");
		int offset = text.indexOf("dependencies");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset+"dependencies".length()+1);

		/* test */
		assertNull(hoverData);
		
	}
	
	@Test
	public void buildfile__with_dependencies_in_root__when_cursor_is_at_dependencies_offset_plus1__has_correct_hoverdata() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test1-dependencies-block-inside-root.gradle");
		int offset = text.indexOf("dependencies");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset+1);

		/* test */
		assertThat(hoverData).isForMethod("org.gradle.api.Project.dependencies", "groovy.lang.Closure");
		
	}
	
	@Test
	public void buildfile__with_dependencies_in_root__when_cursor_is_at_dependencies_offset_plus2__has_correct_hoverdata() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test1-dependencies-block-inside-root.gradle");
		int offset = text.indexOf("dependencies");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset+2);

		/* test */
		assertThat(hoverData).isForMethod("org.gradle.api.Project.dependencies", "groovy.lang.Closure");
		
	}
	
	@Test
	public void buildfile__with_dependencies_in_root__when_cursor_is_at_dependencies_offset_plus_length_of_dependencies__has_correct_hoverdata() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test1-dependencies-block-inside-root.gradle");
		int offset = text.indexOf("dependencies");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset+"dependencies".length());

		/* test */
		assertThat(hoverData).isForMethod("org.gradle.api.Project.dependencies", "groovy.lang.Closure");
		
	}

	@Test
	public void buildfile__with_dependencies_in_subprojects__when_cursor_is_at_dependencies() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test2-dependencies-block-inside-subprojects.gradle");
		int offset = text.indexOf("dependencies");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		assertThat(hoverData).isForMethod("org.gradle.api.Project.dependencies", "groovy.lang.Closure");
	}

	@Test
	public void buildfile__with_dependencies_in_allprojects__when_cursor_is_at_dependencies() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test3-dependencies-block-inside-allprojects.gradle");
		int offset = text.indexOf("dependencies");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		assertThat(hoverData).isForMethod("org.gradle.api.Project.dependencies", "groovy.lang.Closure");
	}

	@Test
	public void buildfile__with_dependencies_in_allprojects__when_cursor_is_at_allprojects() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test3-dependencies-block-inside-allprojects.gradle");
		int offset = text.indexOf("allprojects");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		/* @formatter:off*/
		assertThat(hoverData).isForMethod("org.gradle.api.Project.allprojects","groovy.lang.Closure");
		/* @formatter:on*/

	}

	@Test
	public void buildfile__with_repositories_in_root__when_cursor_is_at_repositories() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test4-repositories-block-inside-root.gradle");
		int offset = text.indexOf("repositories");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		/* @formatter:off*/
		assertThat(hoverData).isForMethod("org.gradle.api.Project.repositories","groovy.lang.Closure");
		/* @formatter:on*/

	}

	@Test
	public void buildfile__with_repositories_in_subprojects__when_cursor_is_at_repositories() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test5-repositories-block-inside-subprojects.gradle");
		int offset = text.indexOf("repositories");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		/* @formatter:off*/
		assertThat(hoverData).isForMethod("org.gradle.api.Project.repositories","groovy.lang.Closure");
		/* @formatter:on*/

	}

	@Test
	public void buildfile__with_repositories_in_root__when_cursor_is_at_mavenLocal() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test4-repositories-block-inside-root.gradle");
		int offset = text.indexOf("mavenLocal");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		/* @formatter:off*/
		assertThat(hoverData).isForMethod("org.gradle.api.artifacts.dsl.RepositoryHandler.mavenLocal");
		/* @formatter:on*/

	}

	@Test
	public void buildfile__with_repositories_in_subprojects__when_cursor_is_at_mavenLocal() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test4-repositories-block-inside-root.gradle");
		int offset = text.indexOf("mavenLocal");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		/* @formatter:off*/
		assertThat(hoverData).isForMethod("org.gradle.api.artifacts.dsl.RepositoryHandler.mavenLocal");
		/* @formatter:on*/

	}

	@Test
	public void buildfile__with_repositories_in_root__when_cursor_is_at_mavenCentral() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test4-repositories-block-inside-root.gradle");
		int offset = text.indexOf("mavenCentral");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		/* @formatter:off*/
		assertThat(hoverData).isForMethod("org.gradle.api.artifacts.dsl.RepositoryHandler.mavenCentral");
		/* @formatter:on*/

	}

	@Test
	public void buildfile__with_repositories_in_subprojects__when_cursor_is_at_mavenCentral() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test5-repositories-block-inside-subprojects.gradle");
		int offset = text.indexOf("mavenCentral");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		/* @formatter:off*/
		assertThat(hoverData).isForMethod("org.gradle.api.artifacts.dsl.RepositoryHandler.mavenCentral");
		/* @formatter:on*/

	}
	
	@Test
	public void buildfile__with_repositories_in_subprojects__when_cursor_is_at_first_mavenCentral_with_parameters() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test6-repositories-block-in-root-with-mavencentral-with-parameters.gradle");
		int offset = text.indexOf("mavenCentral");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		/* @formatter:off*/
		assertThat(hoverData).isForMethod("org.gradle.api.artifacts.dsl.RepositoryHandler.mavenCentral","java.util.Map");
		/* @formatter:on*/

	}
	
	@Test
	public void buildfile__with_repositories_in_subprojects__when_cursor_is_at_second_mavenCentral_with_parameters() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test6-repositories-block-in-root-with-mavencentral-with-parameters.gradle");
		int offset = text.lastIndexOf("mavenCentral");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		/* @formatter:off*/
		assertThat(hoverData).isForMethod("org.gradle.api.artifacts.dsl.RepositoryHandler.mavenCentral","java.util.Map");
		/* @formatter:on*/

	}

	/* short cut method for calculation */
	private HoverData calculateHoverData(String text, int offset) {
		return components.getHoverSupport().caclulateHoverData(text, offset, components.getRelevantCodeCutter(),
				components.buildModel(text), GradleFileType.GRADLE_BUILD_SCRIPT, components.getEstimator());
	}

	private String loadTextFromIntegrationTestFile(String testFileName) {
		String text = components.loadTestFile("integration/" + testFileName);
		assertNotNull("testcase corrupt", text);
		return text;
	}
}

package de.jcup.egradle.integration.test;

import static de.jcup.egradle.integration.HoverDataAssert.*;
import static org.junit.Assert.*;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import de.jcup.egradle.codeassist.RelevantCodeCutter;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater;
import de.jcup.egradle.codeassist.hover.HoverData;
import de.jcup.egradle.codeassist.hover.HoverSupport;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.integration.IntegrationTestComponents;

public class HoverIntegrationTest {

	@Rule
	public IntegrationTestComponents components = IntegrationTestComponents.initialize();

	@Test
	public void buildfile_17__checkstyle_extension(){
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test-17-checkstyle-extension.gradle");
		int offset = text.indexOf("checkstyle");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		assertThat(hoverData).isForExtension("checkstyle", "org.gradle.api.plugins.quality.CheckstyleExtension");
		
	}
	
	@Test
	@Ignore
	/* FIXME ATR, 01.03.2017: tasks must be generated ( + override in sdk bulder) + be available in model. so this test can be made active */
	public void buildfile_16__reports_delegates_toCheckStyleReports(){
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test-16-checkstyle-tasks.checkstyle.gradle");
		int offset = text.indexOf("html");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		assertThat(hoverData).isForMethod("org.gradle.api.plugins.quality.Checkstyle.reports", "groovy.lang.Closure");
		
	}
	
	@Test
	public void buildfile_15__my_task_with_type_checkstyle_reports_delegates_toCheckStyleReports(){
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test-15-checkstyle-task_myCheckStyleTask_type_CheckStyle.gradle");
		int offset = text.indexOf("reports");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		assertThat(hoverData).isForMethod("org.gradle.api.plugins.quality.Checkstyle.reports", "groovy.lang.Closure");
	
	}
	
	
	@Test
	public void buildfile_14__tasks_withType_CheckStyle_reports_delegates_toCheckStyleReports(){
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test-14-checkstyle-tasks.withTypeCheckstyle.gradle");
		int offset = text.indexOf("reports");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset);

		/* test */
		assertThat(hoverData).isForMethod("org.gradle.api.plugins.quality.Checkstyle.reports", "groovy.lang.Closure");
	
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
	
	@Test
	public void buildfile_10_with_dependencies_in_root__when_cursor_is_at_jar_offset__has_correct_hoverdata() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test-10-jar-task-configuration-in-root.gradle");
		int offset = text.indexOf("jar");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset+2);

		/* test */
		assertThat(hoverData).isForExtension("jar", "org.gradle.api.tasks.bundling.Jar");
		
	}
	
	@Test
	public void buildfile_10_with_dependencies_in_root__when_cursor_is_at_manifest_offset__has_correct_hoverdata() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test-10-jar-task-configuration-in-root.gradle");
		int offset = text.indexOf("manifest");

		/* FIXME ATR, 16.02.2017: test fails, because delegation target not available */
		/* execute */
		HoverData hoverData = calculateHoverData(text, offset+2);

		/* test */
		assertThat(hoverData).isForElementType("org.gradle.api.java.archives.Manifest");
		
	}
	/* FIXME ATR, 16.02.2017: override all which is annotated by @DelegatesTo(value=...trategy=1) 
	 */
	
	@Test
	public void buildfile_10_with_dependencies_in_root__when_cursor_is_at_eachFile_offset__has_correct_hoverdata() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test-10-jar-task-configuration-in-root.gradle");
		int offset = text.indexOf("eachFile");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset+2);
		
		/* test */
		/* @formatter:off*/
		assertThat(hoverData).
			isForMethod("org.gradle.api.tasks.AbstractCopyTask.eachFile", "groovy.lang.Closure").
			isForElementType("org.gradle.api.file.FileCopyDetails");
		/* @formatter:on*/
	}
	
	@Test
	public void buildfile_11_with_dependencies_in_root__when_cursor_is_at_eachFile_offset__has_correct_hoverdata() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test-11-war-task-configuration-in-root.gradle");
		int offset = text.indexOf("eachFile");

		/* execute */
		HoverData hoverData = calculateHoverData(text, offset+2);
		
		/* test */
		/* @formatter:off*/
		assertThat(hoverData).
			isForMethod("org.gradle.api.tasks.AbstractCopyTask.eachFile", "groovy.lang.Closure").
			isForElementType("org.gradle.api.file.FileCopyDetails");
		/* @formatter:on*/
	}

	/* short cut method for calculation */
	private HoverData calculateHoverData(String text, int offset) {
		HoverSupport hoverSupport = components.getHoverSupport();
		RelevantCodeCutter relevantCodeCutter = components.getRelevantCodeCutter();
		Model buildModel = components.buildModel(text);
		GradleLanguageElementEstimater estimator = components.getEstimator();
		return hoverSupport.caclulateHoverData(text, offset, relevantCodeCutter,
				buildModel, GradleFileType.GRADLE_BUILD_SCRIPT, estimator);
	}

	private String loadTextFromIntegrationTestFile(String testFileName) {
		String text = components.loadTestFile("integration/" + testFileName);
		assertNotNull("testcase corrupt", text);
		return text;
	}
}

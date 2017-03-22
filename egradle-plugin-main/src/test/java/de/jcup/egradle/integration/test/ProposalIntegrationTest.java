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
 package de.jcup.egradle.integration.test;

import static de.jcup.egradle.core.TestUtil.*;
import static de.jcup.egradle.integration.ProposalsAssert.assertThat;
import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import de.jcup.egradle.codeassist.GradleDSLProposalFactory;
import de.jcup.egradle.codeassist.Proposal;
import de.jcup.egradle.codeassist.ProposalFactoryContentProvider;
import de.jcup.egradle.integration.IntegrationTestComponents;
public class ProposalIntegrationTest {


	@Rule
	public IntegrationTestComponents components = IntegrationTestComponents.initialize();

	
	@Test
	public void buildfile__13_buildscript__before_myCopyTask_comment__proposes_into_dest_dir() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test-13-task-definition-inheriting-from-existing-task.gradle");
		int offset = calculateIndexBefore(text, "//test myCopyTask");

		/* execute */
		Set<Proposal> proposals = createProposals(text, offset);
		
		/* test */
		/* @formatter:off*/
		assertThat(proposals).
			containsAtLeastOneProposal().and().
			containsProposalWithLabel("into(Object destDir)").
		and();
		/* @formatter:on*/
	}
	
	@Test
	@Ignore // currently ignored, because paremter object type dependent and currently not implemented
	/**
	 * Test 18 has a "configure(projectType.javaProjects) {" inside. So this will be currently not provided.
	 * Maybe as an first approach an ugly approach ala "if (parameter name contains project) could do it but of
	 * course a comple type check mechansim would better but time intensive to develop
	 */
	public void buildfile__18_configure_has_dependencies_proposal() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test-18-configure.gradle");
		int offset = calculateIndexBefore(text, "/* inject */");

		/* execute */
		Set<Proposal> proposals = createProposals(text, offset);
		/* test */
		/* @formatter:off*/
		assertThat(proposals).
			containsAtLeastOneProposal().
		and().
			containsProposalWithLabel("apply(Closure closure)").
		and().
			containsProposalWithLabel("dependencies(Closure configureClosure)").
				whichHasDescription().
				hasTemplate("dependencies {\n    $cursor\n}").
		and();
		
		/* @formatter:on*/
	}
	
	
	@Test
	public void buildfile__13_buildscript__before_myCopyTask_comment__proposes_doFirst_with_closure() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test-13-task-definition-inheriting-from-existing-task.gradle");
		int offset = calculateIndexBefore(text, "//test myCopyTask");

		/* execute */
		Set<Proposal> proposals = createProposals(text, offset);
		
		/* test */
		/* @formatter:off*/
		assertThat(proposals).
			containsAtLeastOneProposal().and().
			containsProposalWithLabel("doFirst(Closure action)").
		and();
		/* @formatter:on*/
	}

	@Test
	public void buildfile__12_buildscript__before_test_tag_comment() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test-12-repositories-in-buildscript-asciidoctor-example.gradle");
		int offset = calculateIndexBefore(text, "//test tag");

		/* execute */
		Set<Proposal> proposals = createProposals(text, offset);
		
		/* test */
		/* @formatter:off*/
		assertThat(proposals).
			containsAtLeastOneProposal().
				whichHasReasonType("org.gradle.api.initialization.dsl.ScriptHandler").
		and().
			containsProposalWithLabel("repositories(Closure configureClosure)").
				whichHasReasonType("org.gradle.api.initialization.dsl.ScriptHandler").
				whichHasDescription().
		and();
		/* @formatter:on*/
	}
	
	@Test
	public void buildfile__with_dependencies_in_root__when_cursor_is_after_dependencies_bracket() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test1-dependencies-block-inside-root.gradle");
		int offset = calculateIndexEndOf(text,"dependencies {");

		/* execute */
		Set<Proposal> proposals = createProposals(text, offset);
		
		/* test */
		/* @formatter:off*/
		assertThat(proposals).
			containsAtLeastOneProposal().
				whichHasReasonType("org.gradle.api.artifacts.dsl.DependencyHandler").
		and().
			containsProposalWithLabel("add(String configurationName, Object dependencyNotation, Closure configureClosure)").
				whichHasReasonType("org.gradle.api.artifacts.dsl.DependencyHandler").
				whichHasDescription().
		and();
		/* @formatter:on*/
	}
	@Test
	public void buildfile__with_task_in_root__when_cursor_is_after_task_bracket() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test8-task-inside-root.gradle");
		int offset = calculateIndexEndOf(text,"task myTask {");

		/* execute */
		Set<Proposal> proposals = createProposals(text, offset);
		
		/* test */
		/* @formatter:off*/
		assertThat(proposals).
			containsAtLeastOneProposal().
				whichHasReasonType("org.gradle.api.Task").
		and().
			containsProposalWithLabel("doFirst(Closure action)").
				whichHasReasonType("org.gradle.api.Task").
				whichHasDescription().
		and();
		/* @formatter:on*/
	}
	
	@Test
	public void buildfile__with_task_extendending_jar_in_root__when_cursor_is_after_task_bracket() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test9-task-extending-jar-inside-root.gradle");
		int offset = calculateIndexEndOf(text,"task myTask(type:jar) {");

		/* execute */
		Set<Proposal> proposals = createProposals(text, offset);
		
		/* test */
		/* @formatter:off*/
		assertThat(proposals).
			containsAtLeastOneProposal().
				whichHasReasonType("org.gradle.api.Task").
		and().
			containsProposalWithLabel("doFirst(Closure action)").
				whichHasReasonType("org.gradle.api.Task").
				whichHasDescription().
		and();
		/* @formatter:on*/
	}
	
	@Test
	public void buildfile__with_jar_configuration_in_root__when_cursor_is_after_dependencies_bracket() {
		/* prepare */
		String text = loadTextFromIntegrationTestFile("test-10-jar-task-configuration-in-root.gradle");
		int offset = calculateIndexEndOf(text,"jar {");

		/* execute */
		Set<Proposal> proposals = createProposals(text, offset);
		
		/* test */
		/* @formatter:off*/
		assertThat(proposals).
			containsAtLeastOneProposal().
				whichHasReasonType("org.gradle.api.tasks.bundling.Jar").
		and().
			containsProposalWithLabel("eachFile(Closure closure)").
				whichHasReasonType("org.gradle.api.tasks.bundling.Jar").
				whichHasDescription().
		and();
		/* @formatter:on*/
	}
	
	@Test
	public void buildfile__empty_offset_is_0() {
		/* prepare */
		String text = "";
		int offset = 0;
	
		/* execute */
		Set<Proposal> proposals = createProposals(text, offset);
		/* test */
		/* @formatter:off*/
		assertThat(proposals).
			containsAtLeastOneProposal().
		and().
			containsProposalWithLabel("apply(Closure closure)").
		and().
			containsProposalWithLabel("dependencies(Closure configureClosure)").
				whichHasDescription().
				hasTemplate("dependencies {\n    $cursor\n}").
		and();
		/* @formatter:on*/
	}
	
	private Set<Proposal> createProposals(String text, int offset) {
		ProposalFactoryContentProvider contentProvider = components.buildContentProvider(text,offset);
		GradleDSLProposalFactory gradleDSLProposalFactory = components.getGradleDSLProposalFactory();
		return gradleDSLProposalFactory.createProposals(offset,contentProvider);
	}
	
	private String loadTextFromIntegrationTestFile(String testFileName) {
		String text = components.loadTestFile("integration/" + testFileName);
		assertNotNull("testcase corrupt", text);
		return text;
	}
	
}
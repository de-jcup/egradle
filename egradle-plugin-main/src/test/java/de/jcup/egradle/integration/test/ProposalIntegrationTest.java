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
    @Ignore // currently ignored, because paremter object type dependent and
            // currently not implemented
    /**
     * Test 18 has a "configure(projectType.javaProjects) {" inside. So this will be
     * currently not provided. Maybe as an first approach an ugly approach ala "if
     * (parameter name contains project) could do it but of course a comple type
     * check mechansim would better but time intensive to develop
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
        int offset = calculateIndexEndOf(text, "dependencies {");

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
        int offset = calculateIndexEndOf(text, "task myTask {");

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
        int offset = calculateIndexEndOf(text, "task myTask(type:jar) {");

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
        int offset = calculateIndexEndOf(text, "jar {");

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

    /**
     * Tests, EarPluginConvention.java#getAppDirName() is added to project methods
     */
    @Test
    public void buildfile__empty_offset_is_0__has_ear_convention_method_appDirName() {
        /* prepare */
        String text = "";
        int offset = 0;

        /* execute */
        Set<Proposal> proposals = createProposals(text, offset);
        /* test */
        /* @formatter:off*/
		assertThat(proposals).
			containsProposalWithLabel("appDirName");
		/* @formatter:on*/
    }

    /**
     * Tests, EarPluginConvention.java#getAppDirName() is added to project methods
     */
    @Test
    public void buildfile__empty_ear_extension_block__offset_is_5__has_ear_extension_lib_closure() {
        /* prepare */
        String text = "ear{   }";
        int offset = 5;

        /* execute */
        Set<Proposal> proposals = createProposals(text, offset);
        /* test */
        /* @formatter:off*/
		assertThat(proposals).
			containsProposalWithLabel("lib(Closure configureClosure)");
		/* @formatter:on*/
    }

    @Test
    public void buildfile__empty_offset_is_0__has_apply_and_dependencies() {
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

    @Test
    public void buildfile__empty_offset_is_0__has_scalaRuntime_from_scala_plugin() {
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
			containsProposalWithLabel("scalaRuntime-scala");
		/* @formatter:on*/
    }

    /**
     * Test MavenPluginConvention.java method pom(Closure configureClosure) is in
     * proposals
     */
    @Test
    public void buildfile__empty_offset_is_0__has_method_pom_closure__from_maven_mixing_of_mavenpluginconvention() {
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
			containsProposalWithLabel("pom(Closure configureClosure)-maven");
		/* @formatter:on*/
    }

    /**
     * 
     * <extends targetClass="org.gradle.api.artifacts.dsl.RepositoryHandler"
     * mixinClass="org.gradle.api.plugins.MavenRepositoryHandlerConvention"/>
     */
    @Test
    public void buildfile__empty_offset_is_0__has_method_mavenDeployer_closure__from_maven_mixin_of_MavenRepositoryHandlerConventionTorepositoryHandler() {
        /* prepare */
        String text = "repositories{                            }";
        int offset = 16;

        /* execute */
        Set<Proposal> proposals = createProposals(text, offset);
        /* test */
        /* @formatter:off*/
		assertThat(proposals).
			containsAtLeastOneProposal().
		and().
			containsProposalWithLabel("flatDir(Closure configureClosure)").// origin from RepositoryHandler.xml, must be kept
		and().
			containsProposalWithLabel("mavenDeployer(Closure configureClosure)-maven")
			;
		/* @formatter:on*/
    }

    /**
     * Test MavenPluginConvetion.java method getMavenPomDir() is in proposals as
     * property
     */
    @Test
    public void buildfile__empty_offset_is_0__has_property_mavenPomDir() {
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
			containsProposalWithLabel("mavenPomDir");
		/* @formatter:on*/
    }

    private Set<Proposal> createProposals(String text, int offset) {
        ProposalFactoryContentProvider contentProvider = components.buildContentProvider(text, offset);
        GradleDSLProposalFactory gradleDSLProposalFactory = components.getGradleDSLProposalFactory();
        return gradleDSLProposalFactory.createProposals(offset, contentProvider);
    }

    private String loadTextFromIntegrationTestFile(String testFileName) {
        String text = components.loadTestFile("integration/" + testFileName);
        assertNotNull("testcase corrupt", text);
        return text;
    }

}
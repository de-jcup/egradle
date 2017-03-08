package de.jcup.egradle.integration.test;

import static de.jcup.egradle.integration.TypeAssert.*;

import org.junit.Rule;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.integration.IntegrationTestComponents;

public class XMLTypeIntegrationTest {

	@Rule
	public IntegrationTestComponents components = IntegrationTestComponents.initialize();

	@Test
	public void org_gradle_project__is_interface__and__extends_PluginAware_and_Extension_Aware() {
		Type project = components.getGradleDslProvider().getType("org.gradle.api.Project");
		/* @formatter:off*/
		assertType(project).
			isInterface().// is itself an interface 
			hasInterface("org.gradle.api.plugins.PluginAware"). // is extending interface plugin aware and 
			hasInterface("org.gradle.api.plugins.ExtensionAware");// extension aware
		/* @formatter:on*/
	}

	/*
	 * TODO ATR, 27.02.2017: the inheritance is broken in generated xml - simply
	 * checking if descendant of AbstractTask or DefaultTask does not work
	 * because AbstractCopyTask.xml has no superclass information inside!
	 */
	@Test
	public void org_gradle_copy_task__is_no_interface__and__has_extends_PluginAware_and_Extension_Aware() {
		Type project = components.getGradleDslProvider().getType("org.gradle.api.tasks.Copy");
		/* @formatter:off*/
		assertType(project).
			isNotInterface().// is itself not an interface
			isDecendantOf("org.gradle.api.tasks.AbstractCopyTask");
		/* @formatter:on*/
	}

}

package de.jcup.egradle.test.integregation;

import org.junit.Rule;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.Type;

import static de.jcup.egradle.test.integregation.TypeAssert.*;

public class XMLTypeIntegrationTest {

	
	@Rule
	public IntegrationTestComponents components = IntegrationTestComponents.initialize();

	@Test
	public void org_gradle_project__is_interface__and__extends_PluginAware_and_Extension_Aware(){
		Type project = components.getGradleDslProvider().getType("org.gradle.api.Project");
		/* @formatter:off*/
		assertType(project).
			isInterface().// is itself an interface 
			hasInterface("org.gradle.api.plugins.PluginAware"). // is extending interface plugin aware and 
			hasInterface("org.gradle.api.plugins.ExtensionAware");// extension aware
		/* @formatter:on*/
	}
	
}

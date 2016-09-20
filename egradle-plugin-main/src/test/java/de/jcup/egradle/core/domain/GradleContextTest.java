package de.jcup.egradle.core.domain;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.config.GradleConfiguration;

public class GradleContextTest {

	private GradleConfiguration configuration;
	private GradleRootProject rootProject;
	private GradleContext contextToTest;

	@Before
	public void before() {
		rootProject=mock(GradleRootProject.class);
		configuration=mock(GradleConfiguration.class);
		contextToTest = new GradleContext(rootProject, configuration);
	}
	
	@Test
	public void environment_is_not_null() {
		assertNotNull(contextToTest.getEnvironment());
	}
	
	@Test
	public void gradleProperties_are_not_null() {
		assertNotNull(contextToTest.getGradleProperties());
	}
	
	@Test
	public void systemProperties_are_not_null() {
		assertNotNull(contextToTest.getSystemProperties());
	}

}

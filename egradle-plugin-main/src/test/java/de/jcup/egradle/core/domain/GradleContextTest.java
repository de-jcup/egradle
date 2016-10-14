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
		rootProject = mock(GradleRootProject.class);
		configuration = mock(GradleConfiguration.class);
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

	@Test
	public void when_cancelStateProvider_not_set_the_context_returns_false_for_is_canceled() {
		assertFalse(contextToTest.isCanceled());
	}

	@Test
	public void when_cancelStateProvider_returns_true_the_context_returns_also_true_for_is_canceled() {

		/* prepare */
		CancelStateProvider provider = new CancelStateProvider() {

			@Override
			public boolean isCanceled() {
				return true;
			}

		};
		contextToTest.register(provider);

		/* test */
		assertTrue(contextToTest.isCanceled());

	}

	@Test
	public void when_cancelStateProvider_returns_false_the_context_returns_also_false_for_is_canceled() {

		/* prepare */
		CancelStateProvider provider = new CancelStateProvider() {

			@Override
			public boolean isCanceled() {
				return false;
			}

		};
		contextToTest.register(provider);

		/* test */
		assertFalse(contextToTest.isCanceled());
	}

}

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
 package de.jcup.egradle.eclipse.launch;

import static org.mockito.Mockito.*;

import org.eclipse.core.resources.IResource;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.Constants;

public class EGradleLaunchShortCutTest {

	private EGradleLaunchShortCut shortCutToTest;

	@Before
	public void before(){
		shortCutToTest = new EGradleLaunchShortCut();
	}
	
	@Test
	/* does not work currently, because the mockito dependency is not working. shall be fixed with #84 (enable egradle code to be build by gradle) */
	public void when_virtual_rootproject_name_is_given_the_configuration_contains_not_a_project_name() {
		/* prepare*/
		String projectName = Constants.VIRTUAL_ROOTPROJECT_NAME;
		ILaunchConfigurationWorkingCopy wc = mock(ILaunchConfigurationWorkingCopy.class);
		Object additionalScope = null;
		IResource resource = mock(IResource.class);
		
		/* execute */
		shortCutToTest.createCustomConfiguration(resource, additionalScope, wc, projectName);
		
		/* test*/
		verify(wc).setAttribute(EGradleLauncherConstants.PROPERTY_PROJECTNAME, "");
	}

}

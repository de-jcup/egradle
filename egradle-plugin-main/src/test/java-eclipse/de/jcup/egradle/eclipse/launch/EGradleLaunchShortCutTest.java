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

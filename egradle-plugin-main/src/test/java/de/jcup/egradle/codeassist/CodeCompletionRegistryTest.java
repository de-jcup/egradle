package de.jcup.egradle.codeassist;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.codeassist.CodeCompletionRegistry.RegistryListener;

public class CodeCompletionRegistryTest {

	private CodeCompletionRegistry registryToTest;
	
	@Before
	public void before(){
		registryToTest = new CodeCompletionRegistry();
	}
	
	
	@Test
	public void registrated_listener_is_informed_about_rebuild() {
		/* prepare*/
		RegistryListener mockedRegistryListener = mock(RegistryListener.class);
		registryToTest.addListener(mockedRegistryListener);

		/* execute */
		registryToTest.rebuild();
		
		/* test */
		verify(mockedRegistryListener).onRebuild(any());
		
		
		
	}
	
	@Test
	public void deregistrated_listener_is_NO_longer_informed_about_rebuild() {
		/* prepare*/
		RegistryListener mockedRegistryListener = mock(RegistryListener.class);
		registryToTest.addListener(mockedRegistryListener);
		registryToTest.removeListener(mockedRegistryListener);

		/* execute */
		registryToTest.rebuild();
		
		/* test */
		verify(mockedRegistryListener,times(0)).onRebuild(any());
		
		
		
	}

}

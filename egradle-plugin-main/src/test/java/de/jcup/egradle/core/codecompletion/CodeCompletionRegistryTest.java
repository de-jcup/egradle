package de.jcup.egradle.core.codecompletion;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.codecompletion.CodeCompletionRegistry.RegistryListener;

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
		registryToTest.add(mockedRegistryListener);

		/* execute */
		registryToTest.rebuild();
		
		/* test */
		verify(mockedRegistryListener).onRebuild(any());
		
		
		
	}
	
	@Test
	public void deregistrated_listener_is_NO_longer_informed_about_rebuild() {
		/* prepare*/
		RegistryListener mockedRegistryListener = mock(RegistryListener.class);
		registryToTest.add(mockedRegistryListener);
		registryToTest.remove(mockedRegistryListener);

		/* execute */
		registryToTest.rebuild();
		
		/* test */
		verify(mockedRegistryListener,times(0)).onRebuild(any());
		
		
		
	}

}

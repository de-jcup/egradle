package de.jcup.egradle.core.codecompletion.dsl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.codecompletion.model.Type;
import de.jcup.egradle.core.codecompletion.model.gradledsl.GradleDSLFileLoader;
import de.jcup.egradle.core.codecompletion.model.gradledsl.GradleDSLTypeProvider;

public class GradleDSLTypeProviderTest {

	private GradleDSLFileLoader mockedDSLFileLoader;
	private GradleDSLTypeProvider typeProviderToTest;

	//// C:\dev_custom\projects\JCUP\gradle\subprojects\docs\build\src-egradle\egradle-dsl
	@Before
	public void before(){
		mockedDSLFileLoader = mock(GradleDSLFileLoader.class);
		
		typeProviderToTest = new GradleDSLTypeProvider(mockedDSLFileLoader);
	}
	
	@Test
	public void when_file_loader_cannot_find_type__null_5_calls_to_type_provider_will_only_call_fileloader_one_time() throws Exception {
		/* execute */
		for (int i=0;i<5;i++){
			typeProviderToTest.getType("something");
		}
		/* test */
		verify(mockedDSLFileLoader,times(1)).load("something");
	}
	
	@Test
	public void when_file_loader_can_find_type__null_5_calls_to_type_provider_will_only_call_fileloader_one_time() throws Exception {
		/* prepare */
		Type value = mock(Type.class);
		when(mockedDSLFileLoader.load("something")).thenReturn(value);
		
		/* execute */
		for (int i=0;i<5;i++){
			typeProviderToTest.getType("something");
		}
		/* test */
		verify(mockedDSLFileLoader,times(1)).load("something");
	}

	@Test
	public void when_file_loader_cannot_find_type__result_is_null() throws Exception  {
		/* prepare */
		when(mockedDSLFileLoader.load("something")).thenReturn(null);
		
		/* execute + test */
		assertNull(typeProviderToTest.getType("something"));
	}
	
	@Test
	public void when_file_loader_can_find_type__provider_result_is_file_loader_result() throws Exception {
		/* prepare */
		Type value = mock(Type.class);
		when(mockedDSLFileLoader.load("something")).thenReturn(value);
		
		/* execute */
		assertEquals(value, typeProviderToTest.getType("something"));
		
	}
}

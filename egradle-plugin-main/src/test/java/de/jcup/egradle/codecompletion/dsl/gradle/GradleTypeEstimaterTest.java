package de.jcup.egradle.codecompletion.dsl.gradle;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.codecompletion.dsl.LanguageElement;
import de.jcup.egradle.codecompletion.dsl.Method;
import de.jcup.egradle.codecompletion.dsl.Type;
import de.jcup.egradle.codecompletion.dsl.TypeProvider;
import de.jcup.egradle.codecompletion.dsl.gradle.GradleLanguageElementEstimater;
import de.jcup.egradle.core.model.Item;
public class GradleTypeEstimaterTest {

	private GradleLanguageElementEstimater estimatorToTest;
	private TypeProvider mockedTypeProvider;

	@Before
	public void before(){
		
		mockedTypeProvider = mock(TypeProvider.class);
		estimatorToTest = new GradleLanguageElementEstimater(mockedTypeProvider);
	}
	
	@Test
	public void estimateFromGradleProjectAsRoot__when_parent_is_root__() {
		/* prepare */
		Type happyType = mock(Type.class); 
		Type projectType = mock(Type.class); 
		when(mockedTypeProvider.getType("org.gradle.api.Project")).thenReturn(projectType);
		when(mockedTypeProvider.getType("test.something.HappyType")).thenReturn(happyType);
		when(happyType.getName()).thenReturn("test.something.HappyType");
		
		Set<Method> projectMethods = new LinkedHashSet<>();
		Method happyMethod = mock(Method.class);
		projectMethods.add(happyMethod);
		when(happyMethod.getName()).thenReturn("happy");
		when(happyMethod.getReturnType()).thenReturn(happyType);
		when(projectType.getMethods()).thenReturn(projectMethods);
		
		Item item1 = mock(Item.class);
		Item root = mock(Item.class);
		when(item1.getParent()).thenReturn(root);
		when(item1.getName()).thenReturn("happy");

		when(root.getParent()).thenReturn(null);
		
		
		/* execute */
		LanguageElement element = estimatorToTest.estimate(item1,GradleFileType.GRADLE_BUILD_SCRIPT);
		
		/* test */
		assertEquals(happyMethod,element);
	}

}

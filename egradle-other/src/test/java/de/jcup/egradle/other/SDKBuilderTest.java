package de.jcup.egradle.other;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.util.LinkedHashSet;
import java.util.Set;

import de.jcup.egradle.codeassist.dsl.XMLMethod;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Type;

public class SDKBuilderTest {
	private SDKBuilder preparatorToTest;

	/*
	 * FIXME ATR, 02.02.2017: add testcases - e.g. AntBuilder does currently
	 * have a "This is resolved as per <a
	 * href='type://Project#file(Object)'>Project#file(Object)</a>" inside!.
	 */
	@Before
	public void before() {
		preparatorToTest = new SDKBuilder();
	}

	@Test
	public void estimateDelegationTarget_for_type_set_delegate_from_javadoc_of_method_into_method__simple_name(){
		/* prepare*/
		Type type = mock(Type.class);
		Set<Method> methods = new LinkedHashSet<>();
		XMLMethod m1 = mock(XMLMethod.class);
		when(m1.getDelegationTarget()).thenReturn(null);
		StringBuilder description1 = new StringBuilder();
		description1.append("bla\n<br> bla\n<br> bla <a href='type://Type1'>TypeX</a> and so on ...");
		when(m1.getDescription()).thenReturn(description1.toString());
		methods.add(m1);
		when(type.getMethods()).thenReturn(methods);
		
		/* execute */
		preparatorToTest.estimateDelegateTargets(type);
		
		/* test */
		verify(m1).setDelegationTargetAsString("Type1"); 
	}
	
	@Test
	public void estimateDelegationTarget_for_type_set_delegate_from_javadoc_of_method_into_method_fullpath_name(){
		/* prepare*/
		Type type = mock(Type.class);
		Set<Method> methods = new LinkedHashSet<>();
		XMLMethod m1 = mock(XMLMethod.class);
		when(m1.getDelegationTarget()).thenReturn(null);
		StringBuilder description1 = new StringBuilder();
		description1.append("bla\n<br> bla\n<br> bla <a href='type://org.destination.Type1'>TypeX</a> and so on ...");
		when(m1.getDescription()).thenReturn(description1.toString());
		methods.add(m1);
		when(type.getMethods()).thenReturn(methods);
		
		/* execute */
		preparatorToTest.estimateDelegateTargets(type);
		
		/* test */
		verify(m1).setDelegationTargetAsString("org.destination.Type1"); 
	}
	
	@Test
	public void estimateDelegationTarget_for_type_set_no_delegate_from_javadoc_when_no_link_with_type_protocoll(){
		/* prepare*/
		Type type = mock(Type.class);
		Set<Method> methods = new LinkedHashSet<>();
		XMLMethod m1 = mock(XMLMethod.class);
		when(m1.getDelegationTarget()).thenReturn(null);
		StringBuilder description1 = new StringBuilder();
		description1.append("bla\n<br> bla\n<br> bla <a href='http://www.eclipse.org'>TypeX</a> and so on ...");
		when(m1.getDescription()).thenReturn(description1.toString());
		methods.add(m1);
		when(type.getMethods()).thenReturn(methods);
		
		/* execute */
		preparatorToTest.estimateDelegateTargets(type);
		
		/* test */
		verify(m1,never()).setDelegationTargetAsString(any()); 
	}
	
	@Test
	public void estimateDelegationTarget_for_type_set_delegate_from_javadoc_of_methods1_2_into_methods(){
		/* prepare*/
		Type type = mock(Type.class);
		Set<Method> methods = new LinkedHashSet<>();
		
		XMLMethod m1 = mock(XMLMethod.class);
		when(m1.getDelegationTarget()).thenReturn(null);
		StringBuilder description1 = new StringBuilder();
		description1.append("bla\n<br> bla\n<br> bla <a href='type://Type1'>TypeX</a> and so on ...");
		when(m1.getDescription()).thenReturn(description1.toString());
		methods.add(m1);
		
		XMLMethod m2 = mock(XMLMethod.class);
		when(m2.getDelegationTarget()).thenReturn(null);
		StringBuilder description2 = new StringBuilder();
		description2.append("bla\n<br> bla\n<br> bla <a href='type://Type2'>TypeX</a> and so on ...");
		when(m2.getDescription()).thenReturn(description2.toString());
		methods.add(m2);
		
		when(type.getMethods()).thenReturn(methods);
		
		/* execute */
		preparatorToTest.estimateDelegateTargets(type);
		
		/* test */
		verify(m1).setDelegationTargetAsString("Type1"); 
		verify(m2).setDelegationTargetAsString("Type2"); 
	}
	
	@Test
	public void estimateDelegationTarget_for_type_set_NO_delegate_from_javadoc_of_method_into_method__when_containing_hash(){
		/* prepare*/
		Type type = mock(Type.class);
		Set<Method> methods = new LinkedHashSet<>();
		XMLMethod m1 = mock(XMLMethod.class);
		when(m1.getDelegationTarget()).thenReturn(null);
		StringBuilder description1 = new StringBuilder();
		description1.append("bla\n<br> bla\n<br> bla <a href='type://Type1#blax'>TypeX</a> and so on ...");
		when(m1.getDescription()).thenReturn(description1.toString());
		methods.add(m1);
		when(type.getMethods()).thenReturn(methods);
		
		/* execute */
		preparatorToTest.estimateDelegateTargets(type);
		
		/* test */
		verify(m1,never()).setDelegationTargetAsString(any()); 
	}
	
	@Test
	public void estimateDelegationTarget_for_type_set_type2_when_type1_is_with_hash(){
		/* prepare*/
		Type type = mock(Type.class);
		Set<Method> methods = new LinkedHashSet<>();
		XMLMethod m1 = mock(XMLMethod.class);
		when(m1.getDelegationTarget()).thenReturn(null);
		StringBuilder description1 = new StringBuilder();
		description1.append("bla\n<br> bla\n<br> bla <a href='type://Type1#blax'>TypeX</a> and so <a href='type://Type2'>TypeX</a> on ...");
		when(m1.getDescription()).thenReturn(description1.toString());
		methods.add(m1);
		when(type.getMethods()).thenReturn(methods);
		
		/* execute */
		preparatorToTest.estimateDelegateTargets(type);
		
		/* test */
		verify(m1).setDelegationTargetAsString("Type2"); 
	}
	
	@Test
	public void test_removeWhitespacesAndStars1() {
		assertEquals("alpha", preparatorToTest.removeWhitespacesAndStars("    *alpha"));
	}

	@Test
	public void test_removeWhitespacesAndStars2() {
		assertEquals("alpha", preparatorToTest.removeWhitespacesAndStars(" alpha"));
	}

	@Test
	public void test_removeWhitespacesAndStars3() {
		assertEquals("alpha", preparatorToTest.removeWhitespacesAndStars("alpha"));
	}

	@Test
	public void test_removeWhitespacesAndStars4() {
		assertEquals("a lpha", preparatorToTest.removeWhitespacesAndStars("a lpha"));
	}
	

	@Test
	public void test_removeWhitespacesAndStars5() {
		String line = "*  something";
		String expected = "  something";
		assertEquals(expected, preparatorToTest.removeWhitespacesAndStars(line));
	}

	@Test
	public void testJavadocLinkConversion_simple_link_with_full_path_name() {
		String line = "{@link org.gradle.api.invocation.Gradle}";
		String expected = "<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, preparatorToTest.convertString(line));
	}
	
	@Test
	public void testJavadocLinkConversion_simple_link_with_full_path_name__whitespaces_after_curly() {
		String line = "{      @link org.gradle.api.invocation.Gradle}";
		String expected = "<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, preparatorToTest.convertString(line));
	}
	
	@Test
	public void testJavadocLinkConversion_simple_link_with_full_path_name__whitespaces_after_curly__and_after_link() {
		String line = "{      @link     org.gradle.api.invocation.Gradle}";
		String expected = "<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, preparatorToTest.convertString(line));
	}

	@Test
	public void testJavadocLinkConversion_simple_link_with_short_path_name() {
		String line = "{@link EclipseProject}";
		String expected = "<a href='type://EclipseProject'>EclipseProject</a>";
		assertEquals(expected, preparatorToTest.convertString(line));
	}
	
	@Test
	public void testJavadocParamConversion() {
		String line = "  @param name1 a description";
		String expected = "  <br><b class='param'>param:</b>name1 a description";
		assertEquals(expected, preparatorToTest.convertString(line));
	}
	
	
	
	@Test
	public void testJavadocReturnConversion() {
		String line = "  @return name1 a description";
		String expected = "  <br><b class='return'>returns:</b>name1 a description";
		assertEquals(expected, preparatorToTest.convertString(line));
	}

	@Test
	public void testJavadocLinkConversion_simple_link_with_short_path_name_but_prefix() {
		String line = "More examples in docs for {@link EclipseProject}";
		String expected = "More examples in docs for <a href='type://EclipseProject'>EclipseProject</a>";
		assertEquals(expected, preparatorToTest.convertString(line));
	}

	@Test
	public void testJavaDocLink3() {
		String line = "More examples in docs for { @link EclipseProject}, {@link EclipseClasspath}, {@link EclipseWtp} ";
		String expected = "More examples in docs for " + "<a href='type://EclipseProject'>EclipseProject</a>, "
				+ "<a href='type://EclipseClasspath'>EclipseClasspath</a>, "
				+ "<a href='type://EclipseWtp'>EclipseWtp</a> ";
		assertEquals(expected, preparatorToTest.convertString(line));
	}

	@Test
	public void testJavaDocLink4_mixed_arguments() {
		String line = "More examples in docs for {@link EclipseProject}, {@xxx EclipseClasspath}, {@link EclipseWtp} ";
		String expected = "More examples in docs for " + "<a href='type://EclipseProject'>EclipseProject</a>, "
				+ "{@xxx EclipseClasspath}, " + "<a href='type://EclipseWtp'>EclipseWtp</a> ";
		assertEquals(expected, preparatorToTest.convertString(line));
	}

}

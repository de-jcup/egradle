package de.jcup.egradle.other.sdkbuilder;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.TypeReference;
import de.jcup.egradle.codeassist.dsl.XMLMethod;
import de.jcup.egradle.other.OtherTestUtil;

public class SDKBuilderTest {
	private SDKBuilder sdkBuilderToTest;
	private SDKBuilderContext context;

	/*
	 * FIXME ATR, 02.02.2017: add testcases - e.g. AntBuilder does currently
	 * have a "This is resolved as per <a
	 * href='type://Project#file(Object)'>Project#file(Object)</a>" inside!.
	 */
	@Before
	public void before() {
		sdkBuilderToTest = new SDKBuilder();
		context = mock(SDKBuilderContext.class);
	}

	@Test
	public void estimateDelegationTarget_byJavaDoc__for_type_set_delegate_from_javadoc_of_method_into_method__simple_name() {
		/* prepare */
		Type type = mock(Type.class);
		Set<Method> methods = new LinkedHashSet<>();
		XMLMethod m1 = mock(XMLMethod.class);
		when(m1.getDelegationTarget()).thenReturn(null);
		StringBuilder description1 = new StringBuilder();
		description1.append("bla\n<br> bla\n<br> bla <a href='type://Type1'>TypeX</a> and so on ...");
		when(m1.getDescription()).thenReturn(description1.toString());
		methods.add(m1);
		when(type.getDefinedMethods()).thenReturn(methods);

		/* execute */
		sdkBuilderToTest.estimateDelegateTargets_by_javdoc(type,context,false);

		/* test */
		verify(m1).setDelegationTargetAsString("Type1");
	}
	
	
	@Test
	public void estimateDelegationTarget_byJavaDoc__for_type_set_delegate_from_javadoc_of_interface_into_method_when_method_has_no_data() {
		/* prepare */
		
		/* method 1 - in type, but with inherited doc */
		Type type = mock(Type.class);
		Set<Method> methods1 = new LinkedHashSet<>();
		XMLMethod method1 = mock(XMLMethod.class);
		when(method1.getDelegationTarget()).thenReturn(null);
		
		StringBuilder description1 = new StringBuilder();
		description1.append("@inheritDoc");
		when(method1.getDescription()).thenReturn(description1.toString());
		
		methods1.add(method1);
		when(type.getDefinedMethods()).thenReturn(methods1);

		/* method 2 - in interface */
		Type interfaceType = mock(Type.class);
		Set<Method> interfaceMethods = new LinkedHashSet<>();
		XMLMethod interfaceMethod = mock(XMLMethod.class);
		when(interfaceMethod.getDelegationTarget()).thenReturn(null);

		StringBuilder interfaceMethodDescription = new StringBuilder();
		interfaceMethodDescription.append("bla\n<br> bla\n<br> bla <a href='type://org.destination.Type1'>TypeX</a> and so on ...");
		when(interfaceMethod.getDescription()).thenReturn(interfaceMethodDescription.toString());
		
		interfaceMethods.add(interfaceMethod);
		when(interfaceType.getDefinedMethods()).thenReturn(interfaceMethods);
		
		/* interface references */
		Set<TypeReference> interfaceReferences = new LinkedHashSet<>();
		TypeReference interfaceReference = mock(TypeReference.class);
		when (interfaceReference.getType()).thenReturn(interfaceType);
		interfaceReferences.add(interfaceReference);
		
		when(type.getInterfaces()).thenReturn(interfaceReferences);

		/* execute */
		sdkBuilderToTest.estimateDelegateTargets_by_javdoc(type,context,false);

		/* test */
		verify(method1).setDelegationTargetAsString("org.destination.Type1");
	}
	
	@Test
	public void estimateDelegationTarget_byJavaDoc__for_type_set_delegate_from_javadoc_of_interfaces_first_where_hyperlink_found() {
		/* prepare */
		
		/* method 1 - in type, but with inherited doc */
		Type type = mock(Type.class);
		Set<Method> methods1 = new LinkedHashSet<>();
		XMLMethod method1 = mock(XMLMethod.class);
		when(method1.getDelegationTarget()).thenReturn(null);
		
		StringBuilder description1 = new StringBuilder();
		description1.append("@inheritDoc");
		when(method1.getDescription()).thenReturn(description1.toString());
		
		methods1.add(method1);
		when(type.getDefinedMethods()).thenReturn(methods1);

		/* in interface */
		Type interfaceType1 = mock(Type.class);
		Set<Method> interfaceMethods1 = new LinkedHashSet<>();
		XMLMethod interfaceMethod1 = mock(XMLMethod.class);
		interfaceMethods1.add(interfaceMethod1);
		when(interfaceMethod1.getDescription()).thenReturn("description, but no link");
		when(interfaceType1.getDefinedMethods()).thenReturn(interfaceMethods1);

		Type interfaceType2 = mock(Type.class);
		Set<Method> interfaceMethods2 = new LinkedHashSet<>();
		XMLMethod interfaceMethod2 = mock(XMLMethod.class);
		interfaceMethods2.add(interfaceMethod2);
		
		StringBuilder interfaceMethod2Description = new StringBuilder();
		interfaceMethod2Description.append("bla\n<br> bla\n<br> bla <a href='type://org.destination.Type1'>TypeX</a> and so on ...");
		when(interfaceMethod2.getDescription()).thenReturn(interfaceMethod2Description.toString());

		when(interfaceType2.getDefinedMethods()).thenReturn(interfaceMethods2);
		
		/* interface references */
		TypeReference interfaceReference2 = mock(TypeReference.class);
		when (interfaceReference2.getType()).thenReturn(interfaceType2);
		
		TypeReference interfaceReference1 = mock(TypeReference.class);
		when (interfaceReference1.getType()).thenReturn(interfaceType1);

		Set<TypeReference> interfaceReferences = new LinkedHashSet<>();
		interfaceReferences.add(interfaceReference1); /* first is withing link */
		interfaceReferences.add(interfaceReference2);

		when(type.getInterfaces()).thenReturn(interfaceReferences);

		/* execute */
		sdkBuilderToTest.estimateDelegateTargets_by_javdoc(type,context,false);

		/* test */
		verify(method1).setDelegationTargetAsString("org.destination.Type1");
	}


	@Test
	public void estimateDelegationTarget_byJavaDoc__for_type_set_delegate_from_javadoc_of_method_into_method_fullpath_name() {
		/* prepare */
		Type type = mock(Type.class);
		Set<Method> methods = new LinkedHashSet<>();
		XMLMethod m1 = mock(XMLMethod.class);
		when(m1.getDelegationTarget()).thenReturn(null);
		StringBuilder description1 = new StringBuilder();
		description1.append("bla\n<br> bla\n<br> bla <a href='type://org.destination.Type1'>TypeX</a> and so on ...");
		when(m1.getDescription()).thenReturn(description1.toString());
		methods.add(m1);
		when(type.getDefinedMethods()).thenReturn(methods);

		/* execute */
		sdkBuilderToTest.estimateDelegateTargets_by_javdoc(type, context,false);

		/* test */
		verify(m1).setDelegationTargetAsString("org.destination.Type1");
	}

	@Test
	public void estimateDelegationTarget_byJavaDoc__for_type_set_no_delegate_from_javadoc_when_no_link_with_type_protocoll() {
		/* prepare */
		Type type = mock(Type.class);
		Set<Method> methods = new LinkedHashSet<>();
		XMLMethod m1 = mock(XMLMethod.class);
		when(m1.getDelegationTarget()).thenReturn(null);
		StringBuilder description1 = new StringBuilder();
		description1.append("bla\n<br> bla\n<br> bla <a href='http://www.eclipse.org'>TypeX</a> and so on ...");
		when(m1.getDescription()).thenReturn(description1.toString());
		methods.add(m1);
		when(type.getDefinedMethods()).thenReturn(methods);

		/* execute */
		sdkBuilderToTest.estimateDelegateTargets_by_javdoc(type, context);

		/* test */
		verify(m1, never()).setDelegationTargetAsString(any());
	}

	@Test
	public void estimateDelegationTarget_byJavaDoc__for_type_set_delegate_from_javadoc_of_methods1_2_into_methods() {
		/* prepare */
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

		when(type.getDefinedMethods()).thenReturn(methods);

		/* execute */
		sdkBuilderToTest.estimateDelegateTargets_by_javdoc(type, context,false);

		/* test */
		verify(m1).setDelegationTargetAsString("Type1");
		verify(m2).setDelegationTargetAsString("Type2");
	}

	@Test
	public void estimateDelegationTarget_byJavaDoc__for_type_set_NO_delegate_from_javadoc_of_method_into_method__when_containing_hash() {
		/* prepare */
		Type type = mock(Type.class);
		Set<Method> methods = new LinkedHashSet<>();
		XMLMethod m1 = mock(XMLMethod.class);
		when(m1.getDelegationTarget()).thenReturn(null);
		StringBuilder description1 = new StringBuilder();
		description1.append("bla\n<br> bla\n<br> bla <a href='type://Type1#blax'>TypeX</a> and so on ...");
		when(m1.getDescription()).thenReturn(description1.toString());
		methods.add(m1);
		when(type.getDefinedMethods()).thenReturn(methods);

		/* execute */
		sdkBuilderToTest.estimateDelegateTargets_by_javdoc(type, context,false);

		/* test */
		verify(m1, never()).setDelegationTargetAsString(any());
	}

	@Test
	public void estimateDelegationTarget_byJavaDoc__for_type_set_type2_when_type1_is_with_hash() {
		/* prepare */
		Type type = mock(Type.class);
		Set<Method> methods = new LinkedHashSet<>();
		XMLMethod m1 = mock(XMLMethod.class);
		when(m1.getDelegationTarget()).thenReturn(null);
		StringBuilder description1 = new StringBuilder();
		description1.append(
				"bla\n<br> bla\n<br> bla <a href='type://Type1#blax'>TypeX</a> and so <a href='type://Type2'>TypeX</a> on ...");
		when(m1.getDescription()).thenReturn(description1.toString());
		methods.add(m1);
		when(type.getDefinedMethods()).thenReturn(methods);

		/* execute */
		sdkBuilderToTest.estimateDelegateTargets_by_javdoc(type, context,false);

		/* test */
		verify(m1).setDelegationTargetAsString("Type2");
	}

	@Test
	public void test_removeWhitespacesAndStars1() {
		assertEquals("alpha", sdkBuilderToTest.removeWhitespacesAndStars("    *alpha"));
	}

	@Test
	public void test_removeWhitespacesAndStars2() {
		assertEquals("alpha", sdkBuilderToTest.removeWhitespacesAndStars(" alpha"));
	}

	@Test
	public void test_removeWhitespacesAndStars3() {
		assertEquals("alpha", sdkBuilderToTest.removeWhitespacesAndStars("alpha"));
	}

	@Test
	public void test_removeWhitespacesAndStars4() {
		assertEquals("a lpha", sdkBuilderToTest.removeWhitespacesAndStars("a lpha"));
	}

	@Test
	public void test_removeWhitespacesAndStars5() {
		String line = "*  something";
		String expected = "  something";
		assertEquals(expected, sdkBuilderToTest.removeWhitespacesAndStars(line));
	}

	@Test
	public void testJavadocLinkConversion_simple_link_with_full_path_name() {
		String line = "{@link org.gradle.api.invocation.Gradle}";
		String expected = "<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, sdkBuilderToTest.replaceJavaDocParts(line));
	}

	@Test
	public void testJavadocLinkConversion_simple_link_with_full_path_name__whitespaces_after_curly() {
		String line = "{      @link org.gradle.api.invocation.Gradle}";
		String expected = "<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, sdkBuilderToTest.replaceJavaDocParts(line));
	}
	
	@Test
	public void handle_type_links_without_type() {
		String line = "<a href='type://#method(xyz)'>org.gradle.api.invocation.Gradle</a>";
		String expected = "<a href='type://Blubb#method(xyz)'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, sdkBuilderToTest.handleTypeLinksWithoutType(line,new File("src/res/Blubb.xml"))); 
	}

	@Test
	public void testJavadocLinkConversion_simple_link_with_full_path_name__whitespaces_after_curly__and_after_link() {
		String line = "{      @link     org.gradle.api.invocation.Gradle}";
		String expected = "<a href='type://org.gradle.api.invocation.Gradle'>org.gradle.api.invocation.Gradle</a>";
		assertEquals(expected, sdkBuilderToTest.replaceJavaDocParts(line));
	}

	@Test
	public void testJavadocLinkConversion_simple_link_with_short_path_name() {
		String line = "{@link EclipseProject}";
		String expected = "<a href='type://EclipseProject'>EclipseProject</a>";
		assertEquals(expected, sdkBuilderToTest.replaceJavaDocParts(line));
	}

	@Test
	public void testJavadocParamConversion() {
		String line = "  @param    name1 a description";
		String expected = "  <br><b class='param'>param:</b>name1 a description";
		assertEquals(expected, sdkBuilderToTest.replaceJavaDocParts(line));
	}

	@Test
	public void testJavadocParamConversion2() {

		String line = " * @param gradle The build which has been loaded. Never null.";
		line = sdkBuilderToTest.removeWhitespacesAndStars(line);
		String expected = " <br><b class='param'>param:</b>gradle The build which has been loaded. Never null.";
		assertEquals(expected, sdkBuilderToTest.replaceJavaDocParts(line));
	}

	@Test
	public void testJavadocParamConversion3() throws IOException {
		List<String> list = new ArrayList<>();
		list.add("<method name=\"projectsLoaded\" returnType=\"void\">");
		list.add("        <parameter name=\"gradle\" type=\"org.gradle.api.invocation.Gradle\"/>");
		list.add("        <description><![CDATA[");
		list.add(
				"     * <p>Called when the projects for the build have been created from the settings. None of the projects have been");
		list.add("     * evaluated.</p>");
		list.add("     *");
		list.add("     * @param gradle The build which has been loaded. Never null.");
		list.add("     ]]></description>");
		list.add("    </method>");

		StringBuilder fullDescription = new StringBuilder();
		sdkBuilderToTest.readLines(new HashMap<>(), new File("test"), fullDescription, new LineResolver() {

			Iterator<String> it = list.iterator();

			@Override
			public String getNextLine() throws IOException {
				if (it.hasNext()) {
					return it.next();
				}
				return null;
			}
		});
		String transformed = fullDescription.toString();
		transformed = sdkBuilderToTest.replaceJavaDocParts(transformed);
		assertTrue(transformed.indexOf("@param") == -1);
	}

	@Test
	public void testJavadocParamConversion5_two_params_following() throws Exception {
		String line = "  @param    name1 a description\n  @param    name2 a description\n";
		String converted = sdkBuilderToTest.replaceJavaDocParts(line);
		assertTrue(converted.indexOf("@param") == -1);
	}

	@Test
	public void testJavadocParamConversion4_loadFiles() throws Exception {
		File file = new File(OtherTestUtil.PARENT_OF_TEST, "BuildListener.xml");
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			StringBuilder fullDescription = new StringBuilder();
			sdkBuilderToTest.readLines(new HashMap<>(), file, fullDescription, new LineResolver() {

				@Override
				public String getNextLine() throws IOException {
					return br.readLine();
				}
			});
			String transformed = fullDescription.toString();
			assertFalse(transformed.indexOf("@param") == -1);
			assertFalse(transformed.indexOf("@code") == -1);
			
			transformed = sdkBuilderToTest.replaceJavaDocParts(transformed);
			
			assertTrue(transformed.indexOf("@param") == -1);
			assertTrue(transformed.indexOf("@code") == -1);
		}

	}

	@Test
	public void testJavadocReturnConversion() {
		String line = "  @return name1 a description";
		String expected = "  <br><br><b class='return'>returns:</b>name1 a description";
		assertEquals(expected, sdkBuilderToTest.replaceJavaDocParts(line));
	}

	@Test
	public void testJavadocLinkConversion_simple_link_with_short_path_name_but_prefix() {
		String line = "More examples in docs for {@link EclipseProject}";
		String expected = "More examples in docs for <a href='type://EclipseProject'>EclipseProject</a>";
		assertEquals(expected, sdkBuilderToTest.replaceJavaDocParts(line));
	}

	@Test
	public void testJavaDocLink3() {
		String line = "More examples in docs for { @link EclipseProject}, {@link EclipseClasspath}, {@link EclipseWtp} ";
		String expected = "More examples in docs for " + "<a href='type://EclipseProject'>EclipseProject</a>, "
				+ "<a href='type://EclipseClasspath'>EclipseClasspath</a>, "
				+ "<a href='type://EclipseWtp'>EclipseWtp</a> ";
		assertEquals(expected, sdkBuilderToTest.replaceJavaDocParts(line));
	}

	@Test
	public void testJavaDocLink4_mixed_arguments() {
		String line = "More examples in docs for {@link EclipseProject}, {@xxx EclipseClasspath}, {@link EclipseWtp} ";
		String expected = "More examples in docs for " + "<a href='type://EclipseProject'>EclipseProject</a>, "
				+ "{@xxx EclipseClasspath}, " + "<a href='type://EclipseWtp'>EclipseWtp</a> ";
		assertEquals(expected, sdkBuilderToTest.replaceJavaDocParts(line));
	}

}

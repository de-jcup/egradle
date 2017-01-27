package de.jcup.egradle.codecompletion.dsl;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jcup.egradle.core.TestUtil;
public class XMLDSLTypeImporterTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private File dslFolder;
	private XMLDSLTypeImporter importerToTest;
	private File actionXMLFile;
	
	@Before
	public void before(){
		dslFolder = new File(TestUtil.PARENT_OF_TEST,"dsl/3.0");
		actionXMLFile = new File(dslFolder,"org/gradle/api/TestAction.xml");
		importerToTest=new XMLDSLTypeImporter();
	}
	
	@Test
	public void testfile_type_Action_xml__is_imported_for_type_Action_and_contains_data() throws Exception {
		/* execute */
		Type type = importerToTest.importType(new FileInputStream(actionXMLFile));		
		
		/* test */
		assertNotNull(type);
		assertEquals("org.gradle.api.TestActionName",type.getName());	
		XMLType xmlType = (XMLType)type;
		assertEquals("gradle",xmlType.getLanguage());
		assertEquals("0.1",xmlType.getVersion());
		
		/* test method */
		assertEquals(1, type.getMethods().size());
		Method method = type.getMethods().iterator().next();
		assertNotNull(method);
		assertEquals("execute",method.getName());
		String methodDescription = method.getDescription();
		assertNotNull(methodDescription);
		assertEquals("executeMethodDescription",methodDescription.trim());
		
		/* test method parameter*/
		assertEquals(1, method.getParameters().size());
		Parameter parameter = method.getParameters().iterator().next();
		assertNotNull(parameter);
		assertEquals("param1Name", parameter.getName());
		XMLParameter xmlParam1 = (XMLParameter)parameter;
		assertEquals("param1Type", xmlParam1.getTypeAsString());
		
		/* test property*/
		Set<? extends Property> properties = type.getProperties();
		assertEquals(1, properties.size());
		Property property = properties.iterator().next();
		String propertyDescription = property.getDescription();
		assertNotNull(propertyDescription);
		assertEquals("property1Description", propertyDescription.trim());
		assertEquals("property1Name", property.getName());
		XMLProperty xmlProperty1 = (XMLProperty)property;
		assertEquals("property1Type", xmlProperty1.getTypeAsString());
		
	}

}

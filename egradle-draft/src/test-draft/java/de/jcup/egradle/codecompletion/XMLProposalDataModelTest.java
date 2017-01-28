package de.jcup.egradle.codecompletion;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import de.jcup.egradle.codecompletion.XMLProposalDataModel;
import de.jcup.egradle.codecompletion.XMLProposalDataModel.PreparationException;
import de.jcup.egradle.codecompletion.XMLProposalDataModel.SyntheticXMLProposalContainer;
import de.jcup.egradle.codecompletion.XMLProposalDataModel.XMLProposalContainer;
import de.jcup.egradle.codecompletion.XMLProposalDataModel.XMLProposalData;
import de.jcup.egradle.codecompletion.XMLProposalDataModel.XMLProposalElement;
import de.jcup.egradle.codecompletion.XMLProposalDataModel.XMLProposalRootPathEntry;
import de.jcup.egradle.codecompletion.XMLProposalDataModel.XMLProposalValue;

public class XMLProposalDataModelTest {

	private XMLProposalDataModel modelToTest;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void before() {
		modelToTest = new XMLProposalDataModel();
	}
	@Test
	public void an_xml_element_having_name_but_not_having_a_code_returns_name_with_brackets_and_cursor_variable_as_fallback(){
		XMLProposalElement element = new XMLProposalElement();
		element.name="name";
		assertNull(element.code);
		
		assertEquals("name {\n\t$cursor\n}\n",element.getCode());
		assertNotNull("Element code must be set after call", element.code);
	}
	
	@Test
	public void an_xml_element_having_a_name_and_a_code_definition_returns_code(){
		XMLProposalElement element = new XMLProposalElement();
		element.name="name";
		element.code="code";
		
		assertEquals("code",element.getCode());
	}
	
	@Test
	public void preparation_throws_exception_when_dive_more_than_1000() throws Exception{
		prepareModelWithDiveFromElement1(999);
		/* prepare test*/
		expectedException.expect(PreparationException.class);		
		/* execute */
		modelToTest.ensurePrepared();
	}
	
	@Test
	public void preparation_throws_NO_exception_when_dive_is_1000() throws Exception{
		prepareModelWithDiveFromElement1(998);
		/* execute */
		modelToTest.ensurePrepared();
	}

	private void prepareModelWithDiveFromElement1(int additionalElementChildren) {
		/* prepare */
		modelToTest.id = "id1";

		XMLProposalData proposalData1 = new XMLProposalData();
		modelToTest.getProposals().add(proposalData1);

		// set context
		XMLProposalRootPathEntry rootPath = new XMLProposalRootPathEntry();
		rootPath.path = "";
		proposalData1.getContext().getRootPathEntries().add(rootPath);
		
		XMLProposalRootPathEntry rootPath2 = new XMLProposalRootPathEntry();
		rootPath2.path = "repositories";
		proposalData1.getContext().getRootPathEntries().add(rootPath2);
		

		// add element
		XMLProposalElement element1 = new XMLProposalElement();

		element1.description = "element1:";
		element1.name = "element1";

		proposalData1.getElements().add(element1);
		
		XMLProposalElement parent = element1;
		for (int i=0;i<additionalElementChildren;i++){
			XMLProposalElement subElement = new XMLProposalElement();
			subElement.name="subElement-"+i;
			parent.getElements().add(subElement);
			parent=subElement;
		}
	}

	/* FIXME ATR, 11.01.2017: write test and implement multiple root path entries */
	@Test
	public void path_correct_prepared() throws Exception {
		/* prepare */
		modelToTest.id = "id1";

		XMLProposalData proposalData1 = new XMLProposalData();
		modelToTest.getProposals().add(proposalData1);
		
		// set context
		XMLProposalRootPathEntry rootPath = new XMLProposalRootPathEntry();
		rootPath.path = "";
		proposalData1.getContext().getRootPathEntries().add(rootPath);
		
		XMLProposalRootPathEntry rootPath2 = new XMLProposalRootPathEntry();
		rootPath2.path = "allProjects";
		proposalData1.getContext().getRootPathEntries().add(rootPath2);
		

		// add element
		XMLProposalElement element1 = new XMLProposalElement();

		element1.description = "Repositories closure. Here you define used repos.";
		element1.name = "repositories";

		XMLProposalValue value1 = new XMLProposalValue();
		value1.code = "mavenLocal()";
		value1.description = "Uses your <b>local</b> maven repository";

		element1.getValues().add(value1);

		XMLProposalValue value2 = new XMLProposalValue();
		value2.code = "mavenCentral()";
		value2.description = "Uses <b>maven central</b> as maven repository";

		element1.getValues().add(value2);
		
		XMLProposalValue directValue = new XMLProposalValue();
		directValue.code="apply plugin: '$cursor'";
		directValue.description="Applies plugin";

		proposalData1.getElements().add(element1);
		proposalData1.getValues().add(directValue);
		
		XMLProposalElement subElement = new XMLProposalElement();
		subElement.name="subElement";
		element1.getElements().add(subElement);
			
		
		/* execute */
		modelToTest.ensurePrepared();

		/* test */
		Set<XMLProposalContainer> data0 = modelToTest.getContainersByPath("");
		assertNotNull(data0);
		assertEquals(1,data0.size());
		XMLProposalContainer next = data0.iterator().next();
		assertNotNull(next);
		assertTrue(next instanceof SyntheticXMLProposalContainer);
		// root test element
		List<XMLProposalElement> children = next.getElements();
		assertNotNull(children);
		assertEquals(1,children.size());
		assertEquals(element1,children.iterator().next());
		// root test values
		List<XMLProposalValue> values = next.getValues();
		assertNotNull(values);
		assertEquals(1,values.size());
		assertEquals(directValue, values.iterator().next());
		
		Set<XMLProposalContainer> data1 = modelToTest.getContainersByPath("repositories");
		assertNotNull(data1);
		assertEquals(1,data1.size());
		assertEquals(element1,data1.iterator().next());
		
		Set<XMLProposalContainer> data2 = modelToTest.getContainersByPath("repositories.subElement");
		assertEquals(subElement,data2.iterator().next());
	}
	
	/**
	 * This test does not test JAXB at all of course, but only if the annotations are correct set...
	 * @throws Exception
	 */
	@Test
	public void marshall_and_unmarshal_again_has_same_data() throws Exception {

		/* prepare */
		modelToTest.id = "id1";

		XMLProposalData data1 = new XMLProposalData();
		modelToTest.getProposals().add(data1);

		// set context
		XMLProposalRootPathEntry rootPath = new XMLProposalRootPathEntry();
		rootPath.path = "";
		data1.getContext().getRootPathEntries().add(rootPath);
		
		XMLProposalRootPathEntry rootPath2 = new XMLProposalRootPathEntry();
		rootPath2.path = "allProjects";
		data1.getContext().getRootPathEntries().add(rootPath2);
		

		// add element
		XMLProposalElement element1 = new XMLProposalElement();

		element1.description = "Repositories closure. Here you define used repos.";
		element1.name = "repositories";

		XMLProposalValue value1 = new XMLProposalValue();
		value1.code = "mavenLocal()";
		value1.description = "Uses your <b>local</b> maven repository";

		element1.getValues().add(value1);

		XMLProposalValue value2 = new XMLProposalValue();
		value2.code = "mavenCentral()";
		value2.description = "Uses <b>maven central</b> as maven repository";

		element1.getValues().add(value2);
		
		XMLProposalValue directValue = new XMLProposalValue();
		directValue.code="apply plugin: '$cursor'";
		directValue.description="Applies plugin";

		data1.getElements().add(element1);
		data1.getValues().add(directValue);
		
		XMLProposalElement subElement = new XMLProposalElement();
		subElement.name="subElement";
		element1.getElements().add(subElement);
			
		
		/* execute */

		JAXBContext jc = JAXBContext.newInstance(XMLProposalDataModel.class);
		javax.xml.bind.Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		marshaller.marshal(modelToTest, bos);
		
		String xml = bos.toString("UTF-8");
		
		/* test */
		assertNotNull(xml);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		XMLProposalDataModel loadedModel = (XMLProposalDataModel) unmarshaller.unmarshal(new ByteArrayInputStream(xml.getBytes()));
		assertNotNull(loadedModel);
		
		List<XMLProposalData> loadedPropsals = loadedModel.getProposals();
		assertNotNull(loadedPropsals);
		assertEquals(1, loadedPropsals.size());
		
		XMLProposalData loadedProposal = loadedPropsals.get(0);
		List<XMLProposalRootPathEntry> rootPathEntries = loadedProposal.getContext().getRootPathEntries();
		assertNotNull(rootPathEntries);
		assertEquals(2,rootPathEntries.size());
		
		List<XMLProposalValue> values = loadedProposal.getValues();
		assertNotNull(values);
		assertEquals(1, values.size());
		assertEquals("apply plugin: '$cursor'",values.get(0).getCode());
		assertEquals("Applies plugin",values.get(0).getDescription());
		
		List<XMLProposalElement> elements = loadedProposal.getElements();
		assertNotNull(elements);
		assertEquals(1, elements.size());
		XMLProposalElement loadedRepositories = elements.get(0);
		assertNotNull(loadedRepositories);
		assertEquals("repositories", loadedRepositories.getName());
		
		List<XMLProposalElement> loadedSubElements = loadedRepositories.getElements();
		assertNotNull(loadedSubElements);
		assertEquals(1,loadedSubElements.size());
		XMLProposalElement loadedSubElement = loadedSubElements.get(0);
		assertNotNull(loadedSubElement);
		assertEquals("subElement", loadedSubElement.getName());
		
		List<XMLProposalValue> loadedRepositoriesValues = loadedRepositories.getValues();
		assertNotNull(loadedRepositoriesValues);
		assertEquals(2,loadedRepositoriesValues.size());
		
		
	}

}

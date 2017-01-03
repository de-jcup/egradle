package de.jcup.egradle.core.codecompletion;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.Test;

import de.jcup.egradle.core.codecompletion.XMLProposalDataModel.XMLProposalData;
import de.jcup.egradle.core.codecompletion.XMLProposalDataModel.XMLProposalElement;
import de.jcup.egradle.core.codecompletion.XMLProposalDataModel.XMLProposalRootPathEntry;
import de.jcup.egradle.core.codecompletion.XMLProposalDataModel.XMLProposalValue;

public class XMLProposalDataModelTest {

	private XMLProposalDataModel modelToTest;

	@Before
	public void before() {
		modelToTest = new XMLProposalDataModel();
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

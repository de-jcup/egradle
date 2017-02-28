package de.jcup.egradle.codeassist.dsl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class XMLTasksImporter {

	/**
	 * Import set of tasks by given stream
	 * @param stream - may not be <code>null</code>
	 * @return task set
	 * @throws IOException
	 * 
	 */
	public Set<Task> importTasks(InputStream stream) throws IOException{
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(XMLTasks.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			XMLTasks loadedModel = (XMLTasks) unmarshaller
					.unmarshal(stream);
			return loadedModel.getTasks();
		} catch (JAXBException e) {
			throw new IOException("Was not able to create unmarshaller", e);
		}
	}
}

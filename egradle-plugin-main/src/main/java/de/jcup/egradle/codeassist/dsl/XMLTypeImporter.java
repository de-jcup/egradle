package de.jcup.egradle.codeassist.dsl;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class XMLTypeImporter {

	/**
	 * Import type by given stream
	 * @param stream - may not be <code>null</code>
	 * @return type
	 * @throws IOException
	 * 
	 */
	public XMLType importType(InputStream stream) throws IOException{
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(XMLType.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			XMLType loadedModel = (XMLType) unmarshaller
					.unmarshal(stream);
			return loadedModel;
		} catch (JAXBException e) {
			throw new IOException("Was not able to create unmarshaller", e);
		}
	}
}

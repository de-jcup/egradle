package de.jcup.egradle.other.sdkbuilder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class XMLDSLTypeOverridesImporter {

	/**
	 * Import type info by given stream
	 * @param stream - may not be <code>null</code>
	 * @return task set
	 * @throws IOException
	 * 
	 */
	public XMLDSLTypeOverrides importOverrides(InputStream stream) throws IOException{
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(XMLDSLTypeOverrides.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			XMLDSLTypeOverrides loadedModel = (XMLDSLTypeOverrides) unmarshaller
					.unmarshal(stream);
			return loadedModel;
		} catch (JAXBException e) {
			throw new IOException("Was not able to create unmarshaller", e);
		}
	}
}

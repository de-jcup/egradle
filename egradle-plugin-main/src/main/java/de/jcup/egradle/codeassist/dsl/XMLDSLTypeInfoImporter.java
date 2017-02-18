package de.jcup.egradle.codeassist.dsl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class XMLDSLTypeInfoImporter {

	/**
	 * Import type info by given stream
	 * @param stream - may not be <code>null</code>
	 * @return task set
	 * @throws IOException
	 * 
	 */
	public XMLDSLTypeDocumentation importTypeInfo(InputStream stream) throws IOException{
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(XMLDSLTypeDocumentation.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			XMLDSLTypeDocumentation loadedModel = (XMLDSLTypeDocumentation) unmarshaller
					.unmarshal(stream);
			return loadedModel;
		} catch (JAXBException e) {
			throw new IOException("Was not able to create unmarshaller", e);
		}
	}
}

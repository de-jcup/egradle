package de.jcup.egradle.core.codecompletion.model.gradledsl;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import de.jcup.egradle.core.codecompletion.model.Type;

public class XMLDSLTypeImporter {

	/* FIXME ATR, 18.01.2017: implement full*/
	
	/**
	 * Import type by given steram
	 * @param stream - may not be <code>null</code>
	 * @return type
	 * @throws IOException
	 * 
	 */
	public Type importType(InputStream stream) throws IOException{
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

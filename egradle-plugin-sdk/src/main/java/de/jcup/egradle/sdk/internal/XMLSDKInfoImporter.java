package de.jcup.egradle.sdk.internal;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class XMLSDKInfoImporter {

	/**
	 * Import sdk info by given stream
	 * @param stream - may not be <code>null</code>
	 * @return type
	 * @throws IOException
	 * 
	 */
	public XMLSDKInfo importSDKInfo(InputStream stream) throws IOException{
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(XMLSDKInfo.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			XMLSDKInfo loadedModel = (XMLSDKInfo) unmarshaller
					.unmarshal(stream);
			return loadedModel;
		} catch (JAXBException e) {
			throw new IOException("Was not able to create unmarshaller", e);
		}
	}
}
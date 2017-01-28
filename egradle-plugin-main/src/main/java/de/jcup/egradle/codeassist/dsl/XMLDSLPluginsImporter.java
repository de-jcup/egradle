package de.jcup.egradle.codeassist.dsl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class XMLDSLPluginsImporter {

	/**
	 * Import set of plugins by given stream
	 * @param stream - may not be <code>null</code>
	 * @return type
	 * @throws IOException
	 * 
	 */
	public Set<Plugin> importPlugins(InputStream stream) throws IOException{
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(XMLPlugins.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			XMLPlugins loadedModel = (XMLPlugins) unmarshaller
					.unmarshal(stream);
			return loadedModel.getPlugins();
		} catch (JAXBException e) {
			throw new IOException("Was not able to create unmarshaller", e);
		}
	}
}

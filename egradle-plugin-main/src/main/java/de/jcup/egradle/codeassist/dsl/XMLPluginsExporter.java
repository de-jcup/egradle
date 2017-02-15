package de.jcup.egradle.codeassist.dsl;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class XMLPluginsExporter {

	/**
	 * Export XMLPlugins
	 * @param plugins
	 * @param stream
	 * @throws IOException
	 */
	public void exportPlugins(XMLPlugins plugins , OutputStream stream) throws IOException{
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(XMLPlugins.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(plugins, stream);
		} catch (JAXBException e) {
			throw new IOException("Was not able to create unmarshaller", e);
		}
	}
}

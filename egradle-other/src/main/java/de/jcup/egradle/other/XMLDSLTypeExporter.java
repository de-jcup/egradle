package de.jcup.egradle.other;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import de.jcup.egradle.codeassist.dsl.XMLType;

public class XMLDSLTypeExporter {

	/**
	 * Export XMLType
	 * @param type
	 * @param stream
	 * @throws IOException
	 */
	public void exportType(XMLType type , OutputStream stream) throws IOException{
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(XMLType.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.marshal(type, stream);
		} catch (JAXBException e) {
			throw new IOException("Was not able to create unmarshaller", e);
		}
	}
}

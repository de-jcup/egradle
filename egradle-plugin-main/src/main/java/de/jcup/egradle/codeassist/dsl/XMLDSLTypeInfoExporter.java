package de.jcup.egradle.codeassist.dsl;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class XMLDSLTypeInfoExporter {

	/**
	 * Export XMLDSLTypeInfo
	 * @param dslTypeInfo
	 * @param stream
	 * @throws IOException
	 */
	public void exportTasks(XMLDSLTypeInfo dslTypeInfo , OutputStream stream) throws IOException{
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(XMLDSLTypeInfo.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(dslTypeInfo, stream);
		} catch (JAXBException e) {
			throw new IOException("Was not able to create unmarshaller", e);
		}
	}
}

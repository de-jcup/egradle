package de.jcup.egradle.sdk.internal;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;


public class XMLSDKInfoExporter {

	/**
	 * Export XMLSDKInfo
	 * @param sdkInfo
	 * @param stream
	 * @throws IOException
	 */
	public void exportSDKInfo(XMLSDKInfo sdkInfo , OutputStream stream) throws IOException{
		JAXBContext jc;
		try {
			jc = JAXBContext.newInstance(XMLSDKInfo.class);
			Marshaller marshaller = jc.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.marshal(sdkInfo, stream);
		} catch (JAXBException e) {
			throw new IOException("Was not able to create unmarshaller", e);
		}
	}
}

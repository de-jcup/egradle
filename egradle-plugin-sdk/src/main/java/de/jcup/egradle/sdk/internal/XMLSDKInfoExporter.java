/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 */
package de.jcup.egradle.sdk.internal;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class XMLSDKInfoExporter {

    /**
     * Export XMLSDKInfo
     * 
     * @param sdkInfo
     * @param stream
     * @throws IOException
     */
    public void exportSDKInfo(XMLSDKInfo sdkInfo, OutputStream stream) throws IOException {
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

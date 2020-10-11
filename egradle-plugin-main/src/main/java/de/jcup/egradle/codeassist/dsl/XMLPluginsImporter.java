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
package de.jcup.egradle.codeassist.dsl;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

public class XMLPluginsImporter {

    /**
     * Import set of plugins by given stream
     * 
     * @param stream - may not be <code>null</code>
     * @return plugin set
     * @throws IOException
     * 
     */
    public XMLPlugins importPlugins(InputStream stream) throws IOException {
        JAXBContext jc;
        try {
            jc = JAXBContext.newInstance(XMLPlugins.class);
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            XMLPlugins loadedModel = (XMLPlugins) unmarshaller.unmarshal(stream);
            return loadedModel;
        } catch (JAXBException e) {
            throw new IOException("Was not able to create unmarshaller", e);
        }
    }
}

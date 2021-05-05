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
package de.jcup.egradle.sdk.builder.action.type;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.ModifiableMethod;
import de.jcup.egradle.codeassist.dsl.ModifiableProperty;
import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.XMLType;
import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;
import de.jcup.egradle.sdk.builder.model.XMLDSLMethodInfo;
import de.jcup.egradle.sdk.builder.model.XMLDSLPropertyInfo;
import de.jcup.egradle.sdk.builder.model.XMLDSLTypeDocumentation;

public class MarkDocumentedLanguageElementsAction implements SDKBuilderAction {

    @Override
    public void execute(SDKBuilderContext context) throws IOException {
        for (String typeName : context.originTypeNameToOriginFileMapping.keySet()) {
            XMLType type = (XMLType) context.originGradleFilesProvider.getType(typeName);
            File dslXML = new File(context.gradleSubProjectDocsFolder, "src/docs/dsl/" + typeName + ".xml");
            if (dslXML.exists()) {
                type.setDocumented(true);
                XMLDSLTypeDocumentation dslInfo = context.originDslTypeInfoImporter.collectDSL(dslXML);
                markGradleDSLMethods(type, dslInfo);
                markGradleDSLPropertiess(type, dslInfo);
            } else {
                type.setDocumented(false);
            }
        }

    }

    private void markGradleDSLMethods(XMLType type, XMLDSLTypeDocumentation dslInfo) {
        Set<XMLDSLMethodInfo> methodInfos = dslInfo.getMethods();

        Set<Method> methods = type.getMethods();
        for (Method method : methods) {
            if (!(method instanceof ModifiableMethod)) {
                continue;
            }
            ModifiableMethod modifiableMethod = (ModifiableMethod) method;
            Iterator<XMLDSLMethodInfo> methodInfoIterator = methodInfos.iterator();
            String methodName = modifiableMethod.getName();
            while (methodInfoIterator.hasNext()) {
                XMLDSLMethodInfo methodInfo = methodInfoIterator.next();
                String methodInfoName = methodInfo.getName();
                if (methodName.equals(methodInfoName)) {
                    modifiableMethod.setDocumented(true);
                }
            }
        }
    }

    private void markGradleDSLPropertiess(XMLType type, XMLDSLTypeDocumentation dslInfo) {
        Set<XMLDSLPropertyInfo> propertyInfos = dslInfo.getProperties();

        Set<Property> properties = type.getProperties();
        for (Property method : properties) {
            if (!(method instanceof ModifiableProperty)) {
                continue;
            }
            ModifiableProperty modifiableProperty = (ModifiableProperty) method;
            Iterator<XMLDSLPropertyInfo> propertyInfoIterator = propertyInfos.iterator();
            String propertyName = modifiableProperty.getName();
            while (propertyInfoIterator.hasNext()) {
                XMLDSLPropertyInfo methodInfo = propertyInfoIterator.next();
                String propertyInfoName = methodInfo.getName();
                if (propertyName.equals(propertyInfoName)) {
                    modifiableProperty.setDocumented(true);
                }
            }
        }
    }
}

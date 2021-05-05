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
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;

import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.MethodUtils;
import de.jcup.egradle.codeassist.dsl.ModifiableMethod;
import de.jcup.egradle.codeassist.dsl.XMLType;
import de.jcup.egradle.sdk.builder.SDKBuilderContext;
import de.jcup.egradle.sdk.builder.action.SDKBuilderAction;
import de.jcup.egradle.sdk.builder.model.XMLDSLTypeOverrides;
import de.jcup.egradle.sdk.builder.model.XMLDSLTypeOverridesImporter;

public class ApplyOverridesToTypesAction implements SDKBuilderAction {

    @Override
    public void execute(SDKBuilderContext context) throws IOException {

        XMLDSLTypeOverridesImporter overridesImporter = new XMLDSLTypeOverridesImporter();
        File alternativeOverriesFile = new File(context.PARENT_OF_RES, "sdkbuilder/override/gradle/" + context.sdkInfo.getGradleVersion() + "/alternative-delegatesTo.xml");

        XMLDSLTypeOverrides overrides = null;
        try (FileInputStream fis = new FileInputStream(alternativeOverriesFile)) {
            overrides = overridesImporter.importOverrides(fis);
        }

        for (String typeName : context.originTypeNameToOriginFileMapping.keySet()) {
            XMLType originType = (XMLType) context.originGradleFilesProvider.getType(typeName);
            handleOverrides(originType, overrides);
        }

    }

    void handleOverrides(XMLType type, XMLDSLTypeOverrides overrides) {
        for (XMLType overriden : overrides.getOverrideTypes()) {
            if (overriden.getName().equals(type.getName())) {
                handleOverrideDelegationTarget(type, overriden);
            }
        }
    }

    private void handleOverrideDelegationTarget(XMLType type, XMLType overriden) {
        Set<Method> overridenMethods = overriden.getMethods();
        for (Method overridenMethod : overridenMethods) {
            for (Method method : type.getDefinedMethods()) {
                if (!(method instanceof ModifiableMethod)) {
                    continue;
                }
                if (MethodUtils.haveSameSignatures(method, overridenMethod)) {
                    ModifiableMethod xmlMethod = (ModifiableMethod) method;
                    xmlMethod.setDelegationTargetAsString(overridenMethod.getDelegationTargetAsString());
                }
            }
        }
    }
}

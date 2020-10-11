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
package de.jcup.egradle.codeassist.hover;

import de.jcup.egradle.codeassist.dsl.gradle.LanguageElementMetaData;
import de.jcup.egradle.codeassist.dsl.gradle.estimation.GradleLanguageElementEstimater.EstimationResult;
import de.jcup.egradle.core.model.Item;

public class HoverData implements LanguageElementMetaData {
    EstimationResult result;
    Item item;
    int length;
    int offset;

    public Item getItem() {
        return item;
    }

    public EstimationResult getResult() {
        return result;
    }

    public int getLength() {
        return length;
    }

    public int getOffset() {
        return offset;
    }

    @Override
    public boolean isTypeFromExtensionConfigurationPoint() {
        if (result == null) {
            return false;
        }
        return result.isTypeFromExtensionConfigurationPoint();
    }

    @Override
    public String getExtensionName() {
        if (result == null) {
            return null;
        }
        return result.getExtensionName();
    }
}
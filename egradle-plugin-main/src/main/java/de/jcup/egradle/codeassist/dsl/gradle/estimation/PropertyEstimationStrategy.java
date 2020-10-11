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
package de.jcup.egradle.codeassist.dsl.gradle.estimation;

import de.jcup.egradle.codeassist.dsl.Property;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.core.model.Item;

class PropertyEstimationStrategy extends AbstractEstimationStrategy {

    @Override
    EstimationData visitImpl(Type current, Item item) {
        return findByProperties(current, item);
    }

    private EstimationData findByProperties(Type currentType, Item item) {
        if (currentType == null) {
            return null;
        }
        if (item == null) {
            return null;
        }
        if (item.isClosureBlock()) {
            return null;
        }
        String checkItemName = item.getIdentifier();
        if (checkItemName == null) {
            return null;
        }
        for (Property p : currentType.getProperties()) {
            String propertyName = p.getName();
            if (checkItemName.equals(propertyName)) {
                EstimationData r = new EstimationData();
                r.type = p.getType();
                r.element = p;
                r.percent = 100;
                return r;
            }
        }
        return null;
    }

}
/*
 * Copyright 2017 Albert Tregnaghi
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
package de.jcup.egradle.core.text;

import java.util.ArrayList;
import java.util.List;

public interface DocumentIdentifier {

    public String getId();

    public static IdentifierStringIdBuilder createStringIdBuilder() {
        return new IdentifierStringIdBuilder();
    }

    public class IdentifierStringIdBuilder {
        private List<String> list = new ArrayList<>();

        private IdentifierStringIdBuilder() {

        }

        public String[] build() {
            return list.toArray(new String[list.size()]);
        }

        public IdentifierStringIdBuilder addAll(DocumentIdentifier[] identifiers) {
            if (identifiers == null) {
                return this;
            }
            for (DocumentIdentifier identifier : identifiers) {
                add(identifier);
            }
            return this;
        }

        public IdentifierStringIdBuilder add(DocumentIdentifier identifier) {
            if (identifier == null) {
                return this;
            }
            return add(identifier.getId());
        }

        public IdentifierStringIdBuilder add(String identifierAsString) {
            if (identifierAsString == null) {
                return this;
            }
            list.add(identifierAsString);
            return this;
        }

    }

}

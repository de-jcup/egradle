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
package de.jcup.egradle.template;

public enum Features implements Feature {

    NEW_PROJECT__TYPE_MULTI_PROJECT("feature.newproject.type.multiproject"),

    NEW_PROJECT__SUPPORTS_JAVA("feature.newproject.supports.java"),

    NEW_PROJECT__SUPPORTS_GRADLEWRAPPER("feature.newproject.supports.gradlewrapper"),

    NEW_PROJECT__SUPPORTS_HEADLESS_IMPORT("feature.newproject.supports.headlessimport");

    private String id;

    private Features(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

}

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
package de.jcup.egradle.core;

public class VersionData {

    private static final String TEXT__UNKNOWN = "unknown";

    public static final de.jcup.egradle.core.VersionData UNKNOWN = new VersionData(TEXT__UNKNOWN);

    private String asText;

    public VersionData(String version) {
        if (version == null) {
            version = TEXT__UNKNOWN;
        } else if (version.trim().length() < 1) {
            version = TEXT__UNKNOWN;
        }
        this.asText = version;
    }

    public String getAsText() {
        return asText;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((asText == null) ? 0 : asText.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        VersionData other = (VersionData) obj;
        if (asText == null) {
            if (other.asText != null)
                return false;
        } else if (!asText.equals(other.asText))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "VersionData [" + asText + "]";
    }
}

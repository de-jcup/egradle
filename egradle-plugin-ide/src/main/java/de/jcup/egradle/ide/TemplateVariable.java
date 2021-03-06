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
package de.jcup.egradle.ide;

public interface TemplateVariable {
    public String getVariableName();

    /**
     * 
     * @return <code>true</code> when this variable supports default values
     */
    public boolean hasDefaultValue();

    /**
     * @return default value or <code>null</code> when having no default value
     */
    public String getDefaultValue();
}

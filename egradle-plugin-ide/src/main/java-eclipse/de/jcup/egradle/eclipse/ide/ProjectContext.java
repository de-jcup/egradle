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
package de.jcup.egradle.eclipse.ide;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;

public class ProjectContext {

    private List<IProject> projects = new ArrayList<>();

    /**
     * @return a list of projects - never <code>null</code>
     */
    public List<IProject> getProjects() {
        return projects;
    }

}
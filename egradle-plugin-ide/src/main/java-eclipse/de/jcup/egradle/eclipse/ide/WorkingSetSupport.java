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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

public class WorkingSetSupport {

    public void addTofirstActiveWorkingSetWhenNotAlreadyInside(IProject project) {
        IWorkingSetManager wm = getWorkingSetManager();
        IWorkingSet[] workingSets = wm.getAllWorkingSets();

        for (IWorkingSet workingSet : workingSets) {
            IAdaptable[] elementsBefore = workingSet.getElements();
            List<IAdaptable> list = new ArrayList<IAdaptable>();
            for (IAdaptable adaptable : elementsBefore) {
                list.add(adaptable);
            }
            if (list.contains(project)) {
                /* already added - ignore */
                continue;
            }
            list.add(project);

            /* add it */
            workingSet.setElements(list.toArray(new IAdaptable[list.size()]));

        }

    }

    /**
     * Resolve working sets for given projects
     * 
     * @param project project to scan for
     * @return list of working set data - never <code>null</code>
     */
    public List<WorkingSetData> resolveWorkingSetsForProject(IProject project) {
        if (project == null) {
            return Collections.emptyList();
        }
        return resolveWorkingSetsForProjects(Collections.singletonList(project), getWorkingSetManager());
    }

    /**
     * Resolve working sets for given projects
     * 
     * @param projects projects to scan for
     * @return list of working set data - never <code>null</code>
     */
    public List<WorkingSetData> resolveWorkingSetsForProjects(Collection<IProject> projects) {
        return resolveWorkingSetsForProjects(projects, getWorkingSetManager());
    }

    /**
     * Resolve working sets for given projects
     * 
     * @param projects projects to scan for
     * @param manager  working set manager
     * @return list of working set data - never <code>null</code>
     */
    List<WorkingSetData> resolveWorkingSetsForProjects(Collection<IProject> projects, IWorkingSetManager manager) {
        List<WorkingSetData> list = new ArrayList<>();

        if (manager == null) {
            return list;
        }
        IWorkingSet[] workingSets = manager.getAllWorkingSets();

        if (workingSets == null || workingSets.length == 0) {
            return list;
        }
        for (IWorkingSet workingSet : workingSets) {
            if (workingSet == null) {
                continue;
            }
            try {
                IAdaptable[] elements = workingSet.getElements();
                if (elements == null || elements.length == 0) {
                    continue;
                }
                WorkingSetData data = new WorkingSetData();
                data.workingSet = workingSet;
                list.add(data);

                for (IAdaptable adaptable : elements) {
                    IProject project = adaptable.getAdapter(IProject.class);
                    if (project == null) {
                        continue;
                    }
                    data.projectNamesContainedBefore.add(project.getName());

                }

            } catch (IllegalStateException e) {
                /* ignore this working set */
            }
        }
        return list;

    }

    public static class WorkingSetData {
        IWorkingSet workingSet;
        Set<String> projectNamesContainedBefore = new TreeSet<>();

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append("WorkingSetData for:");
            if (workingSet == null) {
                sb.append("null");
            } else {
                sb.append(workingSet.getName());
            }
            sb.append(", contains:");
            sb.append(projectNamesContainedBefore);

            return sb.toString();
        }
    }

    public void restoreWorkingSetsForProjects(List<WorkingSetData> workingSetDataList, List<IProject> projects) {
        restoreWorkingSetsForProjects(workingSetDataList, projects, getWorkingSetManager());
    }

    private IWorkingSetManager getWorkingSetManager() {
        return PlatformUI.getWorkbench().getWorkingSetManager();
    }

    void restoreWorkingSetsForProjects(List<WorkingSetData> workingSetDataList, List<IProject> projects, IWorkingSetManager mockedManager) {
        if (workingSetDataList == null || workingSetDataList.isEmpty()) {
            return;
        }
        if (projects == null || projects.isEmpty()) {
            return;
        }

        for (WorkingSetData data : workingSetDataList) {
            if (data.projectNamesContainedBefore.isEmpty()) {
                continue;
            }
            updateWorkingSet(data, projects);
        }

    }

    private void updateWorkingSet(WorkingSetData data, List<IProject> projects) {
        IAdaptable[] elementsBefore = data.workingSet.getElements();
        if (elementsBefore == null) {
            elementsBefore = new IAdaptable[] {};
        }
        /* add existings */

        /* remove all still assigned projects from projectsToAdd */
        List<IProject> projectsToAdd = removeProjectWhenAlreadyInWorkingSet(data, elementsBefore, projects);
        IAdaptable[] newElements = buildNewElements(data, projectsToAdd, elementsBefore);

        data.workingSet.setElements(newElements);
    }

    private IAdaptable[] buildNewElements(WorkingSetData data, List<IProject> projectsToAdd, IAdaptable[] elementsBefore) {
        List<IAdaptable> workingSetAdaptables = new ArrayList<>();
        workingSetAdaptables.addAll(Arrays.asList(elementsBefore));

        for (IProject projectToAdd : projectsToAdd) {
            if (data.projectNamesContainedBefore.contains(projectToAdd.getName())) {
                workingSetAdaptables.add(projectToAdd);
            }
        }
        return workingSetAdaptables.toArray(new IAdaptable[workingSetAdaptables.size()]);
    }

    private List<IProject> removeProjectWhenAlreadyInWorkingSet(WorkingSetData data, IAdaptable[] elementsBefore, List<IProject> projects) {
        List<IProject> projectsToAdd = new ArrayList<>(projects);
        for (IProject project : projects) {
            String projectName = project.getName();

            for (IAdaptable elementBefore : elementsBefore) {
                if (!(elementBefore instanceof IProject)) {
                    elementBefore = elementBefore.getAdapter(IProject.class);
                    if (elementBefore == null) {
                        continue;
                    }
                }
                IProject projectBefore = (IProject) elementBefore;

                String projectBeforeName = projectBefore.getName();
                if (!projectName.equals(projectBeforeName)) {
                    continue;
                }

                if (data.projectNamesContainedBefore.contains(projectBeforeName)) {
                    projectsToAdd.remove(project);
                    break;
                }
            }
        }
        return projectsToAdd;
    }
}

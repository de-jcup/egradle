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
package de.jcup.egradle.eclipse.gradleeditor.outline;

import java.io.InputStream;

import org.eclipse.core.runtime.IAdaptable;

import de.jcup.egradle.core.model.BuildContext;
import de.jcup.egradle.core.model.groovyantlr.GradleModelBuilder;
import de.jcup.egradle.core.model.groovyantlr.GroovyModelFilters;
import de.jcup.egradle.core.util.Filter;
import de.jcup.egradle.core.util.MultiFilter;
import de.jcup.egradle.eclipse.api.GroovyBasedModelType;
import de.jcup.egradle.eclipse.ui.AbstractGroovyBasedEditorOutlineContentProvider;
import de.jcup.egradle.eclipse.ui.IExtendedEditor;
import de.jcup.egradle.eclipse.ui.PersistedMarkerHelper;

public class GradleEditorOutlineContentProvider extends AbstractGroovyBasedEditorOutlineContentProvider {

	protected static final GradleOutlineItemFilter GRADLE_FILTER = new GradleOutlineItemFilter();

	public GradleEditorOutlineContentProvider(IAdaptable adaptable) {
		outlineErrorMarkerHelper = createOutlineErrorMarkerHelper();
		if (adaptable == null) {
			return;
		}
		this.editor = adaptable.getAdapter(IExtendedEditor.class);
		this.logSupport = editor.getLogSupport();
	}

	protected PersistedMarkerHelper createOutlineErrorMarkerHelper() {
		return new PersistedMarkerHelper("de.jcup.egradle.parse.error");
	}

	protected GroovyBasedModelType createDefaultModelType() {
		return GradleModelTypes.GRADLE;
	}

	Object[] buildGradleModel(String charset, InputStream is, boolean filteringEnabled) throws Exception {
		GradleModelBuilder builder = new GradleModelBuilder(is);
		if (filteringEnabled) {
			builder.setPreCreationFilter(getPreCreationFilterFilter());
			builder.setPostCreationFilter(getPostCreationFilter());
		}

		BuildContext context = new BuildContext();
		Object[] elements = createModelAndGetRootElements(context, builder);
		appendError(context);
		return elements;
	}

	protected Object[] createElements(String charset, InputStream is, GroovyBasedModelType groovyBasedModelType)
			throws Exception {
		if (GradleModelTypes.GROOVY_FULL_ANTLR.equals(groovyBasedModelType)) {
			return buildGroovyASTModel(charset, is);
		} else if (GradleModelTypes.GRADLE.equals(groovyBasedModelType)) {
			return buildGradleModel(charset, is, true);
		} else if (GradleModelTypes.GRADLE__UNFILTERED.equals(groovyBasedModelType)) {
			return buildGradleModel(charset, is, false);
		}
		return null;
	}

	protected GradleOutlineItemFilter getPostCreationFilter() {
		return GRADLE_FILTER;
	}

	protected Filter getPreCreationFilterFilter() {
		if (filter == null) {
			MultiFilter mfilter = new MultiFilter();
			/*
			 * TODO ATR, 18.11.2016 - make this settings configurable for user -
			 * as done in java outline
			 */
			mfilter.add(GroovyModelFilters.FILTER_IMPORTS);
			filter = mfilter;

		}
		return filter;
	}

}

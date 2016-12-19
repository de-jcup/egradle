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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import de.jcup.egradle.core.api.Filter;
import de.jcup.egradle.core.api.MultiFilter;
import de.jcup.egradle.core.model.BuildContext;
import de.jcup.egradle.core.model.Error;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.model.ModelBuilder;
import de.jcup.egradle.core.model.ModelBuilder.ModelBuilderException;
import de.jcup.egradle.core.model.groovyantlr.GradleModelBuilder;
import de.jcup.egradle.core.model.groovyantlr.GradleModelFilters;
import de.jcup.egradle.core.model.groovyantlr.GroovyASTModelBuilder;
import de.jcup.egradle.eclipse.api.EGradleUtil;
import de.jcup.egradle.eclipse.gradleeditor.GradleEditor;
import de.jcup.egradle.eclipse.ui.PersistedMarkerHelper;

public class GradleEditorOutlineContentProvider implements ITreeContentProvider {
	private static final Object[] NO_OBJECTS = new Object[]{};

	private static final GradleOutlineItemFilter GRADLE_FILTER = new GradleOutlineItemFilter();

	private PersistedMarkerHelper outlineErrorMarker = new PersistedMarkerHelper("de.jcup.egradle.parse.error");

	private static Object[] EMPTY = NO_OBJECTS;

	private ModelType modelType;

	private GradleEditor gradleEditor;

	private Model model;

	private Object monitor = new Object();

	private Filter filter;
	

	public GradleEditorOutlineContentProvider(IAdaptable adaptable) {
		if (adaptable==null){
			return;
		}
		this.gradleEditor = adaptable.getAdapter(GradleEditor.class);
	}
	
	public ModelType getModelType() {
		if (modelType == null) {
			modelType = ModelType.GRADLE;// GROOVY_FULL_ANTLR;

		}
		return modelType;
	}

	public void setModelType(ModelType modelType) {
		this.modelType = modelType;
	}
	
	/**
	 * Clears model cache, so a model rebuild is tried
	 */
	public void clearModelCache(){
		useCachedModel=false;
		if (gradleEditor!=null){
			getElements(gradleEditor.getDocument());
		}
	}

	private boolean useCachedModel;
	
	@Override
	public Object[] getElements(Object inputElement) {
		String dataAsString = null;
		String charset = null;
		
		if (inputElement instanceof IDocument) {
			if (gradleEditor==null){
				return NO_OBJECTS;
			}
			if (useCachedModel && model!=null){
				return getRootChildren();
			}
			useCachedModel=true;
			IDocument document = (IDocument) inputElement;
			dataAsString = document.get();

			IEditorInput input = gradleEditor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFileEditorInput fie = (IFileEditorInput) input;
				IFile file = fie.getFile();
				try {
					charset = file.getCharset();
				} catch (CoreException e) {
					EGradleUtil.log(e);
				}
			}
		}else if (inputElement instanceof String){
			dataAsString=(String) inputElement;
		}else{
			/* do not set dataAsString - so FALL BACK must do the job */
		}
		
		synchronized (monitor) {
			if (dataAsString!=null){
				tryTolLoad(dataAsString, charset);
			}
			if (model != null) {
				return getRootChildren();
			}
			return new Object[] { "no content" };
		}
	}

	private Object[] tryTolLoad(String dataAsString, String charset) {
		/* try to load */
		try (InputStream is = new ByteArrayInputStream(dataAsString.getBytes())) {
			Object[] elements = null;
			ModelType modelType = getModelType();

			switch (modelType) {
			case GROOVY_FULL_ANTLR:
				elements = buildGroovyASTModel(charset, is);
				break;
			case GRADLE:
				elements = buildGradleModel(charset, is,true);
				break;
			case GRADLE__UNFILTERED:
				elements = buildGradleModel(charset, is,false);
				break;
			default:
				elements = new Object[] { modelType + " not supported as modeltype!" };
			}
			return elements;
		} catch (Exception e) {
			EGradleUtil.log("Problems on outline building", e);
			return null;
		}
	}

	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof Item) {
			Item item = (Item) parentElement;
			return item.getChildren();
		}
		return EMPTY;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof Item) {
			Item item = (Item) element;
			return item.getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof Item) {
			Item item = (Item) element;
			return item.hasChildren();
		}
		return false;
	}

	public Item tryToFindByOffset(int offset) {
		synchronized (monitor) {
			if (model == null) {
				return null;
			}
			return model.getItemAt(offset);
		}
	}

	private Object[] buildGroovyASTModel(String charset, InputStream is) throws Exception {
		GroovyASTModelBuilder builder = new GroovyASTModelBuilder(is);
		BuildContext context = new BuildContext();
		Object[] elements = createModelAndGetRootElements(context, builder);
		appendError(context);
		return elements;
	}

	private Object[] buildGradleModel(String charset, InputStream is,boolean filteringEnabled) throws Exception {
		GradleModelBuilder builder = new GradleModelBuilder(is);
		if (filteringEnabled){
			builder.setPreCreationFilter(getFilter());
			builder.setPostCreationFilter(GRADLE_FILTER);
		}

		BuildContext context = new BuildContext();
		Object[] elements = createModelAndGetRootElements(context, builder);
		appendError(context);
		return elements;
	}

	private Filter getFilter() {
		if (filter == null) {
			MultiFilter mfilter = new MultiFilter();
			/*
			 * TODO ATR, 18.11.2016 - make this settings configurable for user -
			 * as done in java outline
			 */
			mfilter.add(GradleModelFilters.FILTER_IMPORTS);
			filter = mfilter;

		}
		return filter;
	}

	private void clearErrorMarkers() {
		IFile file = resolveEditorFile();
		outlineErrorMarker.removeAllMarkers(file);

		EGradleUtil.removeAllValidationErrorsOfConsoleOutput();
	}

	private void appendError(BuildContext context) {

		if (!context.hasErrors()) {
			return;
		}

		IFile file = resolveEditorFile();
		if (file == null) {
			return;
		}

		try {
			for (Error error : context.getErrors()) {
				outlineErrorMarker.createErrorMarker(file, error.getMessage(), error.getLineNumber(),
						error.getCharStart(), error.getCharEnd());
			}
		} catch (CoreException e) {
			EGradleUtil.log(e);
		}
	}

	private IFile resolveEditorFile() {
		if (gradleEditor == null) {
			return null;
		}
		IEditorInput input = gradleEditor.getEditorInput();
		if (input == null) {
			return null;
		}
		IFile file = null;
		if (input instanceof IFileEditorInput) {
			IFileEditorInput fei = (IFileEditorInput) input;
			file = fei.getFile();
		}
		return file;
	}

	private Object[] createModelAndGetRootElements(BuildContext context, ModelBuilder builder)
			throws ModelBuilderException {
		synchronized (monitor) {
			Model newModel = builder.build(context);
			if (context == null || !context.hasErrors()) {
				switchToNewModel(newModel);
			}
		}
		return getRootChildren();
	}

	private void switchToNewModel(Model newModel) {
		clearErrorMarkers();
		model = newModel;
	}

	private Item[] getRootChildren() {
		if (model == null) {
			return new Item[] {};
		}
		return model.getRoot().getChildren();
	}

	public enum ModelType {
		GRADLE,
		
		/* debug variants:*/

		GROOVY_FULL_ANTLR, 
		
		GRADLE__UNFILTERED,

	}

}

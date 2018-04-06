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
package de.jcup.egradle.eclipse.ui;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;

import de.jcup.egradle.core.model.BuildContext;
import de.jcup.egradle.core.model.Error;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;
import de.jcup.egradle.core.model.ModelBuilder;
import de.jcup.egradle.core.model.ModelBuilder.ModelBuilderException;
import de.jcup.egradle.core.model.groovyantlr.GroovyASTModelBuilder;
import de.jcup.egradle.core.util.Filter;
import de.jcup.egradle.core.util.ILogSupport;
import de.jcup.egradle.eclipse.api.GroovyBasedModelType;

public abstract class AbstractGroovyBasedEditorOutlineContentProvider implements ITreeContentProvider {

	private static final Object[] NO_OBJECTS = new Object[] {};

	protected PersistedMarkerHelper outlineErrorMarkerHelper;
	private static Object[] EMPTY = NO_OBJECTS;
	private GroovyBasedModelType groovyBasedModelType;
	protected IExtendedEditor editor;
	private Model model;
	private Object monitor = new Object();
	protected Filter filter;
	protected ILogSupport logSupport;
	private boolean useCachedModel;

	public GroovyBasedModelType getModelType() {
		if (groovyBasedModelType == null) {
			groovyBasedModelType = createDefaultModelType();

		}
		return groovyBasedModelType;
	}

	protected abstract GroovyBasedModelType createDefaultModelType();

	public void setModelType(GroovyBasedModelType groovyBasedModelType) {
		this.groovyBasedModelType = groovyBasedModelType;
	}

	/**
	 * Clears model cache, so a model rebuild is tried
	 */
	public void clearModelCache() {
		useCachedModel = false;
		if (editor != null) {
			getElements(editor.getDocument());
		}
	}

	public AbstractGroovyBasedEditorOutlineContentProvider() {
		super();
	}

	@Override
	public Object[] getElements(Object inputElement) {
		String dataAsString = null;
		String charset = null;

		if (inputElement instanceof IDocument) {
			if (editor == null) {
				return NO_OBJECTS;
			}
			if (useCachedModel && model != null) {
				return getRootChildren();
			}
			useCachedModel = true;
			IDocument document = (IDocument) inputElement;
			dataAsString = document.get();

			IEditorInput input = editor.getEditorInput();
			if (input instanceof IFileEditorInput) {
				IFileEditorInput fie = (IFileEditorInput) input;
				IFile file = fie.getFile();
				try {
					charset = file.getCharset();
				} catch (CoreException e) {
					logSupport.logError("Was not able to get charset of file:" + file, e);
				}
			}
		} else if (inputElement instanceof String) {
			dataAsString = (String) inputElement;
		} else {
			/* do not set dataAsString - so FALL BACK must do the job */
		}

		synchronized (monitor) {
			if (dataAsString != null) {
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
			GroovyBasedModelType groovyBasedModelType = getModelType();

			elements = createElements(charset, is, groovyBasedModelType);
			if (elements == null) {
				elements = new Object[] { groovyBasedModelType + " not supported as modeltype!" };
			}

			return elements;
		} catch (Exception e) {
			logSupport.logError("Problems on outline building", e);
			return null;
		}
	}

	protected abstract Object[] createElements(String charset, InputStream is,
			GroovyBasedModelType groovyBasedModelType) throws Exception;

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

	protected Object[] buildGroovyASTModel(String charset, InputStream is) throws Exception {
		GroovyASTModelBuilder builder = new GroovyASTModelBuilder(is);
		BuildContext context = new BuildContext();
		Object[] elements = createModelAndGetRootElements(context, builder);
		appendError(context);
		return elements;
	}

	/**
	 * Clears error markers for THIS editor
	 */
	private void clearErrorMarkers() {
		IFile file = resolveEditorFile();
		outlineErrorMarkerHelper.removeAllMarkers(file);
	}

	protected void appendError(BuildContext context) {

		if (!context.hasErrors()) {
			return;
		}
		IFile file = resolveEditorFile();
		if (file == null) {
			return;
		}

		try {
			for (Error error : context.getErrors()) {
				outlineErrorMarkerHelper.createErrorMarker(file, error.getMessage(), error.getLineNumber(),
						error.getCharStart(), error.getCharEnd());
			}
		} catch (CoreException e) {
			logSupport.logError("Was not able to create error marker at file:" + file, e);
		}
	}

	private IFile resolveEditorFile() {
		if (editor == null) {
			return null;
		}
		IEditorInput input = editor.getEditorInput();
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

	protected Object[] createModelAndGetRootElements(BuildContext context, ModelBuilder builder)
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

	public Model getModel() {
		return model;
	}

	private Item[] getRootChildren() {
		if (model == null) {
			return new Item[] {};
		}
		return model.getRoot().getChildren();
	}

}
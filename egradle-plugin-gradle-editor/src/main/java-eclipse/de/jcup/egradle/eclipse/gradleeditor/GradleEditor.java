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
package de.jcup.egradle.eclipse.gradleeditor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorInput;

import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.core.util.GradleStringTransformer;
import de.jcup.egradle.core.util.ILogSupport;
import de.jcup.egradle.core.util.MultiMapStringTransformer;
import de.jcup.egradle.eclipse.AbstractGroovySourceViewerConfiguration;
import de.jcup.egradle.eclipse.api.VariableProvider;
import de.jcup.egradle.eclipse.gradleeditor.document.GradleFileDocumentProvider;
import de.jcup.egradle.eclipse.gradleeditor.document.GradleTextFileDocumentProvider;
import de.jcup.egradle.eclipse.gradleeditor.outline.GradleEditorContentOutlinePage;
import de.jcup.egradle.eclipse.gradleeditor.outline.GradleEditorOutlineContentProvider;
import de.jcup.egradle.eclipse.gradleeditor.outline.GradleQuickOutlineDialog;
import de.jcup.egradle.eclipse.preferences.IEditorPreferences;
import de.jcup.egradle.eclipse.ui.AbstractGroovyBasedContentOutlinePage;
import de.jcup.egradle.eclipse.ui.AbstractGroovyBasedEditor;
import de.jcup.egradle.eclipse.ui.AbstractGroovyBasedEditorOutlineContentProvider;
import de.jcup.egradle.eclipse.ui.AbstractGroovyBasedQuickOutline;
import de.jcup.egradle.eclipse.util.ColorManager;
import de.jcup.egradle.eclipse.util.VariablesProviderRegistry;

public class GradleEditor extends AbstractGroovyBasedEditor {

	/** The COMMAND_ID of this editor as defined in plugin.xml */
	public static final String EDITOR_ID = "org.egradle.editors.GradleEditor";
	/** The COMMAND_ID of the editor context menu */
	public static final String EDITOR_CONTEXT_ID = EDITOR_ID + ".context";
	/** The COMMAND_ID of the editor ruler context menu */
	public static final String EDITOR_CONTEXT_RULER_ID = EDITOR_CONTEXT_ID + ".ruler";

	private GradleFileType cachedGradleFileType;

	public GradleEditor() {

	}

	@Override
	protected String getPluginId() {
		return EditorActivator.PLUGIN_ID;
	}

	@Override
	protected void handleEditorInputChanged() {
		cachedGradleFileType = null;
		super.handleEditorInputChanged();
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getAdapter(Class<T> adapter) {
		if (GradleFileType.class.equals(adapter)) {
			return (T) getGradleFileType();
		}
		if (GradleStringTransformer.class.equals(adapter)) {
			return (T) createGradleStringTransformer();
		}
		if (GradleEditor.class.equals(adapter)) {
			return (T) this;
		}
		return super.getAdapter(adapter);
	}

	@Override
	public ILogSupport getLogSupport() {
		return EditorUtil.INSTANCE;
	}

	@Override
	public IEditorPreferences getPreferences() {
		return EditorUtil.getPreferences();
	}

	protected AbstractGroovySourceViewerConfiguration createSourceViewerConfiguration() {
		return new GradleSourceViewerConfiguration(this);
	}

	protected String getEditorInstanceRulerContextId() {
		return EDITOR_CONTEXT_RULER_ID;
	}

	protected String getEditorInstanceContextId() {
		return EDITOR_CONTEXT_ID;
	}

	GradleStringTransformer createGradleStringTransformer() {

		List<Map<String, String>> maps = new ArrayList<>();

		List<VariableProvider> providers = VariablesProviderRegistry.INSTANCE.getProviders();

		for (VariableProvider provider : providers) {

			Map<String, String> map = null;
			if (provider != null) {
				map = provider.getVariables(getEditorInput());
			}
			if (map != null) {
				maps.add(map);
			}
		}
		return new MultiMapStringTransformer(maps);
	}

	protected GradleFileDocumentProvider createStandardEditorInputProvider() {
		return new GradleFileDocumentProvider();
	}

	protected GradleTextFileDocumentProvider createFileStoreEditorInputProvider() {
		return new GradleTextFileDocumentProvider();
	}

	private GradleFileType getGradleFileType() {
		if (cachedGradleFileType != null) {
			return cachedGradleFileType;
		}
		IEditorInput editorInput = getEditorInput();
		if (editorInput == null) {
			return null;
		}
		String name = editorInput.getName();
		if (name == null) {
			return null;
		}
		if (!name.endsWith(".gradle")) {
			cachedGradleFileType = GradleFileType.UNKNOWN;
			return cachedGradleFileType;
		}
		/* It is a gradle file... */
		if (name.equals("settings.gradle")) {
			cachedGradleFileType = GradleFileType.GRADLE_SETTINGS_SCRIPT;
		} else if (name.equals("init.gradle")) {
			/*
			 * We do not check if USER_HOME/.gradle/init.d/ or for
			 * GRADLE_HOME/init.d/... The files are inside workspace and so we
			 * only support init.gradle - for 100% correct variant description
			 * see https://docs.gradle.org/current/userguide/init_scripts.html
			 */
			cachedGradleFileType = GradleFileType.GRADLE_INIT_SCRIPT;
		} else {
			/* nothing special - must be init script */
			cachedGradleFileType = GradleFileType.GRADLE_BUILD_SCRIPT;
		}
		return cachedGradleFileType;
	}

	protected AbstractGroovyBasedEditorOutlineContentProvider createOutlineContentProvider() {
		return new GradleEditorOutlineContentProvider(this);
	}

	protected AbstractGroovyBasedContentOutlinePage createContentOutlinePage() {
		return new GradleEditorContentOutlinePage(this);
	}

	@Override
	protected AbstractGroovyBasedQuickOutline createQuickOutlineDialog(Shell shell) {
		return new GradleQuickOutlineDialog(this, shell, "Quick outline");
	}

	@Override
	protected ColorManager getColorManager() {
		return EditorActivator.getDefault().getColorManager();
	}

	protected String getEditorIconPath() {
		return "icons/gradle-editor.png";
	}

	protected String getEditorIconPathOnError() {
		return "icons/gradle-editor-with-error.png";
	}
}

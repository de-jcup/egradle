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

import static de.jcup.egradle.eclipse.document.GroovyDocumentIdentifiers.*;
import static de.jcup.egradle.eclipse.gradleeditor.document.GradleDocumentIdentifiers.*;
import static de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorSyntaxColorPreferenceConstants.*;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.URLHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

import de.jcup.egradle.codeassist.RelevantCodeCutter;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.core.text.DocumentIdentifier;
import de.jcup.egradle.eclipse.AbstractGroovySourceViewerConfiguration;
import de.jcup.egradle.eclipse.document.GroovyDocumentIdentifiers;
import de.jcup.egradle.eclipse.gradleeditor.codeassist.GradleContentAssistProcessor;
import de.jcup.egradle.eclipse.gradleeditor.document.GradleDocumentIdentifiers;
import de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorPreferences;
import de.jcup.egradle.eclipse.preferences.IEditorPreferences;
import de.jcup.egradle.eclipse.util.ColorManager;

/**
 * 
 * @author Albert Tregnaghi
 *
 */
public class GradleSourceViewerConfiguration extends AbstractGroovySourceViewerConfiguration {
	private GradleContentAssistProcessor gradleContentAssistProcessor;
	private Map<String, GradleTextHover> gradleTextHoverMap = new HashMap<String, GradleTextHover>();

	/**
	 * Creates configuration by given adaptable
	 * 
	 * @param adaptable
	 *            must provide {@link ColorManager} and {@link IFile}
	 */
	public GradleSourceViewerConfiguration(IAdaptable adaptable) {
		super(adaptable, COLOR_NORMAL_TEXT);
		Assert.isNotNull(adaptable, "adaptable may not be null!");
		
		
		/* code completion */
		this.contentAssistant = new ContentAssistant();
		this.gradleContentAssistProcessor = new GradleContentAssistProcessor(adaptable, new RelevantCodeCutter());
		contentAssistant.setContentAssistProcessor(gradleContentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE);
		contentAssistant.setContentAssistProcessor(gradleContentAssistProcessor, GRADLE_APPLY_KEYWORD.getId());
		contentAssistant.setContentAssistProcessor(gradleContentAssistProcessor, GRADLE_KEYWORD.getId());
		contentAssistant.setContentAssistProcessor(gradleContentAssistProcessor, GRADLE_TASK_KEYWORD.getId());
		contentAssistant.setContentAssistProcessor(gradleContentAssistProcessor, GRADLE_VARIABLE.getId());
		contentAssistant.addCompletionListener(gradleContentAssistProcessor.getCompletionListener());

		// contentAssistant.enableColoredLabels(true); - when...
		// ICompletionProposalExtension6 implemented

		/* enable auto activation */
		contentAssistant.enableAutoActivation(true);

		/* set a propert orientation for proposal */
		contentAssistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		addDefaultPresentation(reconciler);

		addPresentation(reconciler, JAVA_KEYWORD.getId(), getPreferences().getColor(COLOR_JAVA_KEYWORD), SWT.BOLD);
		addPresentation(reconciler, GROOVY_KEYWORD.getId(), getPreferences().getColor(COLOR_GROOVY_KEYWORD), SWT.BOLD);
		// Groovy provides different strings: simple and GStrings, so we use
		// separate colors:
		addPresentation(reconciler, STRING.getId(), getPreferences().getColor(COLOR_NORMAL_STRING), SWT.NONE);
		addPresentation(reconciler, GSTRING.getId(), getPreferences().getColor(COLOR_GSTRING), SWT.NONE);

		addPresentation(reconciler, COMMENT.getId(), getPreferences().getColor(COLOR_COMMENT), SWT.NONE);
		addPresentation(reconciler, ANNOTATION.getId(), getPreferences().getColor(COLOR_ANNOTATION), SWT.NONE);
		addPresentation(reconciler, GROOVY_DOC.getId(), getPreferences().getColor(COLOR_GROOVY_DOC), SWT.NONE);
		addPresentation(reconciler, GRADLE_APPLY_KEYWORD.getId(), getPreferences().getColor(COLOR_GRADLE_APPLY_KEYWORD),
				SWT.BOLD);
		addPresentation(reconciler, GRADLE_KEYWORD.getId(), getPreferences().getColor(COLOR_GRADLE_OTHER_KEYWORD),
				SWT.BOLD);
		addPresentation(reconciler, GRADLE_TASK_KEYWORD.getId(), getPreferences().getColor(COLOR_GRADLE_TASK_KEYWORD),
				SWT.BOLD | SWT.ITALIC);

		addPresentation(reconciler, GRADLE_VARIABLE.getId(), getPreferences().getColor(COLOR_GRADLE_VARIABLE),
				SWT.ITALIC);
		addPresentation(reconciler, JAVA_LITERAL.getId(), getPreferences().getColor(COLOR_JAVA_LITERAL), SWT.BOLD);
		return reconciler;
	}

	@Override
	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		contentAssistant.setInformationControlCreator(getInformationControlCreator(sourceViewer));
		return contentAssistant;
	}

	@Override
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		if (sourceViewer == null)
			return null;

		return new IHyperlinkDetector[] { new URLHyperlinkDetector(), new GradleHyperlinkDetector(adaptable) };
	}

	@Override
	public IHyperlinkPresenter getHyperlinkPresenter(ISourceViewer sourceViewer) {
		return super.getHyperlinkPresenter(sourceViewer);
	}

	protected String[] createDefaultConfiguredContentTypes() {
		/* @formatter:off */
		return DocumentIdentifier.createStringIdBuilder().
				add(IDocument.DEFAULT_CONTENT_TYPE). 
				addAll(GroovyDocumentIdentifiers.values()).
				addAll(GradleDocumentIdentifiers.values()).
				
				build();
		/* @formatter:on */
	}

	@Override
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {

		GradleTextHover gradleTextHover = gradleTextHoverMap.get(contentType);
		if (gradleTextHover == null) {
			gradleTextHover = new GradleTextHover(this, sourceViewer, contentType);
			gradleTextHoverMap.put(contentType, gradleTextHover);
		}
		return gradleTextHover;
	}

	public GradleFileType getFileType() {
		return adaptable.getAdapter(GradleFileType.class);
	}

	@Override
	public void updateTextScannerDefaultColorToken() {
		if (gradleScanner == null) {
			return;
		}
		RGB color = getPreferences().getColor(COLOR_NORMAL_TEXT);
		gradleScanner.setDefaultReturnToken(createColorToken(color));
	}

	@Override
	protected IEditorPreferences getPreferences() {
		return GradleEditorPreferences.getInstance();
	}
}
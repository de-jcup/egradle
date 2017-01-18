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

import static de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorPreferences.*;
import static de.jcup.egradle.eclipse.gradleeditor.preferences.GradleEditorSyntaxColorPreferenceConstants.*;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.hyperlink.IHyperlinkPresenter;
import org.eclipse.jface.text.hyperlink.URLHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.texteditor.MarkerAnnotation;

import de.jcup.egradle.core.codecompletion.RelevantCodeCutter;
import de.jcup.egradle.eclipse.api.ColorManager;
import de.jcup.egradle.eclipse.gradleeditor.codecompletion.GradleContentAssistProcessor;
import de.jcup.egradle.eclipse.gradleeditor.document.GradleDocumentIdentifiers;
import de.jcup.egradle.eclipse.gradleeditor.presentation.GradleDefaultTextScanner;
import de.jcup.egradle.eclipse.gradleeditor.presentation.PresentationSupport;

public class GradleSourceViewerConfiguration extends SourceViewerConfiguration {

//	private GradleEditorDoubleClickStrategy doubleClickStrategy;
	private GradleDefaultTextScanner gradleScanner;
	private ColorManager colorManager;

	private TextAttribute defaultTextAttribute;
	private IAnnotationHover annotationHoover;
	private IAdaptable adaptable;
	private ContentAssistant contentAssistant;
	private GradleContentAssistProcessor gradleContentAssistProcessor;
	
	/**
	 * Creates configuration by given adaptable
	 * @param adaptable must provide {@link ColorManager} and {@link IFile}
	 */
	public GradleSourceViewerConfiguration(IAdaptable adaptable) {
		Assert.isNotNull(adaptable,"adaptable may not be null!"); 
		this.adaptable=adaptable;
		this.annotationHoover=new GradleEditorAnnotationHoover();

		this.colorManager = adaptable.getAdapter(ColorManager.class);
		Assert.isNotNull(colorManager," adaptable must support color manager");
		this.defaultTextAttribute=new TextAttribute(colorManager.getColor(EDITOR_PREFERENCES.getColor(COLOR_NORMAL_TEXT)));

		/* code completion */
		this.contentAssistant=new ContentAssistant();
		this.gradleContentAssistProcessor=new GradleContentAssistProcessor(adaptable, new RelevantCodeCutter());
		contentAssistant.setContentAssistProcessor(gradleContentAssistProcessor, IDocument.DEFAULT_CONTENT_TYPE);
		contentAssistant.setContentAssistProcessor(gradleContentAssistProcessor,
				GradleDocumentIdentifiers.GRADLE_APPLY_KEYWORD.getId());
		contentAssistant.setContentAssistProcessor(gradleContentAssistProcessor,
				GradleDocumentIdentifiers.GRADLE_KEYWORD.getId());
		contentAssistant.setContentAssistProcessor(gradleContentAssistProcessor,
				GradleDocumentIdentifiers.GRADLE_TASK_KEYWORD.getId());
		contentAssistant.setContentAssistProcessor(gradleContentAssistProcessor,
				GradleDocumentIdentifiers.GRADLE_VARIABLE.getId());
		contentAssistant.addCompletionListener(gradleContentAssistProcessor.getCompletionListener());
		
		/* enable auto activation */
		contentAssistant.enableAutoActivation(true);
	
		/* set a propert orientation for proposal */
		contentAssistant.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_ABOVE);
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

		return new IHyperlinkDetector[] { new URLHyperlinkDetector(), new GradleFileHyperlinkDetector(adaptable) };
	}

	@Override
	public IHyperlinkPresenter getHyperlinkPresenter(ISourceViewer sourceViewer) {
		return super.getHyperlinkPresenter(sourceViewer);
	}
	
	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		/* @formatter:off */
		return GradleDocumentIdentifiers.allIdsToStringArray( 
				IDocument.DEFAULT_CONTENT_TYPE);
		/* @formatter:on */
	}

	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		// if (doubleClickStrategy == null){
		// doubleClickStrategy = new GradleEditorDoubleClickStrategy();
		// }
		// return doubleClickStrategy;
		return super.getDoubleClickStrategy(sourceViewer, contentType);
	}
	
	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
	    return annotationHoover;
	}
	
	private class GradleEditorAnnotationHoover extends DefaultAnnotationHover{
		@Override
		protected boolean isIncluded(Annotation annotation) {
			if (annotation instanceof MarkerAnnotation){
				return true;
			}
			/* we do not support other annotations*/
			return false;
		}
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		addDefaultPresentation(reconciler);

		addPresentation(reconciler, GradleDocumentIdentifiers.JAVA_KEYWORD.getId(), EDITOR_PREFERENCES.getColor(COLOR_JAVA_KEYWORD),SWT.BOLD);
		addPresentation(reconciler, GradleDocumentIdentifiers.GROOVY_KEYWORD.getId(), EDITOR_PREFERENCES.getColor(COLOR_GROOVY_KEYWORD),SWT.BOLD);
		// Groovy provides different strings: simple and GStrings, so we use separate colors:
		addPresentation(reconciler, GradleDocumentIdentifiers.STRING.getId(), EDITOR_PREFERENCES.getColor(COLOR_NORMAL_STRING),SWT.NONE);
		addPresentation(reconciler, GradleDocumentIdentifiers.GSTRING.getId(), EDITOR_PREFERENCES.getColor(COLOR_GSTRING),SWT.NONE);
		
		addPresentation(reconciler, GradleDocumentIdentifiers.COMMENT.getId(), EDITOR_PREFERENCES.getColor(COLOR_COMMENT),SWT.NONE);
		addPresentation(reconciler, GradleDocumentIdentifiers.GRADLE_APPLY_KEYWORD.getId(), EDITOR_PREFERENCES.getColor(COLOR_GRADLE_APPLY_KEYWORD),SWT.BOLD);
		addPresentation(reconciler, GradleDocumentIdentifiers.GRADLE_KEYWORD.getId(), EDITOR_PREFERENCES.getColor(COLOR_GRADLE_OTHER_KEYWORD),SWT.BOLD);
		addPresentation(reconciler, GradleDocumentIdentifiers.GRADLE_TASK_KEYWORD.getId(), EDITOR_PREFERENCES.getColor(COLOR_GRADLE_TASK_KEYWORD),SWT.BOLD|SWT.ITALIC);
		
		addPresentation(reconciler, GradleDocumentIdentifiers.GRADLE_VARIABLE.getId(), EDITOR_PREFERENCES.getColor(COLOR_GRADLE_VARIABLE),SWT.ITALIC);
		addPresentation(reconciler, GradleDocumentIdentifiers.JAVA_LITERAL.getId(),  EDITOR_PREFERENCES.getColor(COLOR_JAVA_LITERAL),SWT.BOLD);
		return reconciler;
	}
	
	private void addDefaultPresentation(PresentationReconciler reconciler) {
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(getGradleDefaultTextScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);
	}

	private IToken createColorToken(RGB rgb) {
		Token token = new Token(new TextAttribute(colorManager.getColor(rgb)));
		return token;
	}

	private void addPresentation(PresentationReconciler reconciler, String id, RGB rgb, int style) {
		TextAttribute textAttribute = new TextAttribute(colorManager.getColor(rgb),defaultTextAttribute.getBackground(), style);
		PresentationSupport presentation = new PresentationSupport(textAttribute);
		reconciler.setDamager(presentation, id);
		reconciler.setRepairer(presentation, id);
	}

	private GradleDefaultTextScanner getGradleDefaultTextScanner() {
		if (gradleScanner == null) {
			gradleScanner = new GradleDefaultTextScanner(colorManager);
			updateTextScannerDefaultColorToken();
		}
		return gradleScanner;
	}

	public void updateTextScannerDefaultColorToken() {
		if (gradleScanner==null){
			return;
		}
		RGB color = EDITOR_PREFERENCES.getColor(COLOR_NORMAL_TEXT);
		gradleScanner.setDefaultReturnToken(createColorToken(color));
	}

}
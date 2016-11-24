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

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.TextAttribute;
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

import de.jcup.egradle.eclipse.api.ColorManager;
import de.jcup.egradle.eclipse.gradleeditor.document.GradleDocumentIdentifiers;
import de.jcup.egradle.eclipse.gradleeditor.presentation.GradleDefaultTextScanner;
import de.jcup.egradle.eclipse.gradleeditor.presentation.PresentationSupport;

public class GradleSourceViewerConfiguration extends SourceViewerConfiguration {
//	private GradleEditorDoubleClickStrategy doubleClickStrategy;
	private GradleDefaultTextScanner gradleScanner;
	private ColorManager colorManager;

	private TextAttribute defaultTextAttribute;
	private IAnnotationHover annotationHoover;
	
	public GradleSourceViewerConfiguration(ColorManager colorManager) {
		this.annotationHoover=new GradleEditorAnnotationHoover();
		this.colorManager = colorManager;
		this.defaultTextAttribute=new TextAttribute(colorManager.getColor(ColorConstants.BLACK));
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

		addPresentation(reconciler, GradleDocumentIdentifiers.JAVA_KEYWORD.getId(), ColorConstants.KEYWORD_DEFAULT_PURPLE,SWT.BOLD);
		addPresentation(reconciler, GradleDocumentIdentifiers.GROOVY_KEYWORD.getId(), ColorConstants.KEYWORD_DEFAULT_PURPLE,SWT.BOLD);
		// Groovy provides different strings: simple and GStrings, so we use separate colors:
		addPresentation(reconciler, GradleDocumentIdentifiers.STRING.getId(), ColorConstants.STRING_DEFAULT_BLUE,SWT.NONE);
		addPresentation(reconciler, GradleDocumentIdentifiers.GSTRING.getId(), ColorConstants.ROYALBLUE,SWT.NONE);
		
		addPresentation(reconciler, GradleDocumentIdentifiers.COMMENT.getId(), ColorConstants.COMMENT_DEFAULT_GREEN,SWT.NONE);
		addPresentation(reconciler, GradleDocumentIdentifiers.GRADLE_LINK_KEYWORD.getId(), ColorConstants.LINK_DEFAULT_BLUE,SWT.BOLD);
		addPresentation(reconciler, GradleDocumentIdentifiers.GRADLE_KEYWORD.getId(), ColorConstants.KEYWORD_DEFAULT_PURPLE,SWT.BOLD);
		addPresentation(reconciler, GradleDocumentIdentifiers.GRADLE_TASK_KEYWORD.getId(), ColorConstants.TASK_DEFAULT_RED,SWT.BOLD|SWT.ITALIC);
		
		addPresentation(reconciler, GradleDocumentIdentifiers.GRADLE_VARIABLE.getId(), ColorConstants.DARK_GRAY,SWT.ITALIC);
		addPresentation(reconciler, GradleDocumentIdentifiers.JAVA_LITERAL.getId(), ColorConstants.KEYWORD_DEFAULT_PURPLE,SWT.BOLD);
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
			gradleScanner.setDefaultReturnToken(createColorToken(ColorConstants.BLACK));
		}
		return gradleScanner;
	}

}
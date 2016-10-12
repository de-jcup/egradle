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
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;

import de.jcup.egradle.eclipse.api.ColorManager;

public class GradleSourceViewerConfiguration extends SourceViewerConfiguration {
	private GradleEditorDoubleClickStrategy doubleClickStrategy;
	private GradleStringScanner tagScanner;
	private GradleScanner scanner;
	private ColorManager colorManager;

	public GradleSourceViewerConfiguration(ColorManager colorManager) {
		this.colorManager = colorManager;
	}

	@Override
	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, GradlePartitionScanner.GRADLE_COMMENT,
				GradlePartitionScanner.GRADLE_KEYWORD, GradlePartitionScanner.GRADLE_APPLY,
				GradlePartitionScanner.GRADLE_STRING };
	}

	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(ISourceViewer sourceViewer, String contentType) {
		if (doubleClickStrategy == null)
			doubleClickStrategy = new GradleEditorDoubleClickStrategy();
		return doubleClickStrategy;
	}

	protected GradleScanner getXMLScanner() {
		if (scanner == null) {
			scanner = new GradleScanner(colorManager);
			scanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager.getColor(ColorConstants.DEFAULT))));
		}
		return scanner;
	}

	protected GradleStringScanner getXMLTagScanner() {
		if (tagScanner == null) {
			tagScanner = new GradleStringScanner(colorManager);
			tagScanner.setDefaultReturnToken(new Token(new TextAttribute(colorManager.getColor(ColorConstants.TAG))));
		}
		return tagScanner;
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		PresentationReconciler reconciler = new PresentationReconciler();

		DefaultDamagerRepairer dr = null;
		NonRuleBasedDamagerRepairer ndr = null;

		dr = new DefaultDamagerRepairer(getXMLScanner());
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(colorManager.getColor(ColorConstants.COMMENT)));
		reconciler.setDamager(ndr, GradlePartitionScanner.GRADLE_COMMENT);
		reconciler.setRepairer(ndr, GradlePartitionScanner.GRADLE_COMMENT);

		ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(colorManager.getColor(ColorConstants.OTHER_KEYWORDS)));
		reconciler.setDamager(ndr, GradlePartitionScanner.GRADLE_KEYWORD);
		reconciler.setRepairer(ndr, GradlePartitionScanner.GRADLE_KEYWORD);

		ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(colorManager.getColor(ColorConstants.APPLY)));
		reconciler.setDamager(ndr, GradlePartitionScanner.GRADLE_APPLY);
		reconciler.setRepairer(ndr, GradlePartitionScanner.GRADLE_APPLY);

		ndr = new NonRuleBasedDamagerRepairer(new TextAttribute(colorManager.getColor(ColorConstants.STRING)));
		reconciler.setDamager(ndr, GradlePartitionScanner.GRADLE_STRING);
		reconciler.setRepairer(ndr, GradlePartitionScanner.GRADLE_STRING);

		return reconciler;
	}

}
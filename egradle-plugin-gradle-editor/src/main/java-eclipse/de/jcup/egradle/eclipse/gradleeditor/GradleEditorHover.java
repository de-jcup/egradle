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
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;

/* FIXME ATR, 25.11.2016 - currently not used */
public class GradleEditorHover implements ITextHover {

	private GradleEditor gradleEditor;

	GradleEditorHover(GradleEditor editor){
		this.gradleEditor=editor;
	}
	
	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		int offset= hoverRegion.getOffset();
		int length = hoverRegion.getLength();
		
		
		IDocument document = textViewer.getDocument();
		GradleBracketsSupport bracketMatcher = gradleEditor.getBracketMatcher();
		IRegion regionMatching = bracketMatcher.match(document, offset, length);
		if (regionMatching==null){
			return null;
		}
		return null;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return null;
	}

	
	
}

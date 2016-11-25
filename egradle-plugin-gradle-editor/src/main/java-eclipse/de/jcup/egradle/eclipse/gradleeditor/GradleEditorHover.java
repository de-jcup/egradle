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
		GradlePairMatcher bracketMatcher = gradleEditor.getBracketMatcher();
		IRegion regionMatching = bracketMatcher.match(document, offset, length);
		if (regionMatching==null){
			return null;
		}
		return null;
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	
	
}

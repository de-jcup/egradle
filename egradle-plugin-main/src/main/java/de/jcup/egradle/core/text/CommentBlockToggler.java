package de.jcup.egradle.core.text;

import java.util.ArrayList;
import java.util.List;

import de.jcup.egradle.core.util.StringUtilsAccess;

public class CommentBlockToggler {

	/**
	 * Toggles comments of given text lines 
	 * @param originLines
	 * @return a list with same size as given one but converted
	 */
	public List<TextLine> toggle(List<TextLine> originLines) {
		if (originLines==null){
			return null;
		}
		
		List<TextLine> toggledLines = new ArrayList<>();
		int deltaOffset=0;
		for (TextLine originLine: originLines){
			if (originLine==null){
				continue;
			}
			TextLine toggledLine = null;
			int originOffset = originLine.getOffset();
			if (! originLine.getContent().startsWith("//")){
				String withComment = "//"+originLine.getContent();
				toggledLine= new TextLine(originOffset+deltaOffset, withComment);
				deltaOffset+=2;
			}else{
				String withoutComment = StringUtilsAccess.substring(originLine.getContent(), 2);
				toggledLine= new TextLine(originOffset+deltaOffset, withoutComment);
				deltaOffset-=2;
			}
			toggledLines.add(toggledLine);
		}
		return toggledLines;
	}
	
	
}

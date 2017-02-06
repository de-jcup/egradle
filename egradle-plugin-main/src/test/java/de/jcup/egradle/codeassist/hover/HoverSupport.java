package de.jcup.egradle.codeassist.hover;

import de.jcup.egradle.codeassist.RelevantCodeCutter;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater.EstimationResult;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;

public class HoverSupport {

	public HoverData caclulateHoverData(String allText, int offset, RelevantCodeCutter codeCutter, Model model,GradleFileType fileType,GradleLanguageElementEstimater estimator){
		if (model == null) {
			return null;
		}
	
		int startOffset = codeCutter.getRelevantCodeStartOffset(allText, offset);
		if (startOffset==-1){
			return null;
		}
		Item item = model.getItemOnlyAt(startOffset);
		if (item == null) {
			return null;
		}
		HoverData data = new HoverData();
		data.item = item;
		String name = item.getName();
		if (name != null) {
			data.length = name.length();
		}
		data.offset = item.getOffset();
	
		EstimationResult result = estimator.estimate(item, fileType);
		data.result = result;
		return data;
	}
}

package de.jcup.egradle.codeassist.hover;

import de.jcup.egradle.codeassist.RelevantCodeCutter;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater;
import de.jcup.egradle.codeassist.dsl.gradle.GradleLanguageElementEstimater.EstimationResult;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;

public class HoverSupport {

	/**
	 * Calculates hover data
	 * @param allText text for which is hover data calculated , may not be <code>null</code>
	 * @param offset offset inside text , may not be out of range
	 * @param codeCutter necessary , may not be <code>null</code>
	 * @param model necessary , may not be <code>null</code>
	 * @param fileType necessary , may not be <code>null</code>
	 * @param estimator necessary , may not be <code>null</code>
	 * @return hover data or <code>null</code>
	 */
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

/*
 * Copyright 2016 Albert Tregnaghi
 *
 * Licensed under the Apache License, VersionData 2.0 (the "License");
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
 package de.jcup.egradle.codeassist.hover;

import static org.apache.commons.lang3.Validate.*;

import org.apache.commons.lang3.StringUtils;

import de.jcup.egradle.codeassist.RelevantCodeCutter;
import de.jcup.egradle.codeassist.dsl.gradle.GradleFileType;
import de.jcup.egradle.codeassist.dsl.gradle.estimation.GradleLanguageElementEstimater;
import de.jcup.egradle.codeassist.dsl.gradle.estimation.GradleLanguageElementEstimater.EstimationResult;
import de.jcup.egradle.core.model.Item;
import de.jcup.egradle.core.model.Model;

public class HoverSupport {

	/**
	 * Calculates hover data
	 * @param allText text for which is hover data calculated. if not <code>null</code> or blank, result will always be <code>null</code>
	 * @param offset offset inside text ,if out of range, result will always be <code>null</code>
	 * @param codeCutter necessary , may not be <code>null</code>
	 * @param model necessary , may not be <code>null</code>
	 * @param fileType necessary , may not be <code>null</code>
	 * @param estimator necessary , may not be <code>null</code>
	 * @return hover data or <code>null</code>
	 * @throws IllegalArgumentException if one of the mandatory parameters is missing
	 */
	public HoverData caclulateHoverData(String allText, int offset, RelevantCodeCutter codeCutter, Model model,GradleFileType fileType,GradleLanguageElementEstimater estimator){
		if (StringUtils.isBlank(allText)){
			return null;
		}
		if (offset<0){
			return null;
		}
		if (offset>=allText.length()){
			return null;
		}
		notNull(codeCutter, "'codeCutter' may not be null");
		notNull(fileType, "'fileType' may not be null");
		notNull(estimator, "'estimator' may not be null");
		
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

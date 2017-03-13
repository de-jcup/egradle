package de.jcup.egradle.codeassist.hover;

import de.jcup.egradle.codeassist.dsl.gradle.LanguageElementMetaData;
import de.jcup.egradle.codeassist.dsl.gradle.estimation.GradleLanguageElementEstimater.EstimationResult;
import de.jcup.egradle.core.model.Item;

public class HoverData implements LanguageElementMetaData {
		EstimationResult result;
		Item item;
		int length;
		int offset;

		public Item getItem() {
			return item;
		}
		
		public EstimationResult getResult() {
			return result;
		}
		
		public int getLength() {
			return length;
		}

		public int getOffset() {
			return offset;
		}

		@Override
		public boolean isTypeFromExtensionConfigurationPoint() {
			if (result==null){
				return false;
			}
			return result.isTypeFromExtensionConfigurationPoint();
		}

		@Override
		public String getExtensionName() {
			if (result==null){
				return null;
			}
			return result.getExtensionName();
		}
	}
package de.jcup.egradle.eclipse.gradleeditor;

import java.util.List;

import de.jcup.egradle.codeassist.CodeCompletionRegistry;
import de.jcup.egradle.codeassist.dsl.HTMLDescriptionBuilder;
import de.jcup.egradle.codeassist.dsl.LanguageElement;
import de.jcup.egradle.codeassist.dsl.Method;
import de.jcup.egradle.codeassist.dsl.Parameter;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLTypeProvider;
import de.jcup.egradle.core.api.LinkToTypeConverter;
import de.jcup.egradle.core.api.LinkToTypeConverter.LinkData;
import de.jcup.egradle.eclipse.gradleeditor.control.BrowserEGradleLinkListener;
import de.jcup.egradle.eclipse.gradleeditor.control.SimpleBrowserInformationControl;

public class DefaultEGradleLinkListener implements BrowserEGradleLinkListener {

	private LinkToTypeConverter linkToTypeConverter;
	private String fgColor;
	private String bgColor;
	private String commentColor;
	private HTMLDescriptionBuilder builder;

	public DefaultEGradleLinkListener(String fgColor, String bgColor, String commentColor, HTMLDescriptionBuilder builder) {
		this.linkToTypeConverter = new LinkToTypeConverter();
		this.fgColor = fgColor;
		this.bgColor = bgColor;
		this.commentColor = commentColor;
		this.builder=builder;
	}

	@Override
	public void onEGradleHyperlinkClicked(SimpleBrowserInformationControl control, String target) {
		LinkData data = linkToTypeConverter.convertLink(target);
		if (data==null){
			return;
		}
		String convertedName = data.getTypeName();
		String[] dataParameters = data.getParameters();
		String dataMethodName = data.getMethodName();
		if (convertedName == null) {
			return;
		}
		CodeCompletionRegistry registry = Activator.getDefault().getCodeCompletionRegistry();
		if (registry == null) {
			return;
		}
		GradleDSLTypeProvider provider = registry.getService(GradleDSLTypeProvider.class);
		if (provider == null) {
			return;
		}

		/* FIXME ATR, 06.02.2017: move link parameter resolving into own class!! and create testcases! 
		 * maybe it could be used for generation /link building as well
		 * */
		
		Type type = provider.getType(convertedName);
		if (type == null) {
			return;
		}
		LanguageElement elementTarget = type;
		/* check if link data has parameters inside */
		if (dataParameters.length>0){
			
			/* scan for method */
			for (Method methodOfType: type.getMethods()){
				String methodNameOfType = methodOfType.getName();
				if (!methodNameOfType.equals(dataMethodName)){
					continue;
				}
				List<Parameter> mParams = methodOfType.getParameters();
				if (mParams.size()!=dataParameters.length){
					continue;
				}
				boolean allParametersSame=true;
				int i=0;
				for(Parameter mParam: mParams){
					String mParamType = mParam.getTypeAsString();
					String dataParamType = dataParameters[i++];
					if (mParamType== null ){
						allParametersSame=false;
						break;
					}
					if (!mParamType.equals(dataParamType)){
						/* try shorting names*/
						int index = mParamType.lastIndexOf(".");
						if (index==-1 || index==mParamType.length()-1){
							allParametersSame=false;
							break;
						}
						String shortendMParamType = mParamType.substring(index);
						if (!shortendMParamType.equals(dataParamType)){
							allParametersSame=false;
							break;
						}
					}
				}
				if (allParametersSame){
					elementTarget=methodOfType;
					break;
				}
			}
		}
		control.setInformation(builder.buildHTMLDescription(fgColor, bgColor, commentColor, null, elementTarget,null));

	}

	@Override
	public boolean isAcceptingHyperlink(String target) {
		return linkToTypeConverter.isLinkSchemaConvertable(target);
	}

}

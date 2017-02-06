package de.jcup.egradle.eclipse.gradleeditor;

import org.eclipse.jface.text.IInformationControl;

import de.jcup.egradle.codeassist.CodeCompletionRegistry;
import de.jcup.egradle.codeassist.dsl.HTMLDescriptionBuilder;
import de.jcup.egradle.codeassist.dsl.Type;
import de.jcup.egradle.codeassist.dsl.gradle.GradleDSLTypeProvider;
import de.jcup.egradle.core.api.LinkToTypeConverter;
import de.jcup.egradle.eclipse.gradleeditor.control.BrowserEGradleLinkListener;

public class DefaultEGradleLinkListener implements BrowserEGradleLinkListener {

	private LinkToTypeConverter linkToTypeConverter;
	private String fgColor;
	private String bgColor;
	private HTMLDescriptionBuilder builder;

	public DefaultEGradleLinkListener(String fgColor, String bgColor, HTMLDescriptionBuilder builder) {
		this.linkToTypeConverter = new LinkToTypeConverter();
		this.fgColor = fgColor;
		this.bgColor = bgColor;
		this.builder=builder;
	}

	@Override
	public void onEGradleHyperlinkClicked(IInformationControl control, String target) {
		String convertedName = linkToTypeConverter.convertLink(target);
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

		Type type = provider.getType(convertedName);
		if (type == null) {
			return;
		}
		control.setInformation(builder.buildHTMLDescription(fgColor, bgColor, null, type));

	}

	@Override
	public boolean isAcceptingHyperlink(String target) {
		return linkToTypeConverter.isLinkSchemaConvertable(target);
	}

}

package de.jcup.egradle.eclipse.gradleeditor.control;

import org.eclipse.jface.text.IInformationControl;

public interface BrowserEGradleLinkListener {

	public boolean isAcceptingHyperlink(String target);
	
	public void onEGradleHyperlinkClicked(IInformationControl control, String target);
}

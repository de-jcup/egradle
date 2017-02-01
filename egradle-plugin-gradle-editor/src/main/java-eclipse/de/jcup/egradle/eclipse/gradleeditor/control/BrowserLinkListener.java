package de.jcup.egradle.eclipse.gradleeditor.control;

import org.eclipse.jface.text.IInformationControl;

public interface BrowserLinkListener {

	public boolean isAcceptingHyperlink(String target);
	
	public void onHyperlinkClicked(IInformationControl control, String target);
}

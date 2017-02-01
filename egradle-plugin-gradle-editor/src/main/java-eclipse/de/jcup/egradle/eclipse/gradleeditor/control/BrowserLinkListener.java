package de.jcup.egradle.eclipse.gradleeditor.control;

import org.eclipse.jface.text.IInformationControl;

public interface BrowserLinkListener {

	public void onHyperlinkClicked(IInformationControl control, String target);
}

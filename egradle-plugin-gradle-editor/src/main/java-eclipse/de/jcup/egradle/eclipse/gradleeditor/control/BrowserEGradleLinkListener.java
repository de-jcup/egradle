package de.jcup.egradle.eclipse.gradleeditor.control;

public interface BrowserEGradleLinkListener {

	public boolean isAcceptingHyperlink(String target);
	
	public void onEGradleHyperlinkClicked(SimpleBrowserInformationControl control, String target);
}

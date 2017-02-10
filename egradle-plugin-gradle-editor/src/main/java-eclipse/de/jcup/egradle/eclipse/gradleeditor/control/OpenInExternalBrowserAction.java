package de.jcup.egradle.eclipse.gradleeditor.control;

import java.net.URL;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import de.jcup.egradle.eclipse.api.EGradleUtil;

public class OpenInExternalBrowserAction extends Action {
	
	private String target;
	
	public void setTarget(String target) {
		this.target = target;
	}
	
	@Override
	public void run() {
		if (target==null){
			return;
		}
		try {
			URL url = new URL(target);
			// Open default external browser
			IWorkbenchBrowserSupport browserSupport = PlatformUI.getWorkbench().getBrowserSupport();
			IWebBrowser externalBrowser = browserSupport.getExternalBrowser();
			externalBrowser.openURL(url);
			
		} catch (Exception ex) {
			EGradleUtil.log(ex);
		}
	}
}